package tools.vitruv.optggs.transpiler;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGNode;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.graph.pattern.ConstraintPattern;
import tools.vitruv.optggs.transpiler.graph.tgg.Correspondence;
import tools.vitruv.optggs.transpiler.graph.tgg.CorrespondenceType;

public interface NameResolver {
    String resolveName(FQN fqn);

    default String resolveCorrespondenceName(CorrespondenceType correspondence) {
        return correspondence.source().localName() + "To" + correspondence.target().localName();
    }

    default String resolveCorrespondenceName(Correspondence correspondence) {
        return resolveCorrespondenceName(correspondence.toCorrespondenceType());
    }

    default String resolveRuleName(TripleRule rule) {
        if (rule.isLinkRule()) {
            // Link rule
            var originNodes =
                    rule.allSourcesAsSlice()
                            .filterMapNodes(
                                    node -> !node.links().isEmpty(),
                                    node -> node.getType().localName());
            var destinationNodes =
                    rule.allSourcesAsSlice()
                            .filterMapNodes(
                                    node -> node.links().isEmpty(),
                                    node -> node.getType().localName());
            return "Link"
                    + String.join("And", originNodes)
                    + "To"
                    + String.join("And", destinationNodes);
        } else {
            // Select rule
            var greenSources = rule.allSourcesAsSlice().findNodes(TGGNode::isGreen);
            var greenTargets = rule.allTargetsAsSlice().findNodes(TGGNode::isGreen);
            var sources = greenSources.stream().map(node -> node.getType().localName()).toList();
            var targets = greenTargets.stream().map(node -> node.getType().localName()).toList();
            return "Select" + String.join("And", sources) + "As" + String.join("And", targets);
        }
    }

    default String resolvePatternName(ConstraintPattern constraint) {
        return "Pattern" + constraint.getId().toString().replace("-", "");
    }

    default String resolveConstraintName(ConstraintPattern constraint) {
        return "No" + resolvePatternName(constraint);
    }
}

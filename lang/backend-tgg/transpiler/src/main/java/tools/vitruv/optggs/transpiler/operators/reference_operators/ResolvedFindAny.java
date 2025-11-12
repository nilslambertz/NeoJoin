package tools.vitruv.optggs.transpiler.operators.reference_operators;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.pattern.GraphPattern;
import tools.vitruv.optggs.transpiler.graph.pattern.PatternLink;
import tools.vitruv.optggs.transpiler.graph.pattern.PatternNode;
import tools.vitruv.optggs.transpiler.graph.tgg.GraphPathToNode;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRulesBuilder;

public class ResolvedFindAny implements ResolvedReferenceOperator {
    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule latestRule = builder.getLatestRule();

        final GraphPattern pattern = latestRule.convertSourceNodesToGraphPattern();

        final GraphPathToNode pathToLastNode = builder.getPathToLastNode();
        final FQN lastNodeType = pattern.findNestedNode(pathToLastNode).getType();

        // Duplicate last source node and incoming link
        PatternNode duplicatedLastNode = pattern.addNode(lastNodeType);
        PatternLink linkToLastDuplicatedLastNode =
                new PatternLink(pathToLastNode.getLastLink(), duplicatedLastNode);

        // Add the "duplicated" link to the node "above"
        final PatternNode nodeBeforeLastNode =
                pattern.findNestedNode(pathToLastNode.pathToSecondLastNode());
        nodeBeforeLastNode.addLink(linkToLastDuplicatedLastNode);

        builder.addConstraint(pattern);
    }
}

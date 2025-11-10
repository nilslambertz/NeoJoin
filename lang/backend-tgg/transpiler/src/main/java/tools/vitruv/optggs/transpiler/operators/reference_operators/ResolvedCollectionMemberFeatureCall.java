package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.Slice;
import tools.vitruv.optggs.transpiler.graph.TGGNode;
import tools.vitruv.optggs.transpiler.graph.TripleRule;
import tools.vitruv.optggs.transpiler.graph.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.graph.TripleRulesBuilder;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGLink;

@Value
public class ResolvedCollectionMemberFeatureCall implements ResolvedReferenceOperator {
    String feature;
    FQN featureElement;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule rule = builder.getLatestRule().deepCopy();

        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final TGGNode lastSourceNode = rule.findNestedSourceNode(pathToLastNode);

        final Slice sourceSlice = rule.addSourceSlice();
        TGGNode childNode = sourceSlice.addNode(featureElement);
        childNode.makeGreen();

        TGGLink parentLinkToChild = TGGLink.Green(feature, childNode);
        builder.addLinkToPathToLastNode(feature);
        lastSourceNode.addLink(parentLinkToChild);

        builder.addRule(rule);
    }
}

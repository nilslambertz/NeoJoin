package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

@Value
public class ResolvedCollectionMemberFeatureCall implements ResolvedReferenceOperator {
    String feature;
    FQN featureElement;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule rule = builder.getLatestRule().deepCopy();

        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final Node lastSourceNode = rule.findNestedSourceNode(pathToLastNode);

        final Slice sourceSlice = rule.addSourceSlice();
        Node childNode = sourceSlice.addNode(featureElement);
        childNode.makeGreen();

        Link parentLinkToChild = Link.Green(feature, childNode);
        builder.addLinkToPathToLastNode(feature);
        lastSourceNode.addLink(parentLinkToChild);

        builder.addRule(rule);
    }
}

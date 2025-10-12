package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

@Value
public class ResolvedMemberFeatureCall implements ResolvedReferenceOperator {
    String feature;
    FQN featureElement;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule newRule = builder.getLatestRule().deepCopy();

        final Node lastSourceNode =
                newRule.findNestedSourceNode(
                        builder.getSourceRoot(), builder.getReferencesToLastSourceNode());

        final Slice sourceSlice = newRule.addSourceSlice();
        Node childNode = sourceSlice.addNode(featureElement);
        childNode.makeGreen();

        Link parentLinkToChild = Link.Green(feature, childNode);
        builder.addReferenceToLastSourceNode(feature);
        lastSourceNode.addLink(parentLinkToChild);

        builder.addRule(newRule);
    }
}

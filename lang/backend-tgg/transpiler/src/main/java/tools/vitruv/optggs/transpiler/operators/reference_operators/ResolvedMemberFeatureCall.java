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
    boolean isCollection;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule rule;

        // If feature is a collection, we need to create a new rule
        if (isCollection) {
            rule = builder.getLatestRule().deepCopy();
        } else {
            rule = builder.getLatestRule();
        }

        final Node lastSourceNode =
                rule.findNestedSourceNode(
                        builder.getSourceRoot(), builder.getReferencesToLastSourceNode());

        final Slice sourceSlice = rule.addSourceSlice();
        Node childNode = sourceSlice.addNode(featureElement);
        childNode.makeGreen();

        Link parentLinkToChild = Link.Green(feature, childNode);
        builder.addReferenceToLastSourceNode(feature);
        lastSourceNode.addLink(parentLinkToChild);

        builder.addRule(rule);
    }
}

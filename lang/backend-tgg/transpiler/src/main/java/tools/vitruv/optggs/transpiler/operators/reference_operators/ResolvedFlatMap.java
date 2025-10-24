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
public class ResolvedFlatMap implements ResolvedReferenceOperator {
    String feature;
    FQN featureElement;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        // Copy the rule and make all nodes black
        final TripleRule newRule = builder.getLatestRule().deepCopy().makeBlack();

        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final Node lastSourceNode = newRule.findNestedSourceNode(pathToLastNode);

        final Slice sourceSlice = newRule.addSourceSlice();
        Node childNode = sourceSlice.addNode(featureElement);
        childNode.makeGreen();

        Link parentLinkToChild = Link.Green(feature, childNode);
        builder.addLinkToPathToLastNode(feature);
        lastSourceNode.addLink(parentLinkToChild);

        builder.addRule(newRule);
    }
}

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
public class ResolvedFlatMap implements ResolvedReferenceOperator {
    String feature;
    FQN featureElement;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        // Copy the rule and make all nodes black
        final TripleRule newRule = builder.getLatestRule().deepCopy().makeBlack();

        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final TGGNode lastSourceNode = newRule.findNestedSourceNode(pathToLastNode);

        final Slice sourceSlice = newRule.addSourceSlice();
        TGGNode childNode = sourceSlice.addNode(featureElement);
        childNode.makeGreen();

        TGGLink parentLinkToChild = TGGLink.Green(feature, childNode);
        builder.addLinkToPathToLastNode(feature);
        lastSourceNode.addLink(parentLinkToChild);

        builder.addRule(newRule);
    }
}

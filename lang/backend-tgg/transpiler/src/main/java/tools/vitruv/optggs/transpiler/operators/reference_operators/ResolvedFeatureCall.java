package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

@Value
public class ResolvedFeatureCall implements ResolvedReferenceOperator {
    FQN element;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        throw new IllegalStateException(
                "FeatureCall needs the target element to connect to, use custom method");
    }

    public TripleRule createFeatureCallRule(FQN targetTop, TripleRulesBuilder builder) {
        builder.setPathToLastNode(new TripleRulePathToNode(element));

        final TripleRule featureCallRule = builder.addRule();
        final Slice sourceSlice = featureCallRule.addSourceSlice();
        final Node sourceNode = sourceSlice.addNode(element);

        final Slice targetSlice = featureCallRule.addTargetSlice();
        final Node targetNode = targetSlice.addNode(targetTop);

        featureCallRule.addCorrespondenceRule(sourceNode, targetNode);

        return featureCallRule;
    }
}

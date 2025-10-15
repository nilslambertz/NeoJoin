package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

import java.util.List;

@Value
public class ResolvedReferenceOperatorChain {
    List<ResolvedReferenceOperator> referenceOperators;
    FQN targetRoot;
    FQN targetLeaf;
    String targetReference;

    public List<TripleRule> generateRules() {
        TripleRulesBuilder builder = new TripleRulesBuilder();

        final TripleRule featureCallRule;
        if (referenceOperators.getFirst() instanceof ResolvedFeatureCall featureCall) {
            featureCallRule = featureCall.createFeatureCallRule(targetRoot, builder);
        } else {
            throw new IllegalStateException("First Reference Operator must be a FeatureCall");
        }

        final List<ResolvedReferenceOperator> intermediaryOperators =
                referenceOperators.subList(1, referenceOperators.size() - 1);
        intermediaryOperators.forEach(
                resolvedReferenceOperator -> resolvedReferenceOperator.extendRules(builder));

        if (referenceOperators.getLast() instanceof ResolvedCollectReferences collectReferences) {
            collectReferences.extendRulesForCollect(
                    targetRoot, targetLeaf, targetReference, builder);
        } else {
            throw new IllegalStateException("Last Reference Operator must be a CollectReferences");
        }

        // If the feature call rule wasn't extended with any green elements, we can remove it
        if (!featureCallRule.hasAnyGreenElements()) {
            builder.removeRule(featureCallRule);
        }

        return builder.getTripleRules();
    }
}

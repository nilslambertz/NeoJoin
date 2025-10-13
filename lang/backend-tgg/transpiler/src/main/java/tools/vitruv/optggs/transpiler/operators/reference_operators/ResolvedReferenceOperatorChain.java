package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

import java.util.List;

@Value
public class ResolvedReferenceOperatorChain {
    List<ResolvedReferenceOperator> referenceOperators;
    String targetReference;
    FQN targetType;

    public void extendRules(FQN targetTop, TripleRulesBuilder builder) {
        final TripleRule featureCallRule;
        if (referenceOperators.getFirst() instanceof ResolvedFeatureCall featureCall) {
            featureCallRule = featureCall.createFeatureCallRule(targetTop, builder);
        } else {
            throw new IllegalStateException("First Reference Operator must be a FeatureCall");
        }

        final List<ResolvedReferenceOperator> intermediaryOperators =
                referenceOperators.subList(1, referenceOperators.size() - 1);
        intermediaryOperators.forEach(
                resolvedReferenceOperator -> resolvedReferenceOperator.extendRules(builder));

        if (referenceOperators.getLast() instanceof ResolvedCollectReferences collectReferences) {
            collectReferences.extendRulesForCollect(
                    targetTop, targetType, targetReference, builder);
        } else {
            throw new IllegalStateException("Last Reference Operator must be a CollectReferences");
        }

        // TODO: If the feature call rule didn't change (so no following operator modified it, but
        // just copied and created new rules), then we need to remove it from the builder rules list
        // because it only contains context nodes
    }
}

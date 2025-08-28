package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.SkipIntermediateReference;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BlockExpressionUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.ClosureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFieldUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFlatMapUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMemberCallUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkipIntermediateReferenceExtractor {
    public static Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression) {
        XAbstractFeatureCall nextFeatureCall =
                Optional.ofNullable(expression)
                        .flatMap(JvmFeatureCallUtils::asAbstractFeatureCall)
                        .orElse(null);
        if (nextFeatureCall == null) {
            return Optional.empty();
        }

        final List<SkipIntermediateReference.IntermediateReferenceInformation>
                intermediateReferenceInformation = new ArrayList<>();
        while (nextFeatureCall != null) {
            final Optional<SingleArgumentFlatMapCallData> flatMapCallData =
                    getSingleArgumentFlatMapCallData(nextFeatureCall);
            if (flatMapCallData.isEmpty()) {
                // No flatMap operation is found, no match
                break;
            }

            // Add the next intermediate reference data
            intermediateReferenceInformation.add(
                    new SkipIntermediateReference.IntermediateReferenceInformation(
                            flatMapCallData.get().getFeatureSimpleName(),
                            flatMapCallData.get().getFeatureIdentifier()));

            nextFeatureCall = flatMapCallData.get().getNextFeatureCall();
        }

        // If there are no flatMap-operations found, no match
        if (intermediateReferenceInformation.isEmpty()) {
            return Optional.empty();
        }

        // At least one flatMap-operation is found
        return Optional.of(
                new ReferenceOperatorWithNextCallTarget(
                        new SkipIntermediateReference(
                                intermediateReferenceInformation.reversed(), null),
                        nextFeatureCall));
    }

    @Value
    private static class SingleArgumentFlatMapCallData {
        String featureSimpleName;
        String featureIdentifier;
        String returnTypeIdentifier;

        XAbstractFeatureCall nextFeatureCall;
    }

    private static Optional<SingleArgumentFlatMapCallData> getSingleArgumentFlatMapCallData(
            XExpression expression) {
        final Optional<XMemberFeatureCall> memberFeatureCall =
                JvmFeatureCallUtils.asMemberFeatureCall(expression);
        if (memberFeatureCall.isEmpty()) {
            return Optional.empty();
        }

        final Optional<XAbstractFeatureCall> nextMemberCallTarget =
                memberFeatureCall.flatMap(JvmFeatureCallUtils::getNextMemberCallTarget);
        if (nextMemberCallTarget.isEmpty()) {
            return Optional.empty();
        }

        return memberFeatureCall
                .filter(JvmFlatMapUtils::isFlatMapOperation)
                .filter(JvmMemberCallUtils::hasExactlyOneMemberCallArgument)
                .flatMap(JvmMemberCallUtils::getFirstArgument)
                .flatMap(ClosureUtils::asClosure)
                .flatMap(ClosureUtils::getExpression)
                .flatMap(BlockExpressionUtils::asBlockExpression)
                .filter(BlockExpressionUtils::hasExactlyOneExpression)
                .flatMap(BlockExpressionUtils::getFirstExpression)
                .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                .flatMap(JvmFieldUtils::getJvmFieldData)
                // TODO: We probably don't need SingleArgumentFlatMapCallData if we have
                // JvmFieldData?
                .map(
                        fieldData ->
                                new SingleArgumentFlatMapCallData(
                                        fieldData.getFeatureSimpleName(),
                                        fieldData.getFeatureIdentifier(),
                                        fieldData.getReturnTypeIdentifier(),
                                        nextMemberCallTarget.get()));
    }
}

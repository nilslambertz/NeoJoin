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
        final List<SkipIntermediateReference.IntermediateReferenceInformation>
                intermediateReferenceInformation = new ArrayList<>();

        XExpression nextIntermediateReferenceExpression = expression;
        Optional<JvmFieldUtils.JvmFieldData> lastFieldData = Optional.empty();
        String childFeatureSimpleName = null;
        XAbstractFeatureCall lastFeatureCall = null;
        while (nextIntermediateReferenceExpression != null) {
            final Optional<SingleArgumentFlatMapCallData> flatMapCallData =
                    getSingleArgumentFlatMapCallData(nextIntermediateReferenceExpression);
            if (flatMapCallData.isEmpty()) {
                return Optional.empty();
            } else if (childFeatureSimpleName == null) {
                childFeatureSimpleName = flatMapCallData.get().getFeatureSimpleName();
            }

            intermediateReferenceInformation.add(
                    new SkipIntermediateReference.IntermediateReferenceInformation(
                            flatMapCallData.get().getFeatureSimpleName(),
                            flatMapCallData.get().getFeatureIdentifier()));

            lastFieldData =
                    JvmFieldUtils.getJvmFieldData(flatMapCallData.get().getNextFeatureCall());
            if (lastFieldData.isPresent()) {
                lastFeatureCall = flatMapCallData.get().getNextFeatureCall();
                break;
            }

            nextIntermediateReferenceExpression = flatMapCallData.get().getNextFeatureCall();
        }

        return Optional.of(
                new ReferenceOperatorWithNextCallTarget(
                        new SkipIntermediateReference(
                                intermediateReferenceInformation.reversed(),
                                lastFieldData.get().getFeatureSimpleName(),
                                null),
                        lastFeatureCall));
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

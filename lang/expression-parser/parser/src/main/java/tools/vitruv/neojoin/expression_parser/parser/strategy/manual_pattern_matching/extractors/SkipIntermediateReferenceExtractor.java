package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.SkipIntermediateReference;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.JvmFieldData;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.SingleArgumentFlatMapCallData;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmParameterUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmTypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkipIntermediateReferenceExtractor {
    public static Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression) {
        final List<SkipIntermediateReference.IntermediateReferenceInformation>
                intermediateReferenceInformation = new ArrayList<>();

        XExpression nextIntermediateReferenceExpression = expression;
        Optional<JvmFieldData> lastFieldData = Optional.empty();
        String childFeatureSimpleName = null;
        XAbstractFeatureCall lastFeatureCall = null;
        while (nextIntermediateReferenceExpression != null) {
            final Optional<SingleArgumentFlatMapCallData> flatMapCallData =
                    JvmTypeUtils.getSingleArgumentFlatMapCallData(
                            nextIntermediateReferenceExpression);
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
                    JvmTypeUtils.getJvmFieldData(flatMapCallData.get().getNextFeatureCall());
            if (lastFieldData.isPresent()) {
                lastFeatureCall = flatMapCallData.get().getNextFeatureCall();
                break;
            }

            nextIntermediateReferenceExpression = flatMapCallData.get().getNextFeatureCall();
        }

        Optional<JvmParameterUtils.JvmParameterData> lastParameterData =
                JvmParameterUtils.getJvmParameterData(lastFieldData.get().getNextFeatureCall());
        if (lastParameterData.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new ReferenceOperatorWithNextCallTarget(
                        new SkipIntermediateReference(
                                lastParameterData.get().getIdentifier(),
                                intermediateReferenceInformation.reversed(),
                                lastFieldData.get().getFeatureSimpleName(),
                                null),
                        lastFeatureCall));
    }
}

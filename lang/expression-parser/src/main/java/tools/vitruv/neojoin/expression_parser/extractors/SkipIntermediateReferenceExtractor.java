package tools.vitruv.neojoin.expression_parser.extractors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.JvmFieldData;
import tools.vitruv.neojoin.expression_parser.model.JvmParameterData;
import tools.vitruv.neojoin.expression_parser.model.SingleArgumentFlatMapCallData;
import tools.vitruv.neojoin.expression_parser.model.ToListCallData;
import tools.vitruv.neojoin.expression_parser.utils.JvmTypeUtils;
import tools.vitruv.neojoin.reference_operator.SkipIntermediateReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkipIntermediateReferenceExtractor {
    public static Optional<SkipIntermediateReference> extract(XExpression expression) {
        final Optional<ToListCallData> toListCallData = JvmTypeUtils.getToListCallData(expression);
        if (toListCallData.isEmpty()) {
            return Optional.empty();
        }

        final List<SkipIntermediateReference.IntermediateReferenceInformation>
                intermediateReferenceInformation = new ArrayList<>();
        XExpression nextIntermediateReferenceExpression = toListCallData.get().getNextFeatureCall();
        Optional<JvmFieldData> lastFieldData = Optional.empty();
        String childFeatureSimpleName = null;
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
                break;
            }

            nextIntermediateReferenceExpression = flatMapCallData.get().getNextFeatureCall();
        }

        Optional<JvmParameterData> lastParameterData =
                JvmTypeUtils.getJvmParameterData(lastFieldData.get().getNextFeatureCall());
        if (lastParameterData.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new SkipIntermediateReference(
                        lastParameterData.get().getIdentifier(),
                        intermediateReferenceInformation.reversed(),
                        lastFieldData.get().getFeatureSimpleName(),
                        null));
    }
}

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

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SkipIntermediateReferenceExtractor {
    public static Optional<SkipIntermediateReference> extract(XExpression expression) {
        final Optional<ToListCallData> toListCallData = JvmTypeUtils.getToListCallData(expression);
        if (toListCallData.isEmpty()) {
            return Optional.empty();
        }

        final Optional<SingleArgumentFlatMapCallData> flatMapCallData = JvmTypeUtils.getSingleArgumentFlatMapCallData(toListCallData.get().getNextFeatureCall());
        if (flatMapCallData.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JvmFieldData> fieldData = JvmTypeUtils.getJvmFieldData(flatMapCallData.get().getNextFeatureCall());
        if (fieldData.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JvmParameterData> parameterData = JvmTypeUtils.getJvmParameterData(fieldData.get().getNextFeatureCall());
        if (parameterData.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new SkipIntermediateReference(
                parameterData.get().getIdentifier(),
                fieldData.get().getFeatureSimpleName(),
                flatMapCallData.get().getFeatureIdentifier(),
                flatMapCallData.get().getFeatureSimpleName(),
                null
        ));
    }
}

package tools.vitruv.neojoin.expression_parser.extractors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.*;
import tools.vitruv.neojoin.expression_parser.utils.JvmTypeUtils;
import tools.vitruv.neojoin.reference_operator.ReferenceFilter;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReferenceFilterExtractor {
    public static Optional<ReferenceFilter> extract(XExpression expression) {
        final Optional<ToListCallData> toListCallData = JvmTypeUtils.getToListCallData(expression);
        if (toListCallData.isEmpty()) {
            return Optional.empty();
        }

        final Optional<SingleArgumentFilterCallData> filterCallData =
                JvmTypeUtils.getSingleArgumentFilterCallData(
                        toListCallData.get().getNextFeatureCall());
        if (filterCallData.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JvmFieldData> fieldData =
                JvmTypeUtils.getJvmFieldData(filterCallData.get().getNextFeatureCall());
        if (fieldData.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JvmParameterData> parameterData =
                JvmTypeUtils.getJvmParameterData(fieldData.get().getNextFeatureCall());
        if (parameterData.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new ReferenceFilter(
                        parameterData.get().getIdentifier(),
                        fieldData.get().getFeatureSimpleName(),
                        filterCallData.get().getFilterExpression(),
                        null));
    }
}

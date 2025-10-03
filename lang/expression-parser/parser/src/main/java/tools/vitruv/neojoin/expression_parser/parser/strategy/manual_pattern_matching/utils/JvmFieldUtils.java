package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;

import tools.vitruv.neojoin.expression_parser.model.FeatureInformation;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFieldUtils {
    @Value
    public static class JvmFieldData {
        String featureName;
        String featureClassSimpleName;
        String featureClassIdentifier;

        public FeatureInformation toFeatureInformation() {
            return new FeatureInformation(
                    featureName, featureClassSimpleName, featureClassIdentifier);
        }
    }

    public static Optional<JvmFieldData> getData(JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(JvmField::getType)
                .flatMap(JvmTypeReferenceUtils::asParameterizedTypeReference)
                .filter(JvmTypeReferenceUtils::hasExactlyOneArgument)
                .flatMap(JvmTypeReferenceUtils::getFirstArgument)
                .flatMap(JvmTypeReferenceUtils::asParameterizedTypeReference)
                .map(
                        field ->
                                new JvmFieldData(
                                        jvmField.getSimpleName(),
                                        field.getType().getSimpleName(),
                                        field.getType().getIdentifier()));
    }

    public static Optional<JvmField> asJvmField(JvmIdentifiableElement jvmIdentifiableElement) {
        return CastingUtils.cast(jvmIdentifiableElement, JvmField.class);
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFieldUtils {
    @Value
    public static class JvmFieldData {
        String featureSimpleName;
        String featureIdentifier;
        String returnTypeIdentifier;
    }

    public static Optional<JvmFieldData> getJvmFieldData(XAbstractFeatureCall featureCall) {
        return Optional.ofNullable(featureCall)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmFieldUtils::asJvmField)
                .map(
                        jvmField ->
                                new JvmFieldData(
                                        jvmField.getSimpleName(),
                                        jvmField.getIdentifier(),
                                        jvmField.getType().getType().getIdentifier()));
    }

    private static Optional<JvmField> asJvmField(JvmIdentifiableElement jvmIdentifiableElement) {
        return Optional.ofNullable(jvmIdentifiableElement)
                .filter(JvmField.class::isInstance)
                .map(JvmField.class::cast);
    }
}

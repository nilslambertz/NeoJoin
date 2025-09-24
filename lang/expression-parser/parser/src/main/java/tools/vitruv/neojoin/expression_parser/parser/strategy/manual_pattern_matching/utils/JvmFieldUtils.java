package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFieldUtils {
    @Value
    public static class JvmFieldData {
        String featureSimpleName;
        String featureIdentifier;
        String returnTypeIdentifier;
    }

    public static Optional<JvmFieldData> getData(JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(
                        field ->
                                new JvmFieldData(
                                        field.getSimpleName(),
                                        field.getIdentifier(),
                                        field.getType().getType().getIdentifier()));
    }

    public static Optional<JvmField> asJvmField(JvmIdentifiableElement jvmIdentifiableElement) {
        return CastingUtils.cast(jvmIdentifiableElement, JvmField.class);
    }
}

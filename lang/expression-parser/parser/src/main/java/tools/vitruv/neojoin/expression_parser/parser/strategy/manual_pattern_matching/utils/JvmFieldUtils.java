package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFieldUtils {
    public static Optional<JvmField> asJvmField(JvmIdentifiableElement jvmIdentifiableElement) {
        return CastingUtils.cast(jvmIdentifiableElement, JvmField.class);
    }
}

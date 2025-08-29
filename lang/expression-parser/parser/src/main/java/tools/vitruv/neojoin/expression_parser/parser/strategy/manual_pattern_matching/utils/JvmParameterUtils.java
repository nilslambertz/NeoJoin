package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmParameterUtils {
    public static Optional<JvmFormalParameter> asJvmFormalParameter(
            JvmIdentifiableElement jvmIdentifiableElement) {
        return CastingUtils.cast(jvmIdentifiableElement, JvmFormalParameter.class);
    }
}

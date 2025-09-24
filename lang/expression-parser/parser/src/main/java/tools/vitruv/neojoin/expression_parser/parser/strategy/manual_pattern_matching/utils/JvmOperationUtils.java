package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmOperationUtils {
    public static Optional<JvmOperation> asJvmOperation(
            JvmIdentifiableElement jvmIdentifiableElement) {
        return CastingUtils.cast(jvmIdentifiableElement, JvmOperation.class);
    }
}

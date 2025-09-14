package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.XExpression;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmOperationUtils {
    public static boolean isJvmOperation(JvmIdentifiableElement jvmIdentifiableElement) {
        return jvmIdentifiableElement instanceof JvmOperation;
    }

    public static Optional<JvmOperation> asJvmOperation(JvmIdentifiableElement jvmIdentifiableElement) {
        return CastingUtils.cast(jvmIdentifiableElement, JvmOperation.class);
    }
}

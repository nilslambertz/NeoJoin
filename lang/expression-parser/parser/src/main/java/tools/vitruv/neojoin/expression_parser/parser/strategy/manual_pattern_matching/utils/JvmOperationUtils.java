package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmOperationUtils {
    public static boolean isJvmOperation(JvmIdentifiableElement jvmIdentifiableElement) {
        return jvmIdentifiableElement instanceof JvmOperation;
    }
}

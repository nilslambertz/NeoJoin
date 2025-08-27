package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XExpression;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmToListUtils {
    private static final String toListSimpleName = "toList";

    public static boolean isToListOperation(XExpression expression) {
        return JvmTypeUtils.getFeature(expression)
                .filter(JvmTypeUtils::isJvmOperation)
                .map(JvmToListUtils::isToListCall)
                .orElse(false);
    }

    public static boolean isToListCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return toListSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

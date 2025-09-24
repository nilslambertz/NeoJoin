package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmToListUtils {
    private static final String toListSimpleName = "toList";

    public static boolean isToListOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmToListUtils::isToListCall)
                .orElse(false);
    }

    private static boolean isToListCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return toListSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

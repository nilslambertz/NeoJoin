package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFindFirstUtils {
    private static final String findFirstSimpleName = "findFirst";

    public static boolean isFindFirstOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmFindFirstUtils::isFindFirstCall)
                .orElse(false);
    }

    private static boolean isFindFirstCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return findFirstSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

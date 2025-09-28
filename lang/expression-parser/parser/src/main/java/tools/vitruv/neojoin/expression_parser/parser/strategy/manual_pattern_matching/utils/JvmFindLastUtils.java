package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFindLastUtils {
    private static final String findLastSimpleName = "findLast";

    public static boolean isFindLastOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmFindLastUtils::isFindLastCall)
                .orElse(false);
    }

    private static boolean isFindLastCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return findLastSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

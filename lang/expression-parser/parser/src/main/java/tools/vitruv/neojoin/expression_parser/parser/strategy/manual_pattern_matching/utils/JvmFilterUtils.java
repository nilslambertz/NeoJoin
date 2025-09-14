package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFilterUtils {
    private static final String filterSimpleName = "filter";

    public static boolean isFilterOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .filter(JvmOperationUtils::isJvmOperation)
                .map(JvmFilterUtils::isFilterCall)
                .orElse(false);
    }

    private static boolean isFilterCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return filterSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

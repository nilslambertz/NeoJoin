package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFlatMapUtils {
    private static final String flatMapSimpleName = "flatMap";

    public static boolean isFlatMapOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .filter(JvmOperationUtils::isJvmOperation)
                .map(JvmFlatMapUtils::isFlatMapCall)
                .orElse(false);
    }

    private static boolean isFlatMapCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return flatMapSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

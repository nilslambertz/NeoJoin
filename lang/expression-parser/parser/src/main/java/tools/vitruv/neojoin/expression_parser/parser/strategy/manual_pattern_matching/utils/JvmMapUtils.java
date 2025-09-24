package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmMapUtils {
    private static final String mapSimpleName = "map";

    public static boolean isMapOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmMapUtils::isFlatMapCall)
                .orElse(false);
    }

    private static boolean isFlatMapCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return mapSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

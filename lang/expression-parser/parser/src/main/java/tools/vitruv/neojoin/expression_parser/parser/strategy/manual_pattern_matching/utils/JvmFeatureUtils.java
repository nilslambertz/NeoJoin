package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFeatureUtils {
    public static Optional<JvmIdentifiableElement> getFeature(XMemberFeatureCall featureCall) {
        return JvmFeatureCallUtils.asMemberFeatureCall(featureCall)
                .map(XAbstractFeatureCall::getFeature);
    }
}

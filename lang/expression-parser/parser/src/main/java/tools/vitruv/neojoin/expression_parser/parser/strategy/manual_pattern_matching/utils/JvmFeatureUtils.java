package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFeatureUtils {
    public static Optional<JvmIdentifiableElement> getFeature(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        return Optional.ofNullable(memberFeatureCall.getFeature());
    }
}

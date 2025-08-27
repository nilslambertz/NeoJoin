package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFeatureCallUtils {
    public static Optional<XMemberFeatureCall> getAsMemberFeatureCall(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        return Optional.of(memberFeatureCall);
    }

    public static Optional<XAbstractFeatureCall> getNextFeatureCallTarget(XExpression expression) {
        if (!(expression instanceof XAbstractFeatureCall nextMemberCallTarget)) {
            return Optional.empty();
        }

        return Optional.of(nextMemberCallTarget);
    }
}

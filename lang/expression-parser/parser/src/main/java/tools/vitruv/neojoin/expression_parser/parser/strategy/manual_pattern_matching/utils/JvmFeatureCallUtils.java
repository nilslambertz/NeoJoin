package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFeatureCallUtils {
    public static Optional<XMemberFeatureCall> asMemberFeatureCall(XExpression expression) {
        return Optional.ofNullable(expression)
                .filter(XMemberFeatureCall.class::isInstance)
                .map(XMemberFeatureCall.class::cast);
    }

    public static Optional<XFeatureCall> asFeatureCall(XExpression expression) {
        return Optional.ofNullable(expression)
                .filter(XFeatureCall.class::isInstance)
                .map(XFeatureCall.class::cast);
    }

    public static Optional<XAbstractFeatureCall> asAbstractFeatureCall(XExpression expression) {
        return Optional.ofNullable(expression)
                .filter(XAbstractFeatureCall.class::isInstance)
                .map(XAbstractFeatureCall.class::cast);
    }

    public static Optional<XAbstractFeatureCall> getNextMemberCallTarget(XExpression expression) {
        return asMemberFeatureCall(expression)
                .map(XMemberFeatureCall::getMemberCallTarget)
                .flatMap(JvmFeatureCallUtils::asAbstractFeatureCall);
    }
}

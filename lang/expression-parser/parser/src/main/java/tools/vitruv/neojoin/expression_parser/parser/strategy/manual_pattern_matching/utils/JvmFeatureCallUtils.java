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
        return CastingUtils.cast(expression, XMemberFeatureCall.class);
    }

    public static Optional<XFeatureCall> asFeatureCall(XExpression expression) {
        return CastingUtils.cast(expression, XFeatureCall.class);
    }

    public static Optional<XAbstractFeatureCall> asAbstractFeatureCall(XExpression expression) {
        return CastingUtils.cast(expression, XAbstractFeatureCall.class);
    }
}

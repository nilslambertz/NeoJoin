package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;

import java.util.Optional;

public interface ReferenceOperatorParser {
    Optional<ReferenceOperator> parse(PatternMatchingStrategy strategy, XExpression expression)
            throws UnsupportedReferenceExpressionException;

    default Optional<XAbstractFeatureCall> findNextCallTarget(XExpression expression) {
        return JvmFeatureCallUtils.asMemberFeatureCall(expression)
                .map(XMemberFeatureCall::getMemberCallTarget)
                .flatMap(JvmFeatureCallUtils::asAbstractFeatureCall);
    }
}

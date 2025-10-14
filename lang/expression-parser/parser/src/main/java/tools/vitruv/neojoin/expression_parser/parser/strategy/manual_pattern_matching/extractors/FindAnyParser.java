package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XBinaryOperation;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.FindAny;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BinaryOperationUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BlockExpressionUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.ClosureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFindFirstUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFindLastUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMemberCallUtils;

import java.util.Optional;

public class FindAnyParser implements ReferenceOperatorParser {
    public Optional<ReferenceOperator> parse(
            PatternMatchingStrategy strategy, XExpression expression)
            throws UnsupportedReferenceExpressionException {
        Optional<XAbstractFeatureCall> nextMemberCallTarget = findNextCallTarget(expression);

        final Optional<XBinaryOperation> binaryOperation =
                Optional.of(expression)
                        .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                        .filter(
                                memberFeatureCall ->
                                        JvmFindFirstUtils.isFindFirstOperation(memberFeatureCall)
                                                || JvmFindLastUtils.isFindLastOperation(
                                                        memberFeatureCall))
                        .filter(JvmMemberCallUtils::hasExactlyOneMemberCallArgument)
                        .flatMap(JvmMemberCallUtils::getFirstArgument)
                        .flatMap(ClosureUtils::asClosure)
                        .flatMap(ClosureUtils::getExpression)
                        .flatMap(BlockExpressionUtils::asBlockExpression)
                        .filter(BlockExpressionUtils::hasExactlyOneExpression)
                        .flatMap(BlockExpressionUtils::getFirstExpression)
                        .flatMap(BinaryOperationUtils::asBinaryOperation);
        if (binaryOperation.isEmpty()) {
            return Optional.empty();
        }

        final ReferenceOperator foundOperator =
                binaryOperation
                        .flatMap(BinaryOperationUtils::extractBinaryExpression)
                        .map(FindAny::new)
                        .orElseThrow(
                                () ->
                                        new UnsupportedOperationException(
                                                "The MemberFeatureCall couldn't be parsed"));

        final ReferenceOperator followingOperator;
        if (nextMemberCallTarget.isPresent()) {
            followingOperator = strategy.parseReferenceOperator(nextMemberCallTarget.get());
            followingOperator.setFollowingOperator(foundOperator);
            return Optional.of(followingOperator);
        }

        return Optional.of(foundOperator);
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.CollectReferences;
import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.FlatMap;
import tools.vitruv.neojoin.expression_parser.model.Map;
import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BlockExpressionUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.ClosureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMemberCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmOperationUtils;

import java.util.Optional;

public class FlatMapParser implements ReferenceOperatorParser {
    private static final String FLAT_MAP_OPERATION_SIMPLE_NAME = "flatMap";

    public Optional<ReferenceOperator> parse(
            PatternMatchingStrategy strategy, XExpression expression)
            throws UnsupportedReferenceExpressionException {
        Optional<XAbstractFeatureCall> nextMemberCallTarget = findNextCallTarget(expression);

        // Check that expression is flatMap and get single argument
        final Optional<XExpression> flatMapArgument =
                JvmFeatureCallUtils.asMemberFeatureCall(expression)
                        .filter(FlatMapParser::isFlatMapOperation)
                        .filter(JvmMemberCallUtils::hasExactlyOneMemberCallArgument)
                        .flatMap(JvmMemberCallUtils::getFirstArgument);
        if (flatMapArgument.isEmpty()) {
            return Optional.empty();
        }

        // Check that expression can be parsed
        final Optional<XExpression> flatMapArgumentExpression =
                flatMapArgument
                        .flatMap(ClosureUtils::asClosure)
                        .flatMap(ClosureUtils::getExpression)
                        .flatMap(BlockExpressionUtils::asBlockExpression)
                        .filter(BlockExpressionUtils::hasExactlyOneExpression)
                        .flatMap(BlockExpressionUtils::getFirstExpression);
        if (flatMapArgumentExpression.isEmpty()) {
            return Optional.empty();
        }

        final ReferenceOperator flatMapArgumentOperator =
                strategy.parseReferenceOperator(flatMapArgumentExpression.get());
        if (!(flatMapArgumentOperator instanceof FeatureCall)) {
            throw new UnsupportedReferenceExpressionException(
                    "The first element of a flatMap expression must be a feature call",
                    flatMapArgumentExpression.get());
        }

        ReferenceOperator currentOperator = flatMapArgumentOperator.getFollowingOperator();
        if (currentOperator == null) {
            throw new UnsupportedReferenceExpressionException(
                    "The flatMap expression must contain more than a feature call",
                    flatMapArgumentExpression.get());
        }

        final ReferenceOperator operatorHead = new CollectReferences();
        ReferenceOperator lastOperator = operatorHead;
        while (currentOperator != null) {
            final ReferenceOperator nextOperator;
            if (currentOperator instanceof MemberFeatureCall memberFeatureCall
                    && memberFeatureCall.isCollection()) {
                nextOperator = new FlatMap(memberFeatureCall.getFeatureInformation());
            } else if (currentOperator instanceof MemberFeatureCall memberFeatureCall
                    && !memberFeatureCall.isCollection()) {
                nextOperator = new Map(memberFeatureCall.getFeatureInformation());
            } else if (currentOperator instanceof Map mapCall) {
                nextOperator = new Map(mapCall.getFeatureInformation());
            } else if (currentOperator instanceof FlatMap flatMapCall) {
                nextOperator = new FlatMap(flatMapCall.getFeatureInformation());
            } else if (currentOperator instanceof CollectReferences) {
                nextOperator = new CollectReferences();
            } else {
                throw new UnsupportedReferenceExpressionException(
                        "The flatMap expression is not supported", flatMapArgumentExpression.get());
            }

            lastOperator.setFollowingOperator(nextOperator);
            lastOperator = nextOperator;
            currentOperator = currentOperator.getFollowingOperator();
        }

        final ReferenceOperator followingOperator;
        if (nextMemberCallTarget.isPresent()) {
            followingOperator = strategy.parseReferenceOperator(nextMemberCallTarget.get());
            followingOperator.getLastOperatorInChain().setFollowingOperator(operatorHead);
            return Optional.of(followingOperator);
        }

        return Optional.of(operatorHead);
    }

    private static boolean isFlatMapOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmIdentifiableElement::getSimpleName)
                .map(FLAT_MAP_OPERATION_SIMPLE_NAME::equals)
                .orElse(false);
    }
}

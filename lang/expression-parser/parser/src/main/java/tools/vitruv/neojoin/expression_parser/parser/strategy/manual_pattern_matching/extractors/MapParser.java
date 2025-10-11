package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.FlatMap;
import tools.vitruv.neojoin.expression_parser.model.Map;
import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BlockExpressionUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.ClosureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMemberCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmOperationUtils;

import java.util.Optional;

public class MapParser implements ReferenceOperatorParser {
    private static final String MAP_OPERATION_SIMPLE_NAME = "map";

    public Optional<ReferenceOperatorWithNextFeatureCall> parse(
            PatternMatchingStrategy strategy, XExpression expression)
            throws UnsupportedReferenceExpressionException {
        Optional<XAbstractFeatureCall> nextMemberCallTarget = findNextCallTarget(expression);

        // Check that expression is map and get single argument
        final Optional<XExpression> mapArgument =
                JvmFeatureCallUtils.asMemberFeatureCall(expression)
                        .filter(MapParser::isMapOperation)
                        .filter(JvmMemberCallUtils::hasExactlyOneMemberCallArgument)
                        .flatMap(JvmMemberCallUtils::getFirstArgument);
        if (mapArgument.isEmpty()) {
            return Optional.empty();
        }

        // Check that expression can be parsed
        final Optional<XExpression> mapArgumentExpression =
                mapArgument
                        .flatMap(ClosureUtils::asClosure)
                        .flatMap(ClosureUtils::getExpression)
                        .flatMap(BlockExpressionUtils::asBlockExpression)
                        .filter(BlockExpressionUtils::hasExactlyOneExpression)
                        .flatMap(BlockExpressionUtils::getFirstExpression);
        if (mapArgumentExpression.isEmpty()) {
            return Optional.empty();
        }

        ReferenceOperator currentOperator =
                strategy.parseReferenceOperator(mapArgumentExpression.get());
        if (!(currentOperator instanceof FeatureCall)) {
            throw new UnsupportedReferenceExpressionException(
                    "The first element of a map expression must be a feature call",
                    mapArgumentExpression.get());
        }

        ReferenceOperator lastOperator = null;
        currentOperator = currentOperator.getFollowingOperator();
        if (currentOperator == null) {
            throw new UnsupportedReferenceExpressionException(
                    "The map expression must contain more than a feature call",
                    mapArgumentExpression.get());
        }

        while (currentOperator != null) {
            final ReferenceOperator nextOperator;
            if (currentOperator instanceof MemberFeatureCall memberFeatureCall) {
                nextOperator = new Map(memberFeatureCall.getFeatureInformation());
            } else if (currentOperator instanceof Map mapCall) {
                nextOperator = new FlatMap(mapCall.getFeatureInformation());
            } else {
                throw new UnsupportedReferenceExpressionException(
                        "The map expression is not supported", mapArgumentExpression.get());
            }

            nextOperator.setFollowingOperator(lastOperator);
            lastOperator = nextOperator;
            currentOperator = currentOperator.getFollowingOperator();
        }

        return Optional.of(
                new ReferenceOperatorWithNextFeatureCall(
                        lastOperator, nextMemberCallTarget.orElse(null)));
    }

    private static boolean isMapOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmIdentifiableElement::getSimpleName)
                .map(MAP_OPERATION_SIMPLE_NAME::equals)
                .orElse(false);
    }
}

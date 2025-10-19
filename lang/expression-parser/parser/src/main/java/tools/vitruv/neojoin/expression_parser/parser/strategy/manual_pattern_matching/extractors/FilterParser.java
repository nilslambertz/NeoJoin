package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.ReferenceFilter;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BlockExpressionUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.ClosureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFilterUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMemberCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.PredicateExpressionUtils;

import java.util.Optional;

public class FilterParser implements ReferenceOperatorParser {
    public Optional<ReferenceOperator> parse(
            PatternMatchingStrategy strategy, XExpression expression)
            throws UnsupportedReferenceExpressionException {
        final Optional<PredicateExpressionUtils.ConstantPredicate> constantFilterPredicate =
                Optional.of(expression)
                        .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                        .filter(JvmFilterUtils::isFilterOperation)
                        .filter(JvmMemberCallUtils::hasExactlyOneMemberCallArgument)
                        .flatMap(JvmMemberCallUtils::getFirstArgument)
                        .flatMap(ClosureUtils::asClosure)
                        .flatMap(ClosureUtils::getExpression)
                        .flatMap(BlockExpressionUtils::asBlockExpression)
                        .filter(BlockExpressionUtils::hasExactlyOneExpression)
                        .flatMap(BlockExpressionUtils::getFirstExpression)
                        .flatMap(PredicateExpressionUtils::asBinaryOperation)
                        .flatMap(PredicateExpressionUtils::extractConstantPredicate);
        if (constantFilterPredicate.isEmpty()) {
            return Optional.empty();
        }

        return parseAndAppendFollowingExpressionOperators(
                strategy,
                expression,
                new ReferenceFilter(
                        constantFilterPredicate.get().getFeature(),
                        constantFilterPredicate.get().getOperator(),
                        constantFilterPredicate.get().getConstantValue()));
    }
}

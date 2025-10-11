package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching;

import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.NonNull;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.FeatureCallParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.FilterParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.FindAnyParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.FlatMapParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.MapParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.MemberFeatureCallParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.ReferenceOperatorParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.ToListParser;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;

import java.util.List;
import java.util.Optional;

public class ManualPatternMatchingStrategy implements PatternMatchingStrategy {
    private static final List<ReferenceOperatorParser> PARSERS =
            List.of(
                    new FeatureCallParser(),
                    new MemberFeatureCallParser(),
                    new FilterParser(),
                    new ToListParser(),
                    new FlatMapParser(),
                    new MapParser(),
                    new FindAnyParser());

    @Override
    public @NonNull ReferenceOperator parseReferenceOperator(@NonNull XExpression expression)
            throws UnsupportedReferenceExpressionException {
        XExpression currentExpression = expression;
        ReferenceOperator lastOperator = null;
        while (currentExpression != null) {
            Optional<ReferenceOperatorWithNextFeatureCall> nextReferenceOperator =
                    getNextReferenceOperator(currentExpression);
            if (nextReferenceOperator.isEmpty()) {
                throw new UnsupportedReferenceExpressionException(currentExpression);
            }

            ReferenceOperator reversedFoundOperators =
                    nextReferenceOperator.get().getReferenceOperator().reverse();
            reversedFoundOperators.getLastOperatorInChain().setFollowingOperator(lastOperator);
            lastOperator = reversedFoundOperators;

            currentExpression = nextReferenceOperator.get().getNextFeatureCall();
        }

        return lastOperator;
    }

    private Optional<ReferenceOperatorWithNextFeatureCall> getNextReferenceOperator(
            XExpression expression) throws UnsupportedReferenceExpressionException {
        for (var parser : PARSERS) {
            final var result = parser.parse(this, expression);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }
}

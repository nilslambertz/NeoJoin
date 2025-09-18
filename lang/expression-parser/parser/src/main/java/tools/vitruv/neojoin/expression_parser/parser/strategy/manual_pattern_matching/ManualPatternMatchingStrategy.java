package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching;

import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.NonNull;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.FeatureCallExtractor;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.FilterExtractor;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.ReferenceOperatorExtractor;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.SkipIntermediateReferenceExtractor;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors.ToListExtractor;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;

import java.util.List;
import java.util.Optional;

public class ManualPatternMatchingStrategy implements PatternMatchingStrategy {
    private static final List<ReferenceOperatorExtractor> EXTRACTORS =
            List.of(
                    new FeatureCallExtractor(),
                    new FilterExtractor(),
                    new ToListExtractor(),
                    new SkipIntermediateReferenceExtractor());

    @Override
    public @NonNull ReferenceOperator extractReferenceOperator(@NonNull XExpression expression)
            throws UnsupportedReferenceExpressionException {
        XExpression currentExpression = expression;
        ReferenceOperator lastOperator = null;
        while (currentExpression != null) {
            final Optional<ReferenceOperatorWithNextCallTarget> nextReferenceOperator =
                    getNextReferenceOperator(currentExpression);
            if (nextReferenceOperator.isEmpty()) {
                throw new UnsupportedReferenceExpressionException(currentExpression);
            }

            final ReferenceOperator nextOperator =
                    nextReferenceOperator.get().getReferenceOperator();
            nextOperator.setFollowingOperator(lastOperator);
            lastOperator = nextOperator;
            currentExpression = nextReferenceOperator.get().getNextFeatureCall();
        }

        return lastOperator;
    }

    private static Optional<ReferenceOperatorWithNextCallTarget> getNextReferenceOperator(
            XExpression expression) {
        return EXTRACTORS.stream()
                .map(extractor -> extractor.extract(expression))
                .flatMap(Optional::stream)
                .findFirst();
    }
}

package tools.vitruv.neojoin.expression_parser;

import lombok.Value;

import org.apache.log4j.Logger;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;
import tools.vitruv.neojoin.aqr.AQRTargetClass;
import tools.vitruv.neojoin.expression_parser.extractors.ReferenceFilterExtractor;
import tools.vitruv.neojoin.expression_parser.extractors.SkipIntermediateReferenceExtractor;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperatorWithFollowingExpression;
import tools.vitruv.neojoin.reference_operator.ReferenceFilter;
import tools.vitruv.neojoin.reference_operator.ReferenceOperator;
import tools.vitruv.neojoin.reference_operator.SkipIntermediateReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Value
public class ManualPatternMatcher {
    private static final Logger log = Logger.getLogger(ManualPatternMatcher.class);
    AQR aqr;

    public List<ReferenceOperator> extractReferenceOperators() {
        log.info("AQR: " + aqr);
        final List<XExpression> allExpressions =
                aqr.classes().stream()
                        .map(AQRTargetClass::features)
                        .flatMap(List::stream)
                        .filter(feature -> feature instanceof AQRFeature.Reference)
                        .map(feature -> feature.kind().expression())
                        .filter(Objects::nonNull)
                        .toList();
        log.info("allExpressions: " + allExpressions);

        final List<ReferenceOperator> foundOperators = new ArrayList<>();
        for (final var expression : allExpressions) {
            final Optional<ReferenceOperatorWithFollowingExpression> firstOperatorOptional =
                    getNextReferenceOperator(expression);
            if (firstOperatorOptional.isEmpty()) {
                log.info("No operator found in expression: " + expression);
                continue;
            }
            final ReferenceOperatorWithFollowingExpression firstOperator =
                    firstOperatorOptional.get();
            ReferenceOperator currentOperator = firstOperator.getOperator();
            foundOperators.add(currentOperator);

            XExpression currentExpression = firstOperator.getExpression();
            while (currentExpression != null) {
                final Optional<ReferenceOperatorWithFollowingExpression> nextReferenceOperator =
                        getNextReferenceOperator(currentExpression);
                if (nextReferenceOperator.isEmpty()) {
                    throw new RuntimeException("No matching reference operator found");
                }

                final ReferenceOperator nextOperator = nextReferenceOperator.get().getOperator();
                currentOperator.setFollowingOperator(nextOperator);
                currentOperator = nextOperator;
                currentExpression = nextReferenceOperator.get().getExpression();
            }
        }

        return foundOperators;
    }

    private Optional<ReferenceOperatorWithFollowingExpression> getNextReferenceOperator(
            XExpression expression) {
        final Optional<SkipIntermediateReference> skipIntermediateReferenceOperator =
                SkipIntermediateReferenceExtractor.extract(expression);
        if (skipIntermediateReferenceOperator.isPresent()) {
            return Optional.of(
                    new ReferenceOperatorWithFollowingExpression(
                            skipIntermediateReferenceOperator.get(), null));
        }
        final Optional<ReferenceFilter> referenceFilterOperator =
                ReferenceFilterExtractor.extract(expression);
        if (referenceFilterOperator.isPresent()) {
            return Optional.of(
                    new ReferenceOperatorWithFollowingExpression(
                            referenceFilterOperator.get(), null));
        }

        // TODO: Others

        return Optional.empty();
    }
}

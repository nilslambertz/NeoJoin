package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XExpression;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockExpressionUtils {
    public static Optional<XBlockExpression> asBlockExpression(XExpression expression) {
        return Optional.ofNullable(expression)
                .filter(XBlockExpression.class::isInstance)
                .map(XBlockExpression.class::cast);
    }

    public static boolean hasExactlyOneExpression(XBlockExpression blockExpression) {
        return Optional.ofNullable(blockExpression)
                .map(XBlockExpression::getExpressions)
                .map(expressions -> expressions.size() == 1)
                .orElse(false);
    }

    public static Optional<XExpression> getFirstExpression(XBlockExpression blockExpression) {
        return Optional.ofNullable(blockExpression)
                .map(XBlockExpression::getExpressions)
                .filter(expressions -> !expressions.isEmpty())
                .map(List::getFirst);
    }
}

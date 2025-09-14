package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.XStringLiteral;

import tools.vitruv.neojoin.expression_parser.model.binary_expression.ConstantExpression;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantExpressionUtils {
    public static Optional<ConstantExpression> getConstantExpression(XExpression expression) {
        if (expression instanceof XNumberLiteral numberLiteral) {
            return Optional.of(new ConstantExpression(numberLiteral.getValue()));
        } else if (expression instanceof XStringLiteral stringLiteral) {
            return Optional.of(new ConstantExpression(stringLiteral.getValue()));
        }
        return Optional.empty();
    }
}

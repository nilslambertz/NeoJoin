package tools.vitruv.neojoin.expression_parser.parser.strategy;

import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.NonNull;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;

public class ManualPatternMatchingStrategy implements PatternMatchingStrategy {
    @Override
    public @NonNull ReferenceOperator extractReferenceOperator(@NonNull XExpression expression) throws UnsupportedReferenceExpressionException {
        return null;
    }
}

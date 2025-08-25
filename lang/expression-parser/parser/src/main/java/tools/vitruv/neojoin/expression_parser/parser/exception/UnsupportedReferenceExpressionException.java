package tools.vitruv.neojoin.expression_parser.parser.exception;

import org.eclipse.xtext.xbase.XExpression;

public class UnsupportedReferenceExpressionException extends Exception {
    public UnsupportedReferenceExpressionException(String message, XExpression expression) {
        super(String.format("The expression %s is not supported: %s", expression, message));
    }
}

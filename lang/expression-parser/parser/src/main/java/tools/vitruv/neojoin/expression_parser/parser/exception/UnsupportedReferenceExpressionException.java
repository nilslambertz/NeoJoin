package tools.vitruv.neojoin.expression_parser.parser.exception;

import lombok.Value;

import org.eclipse.xtext.xbase.XExpression;

@Value
public class UnsupportedReferenceExpressionException extends Exception {
    XExpression expression;

    public UnsupportedReferenceExpressionException(XExpression expression) {
        super(String.format("The expression %s is not supported", expression));
        this.expression = expression;
    }
}

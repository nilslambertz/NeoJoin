package tools.vitruv.neojoin.expression_parser.model.binary_expression;

import lombok.Value;

@Value
public class ConstantExpression implements OperationExpression {
    String stringValue;
}

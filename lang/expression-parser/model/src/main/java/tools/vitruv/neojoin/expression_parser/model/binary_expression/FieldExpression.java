package tools.vitruv.neojoin.expression_parser.model.binary_expression;

import lombok.Value;

@Value
public class FieldExpression implements OperationExpression {
    String field;
    String identifier;
}

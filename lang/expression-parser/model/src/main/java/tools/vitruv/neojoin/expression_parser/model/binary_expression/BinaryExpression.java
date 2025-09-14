package tools.vitruv.neojoin.expression_parser.model.binary_expression;

import lombok.Value;

@Value
public class BinaryExpression {
    OperationExpression leftOperand;
    ComparisonOperator operator;
    OperationExpression rightOperand;
}

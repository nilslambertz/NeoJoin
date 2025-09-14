package tools.vitruv.neojoin.expression_parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.jspecify.annotations.Nullable;

import tools.vitruv.neojoin.expression_parser.model.binary_expression.BinaryExpression;

@Data
@AllArgsConstructor
public class Filter implements ReferenceOperator {
    BinaryExpression binaryExpression;

    @Nullable ReferenceOperator followingOperator;
}

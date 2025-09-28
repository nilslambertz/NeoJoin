package tools.vitruv.neojoin.expression_parser.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import tools.vitruv.neojoin.expression_parser.model.binary_expression.BinaryExpression;

@Data
@RequiredArgsConstructor
public class Filter implements ReferenceOperator {
    final BinaryExpression binaryExpression;

    @Nullable ReferenceOperator followingOperator;
}

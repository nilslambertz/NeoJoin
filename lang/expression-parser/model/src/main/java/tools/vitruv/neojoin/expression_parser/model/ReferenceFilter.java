package tools.vitruv.neojoin.expression_parser.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import tools.vitruv.neojoin.expression_parser.model.predicate_expression.ComparisonOperator;
import tools.vitruv.neojoin.expression_parser.model.predicate_expression.ConstantValue;

@Data
@RequiredArgsConstructor
public class ReferenceFilter implements ReferenceOperator {
    final String feature;
    final ComparisonOperator operator;
    final ConstantValue constantValue;

    @Nullable ReferenceOperator followingOperator;
}

package tools.vitruv.neojoin.expression_parser.model;

import lombok.Value;

import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.Nullable;

import tools.vitruv.neojoin.reference_operator.ReferenceOperator;

@Value
public class ReferenceOperatorWithFollowingExpression {
    ReferenceOperator operator;
    @Nullable XExpression expression;
}

package tools.vitruv.neojoin.expression_parser.model.binary_expression;

import lombok.Value;

import org.jspecify.annotations.NonNull;

import tools.vitruv.neojoin.expression_parser.model.FeatureInformation;

@Value
public class FieldExpression implements OperationExpression {
    @NonNull FeatureInformation featureInformation;
}

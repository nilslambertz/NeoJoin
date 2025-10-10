package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model;

import lombok.Value;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;

@Value
public class ReferenceOperatorWithNextFeatureCall {
    @NonNull ReferenceOperator referenceOperator;

    @Nullable XAbstractFeatureCall nextFeatureCall;
}

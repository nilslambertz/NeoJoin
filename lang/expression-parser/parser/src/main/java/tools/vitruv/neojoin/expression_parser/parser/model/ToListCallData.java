package tools.vitruv.neojoin.expression_parser.parser.model;

import lombok.Value;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.jspecify.annotations.Nullable;

@Value
public class ToListCallData implements CallDataWithNextFeatureCall {
    @Nullable XAbstractFeatureCall nextFeatureCall;
}

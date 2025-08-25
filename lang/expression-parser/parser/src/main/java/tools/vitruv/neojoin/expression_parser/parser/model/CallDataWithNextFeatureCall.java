package tools.vitruv.neojoin.expression_parser.parser.model;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.jspecify.annotations.Nullable;

public interface CallDataWithNextFeatureCall {
    @Nullable XAbstractFeatureCall getNextFeatureCall();
}

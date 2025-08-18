package tools.vitruv.neojoin.expression_parser.model;

import lombok.Value;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;

@Value
public class SingleArgumentFilterCallData {
    XExpression filterExpression;

    XAbstractFeatureCall nextFeatureCall;
}

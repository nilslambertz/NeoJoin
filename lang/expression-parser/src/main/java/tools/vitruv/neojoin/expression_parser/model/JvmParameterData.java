package tools.vitruv.neojoin.expression_parser.model;

import lombok.Value;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;

@Value
public class JvmParameterData {
    String identifier;
    String simpleName;
}

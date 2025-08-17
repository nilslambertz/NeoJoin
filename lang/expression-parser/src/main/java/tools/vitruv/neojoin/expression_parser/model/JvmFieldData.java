package tools.vitruv.neojoin.expression_parser.model;

import lombok.Value;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;

@Value
public class JvmFieldData {
    String featureSimpleName;
    String featureIdentifier;
    String returnTypeIdentifier;

    XAbstractFeatureCall nextFeatureCall;
}

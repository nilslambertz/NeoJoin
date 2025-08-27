package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model;

import lombok.Value;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;

@Value
public class JvmParameterData {
    String identifier;
    String simpleName;
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.fixtures;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.impl.XFeatureCallImplCustom;

public class XFeatureCallFixtures {
    public static XFeatureCall createXFeatureCall(JvmIdentifiableElement feature) {
        final XFeatureCall featureCall = new XFeatureCallImplCustom();
        featureCall.setFeature(feature);
        return featureCall;
    }
}

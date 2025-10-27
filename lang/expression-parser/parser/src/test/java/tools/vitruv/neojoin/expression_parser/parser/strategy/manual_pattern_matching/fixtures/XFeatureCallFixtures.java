package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.fixtures;

import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.impl.XFeatureCallImplCustom;

public class XFeatureCallFixtures {
    public static XFeatureCall createXFeatureCall() {
        return new XFeatureCallImplCustom();
    }

    public static XFeatureCall featureCallWithEmptyFormalParameter() {
        final JvmFormalParameter emptyFormalParameter =
                JvmFormalParameterFixtures.createJvmFormalParameter();
        final XFeatureCall featureCall = XFeatureCallFixtures.createXFeatureCall();
        featureCall.setFeature(emptyFormalParameter);
        return featureCall;
    }
}

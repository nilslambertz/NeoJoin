package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.fixtures;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.impl.XFeatureCallImplCustom;
import org.eclipse.xtext.xbase.impl.XMemberFeatureCallImplCustom;

public class XFeatureCallFixtures {
    public static XFeatureCall createXFeatureCall(JvmIdentifiableElement feature) {
        final XFeatureCall featureCall = new XFeatureCallImplCustom();
        featureCall.setFeature(feature);
        return featureCall;
    }

    public static XMemberFeatureCall createXMemberFeatureCall(JvmIdentifiableElement feature) {
        final XMemberFeatureCall featureCall = new XMemberFeatureCallImplCustom();
        featureCall.setFeature(feature);
        return featureCall;
    }
}

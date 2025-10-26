package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.fixtures;

import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.impl.XFeatureCallImplCustom;
import org.eclipse.xtext.xbase.impl.XMemberFeatureCallImplCustom;

public class XFeatureCallFixtures {
    public static XFeatureCall createXFeatureCall() {
        return new XFeatureCallImplCustom();
    }

    public static XMemberFeatureCall createXMemberFeatureCall() {
        return new XMemberFeatureCallImplCustom();
    }
}

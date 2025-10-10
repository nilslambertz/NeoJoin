package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFieldUtils;

import java.util.Optional;

public class MemberFeatureCallParser implements ReferenceOperatorParser {
    public Optional<ReferenceOperatorWithNextFeatureCall> parse(XExpression expression) {
        final Optional<XMemberFeatureCall> memberFeatureCall =
                JvmFeatureCallUtils.asMemberFeatureCall(expression);
        if (memberFeatureCall.isEmpty()) {
            return Optional.empty();
        }

        Optional<XAbstractFeatureCall> nextMemberCallTarget =
                memberFeatureCall.flatMap(JvmFeatureCallUtils::getNextMemberCallTarget);
        if (nextMemberCallTarget.isEmpty()) {
            return Optional.empty();
        }

        return memberFeatureCall
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmFieldUtils::asJvmField)
                .flatMap(JvmFieldUtils::getData)
                .map(
                        fieldData ->
                                new ReferenceOperatorWithNextFeatureCall(
                                        new MemberFeatureCall(fieldData.toFeatureInformation()),
                                        nextMemberCallTarget.get()));
    }
}

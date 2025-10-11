package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.FeatureInformation;
import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFieldUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmTypeReferenceUtils;

import java.util.Optional;

public class MemberFeatureCallParser implements ReferenceOperatorParser {
    public Optional<ReferenceOperatorWithNextFeatureCall> parse(
            PatternMatchingStrategy strategy, XExpression expression) {
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

        final Optional<JvmField> jvmField =
                memberFeatureCall
                        .flatMap(JvmFeatureUtils::getFeature)
                        .flatMap(JvmFieldUtils::asJvmField);
        if (jvmField.isEmpty()) {
            return Optional.empty();
        }

        if (JvmTypeReferenceUtils.isListType(jvmField.get().getType())) {
            return jvmField.flatMap(JvmFieldUtils::getData)
                    .map(
                            fieldData ->
                                    new ReferenceOperatorWithNextFeatureCall(
                                            new MemberFeatureCall(fieldData.toFeatureInformation()),
                                            nextMemberCallTarget.get()));
        }

        return jvmField.flatMap(MemberFeatureCallParser::getFeatureInformation)
                .map(
                        featureInformation ->
                                new ReferenceOperatorWithNextFeatureCall(
                                        new MemberFeatureCall(featureInformation),
                                        nextMemberCallTarget.get()));
    }

    private static Optional<FeatureInformation> getFeatureInformation(JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(JvmField::getType)
                .map(JvmTypeReference::getType)
                .map(
                        jvmType ->
                                new FeatureInformation(
                                        jvmField.getSimpleName(),
                                        jvmType.getSimpleName(),
                                        jvmType.getIdentifier()));
    }
}

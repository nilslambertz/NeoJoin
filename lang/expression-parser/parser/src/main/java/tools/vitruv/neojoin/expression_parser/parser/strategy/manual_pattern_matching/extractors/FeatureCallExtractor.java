package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFieldUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmParameterUtils;

import java.util.Optional;

public class FeatureCallExtractor implements ReferenceOperatorExtractor {
    public Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression) {
        final Optional<XMemberFeatureCall> memberFeatureCall =
                JvmFeatureCallUtils.asMemberFeatureCall(expression);
        if (memberFeatureCall.isEmpty()) {
            return Optional.empty();
        }

        final Optional<JvmFieldUtils.JvmFieldData> memberFeatureCallFieldData =
                memberFeatureCall
                        .flatMap(JvmFeatureUtils::getFeature)
                        .flatMap(JvmFieldUtils::asJvmField)
                        .flatMap(JvmFieldUtils::getData);
        if (memberFeatureCallFieldData.isEmpty()) {
            return Optional.empty();
        }

        return memberFeatureCall
                .flatMap(JvmFeatureCallUtils::getNextMemberCallTarget)
                .flatMap(JvmFeatureCallUtils::asFeatureCall)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmParameterUtils::asJvmFormalParameter)
                .map(
                        parameter ->
                                new ReferenceOperatorWithNextCallTarget(
                                        new FeatureCall(
                                                parameter
                                                        .getParameterType()
                                                        .getType()
                                                        .getSimpleName(),
                                                parameter
                                                        .getParameterType()
                                                        .getType()
                                                        .getIdentifier(),
                                                memberFeatureCallFieldData
                                                        .get()
                                                        .getFeatureSimpleName(),
                                                memberFeatureCallFieldData
                                                        .get()
                                                        .getFeatureIdentifier(),
                                                null),
                                        null));
    }
}

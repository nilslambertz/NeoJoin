package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmType;
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
    private static final String LIST_IDENTIFIER = "java.util.List";

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

        if (MemberFeatureCallParser.isListType(jvmField.get().getType())) {
            return jvmField.flatMap(MemberFeatureCallParser::getListFeatureInformation)
                    .map(
                            featureInformation ->
                                    new ReferenceOperatorWithNextFeatureCall(
                                            new MemberFeatureCall(featureInformation, true),
                                            nextMemberCallTarget.get()));
        }

        return jvmField.flatMap(MemberFeatureCallParser::getFeatureInformation)
                .map(
                        featureInformation ->
                                new ReferenceOperatorWithNextFeatureCall(
                                        new MemberFeatureCall(featureInformation, false),
                                        nextMemberCallTarget.get()));
    }

    private static Optional<FeatureInformation> getFeatureInformation(JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(JvmField::getType)
                .flatMap(
                        jvmTypeReference -> {
                            if (isListType(jvmTypeReference)) {
                                return getListFeatureInformation(jvmField);
                            }

                            final JvmType jvmType = jvmTypeReference.getType();
                            return Optional.of(
                                    new FeatureInformation(
                                            jvmField.getSimpleName(),
                                            jvmType.getSimpleName(),
                                            jvmType.getIdentifier()));
                        });
    }

    private static boolean isListType(JvmTypeReference typeReference) {
        return Optional.ofNullable(typeReference)
                .map(JvmTypeReference::getType)
                .map(JvmType::getIdentifier)
                .map(LIST_IDENTIFIER::equals)
                .orElse(false);
    }

    private static Optional<FeatureInformation> getListFeatureInformation(JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(JvmField::getType)
                .flatMap(JvmTypeReferenceUtils::asParameterizedTypeReference)
                .filter(JvmTypeReferenceUtils::hasExactlyOneArgument)
                .flatMap(JvmTypeReferenceUtils::getFirstArgument)
                .flatMap(JvmTypeReferenceUtils::asParameterizedTypeReference)
                .map(
                        field ->
                                new FeatureInformation(
                                        jvmField.getSimpleName(),
                                        field.getType().getSimpleName(),
                                        field.getType().getIdentifier()));
    }
}

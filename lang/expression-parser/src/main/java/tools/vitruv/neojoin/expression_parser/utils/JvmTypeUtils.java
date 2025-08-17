package tools.vitruv.neojoin.expression_parser.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.*;
import tools.vitruv.neojoin.expression_parser.model.JvmFieldData;
import tools.vitruv.neojoin.expression_parser.model.JvmParameterData;
import tools.vitruv.neojoin.expression_parser.model.SingleArgumentFlatMapCallData;
import tools.vitruv.neojoin.expression_parser.model.ToListCallData;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmTypeUtils {
    public static Optional<ToListCallData> getToListCallData(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        if (memberFeatureCall.getFeature() == null) {
            return Optional.empty();
        }

        final JvmIdentifiableElement feature = memberFeatureCall.getFeature();
        if (!(feature instanceof JvmOperation)) {
            return Optional.empty();
        }

        if (!"toList".equals(feature.getSimpleName())) {
            return Optional.empty();
        }

        if (!(memberFeatureCall.getMemberCallTarget() instanceof XAbstractFeatureCall nextMemberCallTarget)) {
            return Optional.empty();
        }

        return Optional.of(new ToListCallData(nextMemberCallTarget));
    }

    public static Optional<SingleArgumentFlatMapCallData> getSingleArgumentFlatMapCallData(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        if (memberFeatureCall.getFeature() == null) {
            return Optional.empty();
        }

        final JvmIdentifiableElement feature = memberFeatureCall.getFeature();
        if (!(feature instanceof JvmOperation)) {
            return Optional.empty();
        }

        if (!"flatMap".equals(feature.getSimpleName())) {
            return Optional.empty();
        }

        if (memberFeatureCall.getMemberCallArguments() == null || memberFeatureCall.getMemberCallArguments().size() != 1) {
            return Optional.empty();
        }

        final XExpression firstArgument = memberFeatureCall.getMemberCallArguments().getFirst();
        if (!(firstArgument instanceof XClosure closureArgument)) {
            return Optional.empty();
        }

        if (closureArgument.getExpression() == null) {
            return Optional.empty();
        }

        final XExpression closureArgumentExpression = closureArgument.getExpression();
        if (!(closureArgumentExpression instanceof XBlockExpression blockExpression)) {
            return Optional.empty();
        }

        if (blockExpression.getExpressions() == null || blockExpression.getExpressions().size() != 1) {
            return Optional.empty();
        }

        final XExpression firstBlockExpression = blockExpression.getExpressions().getFirst();
        if (!(firstBlockExpression instanceof XMemberFeatureCall featureCallBlockExpression)) {
            return Optional.empty();
        }

        if (!(featureCallBlockExpression.getFeature() instanceof JvmField jvmField)) {
            return Optional.empty();
        }

        if (!(memberFeatureCall.getMemberCallTarget() instanceof XAbstractFeatureCall nextMemberCallTarget)) {
            return Optional.empty();
        }

        return Optional.of(new SingleArgumentFlatMapCallData(jvmField.getSimpleName(), jvmField.getIdentifier(), jvmField.getType().getType().getIdentifier(), nextMemberCallTarget));
    }

    public static Optional<JvmFieldData> getJvmFieldData(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        if (!(memberFeatureCall.getFeature() instanceof JvmField jvmField)) {
            return Optional.empty();
        }

        if (!(memberFeatureCall.getMemberCallTarget() instanceof XAbstractFeatureCall nextMemberCallTarget)) {
            return Optional.empty();
        }

        return Optional.of(new JvmFieldData(jvmField.getSimpleName(), jvmField.getIdentifier(), jvmField.getType().getType().getIdentifier(), nextMemberCallTarget));
    }

    public static Optional<JvmParameterData> getJvmParameterData(XExpression expression) {
        if (!(expression instanceof XFeatureCall featureCall)) {
            return Optional.empty();
        }

        if (!(featureCall.getFeature() instanceof JvmFormalParameter jvmFormalParameter)) {
            return Optional.empty();
        }

        return Optional.of(new JvmParameterData(jvmFormalParameter.getParameterType().getType().getIdentifier(), jvmFormalParameter.getParameterType().getType().getSimpleName()));
    }
}

package tools.vitruv.neojoin.expression_parser.parser.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.*;

import tools.vitruv.neojoin.expression_parser.parser.model.ToListCallData;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmTypeUtils {
    public static Optional<ToListCallData> getToListCallData(XExpression expression) {
        return getAsMemberFeatureCall(expression)
                .filter(JvmTypeUtils::isToListOperation)
                .flatMap(JvmTypeUtils::getNextFeatureCallTarget)
                .map(ToListCallData::new);
    }

    private static Optional<XAbstractFeatureCall> getNextFeatureCallTarget(XExpression expression) {
        if (!(expression instanceof XAbstractFeatureCall nextMemberCallTarget)) {
            return Optional.empty();
        }

        return Optional.of(nextMemberCallTarget);
    }

    private static Optional<XMemberFeatureCall> getAsMemberFeatureCall(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        return Optional.of(memberFeatureCall);
    }

    private static Optional<JvmIdentifiableElement> getFeature(XExpression expression) {
        if (!(expression instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        if (memberFeatureCall.getFeature() == null) {
            return Optional.empty();
        }

        return Optional.of(memberFeatureCall.getFeature());
    }

    private static boolean isToListOperation(XExpression expression) {
        return getFeature(expression)
                .filter(JvmTypeUtils::isJvmOperation)
                .map(JvmTypeUtils::isToListCall)
                .orElse(false);
    }

    private static boolean isJvmOperation(JvmIdentifiableElement jvmIdentifiableElement) {
        return jvmIdentifiableElement instanceof JvmOperation;
    }

    private static boolean isToListCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return "toList".equals(jvmIdentifiableElement.getSimpleName());
    }
}

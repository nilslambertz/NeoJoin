package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFieldUtils {
    @Value
    public static class JvmFieldData {
        String featureSimpleName;
        String featureIdentifier;
        String returnTypeIdentifier;

        @Nullable XAbstractFeatureCall nextFeatureCall;
    }

    public static Optional<JvmFieldData> getJvmFieldData(XAbstractFeatureCall featureCall) {
        if (!(featureCall instanceof XMemberFeatureCall memberFeatureCall)) {
            return Optional.empty();
        }

        if (!(memberFeatureCall.getFeature() instanceof JvmField jvmField)) {
            return Optional.empty();
        }

        final XAbstractFeatureCall nextMemberCallTarget =
                Optional.of(memberFeatureCall)
                        .flatMap(JvmFeatureCallUtils::getNextMemberCallTarget)
                        .orElse(null);

        return Optional.of(
                new JvmFieldData(
                        jvmField.getSimpleName(),
                        jvmField.getIdentifier(),
                        jvmField.getType().getType().getIdentifier(),
                        nextMemberCallTarget));
    }
}

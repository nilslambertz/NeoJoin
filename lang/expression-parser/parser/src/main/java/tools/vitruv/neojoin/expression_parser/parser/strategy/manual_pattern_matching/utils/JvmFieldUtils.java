package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmFieldUtils {
    @Value
    public static class JvmFieldData {
        String featureSimpleName;
        String featureIdentifier;
        String returnTypeIdentifier;
    }

    public static Optional<JvmFieldData> getJvmFieldData(XAbstractFeatureCall featureCall) {
        return Optional.ofNullable(featureCall)
                .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                .flatMap(JvmFieldUtils::getJvmFieldData);
    }
}

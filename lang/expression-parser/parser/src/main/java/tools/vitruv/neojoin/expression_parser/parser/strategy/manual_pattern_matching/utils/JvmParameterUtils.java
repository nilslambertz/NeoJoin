package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmParameterUtils {
    @Value
    public static class JvmParameterData {
        String identifier;
        String simpleName;
    }

    public static Optional<JvmParameterData> getJvmParameterData(XAbstractFeatureCall featureCall) {
        return asJvmFormalParameter(featureCall)
                .map(
                        parameter ->
                                new JvmParameterData(
                                        parameter.getParameterType().getType().getIdentifier(),
                                        parameter.getParameterType().getType().getSimpleName()));
    }

    public static Optional<JvmFormalParameter> asJvmFormalParameter(
            XAbstractFeatureCall featureCall) {
        return Optional.ofNullable(featureCall)
                .filter(JvmFormalParameter.class::isInstance)
                .map(JvmFormalParameter.class::cast);
    }
}

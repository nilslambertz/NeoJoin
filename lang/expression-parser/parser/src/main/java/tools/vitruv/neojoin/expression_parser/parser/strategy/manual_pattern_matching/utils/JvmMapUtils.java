package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.xbase.XMemberFeatureCall;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmMapUtils {
    private static final String mapSimpleName = "map";

    public static boolean isMapOperation(XMemberFeatureCall featureCall) {
        return JvmFeatureUtils.getFeature(featureCall)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmMapUtils::isMapCall)
                .orElse(false);
    }

    public static Optional<JvmFieldUtils.JvmFieldData> getMapArgumentData(JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(JvmField::getType)
                .flatMap(JvmTypeReferenceUtils::asParameterizedTypeReference)
                .map(JvmParameterizedTypeReference::getType)
                .flatMap(JvmTypeUtils::asGenericType)
                .map(
                        type ->
                                new JvmFieldUtils.JvmFieldData(
                                        jvmField.getSimpleName(),
                                        type.getSimpleName(),
                                        type.getIdentifier()));
    }

    private static boolean isMapCall(JvmIdentifiableElement jvmIdentifiableElement) {
        return mapSimpleName.equals(jvmIdentifiableElement.getSimpleName());
    }
}

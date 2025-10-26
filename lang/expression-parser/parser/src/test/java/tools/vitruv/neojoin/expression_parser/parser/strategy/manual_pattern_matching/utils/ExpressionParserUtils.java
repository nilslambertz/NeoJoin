package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.impl.XFeatureCallImplCustom;

public class ExpressionParserUtils {
    public static JvmFormalParameter createJvmFormalParameter() {
        return TypesFactory.eINSTANCE.createJvmFormalParameter();
    }

    public static JvmFormalParameter createJvmFormalParameter(JvmTypeReference typeReference) {
        final JvmFormalParameter formalParameter = createJvmFormalParameter();
        formalParameter.setParameterType(typeReference);
        return formalParameter;
    }

    public static XFeatureCall createXFeatureCall(JvmIdentifiableElement feature) {
        final XFeatureCall featureCall = new XFeatureCallImplCustom();
        featureCall.setFeature(feature);
        return featureCall;
    }

    public static JvmTypeReference createJvmTypeReference(JvmType type) {
        final JvmParameterizedTypeReference jvmTypeReference =
                TypesFactory.eINSTANCE.createJvmParameterizedTypeReference();
        jvmTypeReference.setType(type);
        return jvmTypeReference;
    }

    public static JvmType createJvmType(String identifier, String simpleName) {
        final JvmGenericType jvmType = TypesFactory.eINSTANCE.createJvmGenericType();
        jvmType.setSimpleName("Car");
        jvmType.setIdentifier("my.test.package.Car");
        return jvmType;
    }
}

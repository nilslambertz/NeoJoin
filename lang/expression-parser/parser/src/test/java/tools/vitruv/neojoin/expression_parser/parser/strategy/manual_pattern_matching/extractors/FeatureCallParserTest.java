package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.TypesFactory;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.impl.XFeatureCallImplCustom;
import org.junit.jupiter.api.Test;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;

import java.util.Optional;

class FeatureCallParserTest {
    @Test
    public void parseEmptyFeatureCall() {
        // given
        final JvmFormalParameter emptyFormalParameter =
                TypesFactory.eINSTANCE.createJvmFormalParameter();
        final XFeatureCall featureCall = new XFeatureCallImplCustom();
        featureCall.setFeature(emptyFormalParameter);

        // when
        final FeatureCallParser parser = new FeatureCallParser();
        final Optional<ReferenceOperator> resultOptional = parser.parse(null, featureCall);

        // then
        assertTrue(resultOptional.isPresent());

        final ReferenceOperator result = resultOptional.get();
        assertInstanceOf(FeatureCall.class, result);

        final FeatureCall resultFeatureCall = (FeatureCall) result;
        assertNull(resultFeatureCall.getIdentifier());
        assertNull(resultFeatureCall.getSimpleName());
        assertNull(resultFeatureCall.getFollowingOperator());
    }

    @Test
    public void parseNonEmptyFeatureCall() {
        // given
        final JvmGenericType jvmType = TypesFactory.eINSTANCE.createJvmGenericType();
        jvmType.setSimpleName("Car");
        jvmType.setIdentifier("my.test.package.Car");

        final JvmParameterizedTypeReference jvmTypeReference =
                TypesFactory.eINSTANCE.createJvmParameterizedTypeReference();
        jvmTypeReference.setType(jvmType);

        final JvmFormalParameter formalParameter =
                TypesFactory.eINSTANCE.createJvmFormalParameter();
        formalParameter.setParameterType(jvmTypeReference);

        final XFeatureCall featureCall = new XFeatureCallImplCustom();
        featureCall.setFeature(formalParameter);

        // when
        final FeatureCallParser parser = new FeatureCallParser();
        final Optional<ReferenceOperator> resultOptional = parser.parse(null, featureCall);

        // then
        assertTrue(resultOptional.isPresent());

        final ReferenceOperator result = resultOptional.get();
        assertInstanceOf(FeatureCall.class, result);

        final FeatureCall resultFeatureCall = (FeatureCall) result;
        assertEquals("my.test.package.Car", resultFeatureCall.getIdentifier());
        assertEquals("Car", resultFeatureCall.getSimpleName());
        assertNull(resultFeatureCall.getFollowingOperator());
    }
}

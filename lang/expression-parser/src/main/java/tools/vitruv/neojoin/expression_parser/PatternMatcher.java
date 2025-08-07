package tools.vitruv.neojoin.expression_parser;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.common.types.TypesPackage;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XbasePackage;
import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;
import viatra.SkipIntermediateReference;
import viatra.SkipIntermediateReferencePattern;

import java.util.List;

public class PatternMatcher {
	private static final Logger log = Logger.getLogger(PatternMatcher.class);
	private final AQR aqr;

	public PatternMatcher(final AQR aqr) {
		this.aqr = aqr;
	}

	public void matchAndExtract() {
		log.info("Pattern matching called");
		log.info("AQR: " + aqr);

		final List<AQRFeature> features = aqr.classes().stream().flatMap(targetClass -> targetClass.features().stream()).toList();
		final List<AQRFeature.Reference> referenceFeatures = features.stream().filter(feature -> feature instanceof AQRFeature.Reference).map(feature -> (AQRFeature.Reference) feature).toList();

		final AQRFeature.Reference reference = referenceFeatures.getFirst();
		final XExpression expression = reference.kind().expression();
		log.info("expression: " + expression);

		IQueryBackendFactory reteFactory = ReteBackendFactory.INSTANCE;
		ViatraQueryEngineOptions options = new ViatraQueryEngineOptions.Builder().withDefaultBackend(reteFactory).build();
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(XbasePackage.eNS_URI, XbasePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(TypesPackage.eNS_URI, TypesPackage.eINSTANCE);

		ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resourceSet), options);

		SkipIntermediateReference.instance().prepare(engine);
		final var matcher = SkipIntermediateReferencePattern.Matcher.on(engine);
		log.info(String.format("SkipIntermediateReferencePattern matched: %s", matcher.getAllMatches()));
	}
}

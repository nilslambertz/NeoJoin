package tools.vitruv.neojoin.expression_parser;

import jakarta.inject.Inject;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.common.types.TypesPackage;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XbasePackage;
import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;
import viatra.SkipIntermediateReference;
import viatra.SkipIntermediateReferencePattern;
import viatra.SkipIntermediateReferenceWithReturn;
import viatra.SkipIntermediateReferenceWithReturnPattern;

import java.util.List;

public class PatternMatcher {
	private static final Logger log = Logger.getLogger(PatternMatcher.class);
	private final AQR aqr;

	@Inject
	XtextResourceSet resourceSet;


	public PatternMatcher(final AQR aqr) {
		this.aqr = aqr;
	}

	public void matchAndExtract() {
		log.info("Pattern matching called");

		final List<AQRFeature> features = aqr.classes().stream().flatMap(targetClass -> targetClass.features().stream()).toList();
		final List<AQRFeature.Reference> referenceFeatures = features.stream().filter(feature -> feature instanceof AQRFeature.Reference).map(feature -> (AQRFeature.Reference) feature).toList();

		final AQRFeature.Reference reference = referenceFeatures.getFirst();
		final XExpression expression = reference.kind().expression();

		try {
			SkipIntermediateReferencePattern instance = SkipIntermediateReferencePattern.instance();
			SkipIntermediateReferencePattern.Match match = instance.instantiate().newMatch(expression);
			log.info(String.format("SkipIntermediateReferencePattern matched: %s", match));
		} catch (Exception e) {
			log.error("Exception during Pattern matching", e);
		}

		try {
			SkipIntermediateReferenceWithReturnPattern instance = SkipIntermediateReferenceWithReturnPattern.instance();
			SkipIntermediateReferenceWithReturnPattern.Match match = instance.instantiate().newMatch(expression, null, null, null);
			log.info(String.format("SkipIntermediateReferenceWithReturnPattern matched: %s", match));
			log.info(String.format("SkipIntermediateReferenceWithReturnPattern sourceTarget: %s", match.getSourceTarget()));
			log.info(String.format("SkipIntermediateReferenceWithReturnPattern flatMapTarget: %s", match.getFlatMapTarget()));
			log.info(String.format("SkipIntermediateReferenceWithReturnPattern intermediateReferenceTarget: %s", match.getIntermediateReferenceTarget()));
		} catch (Exception e) {
			log.error("Exception during Pattern matching", e);
		}


		IQueryBackendFactory reteFactory = ReteBackendFactory.INSTANCE;
		ViatraQueryEngineOptions options = new ViatraQueryEngineOptions.Builder().withDefaultBackend(reteFactory).build();
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(XbasePackage.eNS_URI, XbasePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(TypesPackage.eNS_URI, TypesPackage.eINSTANCE);

		ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(resourceSet), options);
		SkipIntermediateReferencePattern.Matcher skipIntermediateReferencePattern = SkipIntermediateReference.instance().getSkipIntermediateReferencePattern(engine);
		final var engineTest = skipIntermediateReferencePattern.getOneArbitraryMatch(expression).orElse(null);
		log.info(String.format("engineTest matched: %s", engineTest));

		SkipIntermediateReferenceWithReturnPattern.Matcher skipIntermediateReferenceWithReturnPattern = SkipIntermediateReferenceWithReturn.instance().getSkipIntermediateReferenceWithReturnPattern(engine);
		final var engineTest2 = skipIntermediateReferenceWithReturnPattern.getAllMatches(expression, null, null, null);
		log.info(String.format("engineTest2 matched: %s", engineTest2));
	}
}

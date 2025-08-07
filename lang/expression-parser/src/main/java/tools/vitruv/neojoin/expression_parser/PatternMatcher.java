package tools.vitruv.neojoin.expression_parser;

import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XbaseStandaloneSetup;
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

		Injector injector = new XbaseStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("dummy", new XMIResourceFactoryImpl());
		Resource exprRes = resourceSet.createResource(URI.createURI("dummy:/expr.xbase"));
		exprRes.getContents().add(expression);
		EMFScope scope = new EMFScope(resourceSet);
		ViatraQueryEngine engine = ViatraQueryEngine.on(scope);
		SkipIntermediateReference.instance().prepare(engine);
		final var matcher = SkipIntermediateReferencePattern.Matcher.on(engine);
		log.info(String.format("SkipIntermediateReferencePattern matched: %s", matcher.getAllMatches()));
	}
}

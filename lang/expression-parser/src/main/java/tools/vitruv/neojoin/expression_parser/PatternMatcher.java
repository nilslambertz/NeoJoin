package tools.vitruv.neojoin.expression_parser;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchGenericBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.common.types.TypesPackage;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.XbasePackage;
import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;
import viatra.SkipIntermediateReference;
import viatra.SkipIntermediateReferencePatternStartWithJvm;
import viatra.SkipIntermediateReferencePatternStartWithJvmWithArguments;
import viatra.SkipIntermediateReferencePatternStartWithoutJvm;
import viatra.SkipIntermediateReferencePatternWithArguments;
import viatra.SkipIntermediateReferencePatternWithJvm;
import viatra.SkipIntermediateReferencePatternWithoutJvm;

import java.util.List;

public class PatternMatcher {

	private static final Logger log = Logger.getLogger(PatternMatcher.class);

	public PatternMatcher(final AQR aqr) {
		log.info("Set up pattern matcher");
		referenceFeatures = aqr.classes().stream().filter(targetClass -> !targetClass.name().toLowerCase().equals("root")).flatMap(targetClass -> targetClass.features().stream()).filter(feature -> feature instanceof AQRFeature.Reference).map(feature -> (AQRFeature.Reference) feature).toList();
	}

	private final List<AQRFeature.Reference> referenceFeatures;

	private static EMFScope initializeModelScope(List<EObject> content) {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		rs.getPackageRegistry().put(XbasePackage.eNS_URI, XbasePackage.eINSTANCE);
		rs.getPackageRegistry().put(TypesPackage.eNS_URI, TypesPackage.eINSTANCE);

		Resource r = rs.createResource(URI.createFileURI("something"));
		r.getContents().addAll(content);

		return new EMFScope(rs);
	}

	private ViatraQueryEngine prepareQueryEngine(EMFScope scope) {
		ViatraQueryEngineOptions options = new ViatraQueryEngineOptions.Builder().withDefaultCachingBackend(ReteBackendFactory.INSTANCE).withDefaultSearchBackend(LocalSearchGenericBackendFactory.INSTANCE).build();

		return ViatraQueryEngine.on(scope, options);
	}

	private void matchAndExtractFirstExpression() {
		log.info("Match and extract first expression");

		// get first expression
		final AQRFeature.Reference reference = referenceFeatures.getFirst();
		final XExpression expression = reference.kind().expression();

		// initialize matcher
		final EMFScope scope = initializeModelScope(List.of(expression));
		final ViatraQueryEngine engine = prepareQueryEngine(scope);

		// setup pattern
		SkipIntermediateReference.instance().prepare(engine);

		log.info(((XMemberFeatureCall) expression).getFeature());
		log.info(((XMemberFeatureCall) expression).getFeature().getSimpleName());
		log.info(((XMemberFeatureCall) expression).getFeature().getIdentifier());
		log.info(((XMemberFeatureCall) expression).getFeature().getQualifiedName());

		// Start without JVM
		printDivider("SkipIntermediateReferencePatternStartWithoutJvm");
		final SkipIntermediateReferencePatternStartWithoutJvm.Matcher startWithoutJvmMatcher = SkipIntermediateReferencePatternStartWithoutJvm.Matcher.on(engine);
		for (SkipIntermediateReferencePatternStartWithoutJvm.Match match : startWithoutJvmMatcher.getAllMatches()) {
			log.info(String.format("SkipIntermediateReferencePatternStartWithoutJvm: Matched sub expression: %s", match.getExpression()));
		}

		// Start with JVM
		printDivider("SkipIntermediateReferencePatternStartWithJvm");
		final SkipIntermediateReferencePatternStartWithJvm.Matcher startWithJvmMatcher = SkipIntermediateReferencePatternStartWithJvm.Matcher.on(engine);
		for (SkipIntermediateReferencePatternStartWithJvm.Match match : startWithJvmMatcher.getAllMatches()) {
			log.info(String.format("SkipIntermediateReferencePatternStartWithJvm: Matched sub expression: %s", match.getExpression()));
		}

		// Start with JVM and arguments
		printDivider("SkipIntermediateReferencePatternStartWithJvmWithArguments");
		final SkipIntermediateReferencePatternStartWithJvmWithArguments.Matcher startWithJvmAndArgumentsMatcher = SkipIntermediateReferencePatternStartWithJvmWithArguments.Matcher.on(engine);
		for (SkipIntermediateReferencePatternStartWithJvmWithArguments.Match match : startWithJvmAndArgumentsMatcher.getAllMatches()) {
			log.info(String.format("SkipIntermediateReferencePatternStartWithJvmWithArguments: Matched sub expression: %s", match.getExpression()));
			log.info(String.format("SkipIntermediateReferencePatternStartWithJvmWithArguments: Matched sub feature: %s", match.getFeature()));
			log.info(String.format("SkipIntermediateReferencePatternStartWithJvmWithArguments: Matched sub feature simpleName: %s", match.getFeature().getSimpleName()));
		}

		// Full without JVM
		printDivider("SkipIntermediateReferencePatternWithoutJvm");
		final SkipIntermediateReferencePatternWithoutJvm.Matcher fullWithoutJvmMatcher = SkipIntermediateReferencePatternWithoutJvm.Matcher.on(engine);
		for (SkipIntermediateReferencePatternWithoutJvm.Match match : fullWithoutJvmMatcher.getAllMatches()) {
			log.info(String.format("SkipIntermediateReferencePatternWithoutJvm: Matched sub expression: %s", match.getExpression()));
		}

		// Full with JVM
		printDivider("SkipIntermediateReferencePatternWithJvm");
		final SkipIntermediateReferencePatternWithJvm.Matcher fullWithJvmMatcher = SkipIntermediateReferencePatternWithJvm.Matcher.on(engine);
		for (SkipIntermediateReferencePatternWithJvm.Match match : fullWithJvmMatcher.getAllMatches()) {
			log.info(String.format("SkipIntermediateReferencePatternWithJvm: Matched sub expression: %s", match.getExpression()));
		}

		// Full with separated arguments
		printDivider("SkipIntermediateReferencePatternWithArguments");
		final SkipIntermediateReferencePatternWithArguments.Matcher fullWithArgumentsMatcher = SkipIntermediateReferencePatternWithArguments.Matcher.on(engine);
		for (SkipIntermediateReferencePatternWithArguments.Match match : fullWithArgumentsMatcher.getAllMatches()) {
			log.info(String.format("SkipIntermediateReferencePatternWithArguments: Matched sub expression: %s", match.getExpression()));
			log.info(String.format("SkipIntermediateReferencePatternWithArguments: Matched sub expression feature: %s", ((XMemberFeatureCall) match.getExpression()).getFeature()));
			log.info(String.format("SkipIntermediateReferencePatternWithArguments: Matched sub flatMap: %s", match.getFlatMapTarget()));
			log.info(String.format("SkipIntermediateReferencePatternWithArguments: Matched sub intermediate: %s", match.getIntermediateReferenceTarget()));
			log.info(String.format("SkipIntermediateReferencePatternWithArguments: Matched sub source: %s", match.getSourceTarget()));
		}
	}

	public void matchAndExtract() {
		log.info("Pattern matching called");

		matchAndExtractFirstExpression();
	}

	private static void printDivider(String title) {
		System.out.println("------------- " + title + " -------------");
	}
}

package tools.vitruv.neojoin.expression_parser;

import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchGenericBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.common.types.TypesPackage;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XbaseStandaloneSetup;
import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;
import viatra.SanityCheckPattern;
import viatra.SkipIntermediateReference;
import viatra.SkipIntermediateReferencePattern;
import viatra.SkipIntermediateReferencePatternWithoutJvm;

import java.util.List;

public class PatternMatcher {

	private static final Logger log = Logger.getLogger(PatternMatcher.class);

	public PatternMatcher(final AQR aqr) {
		log.info("Set up pattern matcher");
		referenceFeatures = aqr.classes().stream().filter(targetClass -> !targetClass.name().equalsIgnoreCase("root")).flatMap(targetClass -> targetClass.features().stream()).filter(feature -> feature instanceof AQRFeature.Reference).map(feature -> (AQRFeature.Reference) feature).toList();
	}

	private final List<AQRFeature.Reference> referenceFeatures;

	// --- Helper holder to return both scope and the in-scope copy ---
	private static final class ScopeAndExpr {
		final EMFScope scope;
		final XExpression exprInScope;

		ScopeAndExpr(EMFScope scope, XExpression exprInScope) {
			this.scope = scope;
			this.exprInScope = exprInScope;
		}
	}

	// Build a private Xtext ResourceSet, copy the expression into it, resolve links
	private static ScopeAndExpr initializeModelScopeStandalone(XExpression original) {
		Injector injector = new XbaseStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet rs = injector.getInstance(XtextResourceSet.class);

		rs.getPackageRegistry().put(TypesPackage.eNS_URI, TypesPackage.eINSTANCE);

		// Ensure Jvm types (e.g., IterableExtensions#flatMap/toList) resolve
		injector.getInstance(IJvmTypeProvider.Factory.class).findOrCreateTypeProvider(rs);

		// Ensure rs.createResource(...) returns an XtextResource for any extension
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, injector.getInstance(org.eclipse.xtext.resource.XtextResourceFactory.class));

		XtextResource r = (XtextResource) rs.createResource(URI.createURI("dummy:/expr.tmp"));
		XExpression copy = (XExpression) EcoreUtil.copy(original);
		r.getContents().add(copy);

		// Resolve proxies so XMemberFeatureCall.feature -> JvmOperation isn’t a proxy
		EcoreUtil.resolveAll(rs);

		return new ScopeAndExpr(new EMFScope(rs), copy);
	}

	private ViatraQueryEngine prepareQueryEngine(EMFScope scope) {
		ViatraQueryEngineOptions options = new ViatraQueryEngineOptions.Builder().withDefaultBackend(ReteBackendFactory.INSTANCE).withDefaultCachingBackend(ReteBackendFactory.INSTANCE).withDefaultSearchBackend(LocalSearchGenericBackendFactory.INSTANCE).build();
		return ViatraQueryEngine.on(scope, options);
	}

	private void matchAndExtractFirstExpression() {
		log.info("Match and extract first expression alge1");

		// get first expression
		final AQRFeature.Reference reference = referenceFeatures.getFirst();
		final XExpression originalExpr = reference.kind().expression();
		log.info("expression: " + originalExpr);

		// create isolated scope + COPY of the expression (prevents re-entrancy loops)
		ScopeAndExpr se = initializeModelScopeStandalone(originalExpr);
		ViatraQueryEngine engine = prepareQueryEngine(se.scope);

		// prepare patterns
		SkipIntermediateReference.instance().prepare(engine);

		// main matcher
		final SkipIntermediateReferencePattern.Matcher matcher = SkipIntermediateReferencePattern.Matcher.on(engine);

		// prepare both
		SkipIntermediateReferencePattern.Matcher withJvm = SkipIntermediateReferencePattern.Matcher.on(engine);
		SkipIntermediateReferencePatternWithoutJvm.Matcher withoutJvm = SkipIntermediateReferencePatternWithoutJvm.Matcher.on(engine);

		// counts (bound)
		log.info("WithJvm(bound)    = " + withJvm.countMatches(se.exprInScope));
		log.info("WithoutJvm(bound) = " + withoutJvm.countMatches(se.exprInScope));

		// list matches
		for (var match : withJvm.getAllMatches(se.exprInScope)) {
			log.info("WithJvm match    : " + match.getExpression());
		}
		for (var match : withoutJvm.getAllMatches(se.exprInScope)) {
			log.info("WithoutJvm match : " + match.getExpression());
		}

		// Sanity: plain "is XMemberFeatureCall?" pattern
		SanityCheckPattern.Matcher sanity = SanityCheckPattern.Matcher.on(engine);
		log.info("Sanity(all)   = " + sanity.countMatches());
		log.info("Sanity(bound) = " + sanity.countMatches(se.exprInScope));
	}

	public void matchAndExtract() {
		log.info("Pattern matching called");
		matchAndExtractFirstExpression();
	}
}

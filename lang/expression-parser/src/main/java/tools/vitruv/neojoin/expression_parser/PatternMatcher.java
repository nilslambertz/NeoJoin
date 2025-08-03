package tools.vitruv.neojoin.expression_parser;

import org.apache.log4j.Logger;
import org.eclipse.xtext.xbase.XExpression;
import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;
import viatra.SkipIntermediateReferencePattern;

import java.util.List;

public class PatternMatcher {
	private static final Logger log = Logger.getLogger(PatternMatcher.class);
	private final AQR aqr;

	public PatternMatcher(final AQR aqr) {
		this.aqr = aqr;
	}

	public void matchAndExtract() {
		log.info(String.format("Pattern matching called: %s", aqr));

		final List<AQRFeature> features = aqr.classes().stream().flatMap(targetClass -> targetClass.features().stream()).toList();
		final List<AQRFeature.Reference> referenceFeatures = features.stream().filter(feature -> feature instanceof AQRFeature.Reference).map(feature -> (AQRFeature.Reference) feature).toList();

		final AQRFeature.Reference reference = referenceFeatures.getFirst();
		final XExpression expression = reference.kind().expression();

		try {
			SkipIntermediateReferencePattern instance = SkipIntermediateReferencePattern.instance();
			SkipIntermediateReferencePattern.Match match = instance.instantiate().newMatch(expression);
			log.info(String.format("Pattern matched: %s", match));
		} catch (Exception e) {
			log.error("Exception during Pattern matching", e);
		}


	}
}

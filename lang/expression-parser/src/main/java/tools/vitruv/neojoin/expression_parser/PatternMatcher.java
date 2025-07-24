package tools.vitruv.neojoin.expression_parser;

import org.apache.log4j.Logger;
import org.eclipse.xtext.xbase.XExpression;
import tools.vitruv.neojoin.aqr.AQR;
import tools.vitruv.neojoin.aqr.AQRFeature;

import java.util.List;

public class PatternMatcher {
	private static final Logger log = Logger.getLogger(PatternMatcher.class);

	public static void match(AQR aqr) {
		log.info(String.format("Pattern matching called: %s", aqr));

		final List<AQRFeature> features = aqr.classes().stream().flatMap(targetClass -> targetClass.features().stream()).toList();
		final List<AQRFeature.Reference> referenceFeatures = features.stream().filter(feature -> feature instanceof AQRFeature.Reference).map(feature -> (AQRFeature.Reference) feature).toList();

		final AQRFeature.Reference reference = referenceFeatures.getFirst();
		final XExpression expression = reference.kind().expression();


	}
}

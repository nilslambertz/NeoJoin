package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureCallExtractor {
    public static Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression) {
        return JvmFeatureCallUtils.getNextFeatureCallTarget(expression)
                .flatMap(JvmFeatureCallUtils::getAsFeatureCall)
                .map(
                        nextCallTarget ->
                                new ReferenceOperatorWithNextCallTarget(
                                        new FeatureCall(
                                                nextCallTarget.getFeature().getSimpleName(),
                                                nextCallTarget.getFeature().getSimpleName(),
                                                null),
                                        null));
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmParameterUtils;

import java.util.Optional;

public class FeatureCallExtractor implements ReferenceOperatorExtractor {
    public Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression) {
        return JvmFeatureCallUtils.asFeatureCall(expression)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmParameterUtils::asJvmFormalParameter)
                .map(
                        parameter -> {
                            JvmType parameterType = parameter.getParameterType().getType();
                            return new ReferenceOperatorWithNextCallTarget(
                                    new FeatureCall(
                                            parameterType.getIdentifier(),
                                            parameterType.getSimpleName()),
                                    null);
                        });
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmParameterUtils;

import java.util.Optional;

public class FeatureCallParser implements ReferenceOperatorParser {
    public Optional<ReferenceOperator> parse(
            PatternMatchingStrategy strategy, XExpression expression) {
        return JvmFeatureCallUtils.asFeatureCall(expression)
                .map(XAbstractFeatureCall::getFeature)
                .flatMap(JvmParameterUtils::asJvmFormalParameter)
                .map(
                        parameter -> {
                            if (parameter.getParameterType() == null) {
                                return FeatureCall.empty();
                            }

                            JvmType parameterType = parameter.getParameterType().getType();
                            return new FeatureCall(
                                    parameterType.getIdentifier(), parameterType.getSimpleName());
                        });
    }
}

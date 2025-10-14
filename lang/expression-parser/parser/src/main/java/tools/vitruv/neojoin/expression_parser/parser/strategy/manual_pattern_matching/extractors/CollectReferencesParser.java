package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.CollectReferences;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmOperationUtils;

import java.util.Optional;

public class CollectReferencesParser implements ReferenceOperatorParser {
    private static final String TO_LIST_OPERATION_SIMPLE_NAME = "toList";
    private static final String FLATTEN_OPERATION_SIMPLE_NAME = "flatten";

    public Optional<ReferenceOperator> parse(
            PatternMatchingStrategy strategy, XExpression expression)
            throws UnsupportedReferenceExpressionException {
        if (!isToListOperation(expression) && !isFlattenOperation(expression)) {
            return Optional.empty();
        }

        return parseAndAppendFollowingExpressionOperators(
                strategy, expression, new CollectReferences());
    }

    private static boolean isToListOperation(XExpression expression) {
        return JvmFeatureCallUtils.asMemberFeatureCall(expression)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmIdentifiableElement::getSimpleName)
                .map(TO_LIST_OPERATION_SIMPLE_NAME::equals)
                .orElse(false);
    }

    private static boolean isFlattenOperation(XExpression expression) {
        return JvmFeatureCallUtils.asMemberFeatureCall(expression)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmIdentifiableElement::getSimpleName)
                .map(FLATTEN_OPERATION_SIMPLE_NAME::equals)
                .orElse(false);
    }
}

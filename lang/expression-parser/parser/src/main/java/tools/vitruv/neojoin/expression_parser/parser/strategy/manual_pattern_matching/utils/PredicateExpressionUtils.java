package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.XBinaryOperation;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XNumberLiteral;
import org.eclipse.xtext.xbase.XStringLiteral;

import tools.vitruv.neojoin.expression_parser.model.predicate_expression.ComparisonOperator;
import tools.vitruv.neojoin.expression_parser.model.predicate_expression.ConstantValue;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredicateExpressionUtils {
    private static final Map<String, ComparisonOperator> OPERATOR_MAP =
            Map.ofEntries(
                    Map.entry("operator_equals", ComparisonOperator.Equals),
                    Map.entry("operator_notEquals", ComparisonOperator.NotEquals),
                    Map.entry("operator_lessThan", ComparisonOperator.LessThan),
                    Map.entry("operator_lessEqualsThan", ComparisonOperator.LessEquals),
                    Map.entry("operator_greaterThan", ComparisonOperator.GreaterThan),
                    Map.entry("operator_greaterEqualsThan", ComparisonOperator.GreaterEquals));

    public static Optional<XBinaryOperation> asBinaryOperation(XExpression expression) {
        return CastingUtils.cast(expression, XBinaryOperation.class);
    }

    /**
     * Extracts a ConstantPredicate out of the binary operation. The order of operations is expected
     * to be FIELD - COMPARISON_OPERATOR - CONSTANT_VALUE
     */
    public static Optional<ConstantPredicate> extractConstantPredicate(XBinaryOperation operation) {
        // Left side should be a feature/field call
        final Optional<String> fieldSimpleName =
                Optional.ofNullable(operation.getLeftOperand())
                        .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                        .flatMap(JvmFeatureUtils::getFeature)
                        .flatMap(JvmFieldUtils::asJvmField)
                        .map(JvmField::getSimpleName);
        if (fieldSimpleName.isEmpty()) {
            return Optional.empty();
        }

        final Optional<ComparisonOperator> comparisonOperator = getComparisonOperator(operation);
        if (comparisonOperator.isEmpty()) {
            return Optional.empty();
        }

        // Right side should be a constant
        final Optional<ConstantValue> constantValue =
                getConstantExpressionAsString(operation.getRightOperand());
        if (constantValue.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new ConstantPredicate(
                        fieldSimpleName.get(), comparisonOperator.get(), constantValue.get()));
    }

    private static Optional<ComparisonOperator> getComparisonOperator(XBinaryOperation operation) {
        return Optional.ofNullable(operation)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmOperation::getSimpleName)
                .map(OPERATOR_MAP::get);
    }

    private static Optional<ConstantValue> getConstantExpressionAsString(XExpression expression) {
        if (expression instanceof XNumberLiteral numberLiteral) {
            return Optional.of(ConstantValue.of(numberLiteral.getValue()));
        } else if (expression instanceof XStringLiteral stringLiteral) {
            return Optional.of(ConstantValue.String(stringLiteral.getValue()));
        }
        return Optional.empty();
    }

    @Value
    public static class ConstantPredicate {
        String feature;
        ComparisonOperator operator;
        ConstantValue constantValue;
    }
}

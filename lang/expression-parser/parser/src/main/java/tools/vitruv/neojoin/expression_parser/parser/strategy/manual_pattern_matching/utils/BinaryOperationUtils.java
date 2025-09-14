package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.xbase.XBinaryOperation;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.binary_expression.BinaryExpression;
import tools.vitruv.neojoin.expression_parser.model.binary_expression.ComparisonOperator;
import tools.vitruv.neojoin.expression_parser.model.binary_expression.FieldExpression;
import tools.vitruv.neojoin.expression_parser.model.binary_expression.OperationExpression;

import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryOperationUtils {
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

    public static Optional<BinaryExpression> extractBinaryExpression(XBinaryOperation operation) {
        final Optional<OperationExpression> leftOperand =
                getOperationExpression(operation.getLeftOperand());
        final Optional<ComparisonOperator> operator = getComparisonOperator(operation);
        final Optional<OperationExpression> rightOperand =
                getOperationExpression(operation.getRightOperand());
        if (leftOperand.isEmpty() || operator.isEmpty() || rightOperand.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new BinaryExpression(leftOperand.get(), operator.get(), rightOperand.get()));
    }

    private static Optional<OperationExpression> getOperationExpression(XExpression expression) {
        final Optional<OperationExpression> featureCallExpression =
                Optional.ofNullable(expression)
                        .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                        .flatMap(JvmFieldUtils::getJvmFieldData)
                        .map(
                                fieldData ->
                                        new FieldExpression(
                                                fieldData.getFeatureSimpleName(),
                                                fieldData.getFeatureIdentifier()));
        if (featureCallExpression.isPresent()) {
            return featureCallExpression;
        }

        final Optional<OperationExpression> constantValueExpression =
                Optional.of(expression).flatMap(ConstantExpressionUtils::getConstantExpression);
        if (constantValueExpression.isPresent()) {
            return constantValueExpression;
        }

        return Optional.empty();
    }

    private static Optional<ComparisonOperator> getComparisonOperator(XBinaryOperation operation) {
        return Optional.ofNullable(operation)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmOperation::getSimpleName)
                .map(OPERATOR_MAP::get);
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
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
        // If the operation is a feature call
        final Optional<OperationExpression> featureCallExpression =
                Optional.ofNullable(expression)
                        .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                        .flatMap(JvmFeatureUtils::getFeature)
                        .flatMap(JvmFieldUtils::asJvmField)
                        .flatMap(BinaryOperationUtils::getBinaryOperatorFieldArgumentData)
                        .map(fieldData -> new FieldExpression(fieldData.toFeatureInformation()));
        if (featureCallExpression.isPresent()) {
            return featureCallExpression;
        }

        // Otherwise, check if it's a supported constant expression
        final Optional<OperationExpression> constantValueExpression =
                Optional.ofNullable(expression)
                        .flatMap(ConstantExpressionUtils::getConstantExpression);
        if (constantValueExpression.isPresent()) {
            return constantValueExpression;
        }

        return Optional.empty();
    }

    private static Optional<JvmFieldUtils.JvmFieldData> getBinaryOperatorFieldArgumentData(
            JvmField jvmField) {
        return Optional.ofNullable(jvmField)
                .map(JvmField::getType)
                .flatMap(JvmTypeReferenceUtils::asParameterizedTypeReference)
                .map(JvmParameterizedTypeReference::getType)
                .flatMap(JvmTypeUtils::asGenericType)
                .map(
                        type ->
                                new JvmFieldUtils.JvmFieldData(
                                        jvmField.getSimpleName(),
                                        type.getSimpleName(),
                                        type.getIdentifier()));
    }

    private static Optional<ComparisonOperator> getComparisonOperator(XBinaryOperation operation) {
        return Optional.ofNullable(operation)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmOperationUtils::asJvmOperation)
                .map(JvmOperation::getSimpleName)
                .map(OPERATOR_MAP::get);
    }
}

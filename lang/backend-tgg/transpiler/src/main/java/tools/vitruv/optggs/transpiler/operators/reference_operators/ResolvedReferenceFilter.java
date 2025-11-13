package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.LogicOperator;
import tools.vitruv.optggs.operators.expressions.ConstantExpression;
import tools.vitruv.optggs.operators.expressions.VariableExpression;
import tools.vitruv.optggs.transpiler.graph.tgg.GraphPathToNode;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGNode;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRulesBuilder;
import tools.vitruv.optggs.transpiler.graph.tgg.constraint.NotEqualsConstraint;

@Value
public class ResolvedReferenceFilter implements ResolvedReferenceOperator {
    String feature;
    LogicOperator operator;
    ConstantExpression constantExpression;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule latestRule = builder.getLatestRule();

        final GraphPathToNode pathToLastNode = builder.getPathToLastNode();
        final TGGNode lastSourceNode = latestRule.findNestedSourceNode(pathToLastNode);

        // Restriction by eMoflon: The operator "!=" is not allowed in green nodes
        // Therefore, we need to add an attribute constraint for NotEquals-operations
        if (operator.equals(LogicOperator.NotEquals)) {
            final VariableExpression variable =
                    lastSourceNode.addVariableAttribute(feature, LogicOperator.Equals);
            latestRule.addConstraintRule(new NotEqualsConstraint(variable, constantExpression));
        } else {
            lastSourceNode.addConstantAttribute(feature, operator, constantExpression);
        }
    }
}

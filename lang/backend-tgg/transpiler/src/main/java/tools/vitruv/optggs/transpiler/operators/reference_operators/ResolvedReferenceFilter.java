package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.LogicOperator;
import tools.vitruv.optggs.operators.expressions.ConstantExpression;
import tools.vitruv.optggs.transpiler.graph.Node;
import tools.vitruv.optggs.transpiler.graph.TripleRule;
import tools.vitruv.optggs.transpiler.graph.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.graph.TripleRulesBuilder;

@Value
public class ResolvedReferenceFilter implements ResolvedReferenceOperator {
    String feature;
    LogicOperator operator;
    ConstantExpression constantExpression;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule latestRule = builder.getLatestRule();

        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final Node lastSourceNode = latestRule.findNestedSourceNode(pathToLastNode);

        // TODO: The operator "!=" is not allowed in green nodes.
        // We need to use attribute constraints for this operator
        lastSourceNode.addConstantAttribute(feature, operator, constantExpression);
    }
}

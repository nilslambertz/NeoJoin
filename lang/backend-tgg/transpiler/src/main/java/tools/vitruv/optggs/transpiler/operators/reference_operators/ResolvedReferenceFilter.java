package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.LogicOperator;
import tools.vitruv.optggs.operators.expressions.ConstantExpression;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

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

        lastSourceNode.addConstantAttribute(feature, operator, constantExpression);
    }
}

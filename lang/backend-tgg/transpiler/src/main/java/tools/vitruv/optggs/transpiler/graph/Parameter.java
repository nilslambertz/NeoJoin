package tools.vitruv.optggs.transpiler.graph;

import tools.vitruv.optggs.operators.expressions.ValueExpression;

public record Parameter(String attribute, ValueExpression value) {
    Parameter deepCopy() {
        return new Parameter(attribute, value.deepCopy());
    }
}

package tools.vitruv.optggs.transpiler.graph;

import tools.vitruv.optggs.operators.expressions.ValueExpression;

public final class Parameter {
    private final String attribute;
    private ValueExpression value;

    public Parameter(String attribute, ValueExpression value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String attribute() {
        return attribute;
    }

    public ValueExpression value() {
        return value;
    }

    Parameter deepCopy() {
        return new Parameter(attribute, value.deepCopy());
    }
}

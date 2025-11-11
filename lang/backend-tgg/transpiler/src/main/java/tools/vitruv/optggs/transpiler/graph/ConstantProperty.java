package tools.vitruv.optggs.transpiler.graph;

import tools.vitruv.optggs.operators.LogicOperator;

public record ConstantProperty(String name, LogicOperator operator, String value)
        implements Property {

    @Override
    public String toExpression(boolean green) {
        if (green) {
            return "." + name + " := " + value;
        } else {
            return "." + name + " : " + value;
        }
    }

    @Override
    public ConstantProperty deepCopy() {
        return new ConstantProperty(name, operator, value);
    }
}

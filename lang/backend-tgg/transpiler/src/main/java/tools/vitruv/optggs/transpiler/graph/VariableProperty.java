package tools.vitruv.optggs.transpiler.graph;

public record VariableProperty(String name, String value) implements Property {

    @Override
    public String toExpression(boolean green) {
        if (green) {
            return "." + name + " := <" + value + ">";
        } else {
            return "." + name + " : <" + value + ">";
        }
    }

    @Override
    public VariableProperty deepCopy() {
        return new VariableProperty(name, value);
    }
}

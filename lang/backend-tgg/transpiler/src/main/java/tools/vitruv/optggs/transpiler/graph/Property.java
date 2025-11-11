package tools.vitruv.optggs.transpiler.graph;

public interface Property {
    String name();

    String value();

    String toExpression(boolean green);

    Property deepCopy();
}

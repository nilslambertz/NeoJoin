package tools.vitruv.optggs.transpiler.graph;

public interface Property {
    String name();

    String value();

    String toExpression(Node node);

    Property deepCopy();
}

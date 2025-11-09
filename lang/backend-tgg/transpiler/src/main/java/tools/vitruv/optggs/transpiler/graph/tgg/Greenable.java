package tools.vitruv.optggs.transpiler.graph.tgg;

public interface Greenable<T> {
    boolean isGreen();

    T makeGreen();

    T makeBlack();
}

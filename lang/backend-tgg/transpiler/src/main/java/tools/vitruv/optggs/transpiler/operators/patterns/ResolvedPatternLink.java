package tools.vitruv.optggs.transpiler.operators.patterns;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.Slice;
import tools.vitruv.optggs.transpiler.graph.TGGNode;

public interface ResolvedPatternLink {
    FQN element();

    TGGNode extendSlice(Slice slice, TGGNode lastNode);
}

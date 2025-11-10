package tools.vitruv.optggs.transpiler.graph.tgg;

import tools.vitruv.optggs.transpiler.graph.GraphNodeCopyHelper;
import tools.vitruv.optggs.transpiler.graph.NameRepository;
import tools.vitruv.optggs.transpiler.graph.TGGNode;

public class TGGNodeCopyHelper extends GraphNodeCopyHelper<TGGNode> {
    public TGGNodeCopyHelper(NameRepository copiedNameRepository) {
        super(copiedNameRepository);
    }
}

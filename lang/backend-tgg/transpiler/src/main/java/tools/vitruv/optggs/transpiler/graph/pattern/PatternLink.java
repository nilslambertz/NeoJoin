package tools.vitruv.optggs.transpiler.graph.pattern;

import tools.vitruv.optggs.transpiler.graph.AbstractGraphLink;
import tools.vitruv.optggs.transpiler.graph.GraphNodeCopyHelper;

public class PatternLink extends AbstractGraphLink<PatternNode> {
    public PatternLink(String name, PatternNode target) {
        super(name, target);
    }

    @Override
    public PatternLink deepCopy(GraphNodeCopyHelper<PatternNode> copyHelper) {
        return new PatternLink(this.getName(), copyHelper.getCopiedNode(this.getTarget()));
    }
}

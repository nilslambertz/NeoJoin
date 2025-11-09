package tools.vitruv.optggs.transpiler.graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import tools.vitruv.optggs.transpiler.graph.tgg.Greenable;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Correspondence implements Greenable<Correspondence> {
    private final Node source;
    private final Node target;
    private boolean green;

    public static Correspondence Green(Node source, Node target) {
        return new Correspondence(source, target, true);
    }

    public static Correspondence Black(Node source, Node target) {
        return new Correspondence(source, target, false);
    }

    public Correspondence makeGreen() {
        green = true;
        return this;
    }

    public Correspondence makeBlack() {
        green = false;
        return this;
    }

    public CorrespondenceType toCorrespondenceType() {
        return new CorrespondenceType(source, target);
    }

    Correspondence deepCopy(TripleRuleCopyHelper copyHelper) {
        return new Correspondence(
                copyHelper.getCopiedNode(source), copyHelper.getCopiedNode(target), green);
    }

    @Override
    public String toString() {
        return (green ? "++" : "") + source.getId() + "<-->" + target.getId();
    }
}

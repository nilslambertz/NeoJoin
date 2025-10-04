package tools.vitruv.optggs.transpiler.tgg;

public class Correspondence {
    private final Node source;
    private final Node target;
    private boolean isGreen;

    private Correspondence(Node source, Node target, boolean isGreen) {
        this.source = source;
        this.target = target;
        this.isGreen = isGreen;
    }

    public static Correspondence Green(Node source, Node target) {
        return new Correspondence(source, target, true);
    }

    public static Correspondence Black(Node source, Node target) {
        return new Correspondence(source, target, false);
    }

    public Node source() {
        return source;
    }

    public Node target() {
        return target;
    }

    public boolean isGreen() {
        return isGreen;
    }

    public void makeGreen() {
        isGreen = true;
    }

    public void makeBlack() {
        isGreen = false;
    }

    public CorrespondenceType toCorrespondenceType() {
        return new CorrespondenceType(source, target);
    }

    public Correspondence deepCopy(TripleRuleCopyHelper copyHelper) {
        return new Correspondence(
                copyHelper.getCopiedNode(source), copyHelper.getCopiedNode(target), isGreen);
    }

    @Override
    public String toString() {
        return (isGreen ? "++" : "") + source.id() + "<-->" + target.id();
    }
}

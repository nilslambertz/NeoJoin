package tools.vitruv.optggs.transpiler.tgg;

import tools.vitruv.optggs.operators.FQN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TripleRule {
    public interface RuleExtender {
        Node addNode(FQN type);

        Correspondence addCorrespondence(Node source, Node target);

        AttributeConstraint addConstraint(AttributeConstraint constraint);
    }

    private final NameRepository nameRepository;
    private final List<Node> sourceNodes;
    private final List<Node> targetNodes;
    private final List<Correspondence> correspondences;
    private final List<AttributeConstraint> constraints;
    private boolean isLinkRule;

    public TripleRule() {
        nameRepository = new NameRepository();
        sourceNodes = new ArrayList<>();
        targetNodes = new ArrayList<>();
        correspondences = new ArrayList<>();
        constraints = new ArrayList<>();
        isLinkRule = false;
    }

    private TripleRule(
            NameRepository nameRepository,
            List<Node> sourceNodes,
            List<Node> targetNodes,
            List<Correspondence> correspondences,
            List<AttributeConstraint> constraints,
            boolean isLinkRule) {
        this.nameRepository = nameRepository;
        this.sourceNodes = sourceNodes;
        this.targetNodes = targetNodes;
        this.correspondences = correspondences;
        this.constraints = constraints;
        this.isLinkRule = isLinkRule;
    }

    private Node createNode(FQN type) {
        var name = nameRepository.getLower(type);
        return Node.Black(name, type, nameRepository);
    }

    private Node addSourceNode(FQN type) {
        var node = createNode(type);
        sourceNodes.add(node);
        return node;
    }

    private Node addTargetNode(FQN type) {
        var node = createNode(type);
        targetNodes.add(node);
        return node;
    }

    public Correspondence addCorrespondenceRule(Node source, Node target) {
        Correspondence correspondence = Correspondence.Black(source, target);
        correspondences.add(correspondence);
        return correspondence;
    }

    public AttributeConstraint addConstraintRule(AttributeConstraint constraint) {
        constraints.add(constraint);
        return constraint;
    }

    public Slice addSourceSlice(
            Collection<Node> initialNodes, Collection<Correspondence> initialCorrespondences) {
        return new Slice(
                new RuleExtender() {
                    @Override
                    public Node addNode(FQN type) {
                        return addSourceNode(type);
                    }

                    @Override
                    public Correspondence addCorrespondence(Node source, Node target) {
                        return addCorrespondenceRule(source, target);
                    }

                    @Override
                    public AttributeConstraint addConstraint(AttributeConstraint constraint) {
                        return addConstraintRule(constraint);
                    }
                },
                initialNodes,
                initialCorrespondences);
    }

    public Slice addSourceSlice() {
        return addSourceSlice(List.of(), List.of());
    }

    public Slice addTargetSlice(
            Collection<Node> initialNodes, Collection<Correspondence> initalCorrespondences) {
        return new Slice(
                new RuleExtender() {
                    @Override
                    public Node addNode(FQN type) {
                        return addTargetNode(type);
                    }

                    @Override
                    public Correspondence addCorrespondence(Node source, Node target) {
                        return addCorrespondenceRule(source, target);
                    }

                    @Override
                    public AttributeConstraint addConstraint(AttributeConstraint constraint) {
                        return addConstraintRule(constraint);
                    }
                },
                initialNodes,
                initalCorrespondences);
    }

    public Slice addTargetSlice() {
        return addTargetSlice(List.of(), List.of());
    }

    public Optional<Node> findSourceNodeByType(FQN type) {
        return sourceNodes.stream().filter(node -> node.type().equals(type)).findFirst();
    }

    public Optional<Node> findTargetNodeByType(FQN type) {
        return targetNodes.stream().filter(node -> node.type().equals(type)).findFirst();
    }

    public Slice allSourcesAsSlice() {
        return addSourceSlice(sourceNodes, correspondences);
    }

    public Slice allTargetsAsSlice() {
        return addSourceSlice(targetNodes, correspondences);
    }

    public Collection<Correspondence> correspondences() {
        return correspondences;
    }

    public Collection<AttributeConstraint> constraints() {
        return constraints;
    }

    public boolean isLinkRule() {
        return isLinkRule;
    }

    public void setLinkRule(boolean linkRule) {
        isLinkRule = linkRule;
    }

    public TripleRule makeBlack() {
        allSourcesAsSlice().makeBlack();
        allTargetsAsSlice().makeBlack();
        return this;
    }

    public Node findNestedSourceNode(FQN sourceNodeType, List<String> links) {
        Node lastSourceNode = findSourceNodeByType(sourceNodeType).orElseThrow();
        for (String nextReference : links) {
            lastSourceNode = lastSourceNode.getLinkTarget(nextReference);
        }
        return lastSourceNode;
    }

    public TripleRule deepCopy() {
        final TripleRuleCopyHelper copyHelper = new TripleRuleCopyHelper(nameRepository);

        final List<Node> newSourceNodes = new ArrayList<>();
        for (final Node oldSourceNode : sourceNodes) {
            final Node copiedSourceNode = copyHelper.getCopiedNode(oldSourceNode);
            newSourceNodes.add(copiedSourceNode);
        }

        final List<Node> newTargetNodes = new ArrayList<>();
        for (final Node oldTargetNode : targetNodes) {
            final Node copiedTargetNode = copyHelper.getCopiedNode(oldTargetNode);
            newTargetNodes.add(copiedTargetNode);
        }

        final List<Correspondence> newCorrespondences = new ArrayList<>();
        for (final Correspondence correspondence : correspondences) {
            newCorrespondences.add(correspondence.deepCopy(copyHelper));
        }

        final List<AttributeConstraint> newConstraints = new ArrayList<>();
        for (final AttributeConstraint constraint : constraints) {
            newConstraints.add(constraint.deepCopy());
        }

        return new TripleRule(
                copyHelper.getCopiedNameRepository(),
                newSourceNodes,
                newTargetNodes,
                newCorrespondences,
                newConstraints,
                isLinkRule);
    }

    @Override
    public String toString() {
        var c = (constraints.isEmpty() ? "" : " cs: " + constraints);
        return "src: " + sourceNodes + " tgt: " + targetNodes + " corr: " + correspondences + c;
    }
}

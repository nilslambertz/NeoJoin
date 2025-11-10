package tools.vitruv.optggs.transpiler.graph.tgg;

import lombok.Getter;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.NameRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TripleRule {
    public interface RuleExtender {
        TGGNode addNode(FQN type);

        Correspondence addCorrespondence(TGGNode source, TGGNode target);

        AttributeConstraint addConstraint(AttributeConstraint constraint);
    }

    private final NameRepository nameRepository;
    private final List<TGGNode> sourceNodes;
    private final List<TGGNode> targetNodes;
    private final List<Correspondence> correspondences;
    private final List<AttributeConstraint> constraints;
    @Getter private final boolean isLinkRule;

    public TripleRule() {
        nameRepository = new NameRepository();
        sourceNodes = new ArrayList<>();
        targetNodes = new ArrayList<>();
        correspondences = new ArrayList<>();
        constraints = new ArrayList<>();
        isLinkRule = false;
    }

    public static TripleRule LinkTripleRule() {
        return new TripleRule(
                new NameRepository(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                true);
    }

    private TripleRule(
            NameRepository nameRepository,
            List<TGGNode> sourceNodes,
            List<TGGNode> targetNodes,
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

    private TGGNode createNode(FQN type) {
        var name = nameRepository.getLower(type);
        return TGGNode.Black(name, type, nameRepository);
    }

    private TGGNode addSourceNode(FQN type) {
        var node = createNode(type);
        sourceNodes.add(node);
        return node;
    }

    private TGGNode addTargetNode(FQN type) {
        var node = createNode(type);
        targetNodes.add(node);
        return node;
    }

    public Correspondence addCorrespondenceRule(TGGNode source, TGGNode target) {
        Correspondence correspondence = Correspondence.Black(source, target);
        correspondences.add(correspondence);
        return correspondence;
    }

    public AttributeConstraint addConstraintRule(AttributeConstraint constraint) {
        constraints.add(constraint);
        return constraint;
    }

    public TGGSlice addSourceSlice(
            Collection<TGGNode> initialNodes, Collection<Correspondence> initialCorrespondences) {
        return new TGGSlice(
                new RuleExtender() {
                    @Override
                    public TGGNode addNode(FQN type) {
                        return addSourceNode(type);
                    }

                    @Override
                    public Correspondence addCorrespondence(TGGNode source, TGGNode target) {
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

    public TGGSlice addSourceSlice() {
        return addSourceSlice(List.of(), List.of());
    }

    public TGGSlice addTargetSlice(
            Collection<TGGNode> initialNodes, Collection<Correspondence> initalCorrespondences) {
        return new TGGSlice(
                new RuleExtender() {
                    @Override
                    public TGGNode addNode(FQN type) {
                        return addTargetNode(type);
                    }

                    @Override
                    public Correspondence addCorrespondence(TGGNode source, TGGNode target) {
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

    public TGGSlice addTargetSlice() {
        return addTargetSlice(List.of(), List.of());
    }

    public Optional<TGGNode> findSourceNodeByType(FQN type) {
        return sourceNodes.stream().filter(node -> node.getType().equals(type)).findFirst();
    }

    public Optional<TGGNode> findTargetNodeByType(FQN type) {
        return targetNodes.stream().filter(node -> node.getType().equals(type)).findFirst();
    }

    public TGGSlice allSourcesAsSlice() {
        return addSourceSlice(sourceNodes, correspondences);
    }

    public TGGSlice allTargetsAsSlice() {
        return addSourceSlice(targetNodes, correspondences);
    }

    public Collection<Correspondence> correspondences() {
        return correspondences;
    }

    public Collection<AttributeConstraint> constraints() {
        return constraints;
    }

    public TripleRule makeBlack() {
        allSourcesAsSlice().makeBlack();
        allTargetsAsSlice().makeBlack();
        return this;
    }

    public TGGNode findNestedSourceNode(TripleRulePathToNode path) {
        TGGNode lastSourceNode = findSourceNodeByType(path.getRoot()).orElseThrow();
        for (String nextReference : path.getLinkPath()) {
            lastSourceNode = lastSourceNode.getFirstLinkTarget(nextReference);
        }
        return lastSourceNode;
    }

    public boolean hasAnyGreenElements() {
        return allSourcesAsSlice().hasAnyGreenElements()
                || allTargetsAsSlice().hasAnyGreenElements();
    }

    public TripleRule deepCopy() {
        final TGGNodeCopyHelper copyHelper = new TGGNodeCopyHelper(nameRepository);

        final List<TGGNode> newSourceNodes = new ArrayList<>();
        for (final TGGNode oldSourceNode : sourceNodes) {
            final TGGNode copiedSourceNode = copyHelper.getCopiedNode(oldSourceNode);
            newSourceNodes.add(copiedSourceNode);
        }

        final List<TGGNode> newTargetNodes = new ArrayList<>();
        for (final TGGNode oldTargetNode : targetNodes) {
            final TGGNode copiedTargetNode = copyHelper.getCopiedNode(oldTargetNode);
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

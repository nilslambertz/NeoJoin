package tools.vitruv.optggs.transpiler.tgg;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.selection.PatternLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Slice {
    private final TripleRule.RuleExtender ruleExtender;
    private final List<Node> nodes = new ArrayList<>();
    private final List<Correspondence> correspondences = new ArrayList<>();

    public Slice(
            TripleRule.RuleExtender ruleExtender,
            Collection<Node> initialNodes,
            Collection<Correspondence> initialCorrespondences) {
        this.ruleExtender = ruleExtender;
        this.nodes.addAll(initialNodes);
        this.correspondences.addAll(initialCorrespondences);
    }

    public Collection<Node> nodes() {
        return nodes;
    }

    public Optional<Node> findByType(FQN type) {
        for (var node : nodes) {
            if (node.type().equals(type)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public Node addNode(FQN type) {
        var node = ruleExtender.addNode(type);
        nodes.add(node);
        return node;
    }

    public Correspondence addCorrespondence(Node source, Node target) {
        var correspondence = ruleExtender.addCorrespondence(source, target);
        correspondences.add(correspondence);
        return correspondence;
    }

    public AttributeConstraint addConstraint(AttributeConstraint constraint) {
        return ruleExtender.addConstraint(constraint);
    }

    public Slice makeGreen() {
        for (var node : nodes) {
            node.makeGreen();
            for (var link : node.links()) {
                link.makeGreen();
            }
        }
        for (var correspondence : correspondences) {
            correspondence.makeGreen();
        }
        return this;
    }

    public Slice makeBlack() {
        for (var node : nodes) {
            node.makeBlack();
            for (var link : node.links()) {
                link.makeBlack();
            }
        }
        for (var correspondence : correspondences) {
            correspondence.makeBlack();
        }
        return this;
    }

    public boolean hasAnyGreenElements() {
        final boolean anyNodesAreGreen = nodes.stream().anyMatch(Node::isGreen);
        final boolean anyLinksAreGreen =
                nodes.stream().map(Node::links).flatMap(Collection::stream).anyMatch(Link::isGreen);
        final boolean anyCorrespondencesAreGreen =
                correspondences.stream().anyMatch(Correspondence::isGreen);
        return anyNodesAreGreen || anyLinksAreGreen || anyCorrespondencesAreGreen;
    }

    public <T> Collection<T> mapNodes(Function<Node, T> function) {
        return nodes.stream().map(function).toList();
    }

    public <T> Collection<T> filterMapNodes(Predicate<Node> predicate, Function<Node, T> function) {
        return nodes.stream().filter(predicate).map(function).toList();
    }

    public Collection<Node> findNodes(Predicate<Node> predicate) {
        return nodes.stream().filter(predicate).toList();
    }

    public void extend(PatternLink pattern) {}

    @Override
    public String toString() {
        return nodes.toString();
    }
}

package tools.vitruv.optggs.transpiler.graph.pattern;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.NameRepository;
import tools.vitruv.optggs.transpiler.graph.tgg.GraphPathToNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Value
public class GraphPattern {
    UUID id = UUID.randomUUID();

    NameRepository nameRepository;
    List<PatternNode> nodes;

    public PatternNode addNode(FQN type) {
        final String name = nameRepository.getLower(type);
        final PatternNode node = PatternNode.create(name, type, nameRepository);
        nodes.add(node);
        return node;
    }

    public Optional<PatternNode> findNodeByType(FQN type) {
        return nodes.stream().filter(node -> node.getType().equals(type)).findFirst();
    }

    public PatternNode findNestedNode(GraphPathToNode path) {
        PatternNode lastNode = findNodeByType(path.getRoot()).orElseThrow();
        for (String nextReference : path.getLinkPath()) {
            lastNode = lastNode.getFirstLinkTarget(nextReference);
        }
        return lastNode;
    }
}

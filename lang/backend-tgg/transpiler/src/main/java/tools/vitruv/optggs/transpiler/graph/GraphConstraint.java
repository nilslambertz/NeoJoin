package tools.vitruv.optggs.transpiler.graph;

import lombok.Value;

import tools.vitruv.optggs.transpiler.graph.pattern.PatternNode;

import java.util.List;
import java.util.UUID;

@Value
public class GraphConstraint {
    UUID id = UUID.randomUUID();

    List<PatternNode> nodes;
}

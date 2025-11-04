package tools.vitruv.optggs.transpiler.tgg;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class GraphConstraint {
    UUID id = UUID.randomUUID();

    // TODO: Add specific Node-class for Patterns without TGG properties
    List<Node> nodes;
}

package tools.vitruv.optggs.transpiler.graph.pattern;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class ConstraintPattern {
    UUID id = UUID.randomUUID();

    List<PatternNode> nodes;
}

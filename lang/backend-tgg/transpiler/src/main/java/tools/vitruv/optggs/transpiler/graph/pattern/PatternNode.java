package tools.vitruv.optggs.transpiler.graph.pattern;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.AbstractGraphNode;
import tools.vitruv.optggs.transpiler.graph.Attribute;
import tools.vitruv.optggs.transpiler.graph.GraphNodeCopyHelper;
import tools.vitruv.optggs.transpiler.graph.NameRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class PatternNode extends AbstractGraphNode<PatternLink, PatternNode> {
    public PatternNode(
            String id,
            FQN type,
            NameRepository nameRepository,
            List<PatternLink> links,
            LinkedHashSet<Attribute> attributes) {
        super(id, type, nameRepository, links, attributes);
    }

    public static PatternNode create(String id, FQN type, NameRepository nameRepository) {
        return new PatternNode(id, type, nameRepository, new ArrayList<>(), new LinkedHashSet<>());
    }

    @Override
    public PatternNode deepCopy(GraphNodeCopyHelper<PatternNode> copyHelper) {
        final List<PatternLink> copiedLinks =
                this.links.stream()
                        .map(link -> link.deepCopy(copyHelper))
                        .collect(Collectors.toCollection(ArrayList::new));
        final LinkedHashSet<Attribute> copiedAttributes =
                this.attributes.stream()
                        .map(Attribute::deepCopy)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        return new PatternNode(
                id, type, copyHelper.getCopiedNameRepository(), copiedLinks, copiedAttributes);
    }
}

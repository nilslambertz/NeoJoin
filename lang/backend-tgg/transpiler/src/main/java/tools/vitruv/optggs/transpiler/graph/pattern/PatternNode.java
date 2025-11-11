package tools.vitruv.optggs.transpiler.graph.pattern;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.AbstractGraphNode;
import tools.vitruv.optggs.transpiler.graph.Attribute;
import tools.vitruv.optggs.transpiler.graph.GraphNodeCopyHelper;
import tools.vitruv.optggs.transpiler.graph.NameRepository;
import tools.vitruv.optggs.transpiler.graph.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PatternNode extends AbstractGraphNode<PatternLink, PatternNode> {
    public PatternNode(
            String id,
            FQN type,
            NameRepository nameRepository,
            List<Property> properties,
            List<PatternLink> links,
            List<Attribute> attributes) {
        super(id, type, nameRepository, properties, links, attributes);
    }

    public static PatternNode create(String id, FQN type, NameRepository nameRepository) {
        return new PatternNode(
                id, type, nameRepository, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public PatternNode deepCopy(GraphNodeCopyHelper<PatternNode> copyHelper) {
        final List<Property> copiedProperties =
                this.properties.stream()
                        .map(Property::deepCopy)
                        .collect(Collectors.toCollection(ArrayList::new));
        final List<PatternLink> copiedLinks =
                this.links.stream()
                        .map(link -> link.deepCopy(copyHelper))
                        .collect(Collectors.toCollection(ArrayList::new));
        final List<Attribute> copiedAttributes =
                this.attributes.stream()
                        .map(Attribute::deepCopy)
                        .collect(Collectors.toCollection(ArrayList::new));

        return new PatternNode(
                id,
                type,
                copyHelper.getCopiedNameRepository(),
                copiedProperties,
                copiedLinks,
                copiedAttributes);
    }
}

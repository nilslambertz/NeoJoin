package tools.vitruv.optggs.transpiler.graph;

import lombok.Getter;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.tgg.Greenable;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class TGGNode extends AbstractGraphNode<TGGLink, TGGNode> implements Greenable<TGGNode> {
    private boolean green;

    private TGGNode(
            String id,
            FQN type,
            boolean green,
            NameRepository nameRepository,
            List<Property> properties,
            List<TGGLink> links,
            List<Attribute> attributes) {
        super(id, type, nameRepository, properties, links, attributes);
        this.green = green;
    }

    private static TGGNode create(
            String id, FQN type, boolean green, NameRepository nameRepository) {
        return new TGGNode(
                id,
                type,
                green,
                nameRepository,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public static TGGNode Black(String id, FQN type, NameRepository nameRepository) {
        return create(id, type, false, nameRepository);
    }

    public static TGGNode Green(String id, FQN type, NameRepository nameRepository) {
        return create(id, type, true, nameRepository);
    }

    @Override
    public TGGNode makeGreen() {
        this.green = true;
        return this;
    }

    @Override
    public TGGNode makeBlack() {
        this.green = false;
        return this;
    }

    @Override
    public TGGNode deepCopy(GraphNodeCopyHelper<TGGNode> copyHelper) {
        final List<Property> copiedProperties =
                this.properties.stream()
                        .map(Property::deepCopy)
                        .collect(Collectors.toCollection(ArrayList::new));
        final List<TGGLink> copiedLinks =
                this.links.stream()
                        .map(link -> link.deepCopy(copyHelper))
                        .collect(Collectors.toCollection(ArrayList::new));
        final List<Attribute> copiedAttributes =
                this.attributes.stream()
                        .map(Attribute::deepCopy)
                        .collect(Collectors.toCollection(ArrayList::new));

        return new TGGNode(
                id,
                type,
                green,
                copyHelper.getCopiedNameRepository(),
                copiedProperties,
                copiedLinks,
                copiedAttributes);
    }

    @Override
    public String toString() {
        final String links = String.join(",", links().stream().map(Objects::toString).toList());
        final String attributes =
                String.join(",", attributes().stream().map(Objects::toString).toList());
        return "<"
                + (green ? "++" : "")
                + id
                + ": "
                + type.fqn()
                + ";"
                + links
                + ";"
                + attributes
                + ">";
    }
}

package tools.vitruv.optggs.transpiler.tgg;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.LogicOperator;
import tools.vitruv.optggs.operators.expressions.ConstantExpression;
import tools.vitruv.optggs.operators.expressions.ValueExpression;
import tools.vitruv.optggs.operators.expressions.VariableExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Node {
    private final String id;
    private final FQN type;
    private boolean green;
    private final NameRepository nameRepository;
    private final Map<String, Property> properties;
    private final List<Link> links;
    private final List<Attribute> attributes;

    private static Node create(String id, FQN type, boolean green, NameRepository nameRepository) {
        return new Node(
                id,
                type,
                green,
                nameRepository,
                new HashMap<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    private Node(
            String id,
            FQN type,
            boolean green,
            NameRepository nameRepository,
            Map<String, Property> properties,
            List<Link> links,
            List<Attribute> attributes) {
        this.id = id;
        this.type = type;
        this.green = green;
        this.nameRepository = nameRepository;
        this.properties = properties;
        this.links = links;
        this.attributes = attributes;
    }

    public static Node Black(String id, FQN type, NameRepository nameRepository) {
        return create(id, type, false, nameRepository);
    }

    public static Node Green(String id, FQN type, NameRepository nameRepository) {
        return create(id, type, true, nameRepository);
    }

    public String id() {
        return id;
    }

    public FQN type() {
        return type;
    }

    public boolean isGreen() {
        return green;
    }

    public Node makeGreen() {
        this.green = true;
        return this;
    }

    public Node makeBlack() {
        this.green = false;
        return this;
    }

    public Collection<Property> properties() {
        return properties.values();
    }

    public Property property(String name) {
        return properties.get(name);
    }

    public ValueExpression addVariableAttribute(String name, LogicOperator operator) {
        var variableName = variableNameForProperty(name);
        return addVariableAttribute(name, operator, new VariableExpression(variableName));
    }

    public ValueExpression addVariableAttribute(
            String name, LogicOperator operator, VariableExpression variable) {
        addAttribute(new Attribute(name, operator, variable));
        return variable;
    }

    public void addConstantAttribute(
            String name, LogicOperator operator, ConstantExpression value) {
        addAttribute(new Attribute(name, operator, value));
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public Collection<Attribute> attributes() {
        return Collections.unmodifiableCollection(attributes);
    }

    private String variableNameForProperty(String propertyName) {
        var existingProperty = this.property(propertyName);
        if (existingProperty instanceof VariableProperty) {
            return existingProperty.value();
        } else {
            return nameRepository.getLower(propertyName);
        }
    }

    public Collection<Link> links() {
        return Collections.unmodifiableList(links);
    }

    public Node getFirstLinkTarget(String link) {
        return this.links.stream()
                .filter(someLink -> someLink.name().equals(link))
                .findFirst()
                .orElseThrow()
                .target();
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    Node deepCopy(TripleRuleCopyHelper copyHelper) {
        final Map<String, Property> copiedProperties =
                this.properties.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey, entry -> entry.getValue().deepCopy()));
        final List<Link> copiedLinks =
                this.links.stream()
                        .map(link -> link.deepCopy(copyHelper))
                        .collect(Collectors.toCollection(ArrayList::new));
        final List<Attribute> copiedAttributes =
                this.attributes.stream()
                        .map(Attribute::deepCopy)
                        .collect(Collectors.toCollection(ArrayList::new));

        return new Node(
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
        var links = String.join(",", links().stream().map(Objects::toString).toList());
        var attributes = String.join(",", attributes().stream().map(Objects::toString).toList());
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

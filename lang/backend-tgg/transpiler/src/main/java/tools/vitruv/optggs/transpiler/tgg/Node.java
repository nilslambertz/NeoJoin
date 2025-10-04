package tools.vitruv.optggs.transpiler.tgg;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.LogicOperator;
import tools.vitruv.optggs.operators.expressions.ConstantExpression;
import tools.vitruv.optggs.operators.expressions.ValueExpression;
import tools.vitruv.optggs.operators.expressions.VariableExpression;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Node {
    private final String id;
    private final FQN type;
    private boolean green;
    private final NameRepository nameRepository;
    private final Map<String, Property> properties;
    private final Map<String, Link> links;
    private final Map<String, Attribute> attributes;

    private static Node create(String id, FQN type, boolean green, NameRepository nameRepository) {
        return new Node(
                id, type, green, nameRepository, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    private Node(
            String id,
            FQN type,
            boolean green,
            NameRepository nameRepository,
            Map<String, Property> properties,
            Map<String, Link> links,
            Map<String, Attribute> attributes) {
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
        var existingAttribute = this.attribute(name);
        if (existingAttribute != null) {
            if (existingAttribute.operator() == operator
                    && existingAttribute.value() instanceof VariableExpression v) {
                // keep variable attribute if the same attribute already exists
                return v;
            } else if (existingAttribute.operator() == LogicOperator.Equals
                    && operator == LogicOperator.Equals
                    && existingAttribute.value() instanceof ConstantExpression c) {
                return c;
            }
            throw new RuntimeException(
                    "Tried to set attribute " + name + " on " + this.id + " twice (variable)");
        }
        addAttribute(new Attribute(name, operator, variable));
        return variable;
    }

    public void addConstantAttribute(
            String name, LogicOperator operator, ConstantExpression value) {
        var existingAttribute = this.attribute(name);
        if (existingAttribute != null) {
            throw new RuntimeException(
                    "Tried to set attribute " + name + " on " + this.id + " twice (constant)");
        }
        addAttribute(new Attribute(name, operator, value));
    }

    public void addAttribute(Attribute attribute) {
        attributes.put(attribute.name(), attribute);
    }

    public Attribute attribute(String name) {
        return attributes.get(name);
    }

    public Collection<Attribute> attributes() {
        return attributes.values();
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
        return this.links.values();
    }

    public Node getLinkTarget(String link) {
        return this.links.get(link).target();
    }

    public void addLink(Link link) {
        this.links.put(link.name(), link);
    }

    Node deepCopy(TripleRuleCopyHelper copyHelper) {
        final Map<String, Property> copiedProperties =
                this.properties.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey, entry -> entry.getValue().deepCopy()));
        final Map<String, Link> copiedLinks =
                this.links.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> entry.getValue().deepCopy(copyHelper)));
        final Map<String, Attribute> copiedAttributes =
                this.attributes.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey, entry -> entry.getValue().deepCopy()));

        return new Node(
                id,
                type.deepCopy(),
                green,
                copyHelper.getCopiedNameRepository(),
                copiedProperties,
                copiedLinks,
                copiedAttributes);
    }

    @Override
    public String toString() {
        var links = String.join(",", links().stream().map(Objects::toString).toList());
        var attrbutes = String.join(",", attributes().stream().map(Objects::toString).toList());
        return "<"
                + (green ? "++" : "")
                + id
                + ": "
                + type.fqn()
                + ";"
                + links
                + ";"
                + attrbutes
                + ">";
    }
}

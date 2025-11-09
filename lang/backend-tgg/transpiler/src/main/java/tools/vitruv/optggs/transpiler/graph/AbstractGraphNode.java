package tools.vitruv.optggs.transpiler.graph;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.LogicOperator;
import tools.vitruv.optggs.operators.expressions.ConstantExpression;
import tools.vitruv.optggs.operators.expressions.ValueExpression;
import tools.vitruv.optggs.operators.expressions.VariableExpression;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Value
@NonFinal
public abstract class AbstractGraphNode<
        L extends AbstractGraphLink<N>, N extends AbstractGraphNode<L, N>> {
    protected String id;
    protected FQN type;
    protected NameRepository nameRepository;

    @Getter(AccessLevel.NONE)
    protected List<Property> properties;

    @Getter(AccessLevel.NONE)
    protected List<L> links;

    @Getter(AccessLevel.NONE)
    protected List<Attribute> attributes;

    public abstract N deepCopy(GraphNodeCopyHelper<N> copyHelper);

    public Property property(String name) {
        return properties.stream()
                .filter(property -> property.name().equals(name))
                .findFirst()
                .orElse(null);
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

    public Collection<L> links() {
        return Collections.unmodifiableCollection(links);
    }

    public N getFirstLinkTarget(String link) {
        return this.links.stream()
                .filter(someLink -> someLink.getName().equals(link))
                .findFirst()
                .orElseThrow()
                .getTarget();
    }

    public void addLink(L link) {
        this.links.add(link);
    }

    @Override
    public String toString() {
        var links = String.join(",", links().stream().map(Objects::toString).toList());
        var attributes = String.join(",", attributes().stream().map(Objects::toString).toList());
        return "<" + id + ": " + type.fqn() + ";" + links + ";" + attributes + ">";
    }
}

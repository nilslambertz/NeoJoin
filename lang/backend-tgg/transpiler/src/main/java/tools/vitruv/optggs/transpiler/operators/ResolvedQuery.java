package tools.vitruv.optggs.transpiler.operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Value
public class ResolvedQuery {
    ResolvedSelection selection;
    List<ResolvedProjection> projections;
    List<ResolvedReferenceOperator> referenceOperators;
    List<ResolvedFilter> filters;
    Optional<ResolvedContainment> container;
    List<ResolvedLink> links;

    public Collection<TripleRule> toRules() {
        var rules = new ArrayList<TripleRule>();
        rules.add(createPrimaryRule());

        rules.addAll(
                referenceOperators.stream()
                        .map(this::createReferenceOperatorRules)
                        .flatMap(List::stream)
                        .toList());
        rules.addAll(links.stream().map(this::createLinkRule).toList());
        return rules;
    }

    public TripleRule createPrimaryRule() {
        var rule = new TripleRule();
        selection.extendRule(rule);
        rule.allSourcesAsSlice().makeGreen();
        rule.allTargetsAsSlice().makeGreen();
        for (var filter : filters) {
            filter.extendRule(rule);
        }
        for (var projection : projections) {
            projection.extendRule(rule);
        }
        container.ifPresent(value -> value.extendRule(rule));
        return rule;
    }

    public List<TripleRule> createReferenceOperatorRules(
            ResolvedReferenceOperator referenceOperator) {
        // TODO!! We need the target class and target reference name
        return referenceOperator.addRules(new FQN("CarWithWheels"));
    }

    public TripleRule createLinkRule(ResolvedLink link) {
        var rule = new TripleRule();
        rule.setLinkRule(true);
        selection.extendRule(rule);
        link.extendRule(rule);
        return rule;
    }

    @Override
    public String toString() {
        var p = String.join(".", projections.stream().map(Object::toString).toList());
        var f = String.join(".", filters.stream().map(Object::toString).toList());
        var c = String.join(".", container.stream().map(Object::toString).toList());
        var l = String.join(".", links.stream().map(Object::toString).toList());
        return selection + f + p + c + l;
    }
}

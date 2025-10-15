package tools.vitruv.optggs.transpiler.operators;

import lombok.Value;

import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedReferenceOperatorChain;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Value
public class ResolvedQuery {
    ResolvedSelection selection;
    List<ResolvedProjection> projections;
    List<ResolvedReferenceOperatorChain> referenceOperatorChains;
    List<ResolvedFilter> filters;
    Optional<ResolvedContainment> container;
    List<ResolvedLink> links;

    TripleRulesBuilder rulesBuilder = new TripleRulesBuilder();

    public Collection<TripleRule> toRules() {
        createPrimaryRule();
        links.forEach(this::createLinkRule);

        final Stream<TripleRule> referenceOperatorRules =
                referenceOperatorChains.stream()
                        .map(ResolvedReferenceOperatorChain::generateRules)
                        .flatMap(List::stream);

        return Stream.concat(rulesBuilder.getTripleRules().stream(), referenceOperatorRules)
                .toList();
    }

    public void createPrimaryRule() {
        final TripleRule rule = rulesBuilder.addRule();
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
    }

    public void createLinkRule(ResolvedLink link) {
        final TripleRule rule = rulesBuilder.addRule();
        rule.setLinkRule(true);
        selection.extendRule(rule);
        link.extendRule(rule);
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

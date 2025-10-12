package tools.vitruv.optggs.transpiler.tgg;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import tools.vitruv.optggs.operators.FQN;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class TripleRulesBuilder {
    private final List<TripleRule> tripleRules = new ArrayList<>();
    private TripleRule latestRule = null;

    @Setter private FQN sourceRoot = null;

    @Getter(AccessLevel.NONE)
    private final List<String> referencesToLastSourceNode = new ArrayList<>();

    public TripleRule addRule(TripleRule rule) {
        tripleRules.add(rule);
        latestRule = rule;
        return rule;
    }

    public TripleRule addRule() {
        return addRule(new TripleRule());
    }

    public List<String> getReferencesToLastSourceNode() {
        return List.copyOf(referencesToLastSourceNode);
    }

    public void addReferenceToLastSourceNode(String reference) {
        referencesToLastSourceNode.add(reference);
    }
}

package tools.vitruv.optggs.transpiler.tgg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class TripleRulesBuilder {
    private final List<TripleRule> tripleRules = new ArrayList<>();
    private final List<GraphConstraint> constraints = new ArrayList<>();
    private TripleRule latestRule = null;

    @Setter private TripleRulePathToNode pathToLastNode = null;

    public TripleRule addRule(TripleRule rule) {
        tripleRules.add(rule);
        latestRule = rule;
        return rule;
    }

    public TripleRule addRule() {
        return addRule(new TripleRule());
    }

    public void removeRule(TripleRule rule) {
        tripleRules.remove(rule);
    }

    public void addLinkToPathToLastNode(String link) {
        pathToLastNode.addLinkToPath(link);
    }

    public void addConstraint(GraphConstraint constraint) {
        constraints.add(constraint);
    }
}

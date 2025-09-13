package tools.vitruv.optggs.transpiler.operators;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

import java.util.List;

public interface ResolvedProjection extends RuleGenerator {
    boolean containedInPrimaryRule();

    default List<TripleRule> generateRules(FQN target) {
        return List.of();
    }
}

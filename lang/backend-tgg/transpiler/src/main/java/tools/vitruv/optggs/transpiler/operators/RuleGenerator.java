package tools.vitruv.optggs.transpiler.operators;

import tools.vitruv.optggs.transpiler.graph.TripleRule;

public interface RuleGenerator {
    void extendRule(TripleRule rule);
}

package tools.vitruv.optggs.transpiler.graph;

import lombok.Value;
import tools.vitruv.optggs.transpiler.graph.pattern.ConstraintPattern;
import tools.vitruv.optggs.transpiler.graph.tgg.Correspondence;
import tools.vitruv.optggs.transpiler.graph.tgg.CorrespondenceType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Value
public class TripleGrammar {
    String name;
    Collection<TripleRule> rules;
    List<ConstraintPattern> constraints;
    Set<String> sourceMetamodels;
    Set<String> targetMetamodels;

    public Set<CorrespondenceType> getCorrespondenceTypes() {
        return rules.stream()
                .map(TripleRule::correspondences)
                .flatMap(Collection::stream)
                .map(Correspondence::toCorrespondenceType)
                .collect(Collectors.toSet());
    }
}

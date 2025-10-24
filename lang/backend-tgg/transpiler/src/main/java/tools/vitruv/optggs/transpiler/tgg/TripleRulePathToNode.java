package tools.vitruv.optggs.transpiler.tgg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import tools.vitruv.optggs.operators.FQN;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class TripleRulePathToNode {
    private final FQN root;
    List<String> linkPath = new ArrayList<>();

    public void addLinkToPath(String reference) {
        linkPath.add(reference);
    }
}

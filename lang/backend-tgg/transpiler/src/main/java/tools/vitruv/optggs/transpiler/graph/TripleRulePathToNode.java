package tools.vitruv.optggs.transpiler.graph;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import tools.vitruv.optggs.operators.FQN;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TripleRulePathToNode {
    private final FQN root;
    List<String> linkPath = new ArrayList<>();

    public void addLinkToPath(String reference) {
        linkPath.add(reference);
    }

    public TripleRulePathToNode pathToSecondLastNode() {
        return new TripleRulePathToNode(root, linkPath.subList(0, linkPath.size() - 1));
    }

    public String getLastLink() {
        return linkPath.getLast();
    }
}

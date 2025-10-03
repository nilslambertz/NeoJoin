package tools.vitruv.optggs.transpiler.tgg;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class TripleRuleCopyHelper {
    Map<Node, Node> oldToNewNodes = new HashMap<>();
    NameRepository copiedNameRepository;

    public TripleRuleCopyHelper(NameRepository oldNameRepository) {
        this.copiedNameRepository = oldNameRepository.deepCopy();
    }

    public Node getCopiedNode(Node oldNode) {
        return oldToNewNodes.putIfAbsent(oldNode, oldNode.deepCopy(this));
    }
}

package tools.vitruv.optggs.transpiler.graph;

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
        if (oldToNewNodes.containsKey(oldNode)) {
            return oldToNewNodes.get(oldNode);
        }

        final Node copiedNode = oldNode.deepCopy(this);
        oldToNewNodes.put(oldNode, copiedNode);
        return copiedNode;
    }
}

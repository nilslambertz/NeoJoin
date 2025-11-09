package tools.vitruv.optggs.transpiler.operators.reference_operators;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.GraphConstraint;
import tools.vitruv.optggs.transpiler.graph.Node;
import tools.vitruv.optggs.transpiler.graph.Slice;
import tools.vitruv.optggs.transpiler.graph.TripleRule;
import tools.vitruv.optggs.transpiler.graph.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.graph.TripleRulesBuilder;

// TODO: Not fully working yet, we need additional information on how to build the constraint, e.g.
// if the operator was only used inside an argument
public class ResolvedMapAny implements ResolvedReferenceOperator {
    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule latestRule = builder.getLatestRule();

        final TripleRule copiedRule = latestRule.deepCopy();
        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final FQN lastNodeType = copiedRule.findNestedSourceNode(pathToLastNode).getType();

        // Duplicate last source node and incoming link
        final Slice sourceSlice = copiedRule.addSourceSlice();
        Node duplicatedLastNode = sourceSlice.addNode(lastNodeType);
        Link linkToLastDuplicatedLastNode =
                Link.Black(pathToLastNode.getLastLink(), duplicatedLastNode);

        // Add the "duplicated" link to the node "above"
        final Node nodeBeforeLastNode =
                copiedRule.findNestedSourceNode(pathToLastNode.pathToSecondLastNode());
        nodeBeforeLastNode.addLink(linkToLastDuplicatedLastNode);

        // Now we can create a new constraint with the full chain and the last link and node
        // duplicated
        builder.addConstraint(
                new GraphConstraint(copiedRule.allSourcesAsSlice().nodes().stream().toList()));
    }
}

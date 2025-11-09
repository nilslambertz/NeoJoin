package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.Node;
import tools.vitruv.optggs.transpiler.graph.Slice;
import tools.vitruv.optggs.transpiler.graph.TripleRule;
import tools.vitruv.optggs.transpiler.graph.TripleRulePathToNode;
import tools.vitruv.optggs.transpiler.graph.TripleRulesBuilder;

@Value
public class ResolvedCollectReferences implements ResolvedReferenceOperator {
    FQN targetRoot;
    FQN targetLeaf;
    String targetField;

    @Override
    public void extendRules(TripleRulesBuilder builder) {
        final TripleRule latestRule = builder.getLatestRule();

        final TripleRulePathToNode pathToLastNode = builder.getPathToLastNode();
        final Node lastSourceNode = latestRule.findNestedSourceNode(pathToLastNode);
        lastSourceNode.makeBlack();

        final Node targetSourceNode = latestRule.findTargetNodeByType(targetRoot).orElseThrow();

        final Slice targetSlice = latestRule.addTargetSlice();
        Node targetChildNode = targetSlice.addNode(targetLeaf);

        Link targetParentLinkToChild = Link.Green(targetField, targetChildNode);
        targetSourceNode.addLink(targetParentLinkToChild);

        latestRule.addCorrespondenceRule(lastSourceNode, targetChildNode);
    }
}

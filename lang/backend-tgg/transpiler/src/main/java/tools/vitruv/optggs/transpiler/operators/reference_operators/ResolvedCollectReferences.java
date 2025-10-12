package tools.vitruv.optggs.transpiler.operators.reference_operators;

import lombok.Value;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.tgg.TripleRulesBuilder;

@Value
public class ResolvedCollectReferences implements ResolvedReferenceOperator {
    @Override
    public void extendRules(TripleRulesBuilder builder) {
        throw new IllegalStateException(
                "CollectReferences needs the target element to connect to, use custom method");
    }

    public void extendRulesForCollect(
            FQN targetTop,
            FQN targetChildType,
            String targetReference,
            TripleRulesBuilder builder) {
        final TripleRule latestRule = builder.getLatestRule();
        final Node lastSourceNode =
                latestRule.findNestedSourceNode(
                        builder.getSourceRoot(), builder.getReferencesToLastSourceNode());
        lastSourceNode.makeBlack();

        final Node targetSourceNode = latestRule.findTargetNodeByType(targetTop).orElseThrow();

        final Slice targetSlice = latestRule.addTargetSlice();
        Node targetChildNode = targetSlice.addNode(targetChildType);

        Link targetParentLinkToChild = Link.Green(targetReference, targetChildNode);
        targetSourceNode.addLink(targetParentLinkToChild);

        latestRule.addCorrespondenceRule(lastSourceNode, targetChildNode);
    }
}

package tools.vitruv.optggs.transpiler.operators;

import org.apache.log4j.Logger;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.FlatMap;
import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.reference_operator.NeojoinReferenceOperator;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

import java.util.ArrayList;
import java.util.List;

public class ResolvedReferenceOperator implements RuleAdder {
    private static final Logger log = Logger.getLogger(ResolvedReferenceOperator.class);
    private final ReferenceOperator referenceOperator;
    private final String targetField;

    private FQN sourceRoot = null;
    private List<String> referencesToLastNode = new ArrayList<>();

    public ResolvedReferenceOperator(NeojoinReferenceOperator projection) {
        this.referenceOperator = projection.referenceOperator();
        this.targetField = projection.targetField();
    }

    @Override
    public List<TripleRule> addRules(FQN target) {
        if (!(referenceOperator instanceof FeatureCall featureCall)) {
            throw new RuntimeException("TODO: First operator must be feature call!");
        }
        final List<TripleRule> rules = new ArrayList<>();

        this.sourceRoot = new FQN(featureCall.getSimpleName());
        TripleRule latestRule = generateTripleRuleForFeatureCall(this.sourceRoot, target);
        rules.add(latestRule);

        ReferenceOperator nextReferenceOperator = featureCall.getFollowingOperator();
        while (nextReferenceOperator != null) {
            final TripleRule nextRule =
                    generateTripleRuleForReferenceOperator(latestRule, nextReferenceOperator);
            rules.add(nextRule);
            latestRule = nextRule;
            nextReferenceOperator = featureCall.getFollowingOperator();
        }

        return rules;
    }

    private TripleRule generateTripleRuleForReferenceOperator(
            TripleRule previousRule, ReferenceOperator operator) {
        if (operator instanceof MemberFeatureCall memberFeatureCall) {
            return generateTripleRuleForMemberFeatureCall(previousRule, memberFeatureCall);
        } else if (operator instanceof FlatMap flatMap) {
            return generateTripleRuleForFlatMap(previousRule, flatMap);
        }

        throw new RuntimeException("Unknown operator: " + operator);
    }

    private TripleRule generateTripleRuleForFeatureCall(FQN source, FQN target) {
        final TripleRule firstRule = new TripleRule();
        final Slice sourceSlice = firstRule.addSourceSlice();
        final Node sourceNode = sourceSlice.addNode(source);

        final Slice targetSlice = firstRule.addTargetSlice();
        final Node targetNode = targetSlice.addNode(target);

        firstRule.addCorrespondenceRule(sourceNode, targetNode);

        return firstRule;
    }

    private TripleRule generateTripleRuleForMemberFeatureCall(
            TripleRule previousRule, MemberFeatureCall operator) {
        final TripleRule newRule = previousRule.deepCopy();

        final Node sourceNode = newRule.findSourceNodeByType(this.sourceRoot).orElseThrow();
        Node lastSourceNode = sourceNode;
        for (String nextReference : referencesToLastNode) {
            lastSourceNode = sourceNode.getLinkTarget(nextReference);
        }

        final Slice sourceSlice = newRule.addSourceSlice();
        Node childNode = sourceSlice.addNode(new FQN(operator.getFeatureClassSimpleName()));
        childNode.makeGreen();
        Link parentLinkToChild = Link.Green(operator.getFeatureSimpleName(), childNode);
        lastSourceNode.addLink(parentLinkToChild);
        //        newRule.addSourceSlice(List.of(childNode), List.of());
        return newRule;
    }

    private TripleRule generateTripleRuleForFlatMap(TripleRule previousRule, FlatMap operator) {
        return null;
    }

    @Override
    public String toString() {
        return "Π( TODO Reference operator )";
    }
}

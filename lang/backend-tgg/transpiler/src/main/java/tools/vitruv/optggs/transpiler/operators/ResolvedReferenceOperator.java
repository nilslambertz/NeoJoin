package tools.vitruv.optggs.transpiler.operators;

import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.FlatMap;
import tools.vitruv.neojoin.expression_parser.model.Map;
import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.model.ToList;
import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.reference_operator.NeojoinReferenceOperator;
import tools.vitruv.optggs.transpiler.tgg.Correspondence;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResolvedReferenceOperator implements RuleAdder {
    private final ReferenceOperator referenceOperator;
    private final String targetField;
    private final String targetType;

    private FQN sourceRoot = null;
    private FQN targetRoot = null;
    private List<String> referencesToLastNode = new ArrayList<>();

    public ResolvedReferenceOperator(NeojoinReferenceOperator projection) {
        this.referenceOperator = projection.referenceOperator();
        this.targetField = projection.targetField();
        this.targetType = projection.type();
    }

    @Override
    public List<TripleRule> addRules(FQN target) {
        if (!(referenceOperator instanceof FeatureCall featureCall)) {
            throw new RuntimeException("TODO: First operator must be feature call!");
        }
        final List<TripleRule> rules = new ArrayList<>();

        // Generate the rule for the feature call, but don't add it to the list of rules, because it
        // only consists of context nodes
        this.sourceRoot = new FQN(featureCall.getSimpleName());
        this.targetRoot = target;
        TripleRule latestRule = generateTripleRuleForFeatureCall(this.sourceRoot, target);

        ReferenceOperator nextReferenceOperator = featureCall.getFollowingOperator();
        while (nextReferenceOperator != null) {
            final Optional<TripleRule> nextRule =
                    generateTripleRuleForReferenceOperator(latestRule, nextReferenceOperator);
            if (nextRule.isPresent()) {
                rules.add(nextRule.get());
                latestRule = nextRule.get();
            }
            nextReferenceOperator = nextReferenceOperator.getFollowingOperator();
        }

        return rules;
    }

    private Optional<TripleRule> generateTripleRuleForReferenceOperator(
            TripleRule previousRule, ReferenceOperator operator) {
        if (operator instanceof MemberFeatureCall memberFeatureCall) {
            return Optional.of(
                    generateTripleRuleForMemberFeatureCall(previousRule, memberFeatureCall));
        } else if (operator instanceof FlatMap flatMap) {
            return Optional.of(generateTripleRuleForFlatMap(previousRule, flatMap));
        } else if (operator instanceof Map map) {
            updatePreviousRuleForMap(previousRule, map);
            return Optional.empty();
        } else if (operator instanceof ToList toList) {
            updatePreviousRuleForToList(previousRule, toList);
            return Optional.empty();
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

        final Node lastSourceNode =
                newRule.findNestedSourceNode(this.sourceRoot, referencesToLastNode);

        final Slice sourceSlice = newRule.addSourceSlice();
        Node childNode =
                sourceSlice.addNode(
                        new FQN(operator.getFeatureInformation().getFeatureClassSimpleName()));
        childNode.makeGreen();

        final String featureName = operator.getFeatureInformation().getFeatureName();
        Link parentLinkToChild = Link.Green(featureName, childNode);
        referencesToLastNode.add(featureName);
        lastSourceNode.addLink(parentLinkToChild);

        return newRule;
    }

    private TripleRule generateTripleRuleForFlatMap(TripleRule previousRule, FlatMap operator) {
        // Copy the rule and make all nodes black
        final TripleRule newRule = previousRule.deepCopy().makeBlack();

        final Node lastSourceNode =
                newRule.findNestedSourceNode(this.sourceRoot, referencesToLastNode);

        final Slice sourceSlice = newRule.addSourceSlice();
        Node childNode =
                sourceSlice.addNode(
                        new FQN(operator.getFeatureInformation().getFeatureClassSimpleName()));
        childNode.makeGreen();

        final String featureName = operator.getFeatureInformation().getFeatureName();
        Link parentLinkToChild = Link.Green(featureName, childNode);
        referencesToLastNode.add(featureName);
        lastSourceNode.addLink(parentLinkToChild);

        return newRule;
    }

    private void updatePreviousRuleForMap(TripleRule previousRule, Map operator) {
        final Node lastSourceNode =
                previousRule.findNestedSourceNode(this.sourceRoot, referencesToLastNode);

        final Slice sourceSlice = previousRule.addSourceSlice();
        Node childNode =
                sourceSlice.addNode(
                        new FQN(operator.getFeatureInformation().getFeatureClassSimpleName()));
        childNode.makeGreen();

        final String featureName = operator.getFeatureInformation().getFeatureName();
        Link parentLinkToChild = Link.Green(featureName, childNode);
        referencesToLastNode.add(featureName);
        lastSourceNode.addLink(parentLinkToChild);
    }

    private void updatePreviousRuleForToList(TripleRule previousRule, ToList operator) {
        final Node lastSourceNode =
                previousRule.findNestedSourceNode(this.sourceRoot, referencesToLastNode);

        final Node targetSourceNode =
                previousRule.findTargetNodeByType(this.targetRoot).orElseThrow();

        final Slice targetSlice = previousRule.addTargetSlice();
        Node targetChildNode = targetSlice.addNode(new FQN(targetType));
        targetChildNode.makeGreen();

        Link targetParentLinkToChild = Link.Green(targetField, targetChildNode);
        targetSourceNode.addLink(targetParentLinkToChild);

        final Correspondence newCorrespondence =
                previousRule.addCorrespondenceRule(lastSourceNode, targetChildNode);
        newCorrespondence.makeGreen();
    }

    @Override
    public String toString() {
        return "Π( TODO Reference operator )";
    }
}

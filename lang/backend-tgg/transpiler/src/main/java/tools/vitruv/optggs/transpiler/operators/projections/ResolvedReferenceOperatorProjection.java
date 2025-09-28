package tools.vitruv.optggs.transpiler.operators.projections;

import org.apache.log4j.Logger;

import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.model.FlatMap;
import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.projections.ReferenceOperatorProjection;
import tools.vitruv.optggs.transpiler.operators.ResolvedProjection;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.Slice;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

import java.util.ArrayList;
import java.util.List;

public class ResolvedReferenceOperatorProjection implements ResolvedProjection {
    private static final Logger log = Logger.getLogger(ResolvedReferenceOperatorProjection.class);
    private final ReferenceOperator referenceOperator;
    private final String field;

    public ResolvedReferenceOperatorProjection(ReferenceOperatorProjection projection) {
        this.referenceOperator = projection.referenceOperator();
        this.field = projection.field();
    }

    @Override
    public List<TripleRule> generateRules(FQN target) {
        if (!(referenceOperator instanceof MemberFeatureCall firstOperator)) {
            throw new RuntimeException("TODO: First operator must be feature call!");
        }
        final List<TripleRule> rules = new ArrayList<>();
//        final FQN source = new FQN(firstOperator.getParentSimpleName());
//
//        // TODO: If the only operator is a feature call, we need a rule that adds the nodes on
//        // source and target
//        final TripleRule firstRule = generateTripleRuleForFeatureCall(source, firstOperator);
//        rules.add(firstRule);
//
//        TripleRule lastRule = firstRule;
//        ReferenceOperator nextOperator = firstOperator.getFollowingOperator();
//        while (nextOperator != null) {
//            final List<TripleRule> nextRules =
//                    generateTripleRuleForReferenceOperator(lastRule, nextOperator);
//            rules.addAll(nextRules);
//            if (!nextRules.isEmpty()) {
//                lastRule = nextRules.getLast();
//            }
//            nextOperator = nextOperator.getFollowingOperator();
//        }

        return rules;
    }

    private List<TripleRule> generateTripleRuleForReferenceOperator(
            TripleRule previousRule, ReferenceOperator operator) {
        if (operator instanceof FlatMap flatMap) {
            return generateTripleRuleForSkipIntermediateReference(
                    previousRule, flatMap);
        }

        return List.of();
    }

    private TripleRule generateTripleRuleForFeatureCall(FQN source, MemberFeatureCall operator) {
        final TripleRule firstRule = new TripleRule();
        final Slice sourceSlice = firstRule.addSourceSlice();
        Node parentNode = sourceSlice.addNode(source);
        Node childNode = sourceSlice.addNode(new FQN("Axis"));
        childNode.makeGreen();
        Link parentLinkToChild = Link.Green(operator.getFeatureSimpleName(), childNode);
        parentNode.addLink(parentLinkToChild);
        firstRule.addSourceSlice(List.of(parentNode, childNode), List.of());
        return firstRule;
    }

    private List<TripleRule> generateTripleRuleForSkipIntermediateReference(
            TripleRule previousRule, FlatMap operator) {
        return List.of();
    }

    @Override
    public String toString() {
        return "Π( TODO Reference operator )";
    }

    @Override
    public boolean containedInPrimaryRule() {
        return false;
    }

    @Override
    public void extendRule(TripleRule rule) {
        // Do nothing
    }
}

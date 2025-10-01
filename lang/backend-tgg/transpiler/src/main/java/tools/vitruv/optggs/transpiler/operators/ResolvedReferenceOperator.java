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

    public ResolvedReferenceOperator(NeojoinReferenceOperator projection) {
        this.referenceOperator = projection.referenceOperator();
        this.targetField = projection.targetField();
    }

    @Override
    public List<TripleRule> addRules(FQN target) {
        if (!(referenceOperator instanceof FeatureCall firstOperator)) {
            throw new RuntimeException("TODO: First operator must be feature call!");
        }
        final List<TripleRule> rules = new ArrayList<>();
        final FQN source = new FQN(firstOperator.getSimpleName());

        //        final TripleRule firstRule = generateTripleRuleForFeatureCall(source,
        // firstOperator);
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
            return generateTripleRuleForSkipIntermediateReference(previousRule, flatMap);
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
}

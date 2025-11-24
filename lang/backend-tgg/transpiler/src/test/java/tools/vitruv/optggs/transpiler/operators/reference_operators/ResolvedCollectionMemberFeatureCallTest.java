package tools.vitruv.optggs.transpiler.operators.reference_operators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.NameRepositoryFixtures;
import tools.vitruv.optggs.transpiler.graph.tgg.AbstractTripleGraphGrammarTest;
import tools.vitruv.optggs.transpiler.graph.tgg.Correspondence;
import tools.vitruv.optggs.transpiler.graph.tgg.GraphPathToNode;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGLink;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGNode;
import tools.vitruv.optggs.transpiler.graph.tgg.TGGNodeFixtures;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRule;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRuleFixtures;
import tools.vitruv.optggs.transpiler.graph.tgg.TripleRulesBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

class ResolvedCollectionMemberFeatureCallTest extends AbstractTripleGraphGrammarTest {
    @Test
    public void extendRules() {
        // given
        final TripleRule someOtherRule = getTestTripleRule();
        final TripleRule ruleToCopyAndExtend = getTripleRuleToCopyAndExtend();

        final TripleRulesBuilder tripleRulesBuilder = new TripleRulesBuilder();
        tripleRulesBuilder.addRule(someOtherRule);
        tripleRulesBuilder.addRule(ruleToCopyAndExtend);
        tripleRulesBuilder.setPathToLastNode(
                new GraphPathToNode(
                        new FQN("Metamodel5", "LocalName5"),
                        new ArrayList<>(List.of("anotherTestLink"))));

        final ResolvedCollectionMemberFeatureCall resolvedCollectionMemberFeatureCall =
                new ResolvedCollectionMemberFeatureCall(
                        "someNewFeature", new FQN("NewNodeMetamodel", "NewNodeLocalName"));

        // when
        resolvedCollectionMemberFeatureCall.extendRules(tripleRulesBuilder);

        // then
        assertThat(tripleRulesBuilder.getTripleRules()).hasSize(3);

        // Check that other rules are unchanged
        final TripleRule otherTripleRuleAfterUpdate =
                tripleRulesBuilder.getTripleRules().stream()
                        .filter(rule -> rule.getId().equals(someOtherRule.getId()))
                        .findFirst()
                        .orElseThrow();
        assertIsTestTripleRule(otherTripleRuleAfterUpdate);
        final TripleRule tripleRuleToCopyAndExtendAfterUpdate =
                tripleRulesBuilder.getTripleRules().stream()
                        .filter(rule -> rule.getId().equals(ruleToCopyAndExtend.getId()))
                        .findFirst()
                        .orElseThrow();
        assertIsTripleRuleToCopyAndExtend(tripleRuleToCopyAndExtendAfterUpdate);

        // Check that the rule was extended correctly
        final TripleRule generatedRule =
                tripleRulesBuilder.getTripleRules().stream()
                        .filter(rule -> !rule.getId().equals(ruleToCopyAndExtend.getId()))
                        .filter(rule -> !rule.getId().equals(someOtherRule.getId()))
                        .findFirst()
                        .orElseThrow();

        assertThat(generatedRule.allSourcesAsSlice().nodes()).hasSize(3);

        // New source node
        final TGGNode sourceNode3 =
                generatedRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> !node.getId().equals("anotherRuleNode1"))
                        .filter(node -> !node.getId().equals("anotherRuleNode2"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode3.getId())
                .isNotEqualTo("anotherRuleNode1")
                .isNotEqualTo("anotherRuleNode2")
                .isNotNull();
        assertThat(sourceNode3.getType())
                .isEqualTo(new FQN("NewNodeMetamodel", "NewNodeLocalName"));
        assertThat(sourceNode3.isGreen()).isTrue();
        assertThat(sourceNode3.links()).isEmpty();
        assertThat(sourceNode3.attributes()).isEmpty();

        final TGGNode sourceNode1 =
                generatedRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> node.getId().equals("anotherRuleNode1"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode1.getId()).isEqualTo("anotherRuleNode1");
        assertThat(sourceNode1.getType()).isEqualTo(new FQN("Metamodel4", "LocalName4"));
        assertThat(sourceNode1.isGreen()).isFalse();
        assertThat(sourceNode1.attributes()).isEmpty();

        // New link to new node
        assertThat(sourceNode1.links()).hasSize(1);
        final TGGLink sourceNode1NewLink = sourceNode1.links().stream().toList().getFirst();
        assertThat(sourceNode1NewLink.getName()).isEqualTo("someNewFeature");
        assertThat(sourceNode1NewLink.getTarget()).isEqualTo(sourceNode3);
        assertThat(sourceNode1NewLink.isGreen()).isTrue();

        final TGGNode sourceNode2 =
                generatedRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> node.getId().equals("anotherRuleNode2"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode2.getId()).isEqualTo("anotherRuleNode2");
        assertThat(sourceNode2.getType()).isEqualTo(new FQN("Metamodel5", "LocalName5"));
        assertThat(sourceNode2.isGreen()).isTrue();
        assertThat(sourceNode2.links()).hasSize(1);
        final TGGLink sourceNode2AnotherLink =
                sourceNode2.links().stream()
                        .filter(link -> link.getName().equals("anotherTestLink"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode2AnotherLink.getName()).isEqualTo("anotherTestLink");
        assertThat(sourceNode2AnotherLink.getTarget()).isEqualTo(sourceNode1);
        assertThat(sourceNode2AnotherLink.isGreen()).isTrue();
        assertThat(sourceNode2.attributes()).isEmpty();

        // Assert target nodes
        assertThat(generatedRule.allTargetsAsSlice().nodes()).hasSize(1);

        final TGGNode targetNode1 =
                generatedRule.allTargetsAsSlice().nodes().stream().findFirst().orElseThrow();
        assertThat(targetNode1.getId()).isEqualTo("anotherRuleTargetNode1");
        assertThat(targetNode1.getType()).isEqualTo(new FQN("Metamodel6", "LocalName6"));
        assertThat(targetNode1.isGreen()).isTrue();
        assertThat(targetNode1.links()).isEmpty();
        assertThat(targetNode1.attributes()).isEmpty();

        // Assert correspondences
        assertThat(generatedRule.correspondences()).hasSize(1);

        final Correspondence correspondence =
                generatedRule.correspondences().stream().findFirst().orElseThrow();
        assertThat(correspondence.getSource()).isEqualTo(sourceNode2);
        assertThat(correspondence.getTarget()).isEqualTo(targetNode1);
        assertThat(correspondence.isGreen()).isTrue();
    }

    private TripleRule getTripleRuleToCopyAndExtend() {
        final TGGNode anotherRuleSourceNode1 =
                TGGNodeFixtures.someTGGNode(
                        "anotherRuleNode1",
                        new FQN("Metamodel4", "LocalName4"),
                        false,
                        NameRepositoryFixtures.someNameRepository(),
                        new ArrayList<>(),
                        new LinkedHashSet<>());
        final TGGNode anotherRuleSourceNode2 =
                TGGNodeFixtures.someTGGNode(
                        "anotherRuleNode2",
                        new FQN("Metamodel5", "LocalName5"),
                        true,
                        NameRepositoryFixtures.someNameRepository(),
                        new ArrayList<>(
                                List.of(TGGLink.Green("anotherTestLink", anotherRuleSourceNode1))),
                        new LinkedHashSet<>());
        final TGGNode anotherRuleTargetNode1 =
                TGGNodeFixtures.someTGGNode(
                        "anotherRuleTargetNode1",
                        new FQN("Metamodel6", "LocalName6"),
                        true,
                        NameRepositoryFixtures.someNameRepository(),
                        new ArrayList<>(),
                        new LinkedHashSet<>());
        final Correspondence anotherRuleCorrespondence =
                Correspondence.Green(anotherRuleSourceNode2, anotherRuleTargetNode1);
        return TripleRuleFixtures.someTripleRule(
                new ArrayList<>(List.of(anotherRuleSourceNode1, anotherRuleSourceNode2)),
                new ArrayList<>(List.of(anotherRuleTargetNode1)),
                new ArrayList<>(List.of(anotherRuleCorrespondence)));
    }

    private void assertIsTripleRuleToCopyAndExtend(TripleRule tripleRule) {
        // Assert source nodes
        assertThat(tripleRule.allSourcesAsSlice().nodes()).hasSize(2);

        final TGGNode sourceNode1 =
                tripleRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> node.getId().equals("anotherRuleNode1"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode1.getId()).isEqualTo("anotherRuleNode1");
        assertThat(sourceNode1.getType()).isEqualTo(new FQN("Metamodel4", "LocalName4"));
        assertThat(sourceNode1.isGreen()).isFalse();
        assertThat(sourceNode1.links()).isEmpty();
        assertThat(sourceNode1.attributes()).isEmpty();

        final TGGNode sourceNode2 =
                tripleRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> node.getId().equals("anotherRuleNode2"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode2.getId()).isEqualTo("anotherRuleNode2");
        assertThat(sourceNode2.getType()).isEqualTo(new FQN("Metamodel5", "LocalName5"));
        assertThat(sourceNode2.isGreen()).isTrue();
        assertThat(sourceNode2.links()).hasSize(1);
        assertThat(sourceNode2.links().stream().toList().getFirst().getName())
                .isEqualTo("anotherTestLink");
        assertThat(sourceNode2.links().stream().toList().getFirst().getTarget())
                .isEqualTo(sourceNode1);
        assertThat(sourceNode2.links().stream().toList().getFirst().isGreen()).isTrue();
        assertThat(sourceNode2.attributes()).isEmpty();

        // Assert target nodes
        assertThat(tripleRule.allTargetsAsSlice().nodes()).hasSize(1);

        final TGGNode targetNode1 =
                tripleRule.allTargetsAsSlice().nodes().stream().findFirst().orElseThrow();
        assertThat(targetNode1.getId()).isEqualTo("anotherRuleTargetNode1");
        assertThat(targetNode1.getType()).isEqualTo(new FQN("Metamodel6", "LocalName6"));
        assertThat(targetNode1.isGreen()).isTrue();
        assertThat(targetNode1.links()).isEmpty();
        assertThat(targetNode1.attributes()).isEmpty();

        // Assert correspondences
        assertThat(tripleRule.correspondences()).hasSize(1);

        final Correspondence correspondence =
                tripleRule.correspondences().stream().findFirst().orElseThrow();
        assertThat(correspondence.getSource()).isEqualTo(sourceNode2);
        assertThat(correspondence.getTarget()).isEqualTo(targetNode1);
        assertThat(correspondence.isGreen()).isTrue();
    }
}

package tools.vitruv.optggs.transpiler.graph.tgg;

import static org.assertj.core.api.Assertions.assertThat;

import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.transpiler.graph.NameRepositoryFixtures;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class AbstractTripleGraphGrammarTest {
    public TripleRule getTestTripleRule() {
        final TGGNode someRuleSourceNode1 =
                TGGNodeFixtures.someTGGNode(
                        "someOtherRuleNode1",
                        new FQN("Metamodel1", "LocalName1"),
                        false,
                        NameRepositoryFixtures.someNameRepository(),
                        new ArrayList<>(),
                        new LinkedHashSet<>());
        final TGGNode someRuleSourceNode2 =
                TGGNodeFixtures.someTGGNode(
                        "someOtherRuleNode2",
                        new FQN("Metamodel2", "LocalName2"),
                        true,
                        NameRepositoryFixtures.someNameRepository(),
                        new ArrayList<>(
                                List.of(TGGLink.Green("someTestLink", someRuleSourceNode1))),
                        new LinkedHashSet<>());
        final TGGNode someRuleTargetNode1 =
                TGGNodeFixtures.someTGGNode(
                        "someOtherRuleTargetNode1",
                        new FQN("Metamodel3", "LocalName3"),
                        true,
                        NameRepositoryFixtures.someNameRepository(),
                        new ArrayList<>(),
                        new LinkedHashSet<>());
        final Correspondence someRuleCorrespondence =
                Correspondence.Green(someRuleSourceNode2, someRuleTargetNode1);
        return TripleRuleFixtures.someTripleRule(
                new ArrayList<>(List.of(someRuleSourceNode1, someRuleSourceNode2)),
                new ArrayList<>(List.of(someRuleTargetNode1)),
                new ArrayList<>(List.of(someRuleCorrespondence)));
    }

    public void assertIsTestTripleRule(TripleRule tripleRule) {
        // Assert source nodes
        assertThat(tripleRule.allSourcesAsSlice().nodes()).hasSize(2);

        final TGGNode sourceNode1 =
                tripleRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> node.getId().equals("someOtherRuleNode1"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode1.getId()).isEqualTo("someOtherRuleNode1");
        assertThat(sourceNode1.getType()).isEqualTo(new FQN("Metamodel1", "LocalName1"));
        assertThat(sourceNode1.isGreen()).isFalse();
        assertThat(sourceNode1.links()).isEmpty();
        assertThat(sourceNode1.attributes()).isEmpty();

        final TGGNode sourceNode2 =
                tripleRule.allSourcesAsSlice().nodes().stream()
                        .filter(node -> node.getId().equals("someOtherRuleNode2"))
                        .findFirst()
                        .orElseThrow();
        assertThat(sourceNode2.getId()).isEqualTo("someOtherRuleNode2");
        assertThat(sourceNode2.getType()).isEqualTo(new FQN("Metamodel2", "LocalName2"));
        assertThat(sourceNode2.isGreen()).isTrue();
        assertThat(sourceNode2.links()).hasSize(1);
        assertThat(sourceNode2.links().stream().toList().getFirst().getName())
                .isEqualTo("someTestLink");
        assertThat(sourceNode2.links().stream().toList().getFirst().getTarget())
                .isEqualTo(sourceNode1);
        assertThat(sourceNode2.links().stream().toList().getFirst().isGreen()).isTrue();
        assertThat(sourceNode2.attributes()).isEmpty();

        // Assert target nodes
        assertThat(tripleRule.allTargetsAsSlice().nodes()).hasSize(1);

        final TGGNode targetNode1 =
                tripleRule.allTargetsAsSlice().nodes().stream().findFirst().orElseThrow();
        assertThat(targetNode1.getId()).isEqualTo("someOtherRuleTargetNode1");
        assertThat(targetNode1.getType()).isEqualTo(new FQN("Metamodel3", "LocalName3"));
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

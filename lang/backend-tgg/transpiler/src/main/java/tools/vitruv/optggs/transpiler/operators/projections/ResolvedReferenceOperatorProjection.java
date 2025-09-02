package tools.vitruv.optggs.transpiler.operators.projections;

import org.apache.log4j.Logger;
import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.projections.ReferenceOperatorProjection;
import tools.vitruv.optggs.transpiler.operators.ResolvedProjection;
import tools.vitruv.optggs.transpiler.tgg.Link;
import tools.vitruv.optggs.transpiler.tgg.NameRepository;
import tools.vitruv.optggs.transpiler.tgg.Node;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

public class ResolvedReferenceOperatorProjection implements ResolvedProjection {
    private static final Logger log = Logger.getLogger(ResolvedReferenceOperatorProjection.class);
    private final ReferenceOperator referenceOperator;

    public ResolvedReferenceOperatorProjection(ReferenceOperatorProjection projection) {
        this.referenceOperator = projection.referenceOperator();
    }

    @Override
    public void extendRule(TripleRule rule) {
        if (!(referenceOperator instanceof FeatureCall firstOperator)) {
            return;
        }

        var sourceNode = rule.findSourceNodeByType(new FQN("Car")).orElseThrow();
        var targetNode = rule.findTargetNodeByType(new FQN("CarWithWheels")).orElseThrow();

        final Node intermediateReferenceSourceNode =
                Node.Black(
                        firstOperator.getFeatureSimpleName(),
                        new FQN("Axis"),
                        new NameRepository());
        final Link parentToIntermediateSourceLink = Link.Black("axis", intermediateReferenceSourceNode);
        final Node intermediateReferenceChildSourceNode =
            Node.Black(
                firstOperator.getFeatureSimpleName(),
                new FQN("Axis"),
                new NameRepository());
        final Link intermediateToChildLink = Link.Black("wheels", intermediateReferenceChildSourceNode);
        intermediateReferenceSourceNode.addLink(intermediateToChildLink);
        sourceNode.addLink(parentToIntermediateSourceLink);

        final Node childReferenceTargetNode =
            Node.Black(
                "wheels",
                new FQN("Wheel"),
                new NameRepository());
        final Link targetChildNode = Link.Black("wheels", childReferenceTargetNode);
        targetNode.addLink(targetChildNode);

        log.info("Added Tripe rule nodes for " + referenceOperator);


        // TODO: Generate TGG rule matching reference operators
    }

    @Override
    public String toString() {
        return "Π( TODO Reference operator )";
    }

    @Override
    public boolean containedInPrimaryRule() {
        return false;
    }
}

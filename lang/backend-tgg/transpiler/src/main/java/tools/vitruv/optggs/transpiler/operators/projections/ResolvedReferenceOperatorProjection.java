package tools.vitruv.optggs.transpiler.operators.projections;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.optggs.operators.projections.ReferenceOperatorProjection;
import tools.vitruv.optggs.transpiler.operators.ResolvedProjection;
import tools.vitruv.optggs.transpiler.tgg.TripleRule;

public class ResolvedReferenceOperatorProjection implements ResolvedProjection {
    private final ReferenceOperator referenceOperator;

    public ResolvedReferenceOperatorProjection(ReferenceOperatorProjection projection) {
        this.referenceOperator = projection.referenceOperator();
    }

    @Override
    public void extendRule(TripleRule rule) {
        // TODO: Generate TGG rule matching reference operators
    }

    @Override
    public String toString() {
        return "Π( TODO Reference operator )";
    }
}

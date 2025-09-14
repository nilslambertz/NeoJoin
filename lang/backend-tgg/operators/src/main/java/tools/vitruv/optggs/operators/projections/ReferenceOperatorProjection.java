package tools.vitruv.optggs.operators.projections;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.optggs.operators.Projection;

public record ReferenceOperatorProjection(String field, ReferenceOperator referenceOperator)
        implements Projection {
    @Override
    public String toString() {
        return "π( TODO: Reference Operator projection )";
    }
}

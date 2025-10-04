package tools.vitruv.optggs.operators.reference_operator;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;

public record NeojoinReferenceOperator(
        String targetField, String type, ReferenceOperator referenceOperator) {
    @Override
    public String toString() {
        return "π( TODO: Reference Operator projection )";
    }
}

package tools.vitruv.neojoin.expression_parser.model;

public interface TerminalReferenceOperator extends ReferenceOperator {
    // Terminal operator doesn't have a following operator
    default ReferenceOperator getFollowingOperator() {
        return null;
    }

    default void setFollowingOperator(ReferenceOperator followingOperator) {
        // Ignored
    }
}

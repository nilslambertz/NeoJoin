package tools.vitruv.neojoin.reference_operator;

public interface TerminalReferenceOperator extends ReferenceOperator {
    // Terminal operator doesn't have a following operator
    default ReferenceOperator getFollowingOperator() {
        return null;
    }

    default void setFollowingOperator(ReferenceOperator followingOperator) {
        // Ignored
    }
}

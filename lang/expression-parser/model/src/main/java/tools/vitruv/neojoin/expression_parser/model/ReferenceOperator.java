package tools.vitruv.neojoin.expression_parser.model;

import org.jspecify.annotations.Nullable;

public interface ReferenceOperator {
    @Nullable ReferenceOperator getFollowingOperator();

    void setFollowingOperator(ReferenceOperator followingOperator);

    default ReferenceOperator getLastOperatorInChain() {
        ReferenceOperator lastOperator = this;
        while (lastOperator.getFollowingOperator() != null) {
            lastOperator = lastOperator.getFollowingOperator();
        }
        return lastOperator;
    }

    default ReferenceOperator reverse() {
        ReferenceOperator previous = null;
        ReferenceOperator current = this;
        while (current != null) {
            ReferenceOperator nextReferenceOperator = current.getFollowingOperator();
            current.setFollowingOperator(previous);
            previous = current;
            current = nextReferenceOperator;
        }
        return previous;
    }
}

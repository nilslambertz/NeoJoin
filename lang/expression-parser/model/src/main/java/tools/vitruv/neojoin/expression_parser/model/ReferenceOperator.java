package tools.vitruv.neojoin.expression_parser.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ReferenceOperator {
    @Nullable ReferenceOperator getFollowingOperator();

    void setFollowingOperator(ReferenceOperator followingOperator);

    /** Returns the last ReferenceOperator in this chain */
    @NonNull
    default ReferenceOperator getLastOperatorInChain() {
        ReferenceOperator lastOperator = this;
        while (lastOperator.getFollowingOperator() != null) {
            lastOperator = lastOperator.getFollowingOperator();
        }
        return lastOperator;
    }

    /**
     * Reverses the ReferenceOperator-chain.
     *
     * @implNote This operation <b>mutates the original object</b>
     */
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

    /**
     * Removes all intermediate CollectReferences operators
     *
     * @implNote This operation <b>mutates the original object</b>
     */
    default ReferenceOperator removeAllIntermediateCollectors() {
        ReferenceOperator previous = this;
        ReferenceOperator current = previous.getFollowingOperator();
        while (current != null && current.getFollowingOperator() != null) {
            if (current instanceof CollectReferences) {
                previous.setFollowingOperator(current.getFollowingOperator());
            } else {
                previous = current;
            }

            current = current.getFollowingOperator();
        }

        return this;
    }

    /**
     * Adds a CollectReferences operator at the end of the chain if there is not one already
     *
     * @implNote This operation <b>mutates the original object</b>
     */
    default ReferenceOperator addCollectorAtEndIfNotExists() {
        final ReferenceOperator lastOperator = getLastOperatorInChain();
        if (!(lastOperator instanceof CollectReferences)) {
            lastOperator.setFollowingOperator(new CollectReferences());
        }

        return this;
    }
}

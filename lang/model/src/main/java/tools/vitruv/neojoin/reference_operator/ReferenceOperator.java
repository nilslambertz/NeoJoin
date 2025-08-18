package tools.vitruv.neojoin.reference_operator;

import org.jspecify.annotations.Nullable;

public interface ReferenceOperator {
    @Nullable ReferenceOperator getFollowingOperator();

    void setFollowingOperator(ReferenceOperator followingOperator);
}

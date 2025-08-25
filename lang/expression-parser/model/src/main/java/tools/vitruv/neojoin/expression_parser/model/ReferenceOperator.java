package tools.vitruv.neojoin.expression_parser.model;

import org.jspecify.annotations.Nullable;

public interface ReferenceOperator {
    @Nullable
    ReferenceOperator getFollowingOperator();

    void setFollowingOperator(ReferenceOperator followingOperator);
}

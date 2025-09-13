package tools.vitruv.neojoin.expression_parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
public class FeatureCall implements ReferenceOperator {
    String parentSimpleName;
    String parentIdentifier;

    String featureClassSimpleName;
    String featureSimpleName;

    @Nullable ReferenceOperator followingOperator;

    @Override
    public @Nullable ReferenceOperator getFollowingOperator() {
        return followingOperator;
    }
}

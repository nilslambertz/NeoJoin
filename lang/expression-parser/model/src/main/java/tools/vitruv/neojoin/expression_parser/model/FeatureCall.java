package tools.vitruv.neojoin.expression_parser.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

/** A FeatureCall is the first operation in a reference chain and defines the root type */
@Data
@RequiredArgsConstructor
public class FeatureCall implements ReferenceOperator {
    @Nullable final String identifier;
    @Nullable final String simpleName;

    @Nullable ReferenceOperator followingOperator;

    public static FeatureCall empty() {
        return new FeatureCall(null, null);
    }
}

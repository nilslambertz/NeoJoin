package tools.vitruv.neojoin.expression_parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
public class ToList implements ReferenceOperator {
    @Nullable ReferenceOperator followingOperator;

    @Override
    public @Nullable ReferenceOperator getFollowingOperator() {
        return followingOperator;
    }

    public static ToList empty() {
        return new ToList(null);
    }
}

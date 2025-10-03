package tools.vitruv.neojoin.expression_parser.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Data
@RequiredArgsConstructor
public class FlatMap implements ReferenceOperator {
    @NonNull final FeatureInformation featureInformation;

    @Nullable ReferenceOperator followingOperator;
}

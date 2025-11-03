package tools.vitruv.neojoin.expression_parser.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

@Data
@RequiredArgsConstructor
public class MapAny implements ReferenceOperator {
    @Nullable ReferenceOperator followingOperator;
}

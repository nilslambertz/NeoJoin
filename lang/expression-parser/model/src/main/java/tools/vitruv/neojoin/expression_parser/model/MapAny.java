package tools.vitruv.neojoin.expression_parser.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

// WIP, required information must be defined
@Data
@RequiredArgsConstructor
public class MapAny implements ReferenceOperator {
    @Nullable ReferenceOperator followingOperator;
}

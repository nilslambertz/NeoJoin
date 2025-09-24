package tools.vitruv.neojoin.expression_parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
public class Map implements ReferenceOperator {
    String referenceSimpleName;
    String referenceIdentifier;

    @Nullable ReferenceOperator followingOperator;
}

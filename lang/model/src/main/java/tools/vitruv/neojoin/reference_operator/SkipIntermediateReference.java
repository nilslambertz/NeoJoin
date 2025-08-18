package tools.vitruv.neojoin.reference_operator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import org.jspecify.annotations.Nullable;

import java.util.List;

@Data
@AllArgsConstructor
public class SkipIntermediateReference implements ReferenceOperator {
    String parentClass;
    List<IntermediateReferenceInformation> intermediateReferenceInformation;
    String childReference;

    @Nullable ReferenceOperator followingOperator;

    @Override
    public @Nullable ReferenceOperator getFollowingOperator() {
        return followingOperator;
    }

    @Value
    public static class IntermediateReferenceInformation {
        String skippedReference;
        String childClass;
    }
}

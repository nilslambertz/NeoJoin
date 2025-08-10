package tools.vitruv.neojoin.reference_operator;

import lombok.Value;
import org.jspecify.annotations.Nullable;

@Value
public class SkipIntermediateReference implements ReferenceOperator {
	String parentClass;
	String skippedParentReference;
	String childClass;
	String childReference;

	@Nullable
	ReferenceOperator followingOperator;

	@Override
	public @Nullable ReferenceOperator getFollowingOperator() {
		return followingOperator;
	}
}

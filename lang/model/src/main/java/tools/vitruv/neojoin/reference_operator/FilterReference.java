package tools.vitruv.neojoin.reference_operator;

import lombok.Value;
import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.Nullable;

@Value
public class FilterReference implements ReferenceOperator {
	String parentClass;
	String parentReference;
	// TODO
	XExpression filterExpression;

	@Nullable
	ReferenceOperator followingOperator;

	@Override
	public @Nullable ReferenceOperator getFollowingOperator() {
		return null;
	}
}

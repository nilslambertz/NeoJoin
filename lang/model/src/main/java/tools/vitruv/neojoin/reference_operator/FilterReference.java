package tools.vitruv.neojoin.reference_operator;

import lombok.Data;

import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.Nullable;

@Data
public class FilterReference implements ReferenceOperator {
    String parentClass;
    String parentReference;
    // TODO
    XExpression filterExpression;

    @Nullable ReferenceOperator followingOperator;

    @Override
    public @Nullable ReferenceOperator getFollowingOperator() {
        return null;
    }
}

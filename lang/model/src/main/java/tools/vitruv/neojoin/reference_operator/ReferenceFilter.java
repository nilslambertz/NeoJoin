package tools.vitruv.neojoin.reference_operator;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.eclipse.xtext.xbase.XExpression;
import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
public class ReferenceFilter implements ReferenceOperator {
    String parentIdentifier;
    String parentReference;
    // TODO
    XExpression filterExpression;

    @Nullable ReferenceOperator followingOperator;

    @Override
    public @Nullable ReferenceOperator getFollowingOperator() {
        return null;
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XClosure;
import org.eclipse.xtext.xbase.XExpression;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClosureUtils {
    public static Optional<XClosure> asClosure(XExpression expression) {
        return CastingUtils.cast(expression, XClosure.class);
    }

    public static Optional<XExpression> getExpression(XClosure closure) {
        return Optional.ofNullable(closure).map(XClosure::getExpression);
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CastingUtils {
    public static <T> Optional<T> cast(Object object, Class<T> clazz) {
        return Optional.ofNullable(object).filter(clazz::isInstance).map(clazz::cast);
    }
}

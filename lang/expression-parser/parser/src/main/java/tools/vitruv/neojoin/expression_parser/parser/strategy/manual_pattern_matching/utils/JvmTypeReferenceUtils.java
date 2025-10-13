package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.JvmTypeReference;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JvmTypeReferenceUtils {
    public static Optional<JvmParameterizedTypeReference> asParameterizedTypeReference(
            JvmTypeReference typeReference) {
        return CastingUtils.cast(typeReference, JvmParameterizedTypeReference.class);
    }

    public static boolean hasExactlyOneArgument(JvmParameterizedTypeReference typeReference) {
        return Optional.ofNullable(typeReference)
                .map(JvmParameterizedTypeReference::getArguments)
                .map(args -> args.size() == 1)
                .orElse(false);
    }

    public static Optional<JvmTypeReference> getFirstArgument(
            JvmParameterizedTypeReference typeReference) {
        return Optional.ofNullable(typeReference)
                .map(JvmParameterizedTypeReference::getArguments)
                .filter(args -> !args.isEmpty())
                .map(List::getFirst);
    }
}

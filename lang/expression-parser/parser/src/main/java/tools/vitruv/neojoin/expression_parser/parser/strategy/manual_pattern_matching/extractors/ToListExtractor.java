package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.ToList;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmToListUtils;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToListExtractor {
    public static Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression) {
        return JvmFeatureCallUtils.asMemberFeatureCall(expression)
                .filter(JvmToListUtils::isToListOperation)
                .flatMap(JvmFeatureCallUtils::getNextMemberCallTarget)
                .map(
                        nextCallTarget ->
                                new ReferenceOperatorWithNextCallTarget(
                                        ToList.empty(), nextCallTarget));
    }
}

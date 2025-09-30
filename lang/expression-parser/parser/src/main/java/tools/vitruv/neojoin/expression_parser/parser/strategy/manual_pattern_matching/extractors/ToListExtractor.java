package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.ToList;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmToListUtils;

import java.util.Optional;

public class ToListExtractor implements ReferenceOperatorExtractor<ToList> {
    public Optional<ReferenceOperatorWithNextFeatureCall<ToList>> extract(XExpression expression) {
        return JvmFeatureCallUtils.asMemberFeatureCall(expression)
                .filter(JvmToListUtils::isToListOperation)
                .flatMap(JvmFeatureCallUtils::getNextMemberCallTarget)
                .map(
                        nextCallTarget ->
                                new ReferenceOperatorWithNextFeatureCall<>(
                                        new ToList(), nextCallTarget));
    }
}

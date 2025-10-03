package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.Map;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.BlockExpressionUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.ClosureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureCallUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFeatureUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmFieldUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMapUtils;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.utils.JvmMemberCallUtils;

import java.util.Optional;

public class MapExtractor implements ReferenceOperatorExtractor<Map> {
    public Optional<ReferenceOperatorWithNextFeatureCall<Map>> extract(XExpression expression) {
        XAbstractFeatureCall nextMemberCallTarget =
                Optional.ofNullable(expression)
                        .flatMap(JvmFeatureCallUtils::getNextMemberCallTarget)
                        .orElse(null);
        if (nextMemberCallTarget == null) {
            return Optional.empty();
        }

        return JvmFeatureCallUtils.asMemberFeatureCall(expression)
                .filter(JvmMapUtils::isMapOperation)
                .filter(JvmMemberCallUtils::hasExactlyOneMemberCallArgument)
                .flatMap(JvmMemberCallUtils::getFirstArgument)
                .flatMap(ClosureUtils::asClosure)
                .flatMap(ClosureUtils::getExpression)
                .flatMap(BlockExpressionUtils::asBlockExpression)
                .filter(BlockExpressionUtils::hasExactlyOneExpression)
                .flatMap(BlockExpressionUtils::getFirstExpression)
                .flatMap(JvmFeatureCallUtils::asMemberFeatureCall)
                .flatMap(JvmFeatureUtils::getFeature)
                .flatMap(JvmFieldUtils::asJvmField)
                .flatMap(JvmFieldUtils::getData)
                .map(
                        fieldData ->
                                new ReferenceOperatorWithNextFeatureCall<Map>(
                                        new Map(fieldData.toFeatureInformation()),
                                        nextMemberCallTarget));
    }
}

package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextFeatureCall;

import java.util.Optional;

public interface ReferenceOperatorExtractor<T extends ReferenceOperator> {
   Optional<ReferenceOperatorWithNextFeatureCall<T>> extract(XExpression expression);
}

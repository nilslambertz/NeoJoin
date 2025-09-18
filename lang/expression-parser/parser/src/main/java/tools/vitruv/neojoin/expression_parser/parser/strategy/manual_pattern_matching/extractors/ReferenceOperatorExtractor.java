package tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.extractors;

import org.eclipse.xtext.xbase.XExpression;

import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.strategy.manual_pattern_matching.model.ReferenceOperatorWithNextCallTarget;

import java.util.Optional;

public interface ReferenceOperatorExtractor {
   Optional<ReferenceOperatorWithNextCallTarget> extract(XExpression expression);
}

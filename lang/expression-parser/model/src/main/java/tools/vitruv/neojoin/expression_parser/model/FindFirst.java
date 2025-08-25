package tools.vitruv.neojoin.expression_parser.model;

import lombok.Value;

import org.eclipse.xtext.xbase.XExpression;

@Value
public class FindFirst implements TerminalReferenceOperator {
    String parentClass;
    String parentReference;
    XExpression filterExpression;
}

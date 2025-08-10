package tools.vitruv.neojoin.reference_operator;

import lombok.Value;
import org.eclipse.xtext.xbase.XExpression;

@Value
public class FindFirst implements TerminalReferenceOperator {
	String parentClass;
	String parentReference;
	XExpression filterExpression;
}

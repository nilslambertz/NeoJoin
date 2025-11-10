@import tools.vitruv.optggs.transpiler.graph.Attribute
@import tools.vitruv.optggs.transpiler.graph.TGGNode
@param TGGNode node
@param Attribute attribute
.${attribute.name()} @template.LogicOperator(operator = attribute.operator(), node = node) @template.ValueExpression(expression = attribute.value())

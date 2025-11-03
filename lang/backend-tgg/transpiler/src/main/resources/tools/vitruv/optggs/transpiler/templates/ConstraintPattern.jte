@import tools.vitruv.optggs.transpiler.NameResolver
@import tools.vitruv.optggs.transpiler.tgg.GraphConstraint
@import tools.vitruv.optggs.transpiler.tgg.Node
@param GraphConstraint constraint
@param NameResolver nameResolver
constraint ${nameResolver.resolveConstraintName(constraint)} = ${nameResolver.resolvePatternName(constraint)}
pattern ${nameResolver.resolvePatternName(constraint)} {
    @for(Node node: constraint.getNodes())@template.PatternNode(node = node, nameResolver = nameResolver)
    @endfor
}

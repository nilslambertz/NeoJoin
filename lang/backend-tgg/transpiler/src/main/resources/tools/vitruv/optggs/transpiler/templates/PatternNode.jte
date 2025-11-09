@import tools.vitruv.optggs.transpiler.NameResolver
@import tools.vitruv.optggs.transpiler.graph.Attribute
@import tools.vitruv.optggs.transpiler.graph.Link
@import tools.vitruv.optggs.transpiler.graph.Node
@import tools.vitruv.optggs.transpiler.graph.Property
@param Node node
@param NameResolver nameResolver
${node.getId()}: ${nameResolver.resolveName(node.getType())} {
@for(Property prop : node.getProperties())${prop.toExpression(node)}
@endfor
@for(Attribute attr : node.attributes())@template.Attribute(node = node, attribute = attr)
@endfor
@for(Link link : node.links())@template.PatternLink(link = link)
@endfor
}

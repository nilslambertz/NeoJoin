@import tools.vitruv.optggs.transpiler.NameResolver
@import tools.vitruv.optggs.transpiler.graph.Attribute
@import tools.vitruv.optggs.transpiler.graph.Property
@import tools.vitruv.optggs.transpiler.graph.pattern.PatternLink;import tools.vitruv.optggs.transpiler.graph.pattern.PatternNode
@param PatternNode node
@param NameResolver nameResolver
${node.getId()}: ${nameResolver.resolveName(node.getType())} {
    @for(Property prop : node.getProperties())${prop.toExpression(false)}
    @endfor

    @for(Attribute attr : node.attributes())@template.Attribute(node = node, attribute = attr)
    @endfor

    @for(PatternLink link : node.links())@template.PatternLink(link = link)
    @endfor
}

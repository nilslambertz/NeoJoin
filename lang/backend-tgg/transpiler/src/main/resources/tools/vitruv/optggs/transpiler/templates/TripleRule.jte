@import gg.jte.support.ForSupport
@import tools.vitruv.optggs.transpiler.NameResolver
@import tools.vitruv.optggs.transpiler.graph.tgg.AttributeConstraint
@import tools.vitruv.optggs.transpiler.graph.tgg.Correspondence
@import tools.vitruv.optggs.transpiler.graph.TripleRule
@import tools.vitruv.optggs.transpiler.graph.Node
@param TripleRule rule
@param String schema
@param NameResolver nameResolver
tripleRule ${nameResolver.resolveRuleName(rule)} : ${schema} {
    source {
    @for(Node node: rule.allSourcesAsSlice().nodes())@template.Node(node = node, nameResolver = nameResolver)
    @endfor
    }
    target {
    @for(Node node: rule.allTargetsAsSlice().nodes())@template.Node(node = node, nameResolver = nameResolver)
    @endfor
    }
    correspondence {
    @for(Correspondence corr: rule.correspondences())@if(corr.isGreen())++@endif ${corr.getSource().getId()} <- :${nameResolver.resolveCorrespondenceName(corr)} -> ${corr.getTarget().getId()}
    @endfor
    }
    attributeConstraints {
    @for(AttributeConstraint constraint: rule.constraints())${constraint.getName()}(
        @for(var param: ForSupport.of(constraint.parameters())) ${param.get().attribute()} = @template.ValueExpression(expression=param.get().value()) @if(!param.isLast()),@endif@endfor
        )
    @endfor
    }
}

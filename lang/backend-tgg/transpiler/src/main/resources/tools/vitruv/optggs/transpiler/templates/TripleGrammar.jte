@import tools.vitruv.optggs.transpiler.NameResolver
@import tools.vitruv.optggs.transpiler.graph.GraphConstraint
@import tools.vitruv.optggs.transpiler.graph.TripleGrammar
@import tools.vitruv.optggs.transpiler.graph.TripleRule
@param TripleGrammar grammar
@param NameResolver nameResolver
import "platform:/resource/${grammar.getName()}/src/SourceMetamodel.msl"
import "platform:/resource/${grammar.getName()}/src/TargetMetamodel.msl"
import "platform:/resource/${grammar.getName()}/src/AttributeConstraints.msl"
@template.Schema(grammar = grammar, nameResolver = nameResolver)
@for(TripleRule rule : grammar.getRules())@template.TripleRule(rule = rule, schema = grammar.getName(), nameResolver = nameResolver)
@endfor
@for(GraphConstraint constraint : grammar.getConstraints())@template.ConstraintPattern(constraint = constraint, nameResolver = nameResolver)
@endfor

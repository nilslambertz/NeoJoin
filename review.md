# Information for the Code Review

A general overview of the NeoJoin language and features can be found in the [readme](./readme.md). The first version of
the language frontend (IDE support, CLI) and the backend (transformation from queries to low-level operators and Triple
Graph Grammar rules) was already developed during previous thesis.

## The focus of my thesis

My Master's thesis focuses on reference expressions and how these can be parsed and transformed into Triple Graph
Grammar (TGG) rules. As an example, a source (meta)model could be made up of `Car`s, which can have many `Axis`. Each
`Axis` can have multiple `Wheel`s. If we are only interested in `Car`s and their `Wheel`s in the target view and do not
care which `Axis`
they belong to, the following expression could be used:

```
from Car car create CarWithWheels {
    targetWheels := car.axis.flatMap[it.wheels].toList() create TargetWheel {
        targetWheelId = it.wheelId
        it.pressure
    }
}
```

We receive the query expressions (here `car.axis.flatMap[it.wheels].toList()`) from the frontend and need to validate
that these are supported by our TGG backend. Then we need to transform them into a representation that can be used to
generate the corresponding TGG rules for the expression chain.

## Structure of this repository

The entry point for NeoJoin commands is the [CLI](./lang/frontend/cli/src/main/java/tools/vitruv/neojoin/cli/Main.java).
The textual query is transformed into an Abstract Syntax Tree that contains the joins, selections, expressions etc. in a
structured form.

When the TGG transformation is triggered, the [eMSL](https://emoflon.org/neo/)-files are generated from the provided EMF
sources by the [eMSL Utils](./lang/backend-tgg/emsl_utils).

Then, the [ViewExtractor](./lang/backend-tgg/operators/src/main/java/tools/vitruv/optggs/operators/ViewExtractor.java)
converts the frontend AST into backend operators. This is also the place that calls
the [Reference Expression Parser](./lang/expression-parser/parser/src/main/java/tools/vitruv/neojoin/expression_parser/parser/strategy/PatternMatchingStrategy.java).

Afterward, the
query [QueryResolver](./lang/backend-tgg/transpiler/src/main/java/tools/vitruv/optggs/transpiler/QueryResolver.java)
resolves operators and containments and creates an in-memory representation of the Triple Graph Grammar with
the [ResolvedView](./lang/backend-tgg/transpiler/src/main/java/tools/vitruv/optggs/transpiler/operators/ResolvedView.java).
This TGG is then printed by
the [TripleGraphPrinter](./lang/backend-tgg/transpiler/src/main/java/tools/vitruv/optggs/transpiler/TripleGraphPrinter.java)
using templates.

## My contributions and suggested review order

This section describes my contributions so far and a suggested review order.

### eMSL Utils

The [eMSL Utils](./lang/backend-tgg/emsl_utils) are needed to generate (meta)models in eMSL syntax. The templates are
derived from
the [official EMF to eMSL transformator](https://github.com/eMoflon/emoflon-neo/blob/master/org.emoflon.neo.emf/src/org/emoflon/neo/emf/EMFImporter.xtend).

### Reference Operators and Parser

The Reference operators are implemented as a Reference operator chain. Each operator contains the information necessary
for the following TGG transformation steps and (possibly) a following operator. I suggest the following
review order:

1. Take a look at
   the [Reference operator model](./lang/expression-parser/model/src/main/java/tools/vitruv/neojoin/expression_parser/model)
   package. This contains the supported operators
2. Check
   the [ManualPatternMatchingStrategy](./lang/expression-parser/parser/src/main/java/tools/vitruv/neojoin/expression_parser/parser/strategy/manual_pattern_matching/ManualPatternMatchingStrategy.java).
   This strategy parses the Xbase expressions using pure Java.
3. Review the
   individual [parsers](./lang/expression-parser/parser/src/main/java/tools/vitruv/neojoin/expression_parser/parser/strategy/manual_pattern_matching/parsers).
   The base idea is always:
    - The parser returns an `Optional` that is present, which the expression can be parsed
    - The parser checks that the type and argument(s) of the expression matches the required format
    - The parser flattens the arguments (e.g. inside a `.map()` operation, there can be another reference chain). All
      nested arguments are moved/transformed so that only one "top-level" expression chain is left
    - If the type matches the operator, but arguments or metadata are missing/not supported,
      an [Exception](./lang/expression-parser/parser/src/main/java/tools/vitruv/neojoin/expression_parser/parser/exception/UnsupportedReferenceExpressionException.java)
      is thrown.
4. Now check
   the [ViewExtractor](./lang/backend-tgg/operators/src/main/java/tools/vitruv/optggs/operators/ViewExtractor.java), as
   this is the place which connects the frontend to the backend and which calls the reference expression parser.

### TGG project generation

1. [TranspilerQueryResolver.resolveReferenceOperatorChain](./lang/backend-tgg/transpiler/src/main/java/tools/vitruv/optggs/transpiler/TranspilerQueryResolver.java)

## Building and running the project

The readme contains information on how to [build](./readme.md#build) and [use](./readme.md#usage) the project.

## Libraries

- [jte: Java Template Engine](https://jte.gg/) for creating eMSL (meta)models and the TGG Project
- [Lombok](https://projectlombok.org/) to generate boilerplate methods (getters, setters, constructors)
- [Xtext/Xbase](https://eclipse.dev/Xtext/documentation/305_xbase.html#xbase-language-ref-introduction) as the
  expression language (the expression language we need to parse for our Reference operators)
- [eMoflon::Neo](https://emoflon.org/neo/) as the TGG engine

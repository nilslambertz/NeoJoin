package tools.vitruv.optggs.operators;

import tools.vitruv.neojoin.aqr.*;
import tools.vitruv.optggs.operators.selection.Pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ViewExtractor {
    public static View viewFromAQR(AQR aqr) {
        final View view = new View();

        for (var targetClass : aqr.classes()) {
            view.addQuery(queryFromTargetClass(targetClass));
        }

        return view;
    }

    private static Query queryFromTargetClass(AQRTargetClass targetClass) {
        final FQN source = Optional.ofNullable(targetClass.source()).map(AQRSource::from).map(ViewExtractor::fqn).orElse(fqn(targetClass.name()));
        final FQN target = fqn(targetClass.name());

        final Map<String, FQN> namedRefs = new HashMap<>();
        namedRefs.put("", source);
        namedRefs.put("self", source);

        Pattern sourcePattern = Pattern.from(source);
        final List<AQRJoin> joins = Optional.ofNullable(targetClass.source()).map(AQRSource::joins).orElse(List.of());
        for (var join : joins) {
            sourcePattern = joinFromAST(join, sourcePattern, namedRefs);
        }
        var query = new Query(new Selection(sourcePattern, Pattern.from(target)));

        // TODO: Filters???
        if (targetClass.source() != null && targetClass.source().condition() != null) {
            throw new RuntimeException("Condition expressions are not supported");
        }

        applyProjectionsFromAST(targetClass.features(), query, namedRefs, target);

        if (targetClass.source() != null && !targetClass.source().groupingExpressions().isEmpty()) {
            throw new RuntimeException("Grouping expressions are not supported");
        }

        return query;
    }

    private static Pattern joinFromAST(AQRJoin join, Pattern pattern, Map<String, FQN> namedRefs) {
        var fqn = fqn(join.from());
        Optional<String> joinFromAlias = Optional.ofNullable(join.from().alias()).filter(alias -> !alias.isEmpty());
        joinFromAlias.ifPresent(s -> namedRefs.put(s, fqn));

        if (join.type() == AQRJoin.Type.Inner) {
            var attributes = join.featureConditions().stream().map(AQRJoin.FeatureCondition::features).flatMap(List::stream).map(attr -> new Tuple<>(attr, attr)).toList();
            return pattern.join(fqn, attributes);
        }
        // TODO: Other join types

        return pattern;
    }

    private static void applyProjectionsFromAST(List<AQRFeature> features, Query query, Map<String, FQN> namedRefs, FQN target) {
        for (AQRFeature feature : features) {
            // TODO: Expressions, calculations, references??
            if (feature instanceof AQRFeature.Attribute && feature.kind() instanceof AQRFeature.Kind.Copy) {
                query.project(feature.name());
            } else if (feature instanceof AQRFeature.Reference) {
                // TODO: Reference operators
            } else {
                throw new RuntimeException("Invalid projection: " + feature);
            }
        }
    }

    private static FQN fqn(AQRFrom from) {
        return fqn(from.clazz().getName());
    }

    private static FQN fqn(String name) {
        if (name.contains(".")) {
            var splitted = name.split("\\.");
            return new FQN(splitted[0], splitted[1]);
        } else {
            return new FQN(name);
        }
    }
}

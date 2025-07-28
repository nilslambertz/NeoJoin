package tools.vitruv.optggs.operators;

import tools.vitruv.neojoin.aqr.*;
import tools.vitruv.optggs.operators.selection.Pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewExtractor {
    public static View viewFromAQR(AQR aqr) {
        final View view = new View();

        for (var targetClass : aqr.classes()) {
            view.addQuery(queryFromTargetClass(targetClass));
        }

        return view;
    }

    private static Query queryFromTargetClass(AQRTargetClass targetClass) {
        final FQN source = fqn(targetClass.source().from());
        // TODO: How to get target fqn??
        final FQN target = fqn(targetClass.name());

        final Map<String, FQN> namedRefs = new HashMap<>();
        namedRefs.put("", source);
        namedRefs.put("self", source);

        Pattern sourcePattern = Pattern.from(source);
        for (var join : targetClass.source().joins()) {
            sourcePattern = joinFromAST(join, sourcePattern, namedRefs);
        }
        var query = new Query(new Selection(sourcePattern, Pattern.from(target)));
        // TODO: Filters???
        applyProjectionsFromAST(targetClass.features(), query, namedRefs, target);

        return query;
    }

    private static Pattern joinFromAST(AQRJoin join, Pattern pattern, Map<String, FQN> namedRefs) {
        var fqn = fqn(join.from());
        if (!join.from().alias().isEmpty()) {
            namedRefs.put(join.from().alias(), fqn);
        }

        if (join.type() == AQRJoin.Type.Inner) {
            var attributes = join.featureConditions().stream().flatMap(feature -> feature.features().stream()).map(attr -> new Tuple<>(attr, attr)).toList();
            return pattern.join(fqn, attributes);
        }
        // TODO: Other join types

        return pattern;
    }

    private static void applyProjectionsFromAST(List<AQRFeature> features, Query query, Map<String, FQN> namedRefs, FQN target) {
        for (AQRFeature feature : features) {
            // TODO: Expressions, calculations, references??
            if (feature instanceof AQRFeature.Attribute) {
                query.project(feature.name());
            } else if (feature instanceof AQRFeature.Reference) {

            } else {
                throw new RuntimeException("Invalid feature: " + feature);
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

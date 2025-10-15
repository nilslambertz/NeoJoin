package tools.vitruv.optggs.operators;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.jspecify.annotations.NonNull;

import tools.vitruv.neojoin.aqr.*;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.neojoin.expression_parser.parser.exception.UnsupportedReferenceExpressionException;
import tools.vitruv.neojoin.expression_parser.parser.strategy.PatternMatchingStrategy;
import tools.vitruv.optggs.operators.selection.Pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ViewExtractor {
    private static final Logger log = Logger.getLogger(ViewExtractor.class);

    public static View viewFromAQR(
            AQR aqr, @NonNull PatternMatchingStrategy patternMatchingStrategy)
            throws UnsupportedReferenceExpressionException {
        final View view = new View();

        // Add queries for all classes except the Root class
        for (var targetClass : aqr.classesWithoutRoot()) {
            view.addQuery(
                    queryFromTargetClass(
                            aqr.export().name(), targetClass, patternMatchingStrategy));
        }

        return view;
    }

    private static Query queryFromTargetClass(
            String targetNamespace,
            AQRTargetClass targetClass,
            @NonNull PatternMatchingStrategy patternMatchingStrategy)
            throws UnsupportedReferenceExpressionException {
        final FQN source =
                Optional.ofNullable(targetClass.source())
                        .map(AQRSource::from)
                        .map(ViewExtractor::fqn)
                        .orElse(fqn(targetClass.name()));
        final FQN targetRoot = new FQN(targetNamespace, targetClass.name());

        final Map<String, FQN> namedRefs = new HashMap<>();
        namedRefs.put("", source);
        namedRefs.put("self", source);

        Pattern sourcePattern = Pattern.from(source);
        final List<AQRJoin> joins =
                Optional.ofNullable(targetClass.source()).map(AQRSource::joins).orElse(List.of());
        for (var join : joins) {
            sourcePattern = joinFromAST(join, sourcePattern, namedRefs);
        }
        var query = new Query(new Selection(sourcePattern, Pattern.from(targetRoot)));

        // TODO: Filters???
        if (targetClass.source() != null && targetClass.source().condition() != null) {
            throw new RuntimeException("Condition expressions are not supported");
        }

        applyProjectionsFromAST(
                targetClass.features(), query, namedRefs, targetRoot, patternMatchingStrategy);

        if (targetClass.source() != null && !targetClass.source().groupingExpressions().isEmpty()) {
            throw new RuntimeException("Grouping expressions are not supported");
        }

        return query;
    }

    private static Pattern joinFromAST(AQRJoin join, Pattern pattern, Map<String, FQN> namedRefs) {
        var fqn = fqn(join.from());
        Optional<String> joinFromAlias =
                Optional.ofNullable(join.from().alias()).filter(alias -> !alias.isEmpty());
        joinFromAlias.ifPresent(s -> namedRefs.put(s, fqn));

        if (join.type() == AQRJoin.Type.Inner) {
            var attributes =
                    join.featureConditions().stream()
                            .map(AQRJoin.FeatureCondition::features)
                            .flatMap(List::stream)
                            .map(attr -> new Tuple<>(attr, attr))
                            .toList();
            return pattern.join(fqn, attributes);
        }
        // TODO: Other join types

        return pattern;
    }

    private static void applyProjectionsFromAST(
            List<AQRFeature> features,
            Query query,
            Map<String, FQN> namedRefs,
            FQN targetRoot,
            @NonNull PatternMatchingStrategy patternMatchingStrategy)
            throws UnsupportedReferenceExpressionException {
        for (AQRFeature feature : features) {
            // TODO: Expressions, calculations, references??
            if (feature instanceof AQRFeature.Attribute
                    && feature.kind() instanceof AQRFeature.Kind.Copy) {
                query.project(feature.name());
            } else if (feature instanceof AQRFeature.Reference reference
                    && reference.kind().expression() != null) {
                // TODO: Make pretty
                final ReferenceOperator operator =
                        patternMatchingStrategy.parseReferenceOperator(
                                reference.kind().expression());
                log.info("Found reference operator: " + operator);
                final String sourceTypeNamespace =
                        Optional.ofNullable(reference.type().source())
                                .map(AQRSource::from)
                                .map(AQRFrom::clazz)
                                .map(EClass::getEPackage)
                                .map(EPackage::getNsPrefix)
                                .orElse(null);
                final FQN targetLeaf = targetRoot.withLocalName(reference.type().name());
                query.referenceOperator(
                        sourceTypeNamespace, targetRoot, targetLeaf, reference.name(), operator);
            } else if (feature instanceof AQRFeature.Reference
                    && feature.kind().expression() == null) {
                log.info("TODO: " + feature);
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

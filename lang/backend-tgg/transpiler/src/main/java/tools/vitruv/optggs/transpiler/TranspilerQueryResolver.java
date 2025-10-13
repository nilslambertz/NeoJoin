package tools.vitruv.optggs.transpiler;

import tools.vitruv.neojoin.expression_parser.model.CollectReferences;
import tools.vitruv.neojoin.expression_parser.model.FeatureCall;
import tools.vitruv.neojoin.expression_parser.model.FeatureInformation;
import tools.vitruv.neojoin.expression_parser.model.FlatMap;
import tools.vitruv.neojoin.expression_parser.model.Map;
import tools.vitruv.neojoin.expression_parser.model.MemberFeatureCall;
import tools.vitruv.neojoin.expression_parser.model.ReferenceOperator;
import tools.vitruv.optggs.operators.FQN;
import tools.vitruv.optggs.operators.Filter;
import tools.vitruv.optggs.operators.Projection;
import tools.vitruv.optggs.operators.Query;
import tools.vitruv.optggs.operators.Selection;
import tools.vitruv.optggs.operators.filters.ConstantFilter;
import tools.vitruv.optggs.operators.filters.FunctionFilter;
import tools.vitruv.optggs.operators.projections.DerivedProjection;
import tools.vitruv.optggs.operators.projections.SimpleProjection;
import tools.vitruv.optggs.operators.reference_operator.NeojoinReferenceOperator;
import tools.vitruv.optggs.operators.selection.From;
import tools.vitruv.optggs.operators.selection.Join;
import tools.vitruv.optggs.operators.selection.PatternLink;
import tools.vitruv.optggs.operators.selection.Ref;
import tools.vitruv.optggs.operators.selection.ThetaJoin;
import tools.vitruv.optggs.transpiler.operators.ResolvedContainment;
import tools.vitruv.optggs.transpiler.operators.ResolvedFilter;
import tools.vitruv.optggs.transpiler.operators.ResolvedLink;
import tools.vitruv.optggs.transpiler.operators.ResolvedProjection;
import tools.vitruv.optggs.transpiler.operators.ResolvedQuery;
import tools.vitruv.optggs.transpiler.operators.ResolvedSelection;
import tools.vitruv.optggs.transpiler.operators.ResolvedView;
import tools.vitruv.optggs.transpiler.operators.filters.ResolvedConstantFilter;
import tools.vitruv.optggs.transpiler.operators.filters.ResolvedFunctionFilter;
import tools.vitruv.optggs.transpiler.operators.patterns.ResolvedFrom;
import tools.vitruv.optggs.transpiler.operators.patterns.ResolvedJoin;
import tools.vitruv.optggs.transpiler.operators.patterns.ResolvedPattern;
import tools.vitruv.optggs.transpiler.operators.patterns.ResolvedPatternLink;
import tools.vitruv.optggs.transpiler.operators.patterns.ResolvedRef;
import tools.vitruv.optggs.transpiler.operators.patterns.ResolvedThetaJoin;
import tools.vitruv.optggs.transpiler.operators.projections.ResolvedDerivedProjection;
import tools.vitruv.optggs.transpiler.operators.projections.ResolvedSimpleProjection;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedCollectReferences;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedFeatureCall;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedFlatMap;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedMap;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedMemberFeatureCall;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedReferenceOperator;
import tools.vitruv.optggs.transpiler.operators.reference_operators.ResolvedReferenceOperatorChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TranspilerQueryResolver
        extends QueryResolver<
                ResolvedView,
                ResolvedQuery,
                ResolvedSelection,
                ResolvedProjection,
                ResolvedReferenceOperatorChain,
                ResolvedFilter,
                ResolvedContainment,
                ResolvedLink,
                ResolvedPattern,
                ResolvedPatternLink> {

    @Override
    ResolvedView createView(List<ResolvedQuery> queries) {
        return new ResolvedView(queries);
    }

    @Override
    public ResolvedQuery resolveQuery(
            Query query, Optional<ResolvedContainment> containment, List<ResolvedLink> links) {
        var selection = resolveSelection(query.selection());
        var projections = query.projections().stream().map(this::resolveProjection).toList();
        var referenceOperators =
                query.referenceOperators().stream()
                        .map(this::resolveReferenceOperatorChain)
                        .toList();
        var filters = query.filters().stream().map(this::resolveFilter).toList();
        return new ResolvedQuery(
                selection, projections, referenceOperators, filters, containment, links);
    }

    @Override
    public ResolvedSelection resolveSelection(Selection selection) {
        return new ResolvedSelection(
                resolvePattern(selection.source()), resolvePattern(selection.target()));
    }

    @Override
    public ResolvedProjection resolveProjection(Projection projection) {
        if (projection instanceof SimpleProjection sp) {
            return new ResolvedSimpleProjection(
                    resolvePattern(sp.source()),
                    sp.target(),
                    sp.sourceProperty(),
                    sp.targetProperty());
        } else if (projection instanceof DerivedProjection dp) {
            return new ResolvedDerivedProjection(dp);
        } else {
            throw new RuntimeException("Unknown projection type while resolving");
        }
    }

    @Override
    ResolvedReferenceOperatorChain resolveReferenceOperatorChain(
            NeojoinReferenceOperator referenceOperator) {
        final List<ResolvedReferenceOperator> referenceOperatorChain = new ArrayList<>();

        ReferenceOperator currentReferenceOperator = referenceOperator.referenceOperator();
        while (currentReferenceOperator != null) {
            final ResolvedReferenceOperator resolvedOperator;
            if (currentReferenceOperator instanceof FeatureCall featureCall) {
                resolvedOperator = new ResolvedFeatureCall(new FQN(featureCall.getSimpleName()));
            } else if (currentReferenceOperator instanceof MemberFeatureCall memberFeatureCall) {
                final FeatureInformation featureInformation =
                        memberFeatureCall.getFeatureInformation();
                resolvedOperator =
                        new ResolvedMemberFeatureCall(
                                featureInformation.getFeatureName(),
                                new FQN(featureInformation.getFeatureClassSimpleName()),
                                memberFeatureCall.isCollection());
            } else if (currentReferenceOperator instanceof Map map) {
                final FeatureInformation featureInformation = map.getFeatureInformation();
                resolvedOperator =
                        new ResolvedMap(
                                featureInformation.getFeatureName(),
                                new FQN(featureInformation.getFeatureClassSimpleName()));
            } else if (currentReferenceOperator instanceof FlatMap flatMap) {
                final FeatureInformation featureInformation = flatMap.getFeatureInformation();
                resolvedOperator =
                        new ResolvedFlatMap(
                                featureInformation.getFeatureName(),
                                new FQN(featureInformation.getFeatureClassSimpleName()));
            } else if (currentReferenceOperator instanceof CollectReferences) {
                resolvedOperator = new ResolvedCollectReferences();
            } else {
                throw new IllegalStateException("Unsupported reference operator chain");
            }
            referenceOperatorChain.add(resolvedOperator);

            currentReferenceOperator = currentReferenceOperator.getFollowingOperator();
        }

        return new ResolvedReferenceOperatorChain(
                referenceOperatorChain,
                referenceOperator.targetField(),
                new FQN(referenceOperator.type()));
    }

    @Override
    public ResolvedFilter resolveFilter(Filter filter) {
        if (filter instanceof ConstantFilter cf) {
            return new ResolvedConstantFilter(cf);
        } else if (filter instanceof FunctionFilter ff) {
            return new ResolvedFunctionFilter(ff);
        } else {
            throw new RuntimeException("Unknown filter type while resolving");
        }
    }

    @Override
    public ResolvedContainment createContainment(
            ResolvedPattern source, ResolvedPattern target, List<ResolvedFilter> filters) {
        return new ResolvedContainment(source, target, filters);
    }

    @Override
    public ResolvedLink createLink(
            ResolvedPattern source, ResolvedPattern target, List<ResolvedFilter> filters) {
        return new ResolvedLink(source, target, filters);
    }

    @Override
    ResolvedPattern createPattern(List<ResolvedPatternLink> links) {
        return new ResolvedPattern(links);
    }

    @Override
    ResolvedPatternLink resolvePatternLink(PatternLink patternLink) {
        if (patternLink instanceof From f) {
            return new ResolvedFrom(f.element());
        } else if (patternLink instanceof Join j) {
            return new ResolvedJoin(j.element(), j.constrainedProperties());
        } else if (patternLink instanceof ThetaJoin tj) {
            return new ResolvedThetaJoin(tj.element(), tj.function());
        } else if (patternLink instanceof Ref r) {
            return new ResolvedRef(r.element(), r.reference());
        } else {
            throw new RuntimeException("Unknown pattern type while resolving");
        }
    }
}

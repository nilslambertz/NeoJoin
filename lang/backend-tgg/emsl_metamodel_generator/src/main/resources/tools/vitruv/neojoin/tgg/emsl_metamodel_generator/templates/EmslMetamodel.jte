@import org.eclipse.emf.ecore.EAttribute
@import org.eclipse.emf.ecore.EClass
@import org.eclipse.emf.ecore.EReference
@import java.util.stream.Collectors
@param org.eclipse.emf.ecore.EPackage ePackage

!{var eClasses = ePackage.getEClassifiers().stream().filter(EClass.class::isInstance).map(EClass.class::cast).toList();}

metamodel ${ePackage.getName()} {
@for(EClass eclass : eClasses)
    !{var superTypeNames = eclass.getESuperTypes().stream().map(EClass::getName).collect(Collectors.joining(", "));}

    ${eclass.getName()} @if(!superTypeNames.isEmpty()) : ${superTypeNames} @endif {
    @for(EAttribute attr : eclass.getEAttributes())
        .${attr.getName()} : ${attr.getEType().getName()}
    @endfor

    @for(EReference ref : eclass.getEReferences())
        !{
            var refLowerBound = ref.getLowerBound();
            String refUpperBound = ref.getUpperBound() == -1 ? "*" : ref.getUpperBound() + "";
            String boundsString = refLowerBound + ".." + refUpperBound;
            boolean isContainment = ref.isContainment();
            String operatorPrefix = isContainment ? "<>" : "";
        }
        ${operatorPrefix}-${ref.getName()}(${boundsString})->${ref.getEType().getName()}
    @endfor
    }
@endfor
}

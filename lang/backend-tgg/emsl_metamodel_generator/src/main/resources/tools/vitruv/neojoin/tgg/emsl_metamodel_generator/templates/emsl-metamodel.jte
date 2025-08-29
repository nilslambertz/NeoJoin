@import org.eclipse.emf.ecore.EAttribute
@import org.eclipse.emf.ecore.EClass
@import org.eclipse.emf.ecore.EPackage
@import org.eclipse.emf.ecore.EReference
@import org.eclipse.emf.ecore.resource.Resource
@import java.util.stream.Collectors
@param org.eclipse.emf.ecore.resource.ResourceSet rs

@for(Resource r : rs.getResources())
    @for(EPackage p : r.getContents().stream().filter(EPackage.class::isInstance).map(EPackage.class::cast).toList())
        metamodel ${p.getName()} {
        @for(EClass eclass : p.getEClassifiers().stream().filter(EClass.class::isInstance).map(EClass.class::cast).toList())
            ${eclass.getName()} @if(!eclass.getESuperTypes().isEmpty()) ${eclass.getESuperTypes().stream().map(EClass::getName).collect(Collectors.joining(", "))} @endif {
            @for(EAttribute attr : eclass.getEAttributes())
                .${attr.getName()} : ${attr.getEType().getName()}
            @endfor

            @for(EReference ref : eclass.getEReferences())
                @if(ref.isContainment())<>@endif-${ref.getName()}(${ref.getLowerBound()}..@if(ref.getLowerBound() == -1)*{{@else${ref.getUpperBound()}@endif)->${ref.getEType().getName()}
            @endfor
            }
        @endfor
        }
    @endfor
@endfor

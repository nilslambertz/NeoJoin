@import org.eclipse.emf.common.util.EList
@import org.eclipse.emf.ecore.EAttribute
@import org.eclipse.emf.ecore.EEnum
@import org.eclipse.emf.ecore.EObject
@import org.eclipse.emf.ecore.EReference
@import java.util.ArrayList
@import java.util.List
@import org.eclipse.emf.ecore.EPackage
@import static tools.vitruv.neojoin.tgg.emsl_utils.EmslUtils.escapeKeywords
@import static tools.vitruv.neojoin.tgg.emsl_utils.EmslUtils.extractModelName
@import static tools.vitruv.neojoin.tgg.emsl_utils.EmslUtils.getBuiltInValue
@import static tools.vitruv.neojoin.tgg.emsl_utils.EmslUtils.getUserDefinedValue
@param org.eclipse.emf.ecore.resource.Resource resource
@param java.util.Map<EObject, String> objIDs
@param java.util.Map<String, EEnum> metaModelEnum

!{var contents = resource.getContents().stream().filter(content -> !(content instanceof  EPackage)).toList();}
@for(EObject rootOfModel : contents)
    model ${extractModelName(resource.getURI())} {

    !{
        List<EObject> eAllContents = new ArrayList<>();
        eAllContents.add(rootOfModel);
        var it = rootOfModel.eAllContents();
        while (it.hasNext()) {
            eAllContents.add(it.next());
        }
    }

    @for(EObject modelElt : eAllContents)
        ${objIDs.get(modelElt)} : ${escapeKeywords(modelElt.eClass().getName())}
        @for (EAttribute attr : modelElt.eClass().getEAllAttributes())
            @if (attr.getEType() instanceof EEnum)
                .${escapeKeywords(attr.getName())} : ${getUserDefinedValue(metaModelEnum, attr, modelElt)}
            @elseif (modelElt.eGet(attr) != null)
                .${escapeKeywords(attr.getName())} : ${getBuiltInValue(attr, modelElt)}
            @endif
        @endfor

        @for (EReference ref : modelElt.eClass().getEAllReferences())
            !{ Object refVal = modelElt.eGet(ref); }
            @if (refVal instanceof EList<?>)
                @for (EObject reference : (EList<EObject>) refVal)
                    -${escapeKeywords(ref.getName())} -> ${objIDs.get(reference)}
                @endfor
            @elseif (refVal != null)
                -${escapeKeywords(ref.getName())} -> ${objIDs.get((EObject) refVal)}
            @endif
        @endfor
    @endfor
@endfor
}

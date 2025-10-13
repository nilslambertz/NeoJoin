@import org.eclipse.emf.ecore.EEnum
@import org.eclipse.emf.ecore.EObject
@import org.eclipse.emf.ecore.resource.Resource
@import java.util.HashMap
@import java.util.Map
@param org.eclipse.emf.ecore.resource.ResourceSet rs

!{
    Map<EObject, String> objIDs = new HashMap<>();
    Map<String, EEnum> metaModelEnum = new HashMap<>();
}

@template.EmslMetamodelCollection(rs = rs, metaModelEnum = metaModelEnum)

@for(Resource r : rs.getResources())
    @template.EmslModel(resource = r, objIDs = objIDs, metaModelEnum = metaModelEnum)
@endfor

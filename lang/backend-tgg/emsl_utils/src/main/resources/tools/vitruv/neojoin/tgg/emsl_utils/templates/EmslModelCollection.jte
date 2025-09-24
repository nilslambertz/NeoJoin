@import org.eclipse.emf.ecore.EEnum
@import org.eclipse.emf.ecore.EObject
@import org.eclipse.emf.ecore.resource.Resource
@param org.eclipse.emf.ecore.resource.ResourceSet rs
@param java.util.Map<EObject, String> objIDs
@param java.util.Map<String, EEnum> metaModelEnum

@template.EmslMetamodelCollection(rs = rs, metaModelEnum = metaModelEnum)

@for(Resource r : rs.getResources())
    @template.EmslModel(resource = r, objIDs = objIDs, metaModelEnum = metaModelEnum)
@endfor

@import org.eclipse.emf.ecore.EPackage
@import org.eclipse.emf.ecore.resource.Resource
@param org.eclipse.emf.ecore.resource.ResourceSet rs

@for(Resource r : rs.getResources())
    !{var packagesOfResource = r.getContents().stream().filter(EPackage.class::isInstance).map(EPackage.class::cast).toList();}
    @for(EPackage ePackage : packagesOfResource)
        @template.EmslMetamodel(ePackage = ePackage)
    @endfor
@endfor

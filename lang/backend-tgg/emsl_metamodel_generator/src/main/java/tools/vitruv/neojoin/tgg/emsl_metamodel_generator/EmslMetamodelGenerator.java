package tools.vitruv.neojoin.tgg.emsl_metamodel_generator;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.FileOutput;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;
import java.nio.file.Path;

public class EmslMetamodelGenerator {
    private static final String EMSL_METAMODEL_COLLECTION_TEMPLATE = "EmslMetamodelCollection.jte";

    public static void generateMetamodels(ResourceSet set, Path output) {
        TemplateEngine engine = TemplateEngine.createPrecompiled(ContentType.Plain);
        try (FileOutput out = new FileOutput(output)) {
            engine.render(EMSL_METAMODEL_COLLECTION_TEMPLATE, set, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

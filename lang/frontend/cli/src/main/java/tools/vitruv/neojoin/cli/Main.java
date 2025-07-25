package tools.vitruv.neojoin.cli;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.xtext.validation.Issue;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import tools.vitruv.neojoin.NeoJoinStandaloneSetup;
import tools.vitruv.neojoin.Parser;
import tools.vitruv.neojoin.SourceLocation;
import tools.vitruv.neojoin.collector.InstanceModelCollector;
import tools.vitruv.neojoin.collector.PackageModelCollector;
import tools.vitruv.neojoin.generation.MetaModelGenerator;
import tools.vitruv.neojoin.transformation.Transformator;
import tools.vitruv.neojoin.transformation.TransformatorException;
import tools.vitruv.neojoin.utils.EMFUtils;
import tools.vitruv.neojoin.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@NullUnmarked
@Command(name = "NeoJoin", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

	@Parameters(index = "0", paramLabel = "FILE", description = "Path to the query file")
	Path queryFile;

	@Option(names = {"-m", "--meta-model-path"}, paramLabel = "MODEL PATH", required = true, description = "Path specification (including URI scheme) to find referenced meta-models")
	String metaModelPath;


	@ArgGroup(exclusive = false, heading = "Generate the meta-model:%n")
	@Nullable
	Generate generate;

	static class Generate {

		@Option(names = {"-g", "--generate"}, paramLabel = "OUTPUT", description = "Generate the meta-model and write it to the given file or directory")
		Path output;

	}


	@ArgGroup(exclusive = false, heading = "Transform the input models:%n")
	@Nullable
	Transform transform;

	static class Transform {

		@Option(names = {"-t", "--transform"}, paramLabel = "OUTPUT", required = true, description = "Transform the given input models based on the query and write the result to the given file or directory")
		Path output;

		@Option(names = {"-i", "--instance-model-path"}, paramLabel = "MODEL PATH", required = true, description = "Path specification (including URI scheme) to find instance models")
		String instanceModelPath;

	}

	/**
	 * CLI entry point.
	 *
	 * @return exit code
	 */
	@Override
	public Integer call() {
		try {
			return execute();
		} catch (InvalidPathException ex) {
			printError("Invalid meta-model path: %s", ex.getMessage());
		} catch (TransformatorException e) {
			var source = e.getSourceLocation();
			if (source != null) {
				printError("%s (%s)", e.getMessage(), source.display());
			} else {
				printError(e.getMessage());
			}
		} catch (IOException e) {
			printError("Failed to write output: %s", e.getMessage());
		} catch (WrappedException e) {
			printError("Failed to read input model: %s", e.getMessage());
		}

		return 1;
	}

	private void printError(String message, @Nullable Object... args) {
		System.err.print("[ERROR] ");
		System.err.printf(message, args);
		System.err.println();
	}

	private int execute() throws IOException {
		// collect available meta-models
		EPackage.Registry registry = new PackageModelCollector(metaModelPath).collect();

		// parse query
		var setup = new NeoJoinStandaloneSetup(registry);
		var result = setup.getParser().parse(URI.createFileURI(queryFile.toString()));

		printIssues(result.issues());

		if (result instanceof Parser.Result.Failure) {
			return 1;
		}

		if (generate == null && transform == null) {
			// neither generation nor transformation requested
			System.out.println("Query is valid. Use --generate or --transform for further processing.");
			return 0;
		}

		var aqr = ((Parser.Result.Success) result).aqr();

		// generate meta-model
		var targetMetaModel = new MetaModelGenerator(aqr).generate();
		printIssues(targetMetaModel.diagnostic());
		if (generate != null) {
			write(getOutputURI(generate.output, "ecore"), targetMetaModel.pack());
		}

		if (transform != null) {
			// transform instance models
			var inputModels = new InstanceModelCollector(transform.instanceModelPath, registry).collect();
			var targetInstanceModel = new Transformator(
				setup.getExpressionHelper(),
				aqr,
				targetMetaModel.pack(),
				inputModels
			).transform();
			write(getOutputURI(transform.output, "xmi"), targetInstanceModel);
			validateInstanceModel(targetInstanceModel);
		}

		return 0;
	}

	private static void printIssues(List<Issue> issues) {
		for (Issue issue : issues) {
			System.err.printf(
				"[%s] %s (%s)%n",
				issue.getSeverity().name(),
				issue.getMessage(),
				SourceLocation.from(issue).display()
			);
		}
	}

	private static void printIssues(Diagnostic rootDiagnostic) {
		if (rootDiagnostic.getSeverity() == Diagnostic.OK) {
			return;
		}

		rootDiagnostic.getChildren().forEach(d -> {
			System.err.printf("[%s] %s%n", EMFUtils.diagnosticSeverityText(d), d.getMessage());
		});
	}

	private @Nullable ResourceSet resourceSet;

	private void write(URI outputUri, EObject object) throws IOException {
		if (resourceSet == null) {
			resourceSet = new ResourceSetImpl();
		}

		var resource = resourceSet.createResource(outputUri);
		resource.getContents().add(object);
		var options = Map.of(XMLResource.OPTION_URI_HANDLER, new RelativeURIResolver(resource.getURI()));
		resource.save(options);
	}

	private URI getOutputURI(Path output, String extension) {
		if (Files.isDirectory(output)) {
			String outputFileName = Utils.removeSuffix(queryFile.getFileName().toString(), ".nj") + "." + extension;
			output = output.resolve(outputFileName);
		}

		return URI.createFileURI(output.toString());
	}

	private void validateInstanceModel(EObject instanceModel) {
		var rootDiagnostic = Diagnostician.INSTANCE.validate(instanceModel);
		if (rootDiagnostic.getSeverity() == Diagnostic.OK) {
			return;
		}

		System.err.println("[WARNING] Generated instance model failed validation:");
		for (var diagnostic : rootDiagnostic.getChildren()) {
			if (diagnostic.getSeverity() != Diagnostic.OK) {
				System.err.printf("[%s] %s%n", getSeverityString(diagnostic), diagnostic.getMessage());
			}
		}
	}

	private String getSeverityString(Diagnostic diagnostic) {
		return switch (diagnostic.getSeverity()) {
			case Diagnostic.OK -> "OK";
			case Diagnostic.INFO -> "INFO";
			case Diagnostic.WARNING -> "WARNING";
			case Diagnostic.ERROR -> "ERROR";
			default -> "UNKNOWN";
		};
	}

	public static void main(String[] args) {
		System.exit(new CommandLine(new Main()).execute(args));
	}

}

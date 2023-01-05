package veogen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.PathUtils;
import org.objectstyle.wolips.eogenerator.core.runner.VelocityEOGeneratorRunner;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.factories.BundleEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.EclipseProjectEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.IDEAProjectEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.SimpleManifestEOModelGroupFactory;

public class Veogen {
	protected static String path(File workingDir, String path) {
		String finalPath;
		if (workingDir == null || new File(path).isAbsolute()) {
			finalPath = path;
		}
		else {
			try {
        finalPath = new File(workingDir, path).getCanonicalPath();
      } catch (IOException e) {
        finalPath = new File(workingDir, path).getPath();
      }
		}
		return finalPath;
	}
	
	protected static File workingDir(List<String> argsList) throws IOException {
	  File workingDir = null;
	  for (int argNum = 0; argNum < argsList.size(); argNum++) {
	    String arg = argsList.get(argNum);
	    if ("-workingDir".equals(arg)) {
	      workingDir = new File(argsList.get(++argNum)).getCanonicalFile();
	      break;
	    }
	  }
	  return workingDir;
	}
	
	public static void main(String[] args2) throws Exception {
		EOGeneratorModel eogeneratorModel = new EOGeneratorModel();
		eogeneratorModel.setPackageDirs(Boolean.TRUE);
		eogeneratorModel.setExtension("java");
		
		List<String> argsList = new LinkedList<String>();
		for (String arg : args2) {
			argsList.add(arg);
		}

		File workingDir = workingDir(argsList);
		if (argsList.size() >= 2 && "-config".equals(argsList.get(0))) {
			BufferedReader br = new BufferedReader(new FileReader(PathUtils.getAbsolutePath(argsList.get(1), workingDir)));
			try {
				StringBuilder sb = new StringBuilder();
				String configLine;
				while ((configLine = br.readLine()) != null) {
					sb.append(configLine.replaceFirst("\\\\$", ""));
				}
				eogeneratorModel.readFromString(sb.toString(), workingDir);
			}
			finally {
				br.close();
			}
		}

		if (workingDir == null) {
		  workingDir = workingDir(argsList);
		}
		
		boolean loadModelGroup = false;
		File modelGroupFolder = workingDir == null ? new File(".") : workingDir;
		List<String> modelPaths = new LinkedList<String>();
		List<String> entityList = new LinkedList<String>();
		boolean force = false;
		boolean useStdout = false;
		
		for (int argNum = 0; argNum < argsList.size(); argNum++) {
			String arg = argsList.get(argNum);
			if ("-model".equalsIgnoreCase(arg)) {
				modelPaths.add(Veogen.path(workingDir, argsList.get(++argNum)));
			}
			else if ("-modelgroup".equalsIgnoreCase(arg)) {
				modelGroupFolder = new File(Veogen.path(workingDir, argsList.get(++argNum)));
				loadModelGroup = true;
			}
			else if ("-templateDir".equalsIgnoreCase(arg)) {
				eogeneratorModel.setTemplateDir(Veogen.path(workingDir, argsList.get(++argNum)));
			}
			else if ("-superclassTemplate".equalsIgnoreCase(arg)) {
				eogeneratorModel.setJavaTemplate(argsList.get(++argNum));
			}
			else if ("-subclassTemplate".equalsIgnoreCase(arg)) {
				eogeneratorModel.setSubclassJavaTemplate(argsList.get(++argNum));
			}
			else if ("-javaTemplate".equalsIgnoreCase(arg)) {
			  eogeneratorModel.setJavaTemplate(argsList.get(++argNum));
			}
			else if ("-subclassJavaTemplate".equalsIgnoreCase(arg)) {
			  eogeneratorModel.setSubclassJavaTemplate(argsList.get(++argNum));
			}
			else if ("-extension".equalsIgnoreCase(arg)) {
				eogeneratorModel.setExtension(argsList.get(++argNum));
			}
			else if ("-java".equalsIgnoreCase(arg)) {
				eogeneratorModel.setJava(Boolean.TRUE);
			}
			else if ("-javaClient".equalsIgnoreCase(arg)) {
				eogeneratorModel.setJavaClient(Boolean.TRUE);
			}
			else if ("-destination".equalsIgnoreCase(arg)) {
				eogeneratorModel.setDestination(Veogen.path(workingDir, argsList.get(++argNum)));
			}
			else if ("-subclassDestination".equalsIgnoreCase(arg)) {
				eogeneratorModel.setSubclassDestination(Veogen.path(workingDir, argsList.get(++argNum)));
			}
			else if ("-prefix".equalsIgnoreCase(arg)) {
				eogeneratorModel.setPrefix(argsList.get(++argNum));
			}
			else if ("-superclassPackage".equalsIgnoreCase(arg)) {
				eogeneratorModel.setSuperclassPackage(argsList.get(++argNum));
			}
			else if ("-verbose".equalsIgnoreCase(arg)) {
				eogeneratorModel.setVerbose(Boolean.TRUE);
			}
			else if ("-java14".equalsIgnoreCase(arg)) {
				eogeneratorModel.setJava14(true);
			}
			else if ("-generate".equalsIgnoreCase(arg)) {
				String modelName = argsList.get(++argNum);
				eogeneratorModel.addModel(new EOModelReference(new Path(modelName)));
			}
			else if ("-loadModelGroup".equalsIgnoreCase(arg)) {
				loadModelGroup = true;
			}
			else if ("-force".equalsIgnoreCase(arg)) {
			  force = true;
			}
			else if ("-workingDir".equalsIgnoreCase(arg)) {
				argNum ++;
			}
			else if ("-config".equalsIgnoreCase(arg)) {
				argNum ++;
			}
			else if ("-stdout".equalsIgnoreCase(arg))
			  useStdout = true;
			else {
			  entityList.add(arg);
			}
		}

		if (eogeneratorModel.getModels() == null || eogeneratorModel.getModels().isEmpty()) {
			for (String modelPath : modelPaths) {
				eogeneratorModel.addModel(new EOModelReference(new Path(modelPath)));
			}
		}

		if (eogeneratorModel.getDestination() == null || eogeneratorModel.getModels().size() == 0) {
			System.out.println("veogen");
			System.out.println("  -config /path/to/eogen");
			System.out.println("    the path to an eogen file to use as the config");
			System.out.println("  -model /path/to/model.eomodeld");
			System.out.println("    the path to a model to add to this model group (multiples allowed)");
			System.out.println("  -modelgroup /path/to/modelgroup/folder");
			System.out.println("    the path that can be traversed to find models (finds dependencies), implies -loadModelGroup");
			System.out.println("  -templateDir /path/containing/templates");
			System.out.println("    the path to the folder that contains templates");
			System.out.println("  -superclassTemplate nameOfTemplate.java");
			System.out.println("    the name of the superclass template file (_File.java)");
			System.out.println("  -subclassTemplate nameOfTemplate.java");
			System.out.println("    the name of the subclass template file (File.java)");
			System.out.println("  -extension");
			System.out.println("    the file extension for generated files (default \".java + \")");
			System.out.println("  -java");
			System.out.println("    generate regular java output (default)");
			System.out.println("  -javaClient");
			System.out.println("    generate java client output");
			System.out.println("  -destination /path/for/output");
			System.out.println("    the path to the folder to write generated superclass output input");
			System.out.println("  -subclassDestination /path/for/subclass/output");
			System.out.println("    the path to the folder to write generated subclass output input");
			System.out.println("  -prefix thePrefix");
			System.out.println("    the prefix to prepend to superclass names (default: _)");
			System.out.println("  -superclassPackage packageName");
			System.out.println("    the package name to append to superclass package name (default: none)");
			System.out.println("  -verbose");
			System.out.println("    generate verbose output");
			System.out.println("  -java14");
			System.out.println("    switch to Java 1.4 templates");
			System.out.println("  -generate modelName");
			System.out.println("    generate source files for the model with the given name (multiples allowed).");
			System.out.println("    if no explicit generate flags are specified, the items from -model will be used.");
			System.out.println("  -loadModelGroup");
			System.out.println("    if specified and -modelgroup is not, an implicit modelgroup folder of '.' will be used.");
			System.out.println("  -workingDir");
			System.out.println("    the working directory to prepend to all the provided paths");
			System.out.println("  -force");
			System.out.println("    force generation even if there are errors");
			System.out.println("  -stdout");
			System.out.println("    send output to the console instead of the filesystem");
			System.out.println();
			System.out.println("You must specify at least -destination and a -generate or -model.");
			System.exit(0);
		}

		if (eogeneratorModel.getSubclassDestination() == null) {
			eogeneratorModel.setSubclassDestination(eogeneratorModel.getDestination());
		}

		EOModelGroup modelGroup = new EOModelGroup();
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		if (loadModelGroup || modelPaths.size() == 0) {
		  if (System.getProperty("veogen.modelgroup.nobundle") == null) {
		    new BundleEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, loadModelGroup, new NullProgressMonitor());
		  }
		  if (System.getProperty("veogen.modelgroup.nomanifest") == null) {
		    new SimpleManifestEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
		  }
		  if (System.getProperty("veogen.modelgroup.noidea") == null) {
		    new IDEAProjectEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
		  }
		  if (System.getProperty("veogen.modelgroup.noeclipse") == null) {
		    new EclipseProjectEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
		  }
		}
		for (String modelPath : modelPaths) {
			modelGroup.loadModelFromURL(new File(modelPath).toURI().toURL());
		}

		modelGroup.resolve(failures);
		modelGroup.verify(failures);
		boolean hasFailures = false;
		for (EOModelVerificationFailure failure : failures) {
		  if (!failure.isWarning()) {
		    System.err.println("Failure: " + failure.getMessage());
		    hasFailures = true;
		  }
		}
		if (hasFailures && force) {
		    System.err.println("**** There were errors but -force was specified.");
		}
		if (!hasFailures || force) {
		  VelocityEOGeneratorRunner eogenRunner = new VelocityEOGeneratorRunner(false);
		  eogenRunner.setUseStdout(useStdout);
		  StringBuffer results = new StringBuffer();
		  eogenRunner.generate(eogeneratorModel, results, modelGroup, entityList, VeogenResourceLoader.class, new NullProgressMonitor());
		}
	}
}

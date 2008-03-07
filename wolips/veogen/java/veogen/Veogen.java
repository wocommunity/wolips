package veogen;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.runner.VelocityEOGeneratorRunner;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.factories.EclipseProjectEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.IDEAProjectEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.factories.SimpleManifestEOModelGroupFactory;

public class Veogen {
  public static void main(String[] args) throws Exception {
    EOGeneratorModel eogeneratorModel = new EOGeneratorModel();
    eogeneratorModel.setPackageDirs(Boolean.TRUE);
    
    File modelGroupFolder = new File(".");
    List<String> modelPaths = new LinkedList<String>();
    for (int argNum = 0; argNum < args.length; argNum++) {
      String arg = args[argNum];
      if ("-model".equals(arg)) {
        modelPaths.add(args[++argNum]);
      }
      else if ("-modelgroup".equals(arg)) {
        modelGroupFolder = new File(args[++argNum]);
      }
      else if ("-templates".equals(arg)) {
        eogeneratorModel.setTemplateDir(args[++argNum]);
      }
      else if ("-superclassTemplate".equals(arg)) {
        eogeneratorModel.setJavaTemplate(args[++argNum]);
      }
      else if ("-subclassTemplate".equals(arg)) {
        eogeneratorModel.setSubclassJavaTemplate(args[++argNum]);
      }
      else if ("-java".equals(arg)) {
        eogeneratorModel.setJava(Boolean.TRUE);
      }
      else if ("-javaClient".equals(arg)) {
        eogeneratorModel.setJavaClient(Boolean.TRUE);
      }
      else if ("-destination".equals(arg)) {
        eogeneratorModel.setDestination(args[++argNum]);
      }
      else if ("-subclassDestination".equals(arg)) {
        eogeneratorModel.setSubclassDestination(args[++argNum]);
      }
      else if ("-prefix".equals(arg)) {
        eogeneratorModel.setPrefix(args[++argNum]);
      }
      else if ("-superclassPackage".equals(arg)) {
        eogeneratorModel.setSuperclassPackage(args[++argNum]);
      }
      else if ("-verbose".equals(arg)) {
        eogeneratorModel.setVerbose(Boolean.TRUE);
      }
      else if ("-java14".equals(arg)) {
        eogeneratorModel.setJava14(true);
      }
      else if ("-generate".equals(arg)) {
        String modelName = args[++argNum];
        eogeneratorModel.addModel(new EOModelReference(new Path(modelName)));
      }
    }
    
    if (eogeneratorModel.getDestination() == null || eogeneratorModel.getModels().size() == 0) {
      System.out.println("veogen -destination /path/to/output/folder [-model /path/to/model.eomodeld]* [-modelgroup /path/to/working/dir] [-templates /path/to/templates]");
      System.exit(0);
    }

    if (eogeneratorModel.getSubclassDestination() == null) {
      eogeneratorModel.setSubclassDestination(eogeneratorModel.getDestination());
    }
    
    EOModelGroup modelGroup = new EOModelGroup();
    Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
    if (modelPaths.size() == 0) {
      new SimpleManifestEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
      new IDEAProjectEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
      new EclipseProjectEOModelGroupFactory().loadModelGroup(modelGroupFolder, modelGroup, failures, true, new NullProgressMonitor());
    }
    else {
      for (String modelPath : modelPaths) {
        modelGroup.loadModelFromURL(new File(modelPath).toURL());
      }
    }
    modelGroup.resolve(failures);
    modelGroup.verify(failures);
    
    VelocityEOGeneratorRunner eogenRunner = new VelocityEOGeneratorRunner(false);
    StringBuffer results = new StringBuffer();
    eogenRunner.generate(eogeneratorModel, results, modelGroup);
  }
}

package entitymodeler;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
  @Override
  public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    return new ApplicationWorkbenchWindowAdvisor(configurer);
  }

  @Override
  public String getInitialWindowPerspectiveId() {
    return EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID;
  }

  @Override
  public void preStartup() {
    Location loc = Platform.getInstanceLocation();
    try {
      //loc.setURL(new File("/tmp/.entityModeler").toURL(), false);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    super.preStartup();
  }

  @Override
  public void postStartup() {
    super.postStartup();
    try {
      String[] nonFrameworkArgs = EclipseEnvironmentInfo.getDefault().getNonFrameworkArgs();

      String modelPath = null;
      boolean optionValue = false;
      for (String nonFrameworkArg : nonFrameworkArgs) {
        if (nonFrameworkArg.startsWith("-")) {
          optionValue = true;
        }
        else if (optionValue) {
          optionValue = false;
        }
        else {
          modelPath = nonFrameworkArg;
        }
      }
      
      System.out.println("ApplicationWorkbenchAdvisor.postStartup: " + modelPath);
      ApplicationWorkbenchAdvisor.openModelPath(modelPath);
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public static void openModelPath(String modelPath) throws CoreException {
    if (modelPath == null) {
      return;
    }
    IWorkspace ws = ResourcesPlugin.getWorkspace();
    IProject project = ws.getRoot().getProject("EntityModeler");
    if (!project.exists()) {
      project.create(null);
    }
    if (!project.isOpen()) {
      project.open(null);
    }
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    Shell shell = window.getShell();
    File modelPathFile = new File(modelPath).getAbsoluteFile();
    IPath location;
    if (modelPathFile.isDirectory()) {
      location = new Path(modelPath);
    }
    else {
      location = new Path(modelPathFile.getParentFile().getAbsolutePath());
    }
    IFolder modelFolder = project.getFolder(location.lastSegment());
    if (modelFolder.exists()) {
      modelFolder.delete(true, null);
    }
    modelFolder.createLink(location, IResource.NONE, null);
    IWorkbenchPage page = window.getActivePage();
    if (page != null) {
      page.openEditor(new FileEditorInput(modelFolder.getFile("index.eomodeld")), EOModelEditor.EOMODEL_EDITOR_ID);
    }
  }
}

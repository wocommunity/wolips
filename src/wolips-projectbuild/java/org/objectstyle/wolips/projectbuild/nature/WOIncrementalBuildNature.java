package org.objectstyle.wolips.projectbuild.nature;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.projectbuild.WOProjectBuildConstants;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Harald Niesche
 * 
 * 
 */
public class WOIncrementalBuildNature 
  implements IProjectNature, WOProjectBuildConstants 
{


  /**
   * Constructor for WebObjectsNature.
   */
  public WOIncrementalBuildNature() {
    super();
  }

  /**
   * @see org.eclipse.core.resources.IProjectNature#configure()
   */
  public void configure() throws CoreException {
    IProject project = getProject();

    System.out.println("configure - "+project);
    
    IProjectDescription desc = project.getDescription();
    
    ICommand bc[] = desc.getBuildSpec();
    
    boolean found = false;
    
    for (int i = 0; i < bc.length; i++) {
      if (bc[i].getBuilderName().equals (BUILDER_ID)) {
        found = true;
      }
    }
    
    if (!found) {
      List buildCommands = new ArrayList(Arrays.asList(bc));
      ICommand newCommand = desc.newCommand();
      newCommand.setBuilderName(BUILDER_ID);
      buildCommands.add(newCommand);
      desc.setBuildSpec((ICommand[])buildCommands.toArray(new ICommand[buildCommands.size()]));
      project.setDescription(desc, null);
    }
    
    IFolder buildFolder = project.getFolder("build");
    if (!buildFolder.exists()) {
      buildFolder.create(IFolder.FORCE, true, null);
    }
    buildFolder.setDerived (true);

    IPath outputPath = ((IJavaProject)project.getNature (JavaCore.NATURE_ID)).getOutputLocation();
    IFolder outputFolder = project.getFolder (outputPath);
    
    System.out.println (outputFolder.isDerived ());
  }

  /**
   * @see org.eclipse.core.resources.IProjectNature#deconfigure()
   */
  public void deconfigure() throws CoreException {
    IProject project = getProject();
    
    System.out.println("deconfigure - "+project);
    IProjectDescription desc = project.getDescription();
    
    ICommand bc[] = desc.getBuildSpec();
    
    ICommand found = null;
    
    for (int i = 0; i < bc.length; i++) {
      if (bc[i].getBuilderName().equals (BUILDER_ID)) {
        found = bc[i];
      }
    }
    
    if (null != found) {
      List buildCommands = new ArrayList(Arrays.asList(bc));
      buildCommands.remove(found);
      desc.setBuildSpec((ICommand[])buildCommands.toArray(new ICommand[buildCommands.size()]));
      project.setDescription(desc, null);
    }
    
    IFolder buildFolder = project.getFolder("build");
    if (buildFolder.exists() && buildFolder.isDerived()) {
      buildFolder.delete(true, false, null);
    }
  }
  /**
   * @see org.eclipse.core.resources.IProjectNature#getProject()
   */
  public IProject getProject() {
    return _project;
  }

  /**
   * @see org.eclipse.core.resources.IProjectNature#setProject(IProject)
   */
  public void setProject(IProject project) {
    _project = project;
  }

  /* ************************************************************************ */


  /**
   * 
   */
  public IJavaProject getJavaProject() {
    try {
      return ((IJavaProject)getProject().getNature(JavaCore.NATURE_ID));
    } catch (CoreException up) {
    }
    
    return (null);
  }
  
  public String getStringProperty (String key) {
    String result = "";
    try {
      result = getProject().getPersistentProperty(
        new QualifiedName ("", key)
      );
    } catch (CoreException up) {
		up.printStackTrace();
    }
    
    return (result);
  }

  public void setStringProperty (String key, String value) {
    try {
      getProject().setPersistentProperty(
        new QualifiedName ("", key), value
      );
    } catch (CoreException up) {
    	up.printStackTrace();
    }
    
  }

  public boolean getBooleanProperty (String key) {
    return ("true".equals(getStringProperty (key)));
  }

  public void setBooleanProperty (String key, boolean value) {
    setStringProperty (key, value ? "true" : "false");
  }

  public boolean isFramework () {
    return (getBooleanProperty (FRAMEWORK_PROPERTY));
  }

  public void setIsFramework (boolean isFramework) {
    setBooleanProperty (FRAMEWORK_PROPERTY, isFramework);
  }
  
  /**
   * either name.woa or name.framework
   */ 
  public String getResultName () {
    String name = getProject().getName();
    
    if (isFramework ()) {
      return (name + ".framework");
    } else {
      return (name + ".woa");
    }
  }
  
  public IPath getBuildPath () {
    return (getProject().getFullPath().append("build"));
  }

  public IPath getResultPath () {
    return (getBuildPath ().append(getResultName()));
  }
  
  public IPath getInfoPath () {
    if (isFramework()) {
      return (getResultPath().append("Resources"));
    } else { 
      return (getResultPath().append(APPINFO_PATH));
    }
  }
  
  public IPath _getResultPath () {
    if (isFramework()) {
      return (getResultPath());
    } else { 
      return (getResultPath().append(APPINFO_PATH));
    }
  }

  public IPath getResourceOutputPath () {
    return (_getResultPath ().append(RESOURCE_PATH));
  }

  public IPath getJavaOutputPath () {
    return (_getResultPath ().append(JAVA_PATH));
  }

  public IPath getWebResourceOutputPath () {
    return (_getResultPath ().append(WEBRESOURCE_PATH));
  }
  
  
  public String getResourceName () {
    String result = "";
    if (!isFramework()) {
      result += APPINFO_PATH+"/";
    }
    result += RESOURCE_PATH;
    return (result);
  }
  
  public String getWebResourceName () {
    String result = "";
    if (!isFramework()) {
      result += APPINFO_PATH+"/";
    }
    result += WEBRESOURCE_PATH;
    return (result);
  }
  
  /* ************************************************************************ */

  public IPath getDestinationPath (IResource res) {

    IPath fullPath = res.getFullPath();
    
    if (
      getBuildPath().isPrefixOf(fullPath)
    ) {
      return (null);
    }

    try {    
      if (
        !getJavaProject().getOutputLocation().equals(getJavaProject().getPath())
        && getJavaProject().getOutputLocation().isPrefixOf(fullPath)
      ) {
        return (null);
      }
    } catch (CoreException up) {
      up.printStackTrace();
    }

    
    IPath result = asResourcePath (fullPath, res);
    if (null == result) 
      result = asWebResourcePath (fullPath, res);
    
    return (result);
  }

  private IPath _appendSpecial (IPath p1, IPath p2) {
    String segments[] = p2.segments();

    int n = segments.length-1;
    for (int i = n; i >= 0; --i) {
      if (segments[i].endsWith(".lproj")) {
        n = i;
      }
    }
    IPath tmp = p1;
    while (n < segments.length) {
      tmp = tmp.append (segments[n++]);
    }
    return (tmp);
  }

  public IPath asResourcePath (IPath path, IResource res) {

    /*if (IResource.FOLDER == res.getType()) {
      if (
        lastSegment.endsWith (".wo")
        || lastSegment.endsWith (".eomodeld")
      ) {
        return (getResourceOutputPath().append(lastSegment));
      }
    } else */ 
    if (IResource.FILE == res.getType()) {
      String lastSegment = path.lastSegment();
      if (
        lastSegment.equals("Properties")
        || lastSegment.endsWith(".api")
        || lastSegment.endsWith(".d2wmodel")
        || lastSegment.endsWith(".plist") && (-1 == path.toString().indexOf(".eomodeld/"))
      ) {
        return (_appendSpecial(getResourceOutputPath(), path));
      }
      
      String parentName = res.getParent().getName();
      if (
        parentName.endsWith (".wo")
        || parentName.endsWith (".eomodeld")
      ) {
        return (_appendSpecial(getResourceOutputPath(), res.getParent().getProjectRelativePath()).append(lastSegment));
        //return (getResourceOutputPath().append(parentName).append(lastSegment));
      }
      
    }
    
    return (null);
  }

  public IPath asWebResourcePath (IPath path, IResource res) {
    if (IResource.FILE == res.getType()) {
      String lastSegment = path.lastSegment();
      if (
        lastSegment.endsWith (".js")
        || lastSegment.endsWith (".css")
        || lastSegment.endsWith (".gif")
        || lastSegment.endsWith (".jpg")
        || lastSegment.endsWith (".png")
      ) {
        return _appendSpecial(getWebResourceOutputPath(), path);
      }
    }
    
    return (null);
  }


  /* ************************************************************************ */

  public static void s_addToProject (IProject project) throws CoreException {
    IProjectDescription desc = project.getDescription();
    
    String natures_array[] = desc.getNatureIds();
    
    List natures = new ArrayList(Arrays.asList(natures_array));
    if (!natures.contains(NATURE_ID)) {
      natures.add (NATURE_ID);
      natures_array = (String[])natures.toArray(new String[natures.size()]);
      desc.setNatureIds(natures_array);
      s_setDescription (project, desc);
    }
  }
  
  public static void s_removeFromProject (IProject project) throws CoreException {
    IProjectDescription desc = project.getDescription();
    
    String natures_array[] = desc.getNatureIds();
    
    List natures = new ArrayList(Arrays.asList(natures_array));
    
    if (natures.contains(NATURE_ID)) {
      natures.remove (NATURE_ID);
      natures_array = (String[])natures.toArray(new String[natures.size()]);
      desc.setNatureIds(natures_array);
      s_setDescription (project, desc);
    }
  }


  private static void s_setDescription (final IProject f_project, final IProjectDescription f_desc) {
    s_showProgress(
      new IRunnableWithProgress () {
        public void run(IProgressMonitor pm) {
          try {
            f_project.setDescription(f_desc, pm);
          } catch (CoreException up) {
            pm.done();
          }
        }
      }
    );
  }

  public static WOIncrementalBuildNature s_getNature (IProject project) throws CoreException {
    return ((WOIncrementalBuildNature)project.getNature(NATURE_ID));
  }

  public static void  s_showProgress(IRunnableWithProgress rwp) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell shell = null;
    if (null != workbench) {
      IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
      if (null != window) {
        shell = window.getShell();
      }
    }
    
    ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
    
    try {
      pmd.run (true, true, rwp);
    } catch (InvocationTargetException e) {
      // handle exception
      e.printStackTrace ();
    } catch (InterruptedException e) {
      // handle cancelation
      e.printStackTrace ();
    }
  }


  IProject _project = null;

  private static final String NAME_PROPERTY      = "PROJECT_NAME";
  private static final String FRAMEWORK_PROPERTY = "IS_FRAMEWORK";

  private static final String APPINFO_PATH     = "Contents";
  private static final String FWINFO_PATH      = "Resources";
  private static final String RESOURCE_PATH    = "Resources";
  private static final String WEBRESOURCE_PATH = "WebServerResources";
  private static final String JAVA_PATH        = "Resources/Java";
}

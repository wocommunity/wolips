package org.objectstyle.wolips.projectbuild.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Harald Niesche
 *
 */
public class ResourceUtilities {

  /** 
   * create a Folder recursively 
   * @param f the Folder to be created
   * @param m a ProgressMonitor
   */
  public static void createFolder (IFolder f, IProgressMonitor m) 
    throws CoreException
  {
    if (f.exists()) {
      return;
    }
    IContainer parent = f.getParent ();
    if (!f.getParent().exists()) {
      if (parent instanceof IFolder) {
        createFolder((IFolder)parent, m);
      }
    }
    f.create(true, true, m);
  }


  /** 
   * check if a folder exists under a path, create it if necessary
   * @param path the path to the folder to be created (relative to the workspace root or absolute)
   * @param m a ProgressMonitor
   */
  public static boolean checkDir (IPath path, IProgressMonitor m) 
    throws CoreException 
  {
    boolean result = true;
    
    IFolder f = getWorkspaceRoot().getFolder (path);
    if (!f.exists()) {
      createFolder (f, m);
      result = false;
    }
    
    return (result);
  }

  /**
   * checks if a path is fit to be used as destination for a copy operation
   * if not, the destination is prepared to be used as destination
   * (i.e., existing files and folders are deleted, the parent path is created,
   * if necessary)
   * @param path the candidate destination path
   * @param m a ProgressMonitor
   */
  public static void checkDestination (IPath path, IProgressMonitor m) throws CoreException {
    if (checkDir (path.removeLastSegments(1), m)) {
      IResource res  = getWorkspaceRoot().findMember(path);
      if (null != res && res.exists()) {
        res.delete(true, m);
        //res.refreshLocal(IResource.DEPTH_ONE, m);
      }
    }
  }

  public static IWorkspace getWorkspace () {
    return ResourcesPlugin.getWorkspace();
  }

  public static IWorkspaceRoot getWorkspaceRoot () {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
  
  protected ResourceUtilities () {}
}

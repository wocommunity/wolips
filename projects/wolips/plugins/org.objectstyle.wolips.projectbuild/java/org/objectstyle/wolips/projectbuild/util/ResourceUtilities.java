/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
 
package org.objectstyle.wolips.projectbuild.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
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

  public static boolean checkDerivedDir (IPath path, IProgressMonitor m) 
    throws CoreException 
  {
    boolean result = true;
    
    IFolder f = getWorkspaceRoot().getFolder (path);
    if (!f.exists()) {
      createFolder (f, m);
      f.setDerived(true);
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
  public static IResource checkDestination (IPath path, IProgressMonitor m) throws CoreException {
    if (checkDir (path.removeLastSegments(1), m)) {
      IResource res  = getWorkspaceRoot().findMember(path);
      if (null != res && res.exists()) {
        try {
          res.delete(true, m);
//        } catch (org.eclipse.core.internal.resources.ResourceException e) {
        } catch (CoreException ce) {
          ce.printStackTrace();
          
//          IPath trashFolder = res.getProject().getFullPath().append("build/.trash");
//          IPath trashPath = trashFolder.append(res.getName()+(_uniqifier++));
//          checkDir (trashFolder, m);
//          res.move(trashPath, true, null);

          IPath newName = res.getLocation().removeLastSegments(1);
          newName = newName.append(res.getName()+_getUniqifier());
          File resFile = res.getLocation().toFile();
          if (!resFile.renameTo(newName.toFile())) {
            throw ce;
          }          
        }
        //res.refreshLocal(IResource.DEPTH_ONE, m);
      }
      return res;
    }
    return null;
  }
  
  private static synchronized int _getUniqifier () {
    return _uniqifier++;
  }
  
  public static void copyDerived (IResource res, IPath dest, IProgressMonitor m) throws CoreException  {
    IResource rdest = checkDestination (dest, m);
    res.copy(dest, true, null);
    if (null != rdest) {
      rdest.setDerived(true);
    }
  }

  public static IWorkspace getWorkspace () {
    return ResourcesPlugin.getWorkspace();
  }

  public static IWorkspaceRoot getWorkspaceRoot () {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
  
  protected ResourceUtilities () {}
  
  private static int _uniqifier = 1;


	public static void unmarkResource(IResource res, String markerId) throws CoreException {
	  if (res.exists()) {
	    res.deleteMarkers(markerId, true, 0);
	  }
	}


	public static IMarker markResource(IResource res, String markerId, int severity, String message, String location) throws CoreException {
	  IMarker marker[] = res.findMarkers(markerId, true, 0);
	  if (marker.length != 1) {
	    if (marker.length > 1) {
	      res.deleteMarkers(markerId, false, 0);
	    }
	    marker = new IMarker[1];
	    marker[0] = res.createMarker(markerId);
	  }
	        
	  if (!marker[0].exists()) {
	    marker[0] = res.createMarker(markerId);
	  }
	  Map attr = new HashMap ();
	
	  attr.put(IMarker.PRIORITY, new Integer(IMarker.PRIORITY_HIGH));
	  attr.put(IMarker.SEVERITY, new Integer(severity));
	  attr.put(IMarker.MESSAGE, message);
	  attr.put(IMarker.LOCATION, location);
	
	  marker[0].setAttributes (attr);
    
    return marker[0];
	}
}

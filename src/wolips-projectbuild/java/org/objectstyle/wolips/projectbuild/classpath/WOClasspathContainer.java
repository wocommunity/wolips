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
 
package org.objectstyle.wolips.projectbuild.classpath;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * @author Harald Niesche
 *
 */
public class WOClasspathContainer 
  implements IClasspathContainer
{
  
	public static final String WOCP_IDENTITY 
	  = "org.objectstyle.wolips.WO_CLASSPATH";
	  // de.thecode.eclipse.wosupport.WO_CLASSPATH

	public static final String WOCP_OLD_IDENTITY 
	  = "de.thecode.eclipse.wosupport.WO_CLASSPATH";

  /**
   * Constructor for WOClassPathContainer.
   */
  public WOClasspathContainer() {
    super();
  }

  /**
   * Constructor for WOClassPathContainer.
   */
  public WOClasspathContainer(IPath id, IJavaProject project) {
    super();
    _id = id;
    //_id.segments()[1]
    
    _project = project;
    
    _initPath ();
  }

  /**
   * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
   */
  public IClasspathEntry[] getClasspathEntries() {

    if (_path.size() > 0) {
      IClasspathEntry e = (IClasspathEntry)_path.get(0);
      if (e.isExported() != _isExported()) {
        _path.clear();
        _initPath();
      }
    }

    IClasspathEntry[] cpes = (IClasspathEntry[])_path.toArray(new IClasspathEntry [_path.size()]);

    return (cpes);
  }

  /**
   * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
   */
  public String getDescription() {
    return "WebObjects Frameworks";
  }

  /**
   * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
   */
  public int getKind() {
    return IClasspathContainer.K_APPLICATION;
  }

  /**
   * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
   */
  public IPath getPath() {
    return _id;    
  }


  private void _initPath () {
    File fwBase = _getFrameworkBase();
    File fwBaseL = _getLocalFrameworkBase ();

    int start = 1;
    
    if (_id.segment(start).startsWith ("export=")) {
      start = 2;
    }

    boolean isExported = _isExported();
    
    for (int nFW = start; nFW < _id.segmentCount(); ++nFW) {
      String fw = _id.segment(nFW);
      
      File thisFW = new File (fwBase, fw + ".framework/Resources/Java");
      if (!thisFW.isDirectory()) {
        thisFW = new File (fwBaseL, fw + ".framework/Resources/Java");
      }
      if (thisFW.isDirectory()) {
        String archives[] = thisFW.list (new FilenameFilter () {
          public boolean accept (File dir, String name) {
            String lowerName = name.toLowerCase ();
            return (lowerName.endsWith (".zip") || lowerName.endsWith (".jar"));
          }
        });
        
        for (int i = 0; i < archives.length; ++i) {
          IPath archivePath = new Path (thisFW.getAbsolutePath()+"/"+archives[i]);
          //IClasspathEntry entry = JavaCore.newLibraryEntry(archivePath, null, null);
          IClasspathEntry entry = JavaCore.newLibraryEntry(archivePath, null, null, isExported);
          _path.add (entry);
        }
      }
      
    }
    
  }
  
  
  private boolean _isExported () {
    boolean exported = false;
    try {
      IClasspathEntry entries[] = _project.getRawClasspath();
      IClasspathEntry me = null;
  
      for (int i = 0; i < entries.length; ++i) {
        if (
          (entries[i].getEntryKind() == IClasspathEntry.CPE_CONTAINER)
          && (entries[i].getPath().equals(_id))
        ) {
          me = entries[i];
          exported = me.isExported();
          break;
        }
      }
    } catch (JavaModelException up) {
    }
    return exported;          
  }
  
  static File _getFrameworkBase () {
	File result = null;
	
	result = new File (WOLipsPlugin.getDefault().getWOEnvironment().getWOVariables().libraryDir()+"/Frameworks");
	
	return result;
  }

  static File _getLocalFrameworkBase () {
	File result = null;

	result = new File (WOLipsPlugin.getDefault().getWOEnvironment().getWOVariables().localLibraryDir()+"/Frameworks");
	
	return result;
//	File fwBase = new File (WOSupportPlugin.getWORoot(), "Library/Frameworks");
//	File fwBaseL = new File (WOSupportPlugin.getLocalRoot(), "Library/Frameworks");
//    
//	if (
//	  !fwBase.exists() || !fwBase.isDirectory()
//	) {
//	   // might wanna give some message...
//	  System.out.println("WO_ROOT not found");
//	  return ;
//	}
//    
//	if (
//	  !fwBaseL.exists() || !fwBaseL.isDirectory()
//	) {
//	   // might wanna give some message...
//	  System.out.println("LOCAL_ROOT ("+fwBaseL+") does not exist, using WO_ROOT/Local ");
//	  fwBaseL = new File (WOSupportPlugin.getWORoot(), "Local/Library/Frameworks");
//	}
  }

  private IPath _id = new Path ("de.thecode.eclipse.wosupport.WO_CLASSPATH");

  private List _path = new ArrayList ();
  
  private IJavaProject _project = null;
}


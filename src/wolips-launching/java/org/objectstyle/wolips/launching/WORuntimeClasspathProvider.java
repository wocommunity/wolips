/*
 * Created on 18.08.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.objectstyle.wolips.launching;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathProvider;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;
import org.objectstyle.wolips.core.project.WOLipsProject;


/**
 * @author hn3000
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class WORuntimeClasspathProvider
	extends StandardClasspathProvider
	implements IRuntimeClasspathProvider 
{
  public final static String ID = "org.objectstyle.wolips.launching.WORuntimeClasspath";

  /* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#computeUnresolvedClasspath(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration)
		throws CoreException 
  {
		return super.computeUnresolvedClasspath(configuration);
	}


  /* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathProvider#resolveClasspath(org.eclipse.jdt.launching.IRuntimeClasspathEntry[], org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveClasspath(
		IRuntimeClasspathEntry[] entries,
		ILaunchConfiguration configuration
  )
		throws CoreException 
  {
    
    
    List others = null;
    List resolved = null;
    
    // used for duplicate removal
    Set allEntries = new HashSet();

    // resolve WO framework projects ourselves, let super do the rest
    for (int i = 0; i < entries.length; ++i) {
      IResource archive = _getWOJavaArchive(entries[i]);
      if (null != archive) {
        if (!allEntries.contains(archive.getLocation())) {
          if (null == resolved) resolved = new ArrayList ();
          resolved.add(JavaRuntime.newArchiveRuntimeClasspathEntry(archive));
          
          allEntries.add(archive.getLocation());
        }
      } else {
        if (null == others) others = new ArrayList ();
        others.add(entries[i]);
      }
    }

    // ... let super do the rest but remove duplicates from the resulting classpath ...
    if (null != others) {
      IRuntimeClasspathEntry oe[] = super.resolveClasspath((IRuntimeClasspathEntry[])others.toArray(new IRuntimeClasspathEntry[others.size()]), configuration);

      if (null == resolved) resolved = new ArrayList ();
      
      for (int i = 0; i < oe.length; ++i) {
        IRuntimeClasspathEntry entry = oe[i];
        String  ls = entry.getLocation();
        IPath loc = (null == ls) ? null : new Path(ls); 
        if (null == loc) {
          resolved.add(entry);
        } else {
          if (!allEntries.contains(loc)) {
            resolved.add(entry);
            allEntries.add(loc);
          }
        }
      }
    }

    return (IRuntimeClasspathEntry[])resolved.toArray(new IRuntimeClasspathEntry[resolved.size()]);
	}

  IResource _getWOJavaArchive (IRuntimeClasspathEntry entry) throws CoreException {
    IResource result = null;
    
    if (IRuntimeClasspathEntry.PROJECT == entry.getType()) {
      IProject project = (IProject)entry.getResource();
      
      WOLipsProject wop = new WOLipsProject (project);
      WOLipsProject.NaturesAccessor na = wop.getNaturesAccessor();
      
      String projectName = project.getName();
      String projectNameLC = projectName.toLowerCase();
      
      // I'd rather use the knowledge from the IncrementalNature, but that fragment is not
      // visible here (so I can't use the class, I think) [hn3000]
      if (na.isFramework()) {
        if (na.isAnt()) {
          result = project.getFolder("dist/"+projectName+".framework/Resources/Java/"+projectNameLC+".jar");
        } else if (na.isIncremental()) {
          result = project.getFolder("build/"+projectName+".framework/Resources/Java");
        }
      } else if (na.isApplication()) { // must be application
        if (na.isAnt()) {
          result = project.getFolder("dist/"+projectName+".woa/Contents/Resources/Java/"+projectNameLC+".jar");
        } else if (na.isIncremental()) {
          result = project.getFolder("build/"+projectName+".woa/Contents/Resources/Java");
        }
      }
      
      // check if folder exists, otherwise let Eclipse to its default thing
      if (
        (null != result) 
        && (!result.exists())
      )  {
        System.out.println("expected resource is not there: "+result.getLocation().toOSString());
        result = null;
      } 
    }
    return result;
  }
}

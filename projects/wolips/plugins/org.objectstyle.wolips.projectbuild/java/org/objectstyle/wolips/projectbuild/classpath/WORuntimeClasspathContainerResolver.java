/*
 * Created on 18.08.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.objectstyle.wolips.projectbuild.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author hn3000
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class WORuntimeClasspathContainerResolver
	implements IRuntimeClasspathEntryResolver {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(org.eclipse.jdt.launching.IRuntimeClasspathEntry, org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(
		IRuntimeClasspathEntry entry,
		ILaunchConfiguration configuration
  )
		throws CoreException 
  {    
    IPath path = entry.getClasspathEntry().getPath();
    IJavaProject prj = JavaRuntime.getJavaProject(configuration);
    
    List rawEntries = new ArrayList (Arrays.asList(prj.getRawClasspath()));
    
    IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
    
    Set referencedFrameworks = new HashSet();
    while (!rawEntries.isEmpty()) {
      IClasspathEntry thisOne = (IClasspathEntry)rawEntries.remove(0);
      if (IClasspathEntry.CPE_PROJECT == thisOne.getEntryKind()) {
        String name = thisOne.getPath().lastSegment();
        if (!referencedFrameworks.contains(name)) {
          referencedFrameworks.add(name);
          try {
            IJavaProject refPrj = JavaCore.create(wsRoot.getProject(name));
            rawEntries.addAll (Arrays.asList(refPrj.getRawClasspath()));
          } catch (CoreException up) {
            // ignore, for now
            System.out.println(up);
          }
        }
      }
    }
    
    IPath resultPath = new Path (path.segment(0));
    
    for (int i = 1; i < path.segmentCount(); ++i) {
      String segment = path.segment(i);
      if (!referencedFrameworks.contains(segment)) {
        resultPath = resultPath.append(segment);
      }
    }
    
    WOClasspathContainer rcc = new WOClasspathContainer (resultPath, prj);
    
    IClasspathEntry re[] = rcc.getClasspathEntries();
    IRuntimeClasspathEntry rrce[] = new IRuntimeClasspathEntry[re.length];
    
    for (int i = 0; i < re.length; ++i) {
      rrce[i] = JavaRuntime.newArchiveRuntimeClasspathEntry(re[i].getPath());
    }
    
		return rrce;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver#resolveRuntimeClasspathEntry(org.eclipse.jdt.launching.IRuntimeClasspathEntry, org.eclipse.jdt.core.IJavaProject)
	 */
	public IRuntimeClasspathEntry[] resolveRuntimeClasspathEntry(
		IRuntimeClasspathEntry entry,
		IJavaProject project
  )
		throws CoreException 
  {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeClasspathEntryResolver#resolveVMInstall(org.eclipse.jdt.core.IClasspathEntry)
	 */
	public IVMInstall resolveVMInstall(IClasspathEntry entry)
		throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}

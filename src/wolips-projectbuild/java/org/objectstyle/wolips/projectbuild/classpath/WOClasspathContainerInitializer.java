/**
 * Created on 28.06.2002
 *
 */
package org.objectstyle.wolips.projectbuild.classpath;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author Harald Niesche
 *
 */
public class WOClasspathContainerInitializer
    extends ClasspathContainerInitializer 
{

    /**
     * Constructor for WOClasspathContainerInitializer.
     */
    public WOClasspathContainerInitializer() {
        super();
    }

    /**
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(IPath, IJavaProject)
     */
    public void initialize(IPath containerPath, IJavaProject project)
        throws CoreException 
    {
      //System.out.println ("_ " + containerPath + " _ " + project + " _");
      int size = containerPath.segmentCount();
      if (size > 0) {
      	String firstSegment = containerPath.segment(0); 
        if (
          firstSegment.startsWith(WOClasspathContainer.WOCP_IDENTITY)
          || firstSegment.startsWith(WOClasspathContainer.WOCP_OLD_IDENTITY)
        ) {
          
            JavaCore.setClasspathContainer(
              containerPath, 
              new IJavaProject[] {project}, 
              new IClasspathContainer[] {new WOClasspathContainer (containerPath, project)}, 
              null
            );
        }
      }
    }

}

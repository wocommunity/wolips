/*
 * Created on 01.12.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.core.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IClasspathAccessor {

	public abstract IResource getWOJavaArchive() throws CoreException;
	public abstract IFolder getSubprojectSourceFolder(
		IFolder subprojectFolder, boolean forceCreation);
	public abstract void removeSourcefolderFromClassPath(
				IFolder folderToRemove,
				IProgressMonitor monitor)
				throws InvocationTargetException;
}

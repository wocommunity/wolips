/*
 * Created on 01.12.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.objectstyle.wolips.datasets.project;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IClasspathAccessor {

	public abstract IPath getWOJavaArchive() throws CoreException;
	public abstract IFolder getSubprojectSourceFolder(
		IFolder subprojectFolder, boolean forceCreation);
	public abstract void removeSourcefolderFromClassPath(
				IFolder folderToRemove,
				IProgressMonitor monitor)
				throws InvocationTargetException;
	public abstract IClasspathEntry[] addFrameworkListToClasspathEntries(List frameworkList) throws JavaModelException;
	public abstract IContainer getProjectSourceFolder();
	public abstract void addNewSourcefolderToClassPath(IFolder newSubprojectSourceFolder, IProgressMonitor monitor)
	throws InvocationTargetException;
}

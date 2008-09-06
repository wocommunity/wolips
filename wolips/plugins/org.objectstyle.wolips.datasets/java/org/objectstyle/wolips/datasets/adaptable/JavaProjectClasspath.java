/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
package org.objectstyle.wolips.datasets.adaptable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public class JavaProjectClasspath extends AbstractJavaProjectAdapterType {
	/**
	 * @param project
	 */
	protected JavaProjectClasspath(IProject project) {
		super(project);
	}

	/**
	 * Method addNewSourcefolderToClassPath.
	 * 
	 * @param newSourceFolder
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	public void addNewSourcefolderToClassPath(IFolder newSourceFolder, IProgressMonitor monitor) throws InvocationTargetException {
		// add source classpath entry for project
		IJavaProject actualJavaProject = null;
		IClasspathEntry[] oldClassPathEntries = null;
		IClasspathEntry[] newClassPathEntries = null;
		try {
			actualJavaProject = JavaCore.create(newSourceFolder.getProject());
			oldClassPathEntries = actualJavaProject.getRawClasspath();
		} catch (JavaModelException e) {
			actualJavaProject = null;
			throw new InvocationTargetException(e);
		}
		newClassPathEntries = new IClasspathEntry[oldClassPathEntries.length + 1];
		System.arraycopy(oldClassPathEntries, 0, newClassPathEntries, 1, oldClassPathEntries.length);
		newClassPathEntries[0] = JavaCore.newSourceEntry(newSourceFolder.getFullPath());
		try {
			actualJavaProject.setRawClasspath(newClassPathEntries, monitor);
		} catch (JavaModelException e) {
			actualJavaProject = null;
			oldClassPathEntries = null;
			newClassPathEntries = null;
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Method getSubprojectSourceFolder. Searches classpath source entries for
	 * correspondending subproject source folder (first found source folder in
	 * subproject folder)
	 * 
	 * @param subprojectFolder
	 * @param forceCreation -
	 *            create folder if necessary
	 * @return IFolder
	 */
	public IFolder getSubprojectSourceFolder(IFolder subprojectFolder, boolean forceCreation) {
		// ensure that the folder is a subproject
		if (!IWOLipsModel.EXT_SUBPROJECT.equals(subprojectFolder.getFileExtension())) {
			IFolder parentFolder = this.getParentFolderWithPBProject(subprojectFolder);
			// this belongs to the project and not a subproject
			if (parentFolder == null)
				return subprojectFolder.getProject().getFolder(this.getProjectSourceFolder().getProjectRelativePath());
			subprojectFolder = parentFolder;
		}
		List subprojectFolders = getSubProjectsSourceFolder();
		for (int i = 0; i < subprojectFolders.size(); i++) {
			if (((IFolder) subprojectFolders.get(i)).getFullPath().removeLastSegments(1).equals(subprojectFolder.getFullPath())) {
				return (IFolder) subprojectFolders.get(i);
			}
		}
		if (forceCreation) {
			// no folder found - create new source folder
			IFolder subprojectSourceFolder = subprojectFolder.getProject().getFolder(subprojectFolder.getName() + "/" + IWOLipsModel.EXT_SRC);
			if (!subprojectSourceFolder.exists()) {
				try {
					subprojectSourceFolder.create(true, true, null);
				} catch (CoreException e) {
					DataSetsPlugin.getDefault().getPluginLogger().log(e);
				}
			} // add folder to classpath
			try {
				this.addNewSourcefolderToClassPath(subprojectSourceFolder, null);
			} catch (InvocationTargetException e) {
				DataSetsPlugin.getDefault().getPluginLogger().log(e);
			}
			return subprojectSourceFolder;
		}
		return null;
	}

	/**
	 * Method getProjectSourceFolder. Searches classpath source entries for
	 * project source folder. The project source folder is the first found
	 * source folder the project container contains.
	 * 
	 * @return IContainer found source folder
	 */
	public IContainer getProjectSourceFolder() {
		IClasspathEntry[] classpathEntries;
		try {
			classpathEntries = this.getIJavaProject().getRawClasspath();
		} catch (JavaModelException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
			return null;
		}
		for (int i = 0; i < classpathEntries.length; i++) {
			if (IClasspathEntry.CPE_SOURCE == classpathEntries[i].getEntryKind()) {
				// source entry found
				if (classpathEntries[i].getPath() != null && classpathEntries[i].getPath().removeLastSegments(1).equals(this.getIProject().getFullPath())) {
					// source folder's parent is project
					// project source folder found
					return this.getIProject().getWorkspace().getRoot().getFolder(classpathEntries[i].getPath());
				}
				/*
				 * if (classpathEntries[i].getPath() != null &&
				 * classpathEntries[i].getPath().toString().indexOf( "." +
				 * IWOLipsPluginConstants.EXT_SUBPROJECT + "." +
				 * IWOLipsPluginConstants.EXT_SRC) == -1) { // non subproject
				 * entry found if (classpathEntries[i].getPath().segmentCount() >
				 * 1) { return project.getWorkspace().getRoot().getFolder(
				 * classpathEntries[i].getPath()); } break; }
				 */
			}
		}
		// no source folder found -> create new one
		IFolder projectSourceFolder = this.getIProject().getFolder(IWOLipsModel.EXT_SRC);
		if (!projectSourceFolder.exists()) {
			try {
				projectSourceFolder.create(true, true, null);
			} catch (CoreException e) {
				DataSetsPlugin.getDefault().getPluginLogger().log(e);
			}
		}
		// add to classpath
		try {
			this.addNewSourcefolderToClassPath(projectSourceFolder, null);
		} catch (InvocationTargetException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
		return projectSourceFolder;
	}

	/**
	 * Method getSubProjectsSourceFolder. Searches classpath source entries for
	 * all source folders who's parents are NOT project.
	 * 
	 * @return List
	 */
	public List getSubProjectsSourceFolder() {
		IClasspathEntry[] classpathEntries;
		ArrayList foundFolders = new ArrayList();
		try {
			classpathEntries = this.getIJavaProject().getRawClasspath();
		} catch (JavaModelException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
			return null;
		}
		for (int i = 0; i < classpathEntries.length; i++) {
			if (IClasspathEntry.CPE_SOURCE == classpathEntries[i].getEntryKind()) {
				// source entry found
				if (classpathEntries[i].getPath() != null && !classpathEntries[i].getPath().removeLastSegments(1).equals(this.getIProject().getFullPath())) {
					// source folder's parent is not project
					// project source folder found
					foundFolders.add(this.getIProject().getWorkspace().getRoot().getFolder(classpathEntries[i].getPath()));
				}
				/*
				 * if (classpathEntries[i].getPath() != null &&
				 * classpathEntries[i].getPath().toString().indexOf( "." +
				 * IWOLipsPluginConstants.EXT_SUBPROJECT + "/" +
				 * IWOLipsPluginConstants.EXT_SRC) != -1) { foundFolders.add(
				 * project.getWorkspace().getRoot().getFolder(
				 * classpathEntries[i].getPath())); }
				 */
			}
		}
		return foundFolders;
	}

	private IResource getJar(String prefix, String postfix) {
		IResource result = null;
		String projectName = this.getIProject().getName();
		result = this.getIProject().getFile(prefix + projectName + postfix + "Resources/Java/" + projectName + ".jar");
		if (result == null || !result.exists()) {
			result = this.getIProject().getFile(prefix + projectName + postfix + "Resources/Java/" + projectName.toLowerCase() + ".jar");
		}
		return result;
	}

	/**
	 * Method isTheLaunchAppOrFramework.
	 * 
	 * @param iProject
	 * @return boolean
	 */
	public boolean isAFramework(IProject iProject) {
		IJavaProject buildProject = null;
		try {
			buildProject = this.getIJavaProject();
			Project project = (Project) iProject.getAdapter(Project.class);
			if (project.isFramework() && projectISReferencedByProject(iProject, buildProject.getProject()))
				return true;
		} catch (Exception anException) {
			DataSetsPlugin.getDefault().getPluginLogger().log(anException);
			return false;
		}
		return false;
	}

	/**
	 * Method projectISReferencedByProject.
	 * 
	 * @param child
	 * @param mother
	 * @return boolean
	 */
	public boolean projectISReferencedByProject(IProject child, IProject mother) {
		IProject[] projects = null;
		try {
			projects = mother.getReferencedProjects();
		} catch (Exception anException) {
			DataSetsPlugin.getDefault().getPluginLogger().log(anException);
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].equals(child))
				return true;
		}
		return false;
	}

}
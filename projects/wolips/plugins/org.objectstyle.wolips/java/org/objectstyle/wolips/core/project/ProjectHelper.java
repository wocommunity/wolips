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
package org.objectstyle.wolips.core.project;
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
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.logging.WOLipsLog;
/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 * @deprecated use WOLipsProject's methods instead
 */
public class ProjectHelper implements IWOLipsPluginConstants {

	////////////////// source folder stuff
	/**
	 * Method getProjectSourceFolder. Searches classpath source entries for project source folder.
	 * The project source folder is the first found source folder the project container contains.
	 * @param project
	 * @return IContainer found source folder
	 */
	public static IContainer getProjectSourceFolder(IProject project) {
		IClasspathEntry[] classpathEntries;
		IJavaProject javaProject;
		try {
			javaProject = JavaCore.create(project);
			classpathEntries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			WOLipsLog.log(e);
			return null;
		}
		for (int i = 0; i < classpathEntries.length; i++) {
			if (IClasspathEntry.CPE_SOURCE
				== classpathEntries[i].getEntryKind()) {
				// source entry found
				if (classpathEntries[i].getPath() != null
					&& classpathEntries[i].getPath().removeLastSegments(1).equals(
						project.getFullPath())) {
					// source folder's parent is project
					// project source folder found
					return project.getWorkspace().getRoot().getFolder(
						classpathEntries[i].getPath());
				}
				/*
				if (classpathEntries[i].getPath() != null
						&& classpathEntries[i].getPath().toString().indexOf(
					"."
				+ IWOLipsPluginConstants.EXT_SUBPROJECT
				+ "."
				+ IWOLipsPluginConstants.EXT_SRC)
				== -1) {
					// non subproject entry found
					if (classpathEntries[i].getPath().segmentCount() > 1) {
							return project.getWorkspace().getRoot().getFolder(
						classpathEntries[i].getPath());
					}
						break;
				}
				*/
			}
		}
		// no source folder found -> create new one
		IFolder projectSourceFolder =
			project.getFolder(IWOLipsPluginConstants.EXT_SRC);
		if (!projectSourceFolder.exists()) {
			try {
				projectSourceFolder.create(true, true, null);
			} catch (CoreException e) {
				WOLipsLog.log(e);
			}
		}
		// add to classpath
		try {
			addNewSourcefolderToClassPath(projectSourceFolder, null);
		} catch (InvocationTargetException e) {
			WOLipsLog.log(e);
		}
		return projectSourceFolder;
	}
	/**
	 * Method getSubprojectSourceFolder. Searches classpath source entries for correspondending
	 * subproject source folder (first found source folder in subproject folder)
	 * @param subprojectFolder
	 * @param forceCreation - create folder if necessary
	 * @return IFolder
	 */
	public static IFolder getSubprojectSourceFolder(
		IFolder subprojectFolder,
		boolean forceCreation) {
		//ensure that the folder is a subproject
		if (!EXT_SUBPROJECT.equals(subprojectFolder.getFileExtension())) {
			IFolder parentFolder =
				getParentFolderWithPBProject(subprojectFolder);
			//this belongs to the project and not a subproject
			if (parentFolder == null)
				return subprojectFolder.getProject().getFolder(
					ProjectHelper
						.getProjectSourceFolder(subprojectFolder.getProject())
						.getProjectRelativePath());
			subprojectFolder = parentFolder;
		}
		List subprojectFolders =
			getSubProjectsSourceFolder(subprojectFolder.getProject());
		for (int i = 0; i < subprojectFolders.size(); i++) {
			if (((IFolder) subprojectFolders.get(i))
				.getFullPath()
				.removeLastSegments(1)
				.equals(subprojectFolder.getFullPath())) {
				return (IFolder) subprojectFolders.get(i);
			}
		}
		if (forceCreation) {
			// no folder found - create new source folder
			IFolder subprojectSourceFolder =
				subprojectFolder.getProject().getFolder(
					subprojectFolder.getName()
						+ "/"
						+ IWOLipsPluginConstants.EXT_SRC);
			if (!subprojectSourceFolder.exists()) {
				try {
					subprojectSourceFolder.create(true, true, null);
				} catch (CoreException e) {
					WOLipsLog.log(e);
				}
			} // add folder to classpath
			try {
				addNewSourcefolderToClassPath(subprojectSourceFolder, null);
			} catch (InvocationTargetException e) {
				WOLipsLog.log(e);
			}
			return subprojectSourceFolder;
		}
		return null;
	}
	/**
	 * Method getSubProjectsSourceFolder. Searches classpath source entries for all source
	 * folders who's parents are NOT project.
	 * @param project
	 * @return List
	 */
	public static List getSubProjectsSourceFolder(IProject project) {
		IClasspathEntry[] classpathEntries;
		IJavaProject javaProject;
		ArrayList foundFolders = new ArrayList();
		try {
			javaProject = JavaCore.create(project);
			classpathEntries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			WOLipsLog.log(e);
			return null;
		}
		for (int i = 0; i < classpathEntries.length; i++) {
			if (IClasspathEntry.CPE_SOURCE
				== classpathEntries[i].getEntryKind()) {
				// source entry found
				if (classpathEntries[i].getPath() != null
					&& !classpathEntries[i].getPath().removeLastSegments(
						1).equals(
						project.getFullPath())) {
					// source folder's parent is not project
					// project source folder found
					foundFolders.add(
						project.getWorkspace().getRoot().getFolder(
							classpathEntries[i].getPath()));
				}
				/*
				if (classpathEntries[i].getPath() != null
					&& classpathEntries[i].getPath().toString().indexOf(
						"."
							+ IWOLipsPluginConstants.EXT_SUBPROJECT
							+ "/"
							+ IWOLipsPluginConstants.EXT_SRC)
						!= -1) {
					foundFolders.add(
						project.getWorkspace().getRoot().getFolder(
							classpathEntries[i].getPath()));
				}
				*/
			}
		}
		return foundFolders;
	}
	/**
	 * Method addNewSourcefolderToClassPath.
	 * @param newSourceFolder
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	public static void addNewSourcefolderToClassPath(
		IFolder newSourceFolder,
		IProgressMonitor monitor)
		throws InvocationTargetException {
		// add source classpath entry for project
		IJavaProject actualJavaProject = null;
		IClasspathEntry[] oldClassPathEntries = null;
		IClasspathEntry[] newClassPathEntries = null;
		try {
			actualJavaProject = JavaCore.create(newSourceFolder.getProject());
			oldClassPathEntries = actualJavaProject.getRawClasspath();
		} catch (JavaModelException e) {
			actualJavaProject = null;
			oldClassPathEntries = null;
			throw new InvocationTargetException(e);
		}
		newClassPathEntries =
			new IClasspathEntry[oldClassPathEntries.length + 1];
		System.arraycopy(
			oldClassPathEntries,
			0,
			newClassPathEntries,
			1,
			oldClassPathEntries.length);
		newClassPathEntries[0] =
			JavaCore.newSourceEntry(newSourceFolder.getFullPath());
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
	 * Method getParentFolderWithPBProject.
	 * @param aFolder
	 * @return IFolder or one the parents with PB.project if one is found. Null
	 * is returned when Projects PB.project is found
	 */
	public static IFolder getParentFolderWithPBProject(IFolder aFolder) {
		IFolder findFolder = aFolder;
		while ((findFolder.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
			== null)
			&& (findFolder.getParent() != null)
			&& (findFolder.getParent().getType() != IResource.PROJECT)) {
			findFolder = (IFolder) findFolder.getParent();
		}
		if (findFolder.getParent() == null)
			return null;
		if (findFolder.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
			!= null)
			return findFolder;
		return null;
	}
}

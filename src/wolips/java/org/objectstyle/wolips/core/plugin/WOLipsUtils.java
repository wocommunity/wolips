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
package org.objectstyle.wolips.core.plugin;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.objectstyle.wolips.core.plugin.logging.WOLipsLog;

/**
 * @author mnolte
 *
 */
public class WOLipsUtils implements IWOLipsPluginConstants {

	/**
	 * Constructor for WOLipsUtils.
	 */
	private WOLipsUtils() {
		super();
	}

	/**
		 * Method arrayListFromCSV.
		 * @param csvString
		 * @return ArrayList
		 */
	public static synchronized ArrayList arrayListFromCSV(String csvString) {
		if (csvString == null || csvString.length() == 0) {
			return new ArrayList();
		}
		StringTokenizer valueTokenizer = new StringTokenizer(csvString, ",");
		ArrayList resultList = new ArrayList(valueTokenizer.countTokens());
		while (valueTokenizer.hasMoreElements()) {
			resultList.add(valueTokenizer.nextElement());
		}
		return resultList;
	}

	/**
	 * Method classPathVariableToExpand.
	 * @param aString
	 * @return String
	 */
	public static String classPathVariableToExpand(String aString) {
		if (aString == null)
			return null;
		if (aString.equals("webobjects.next.root"))
			return WOLipsPlugin.getDefault().getWOEnvironment().getWOVariables().systemRoot();
		if (aString.equals("webobjects.system.library.dir"))
			return WOLipsPlugin.getDefault().getWOEnvironment().getWOVariables().libraryDir();
		WOLipsLog.log("Can not resolve classpath variable: " + aString);
		return null;
	}

	/**
	 * Method woTemplateDirectory.
	 * @return String
	 */
	public static String woTemplateDirectory() {
		return "templates";
	}
	/**
	 * Method woTemplateFiles.
	 * @return String
	 */
	public static String woTemplateFiles() {
		return "/wo_file_templates.xml";
	}
	/**
	 * Method woTemplateProject.
	 * @return String
	 */
	public static String woTemplateProject() {
		return "/wo_project_templates.xml";
	}

	// mn: not deleted yet
	// possible to use this class in future
	// e.g.
	// public static void handleException
	// public static DocumentBuilder documentBuilder()

	/*
		public static IContainer getProjectSourceFolder(IProject project) {
			IClasspathEntry[] classpathEntries;
			IJavaProject javaProject;
	
			try {
				javaProject = JavaCore.create(project);
	
				classpathEntries = javaProject.getRawClasspath();
			} catch (JavaModelException e) {
				WOLipsPlugin.log(e);
				return null;
			}
	
			for (int i = 0; i < classpathEntries.length; i++) {
				if (IClasspathEntry.CPE_SOURCE
					== classpathEntries[i].getEntryKind()) {
					// source entry found
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
				}
			}
			// project is source container
			return project;
		}
	
		public static IFolder getSubprojectSourceFolder(IFolder subprojectFolder) {
			List subprojectFolders =
				getSubProjectsSourceFolder(subprojectFolder.getProject());
			for (int i = 0; i < subprojectFolders.size(); i++) {
				if (((IFolder) subprojectFolders.get(i))
					.getName()
					.equals(subprojectFolder.getName() + "." + EXT_SRC)) {
					return (IFolder) subprojectFolders.get(i);
				}
			}
			// no folder found - create new source folder
			IFolder subprojectSourceFolder =
				subprojectFolder.getProject().getFolder(
					subprojectFolder.getName() + "." + EXT_SRC);
			if (!subprojectSourceFolder.exists()) {
				try {
					subprojectSourceFolder.create(true, true, null);
				} catch (CoreException e) {
					WOLipsPlugin.log(e);
				}
			}
			// add
			try {
				addNewSourcefolderToClassPath(subprojectSourceFolder, null);
			} catch (InvocationTargetException e) {
				WOLipsPlugin.log(e);
			}
			return subprojectSourceFolder;
		}
	
		public static List getSubProjectsSourceFolder(IProject project) {
			IClasspathEntry[] classpathEntries;
			IJavaProject javaProject;
			ArrayList foundFolders = new ArrayList();
			try {
				javaProject = JavaCore.create(project);
	
				classpathEntries = javaProject.getRawClasspath();
			} catch (JavaModelException e) {
				WOLipsPlugin.log(e);
				return null;
			}
	
			for (int i = 0; i < classpathEntries.length; i++) {
				if (IClasspathEntry.CPE_SOURCE
					== classpathEntries[i].getEntryKind()) {
					// source entry found
					if (classpathEntries[i].getPath() != null
						&& classpathEntries[i].getPath().toString().indexOf(
							"." + EXT_SUBPROJECT + "." + EXT_SRC)
							!= -1) {
						foundFolders.add(
							project.getWorkspace().getRoot().getFolder(
								classpathEntries[i].getPath()));
					}
				}
			}
			return foundFolders;
		}
	
		public static void addNewSourcefolderToClassPath(
			IFolder newSourceFolder,
			IProgressMonitor monitor)
			throws InvocationTargetException {
			// add source classpath entry for project
			IJavaProject actualJavaProject =
				JavaCore.create(newSourceFolder.getProject());
	
			IClasspathEntry[] oldClassPathEntries;
			try {
				oldClassPathEntries = actualJavaProject.getRawClasspath();
			} catch (JavaModelException e) {
				throw new InvocationTargetException(e);
			}
	
			IClasspathEntry[] newClassPathEntries =
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
				throw new InvocationTargetException(e);
			}
	
		}
	
		public static boolean isWOProjectResource(IResource aResource) {
			if (aResource != null) {
				try {
					switch (aResource.getType()) {
						case IResource.PROJECT :
							return ((IProject) aResource).hasNature(
								WO_APPLICATION_NATURE)
								|| ((IProject) aResource).hasNature(
									WO_FRAMEWORK_NATURE);
	
						default :
							return aResource.getProject() != null
								&& (aResource
									.getProject()
									.hasNature(WO_APPLICATION_NATURE)
									|| aResource.getProject().hasNature(
										WO_FRAMEWORK_NATURE));
					}
				} catch (CoreException e) {
					return false;
				}
			} else {
				return false;
			}
		}
	*/
}

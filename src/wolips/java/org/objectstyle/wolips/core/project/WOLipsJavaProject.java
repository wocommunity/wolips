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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.logging.WOLipsLog;
import org.objectstyle.woproject.util.FileStringScanner;

/**
 * @author uli
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class WOLipsJavaProject extends WOLipsProject {
	IJavaProject javaProject;
	private ClasspathAccessor classpathAccessor;
	private LaunchParameterAccessor launchParameterAccessor;

	/**
	 * @param javaProject
	 */
	public WOLipsJavaProject(IJavaProject javaProject) {
		super(javaProject.getProject());
		this.javaProject = javaProject;
	}
	/**
	 * @return IJavaProject
	 */
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	/**
	 * @return ClasspathAccessor
	 */
	public ClasspathAccessor getClasspathAccessor() {
		if (classpathAccessor == null)
			classpathAccessor = new ClasspathAccessor(this);
		return classpathAccessor;
	}

	/**
	 * @return ClasspathAccessor
	 */
	public LaunchParameterAccessor getLaunchParameterAccessor() {
		if (launchParameterAccessor == null)
			launchParameterAccessor = new LaunchParameterAccessor(this);
		return launchParameterAccessor;
	}

	/**
	 * @author uli
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	private class WOLipsJavaProjectInnerClass {
		private WOLipsJavaProject woLipsJavaProject;
		/**
		 * @param woLipsProject
		 */
		protected WOLipsJavaProjectInnerClass(WOLipsJavaProject woLipsJavaProject) {
			this.woLipsJavaProject = woLipsJavaProject;
		}
		/**
		 * @return IProject
		 */
		protected IProject getProject() {
			return woLipsJavaProject.getProject();
		}
		/**
		 * @return IJavaProject
		 */
		protected IJavaProject getJavaProject() {
			return woLipsJavaProject.getJavaProject();
		}
		/**
		 * @return WOLipsJavaProject
		 */
		protected WOLipsJavaProject getWOLipsJavaProject() {
			return woLipsJavaProject;
		}
	}
	/**
	 * @author uli
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	public class ClasspathAccessor extends WOLipsJavaProjectInnerClass {

		/**
		 * @param wolipsProject
		 */
		protected ClasspathAccessor(WOLipsJavaProject wolipsJavaProject) {
			super(wolipsJavaProject);
		}
		/**
		 * Method addNewSourcefolderToClassPath.
		 * @param newSourceFolder
		 * @param monitor
		 * @throws InvocationTargetException
		 */
		public void addNewSourcefolderToClassPath(
			IFolder newSourceFolder,
			IProgressMonitor monitor)
			throws InvocationTargetException {
			// add source classpath entry for project
			IJavaProject actualJavaProject = null;
			IClasspathEntry[] oldClassPathEntries = null;
			IClasspathEntry[] newClassPathEntries = null;
			try {
				actualJavaProject =
					JavaCore.create(newSourceFolder.getProject());
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
		 * Method getSubprojectSourceFolder. Searches classpath source entries for correspondending
		 * subproject source folder (first found source folder in subproject folder)
		 * @param subprojectFolder
		 * @param forceCreation - create folder if necessary
		 * @return IFolder
		 */
		public IFolder getSubprojectSourceFolder(
			IFolder subprojectFolder,
			boolean forceCreation) {
			//ensure that the folder is a subproject
			if (!EXT_SUBPROJECT.equals(subprojectFolder.getFileExtension())) {
				IFolder parentFolder =
					this
						.getWOLipsJavaProject()
						.getPBProjectFilesAccessor()
						.getParentFolderWithPBProject(subprojectFolder);
				//this belongs to the project and not a subproject
				if (parentFolder == null)
					return subprojectFolder.getProject().getFolder(
						this.getProjectSourceFolder().getProjectRelativePath());
				subprojectFolder = parentFolder;
			}
			List subprojectFolders = getSubProjectsSourceFolder();
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
					this.addNewSourcefolderToClassPath(
						subprojectSourceFolder,
						null);
				} catch (InvocationTargetException e) {
					WOLipsLog.log(e);
				}
				return subprojectSourceFolder;
			}
			return null;
		}
		/**
		 * Method getProjectSourceFolder. Searches classpath source entries for project source folder.
		 * The project source folder is the first found source folder the project container contains.
		 * @param project
		 * @return IContainer found source folder
		 */
		public IContainer getProjectSourceFolder() {
			IClasspathEntry[] classpathEntries;
			try {
				classpathEntries = this.getJavaProject().getRawClasspath();
			} catch (JavaModelException e) {
				WOLipsLog.log(e);
				return null;
			}
			for (int i = 0; i < classpathEntries.length; i++) {
				if (IClasspathEntry.CPE_SOURCE
					== classpathEntries[i].getEntryKind()) {
					// source entry found
					if (classpathEntries[i].getPath() != null
						&& classpathEntries[i].getPath().removeLastSegments(
							1).equals(
							this.getProject().getFullPath())) {
						// source folder's parent is project
						// project source folder found
						return this
							.getProject()
							.getWorkspace()
							.getRoot()
							.getFolder(
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
				this.getProject().getFolder(IWOLipsPluginConstants.EXT_SRC);
			if (!projectSourceFolder.exists()) {
				try {
					projectSourceFolder.create(true, true, null);
				} catch (CoreException e) {
					WOLipsLog.log(e);
				}
			}
			// add to classpath
			try {
				this.addNewSourcefolderToClassPath(projectSourceFolder, null);
			} catch (InvocationTargetException e) {
				WOLipsLog.log(e);
			}
			return projectSourceFolder;
		}
		/**
		 * Method getSubProjectsSourceFolder. Searches classpath source entries for all source
		 * folders who's parents are NOT project.
		 * @param project
		 * @return List
		 */
		public List getSubProjectsSourceFolder() {
			IClasspathEntry[] classpathEntries;
			ArrayList foundFolders = new ArrayList();
			try {
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
							this.getProject().getFullPath())) {
						// source folder's parent is not project
						// project source folder found
						foundFolders.add(
							this
								.getProject()
								.getWorkspace()
								.getRoot()
								.getFolder(
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
		 * Method removeSourcefolderFromClassPath.
		 * @param folderToRemove
		 * @param monitor
		 * @throws InvocationTargetException
		 */
		public void removeSourcefolderFromClassPath(
			IFolder folderToRemove,
			IProgressMonitor monitor)
			throws InvocationTargetException {
			if (folderToRemove != null) {
				IClasspathEntry[] oldClassPathEntries;
				try {
					oldClassPathEntries =
						this.getJavaProject().getRawClasspath();
				} catch (JavaModelException e) {
					oldClassPathEntries = null;
					throw new InvocationTargetException(e);
				}
				IClasspathEntry[] newClassPathEntries =
					new IClasspathEntry[oldClassPathEntries.length - 1];
				int offSet = 0;
				for (int i = 0; i < oldClassPathEntries.length; i++) {
					if (IClasspathEntry.CPE_SOURCE
						== oldClassPathEntries[i].getEntryKind()
						&& oldClassPathEntries[i].getPath().equals(
							folderToRemove.getFullPath())) {
						offSet = 1;
					} else {
						newClassPathEntries[i - offSet] =
							oldClassPathEntries[i];
					}
				}
				if (offSet != 0) {
					try {
						this.getJavaProject().setRawClasspath(
							newClassPathEntries,
							monitor);
					} catch (JavaModelException e) {
						oldClassPathEntries = null;
						newClassPathEntries = null;
						throw new InvocationTargetException(e);
					}
				}
			}
		}
	}

	/**
	 * @author uli
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	public class LaunchParameterAccessor extends WOLipsJavaProjectInnerClass {

		/**
		 * @param wolipsProject
		 */
		protected LaunchParameterAccessor(WOLipsJavaProject wolipsJavaProject) {
			super(wolipsJavaProject);
		}
		
		/**
		 * Method getWOApplicationClassNameArgument.
		 * @return String
		 */
		public String getWOApplicationClassNameArgument(ILaunchConfiguration config) {
			String main = null;
			try {
				main =
					config.getAttribute(
						IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
						"");
			} catch (Exception anException) {
				WOLipsLog.log(anException);
				return "";
			}
			if ("".equals(main))
				return "";
			return main;
		}

		public boolean isOnMacOSX() {
			return WOLipsPlugin
				.getDefault()
				.getWOEnvironment()
				.getWOVariables()
				.systemRoot()
				.startsWith("/System");
		}



		/**
		 * Method projectISReferencedByProject.
		 * @param child
		 * @param mother
		 * @return boolean
		 */
		public boolean projectISReferencedByProject(
			IProject child,
			IProject mother) {
			IProject[] projects = null;
			try {
				projects = mother.getReferencedProjects();
			} catch (Exception anException) {
				WOLipsLog.log(anException);
				return false;
			}
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].equals(child))
					return true;
			}
			return false;
		}

		/**
		 * Method isTheLaunchAppOrFramework.
		 * @param project
		 * @param configuration
		 * @return boolean
		 */
		public boolean isAFramework(
			IProject project,
			ILaunchConfiguration configuration) {
			IJavaProject buildProject = null;
			try {
				buildProject = this.getJavaProject();
				WOLipsProject woLipsProject = new WOLipsProject(project);
				if (woLipsProject.getNaturesAccessor().isFramework()
					&& projectISReferencedByProject(
						project,
						buildProject.getProject()))
					return true;
			} catch (Exception anException) {
				WOLipsLog.log(anException);
				return false;
			}
			return false;
		}

		/**
		 * Method isTheLaunchAppOrFramework.
		 * @param project
		 * @param configuration
		 * @return boolean
		 */
		public boolean isTheLaunchApp(
			IProject project,
			ILaunchConfiguration configuration) {
			IJavaProject buildProject = null;
			try {
				buildProject = this.getJavaProject();
				if (project.equals(buildProject.getProject()))
					return true;
			} catch (Exception anException) {
				WOLipsLog.log(anException);
			}
			return false;
		}

		/**
		 * Method isValidProjectPath.
		 * @param project
		 * @param configuration
		 * @return boolean
		 */
		public boolean isValidProjectPath(
			IProject project,
			ILaunchConfiguration configuration) {
			try {
				return project.getLocation().toOSString().indexOf("-") == -1;
			} catch (Exception anException) {
				WOLipsLog.log(anException);
				return false;
			}
		}

		/**
		 * Method getGeneratedByWOLips.
		 * @return String
		 */
		public String getGeneratedByWOLips(ILaunchConfiguration configuration) {
			String returnValue = "";
			IProject[] projects =
				ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < projects.length; i++) {
				if (isValidProjectPath(projects[i], configuration)) {
					if (isAFramework(projects[i], configuration)) {
						if (returnValue.length() > 0) {
							returnValue = returnValue + ",";
						}
						returnValue =
							returnValue
								+ "\""
								+ projects[i].getLocation().toOSString()
								+ "\"";
					}
					if (isTheLaunchApp(projects[i], configuration)) {
						if (returnValue.length() > 0) {
							returnValue = returnValue + ",";
						}
						returnValue =
							returnValue
								+ "\""
								+ ".."
								+ "\""
								+ ","
								+ "\""
								+ "../.."
								+ "\"";
					}
				}
			}
			returnValue = FileStringScanner.replace(returnValue, "\\", "/");
			returnValue = this.addPreferencesValue(returnValue);
			if ("".equals(returnValue))
				returnValue = "\"" + ".." + "\"";

			returnValue = "(" + returnValue + ")";

			return returnValue;
		}

		/**
		 * Method addPreferencesValue.
		 * @param aString
		 * @return String
		 */
		private String addPreferencesValue(String aString) {
			if (aString == null)
				return aString;
			String nsProjectSarchPath =
				Preferences.getString(
					IWOLipsPluginConstants.PREF_NS_PROJECT_SEARCH_PATH);
			if (nsProjectSarchPath == null || nsProjectSarchPath.length() == 0)
				return aString;
			if (aString.length() > 0)
				aString = aString + ",";
			return aString + nsProjectSarchPath;
		}

		public File getWDFolder(IProject theProject, IPath wd) throws CoreException {
			WOLipsProject wolipsProject = new WOLipsProject(theProject);
			WOLipsProject.NaturesAccessor na = wolipsProject.getNaturesAccessor();

			File wdFile = null;
			if (wd == null) {
				IFolder wdFolder;
				if (na.isAnt()) {
					wdFolder =
						theProject.getFolder(
							"dist/" + theProject.getName() + ".woa");
				} else {
					wdFolder =
						theProject.getFolder(
							"build/" + theProject.getName() + ".woa");
				}
				if (wdFolder == null || !wdFolder.exists()) {
					wdFolder = theProject.getFolder(theProject.getName() + ".woa");
				}
				if (wdFolder != null && !wdFolder.exists()) {
					wdFolder = null;
				} else {
					wdFile = wdFolder.getLocation().toFile();
				}
			}
			return wdFile;
		}


	}
}

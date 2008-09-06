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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.woenvironment.util.FileStringScanner;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.variables.VariablesPlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author ulrich
 * @deprecated Use org.objectstyle.wolips.core.* instead.
 */
public class JavaProjectLaunchParameter extends JavaProjectClasspath {
	/**
	 * @param project
	 */
	protected JavaProjectLaunchParameter(IProject project) {
		super(project);
	}
	
	/**
	 * @return
	 */
	public boolean isOnMacOSX() {
		String osName = System.getProperty("os.name");
		return osName != null && osName.indexOf("Mac") != -1;
	}

	/**
	 * Method isTheLaunchAppOrFramework.
	 * 
	 * @param project
	 * @return boolean
	 */
	public boolean isTheLaunchApp(IProject project) {
		IJavaProject buildProject = null;
		try {
			buildProject = this.getIJavaProject();
			if (project.equals(buildProject.getProject()))
				return true;
		} catch (Exception anException) {
			DataSetsPlugin.getDefault().getPluginLogger().log(anException);
		}
		return false;
	}

	/**
	 * Method isValidProjectPath.
	 * 
	 * @param project
	 * @return boolean
	 */
	public boolean isValidProjectPath(IProject project) {
		try {
			return project.getLocation().toOSString().indexOf("-") == -1;
		} catch (Exception anException) {
			DataSetsPlugin.getDefault().getPluginLogger().log(anException);
			return false;
		}
	}

	protected void addProjectsToSearchPath(IProject project, StringBuffer searchPathBuffer, Set visitedProjects, Set invalidProjects) throws JavaModelException {
		if (!visitedProjects.contains(project)) {
			visitedProjects.add(project);
			if (isTheLaunchApp(project)) {
				searchPathBuffer.append("\"..\",\"../..\"");
			}
			if (isValidProjectPath(project)) {
				if (isAFramework(project)) {
					searchPathBuffer.append(",\"");
					searchPathBuffer.append(project.getLocation().toOSString());
					searchPathBuffer.append("\"");
				}
			} else {
				invalidProjects.add(project);
			}
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject != null) {
				String[] requiredProjectNames = javaProject.getRequiredProjectNames();
				for (int requiredProjectNameNum = 0; requiredProjectNameNum < requiredProjectNames.length; requiredProjectNameNum++) {
					String requiredProjectName = requiredProjectNames[requiredProjectNameNum];
					IProject requiredProject = ResourcesPlugin.getWorkspace().getRoot().getProject(requiredProjectName);
					addProjectsToSearchPath(requiredProject, searchPathBuffer, visitedProjects, invalidProjects);
				}
			}
		}
	}

	/**
	 * Method getGeneratedByWOLips.
	 * 
	 * @param projectSearchPathPreferences
	 * 
	 * @return String
	 * @throws JavaModelException
	 */
	public String getGeneratedByWOLips(String projectSearchPathPreferences) throws JavaModelException {
		StringBuffer searchPathBuffer = new StringBuffer();
		final HashSet invalidProjects = new HashSet();
		addProjectsToSearchPath(getIProject(), searchPathBuffer, new HashSet(), invalidProjects);
		String returnValue = FileStringScanner.replace(searchPathBuffer.toString(), "\\", "/");
		returnValue = this.addPreferencesValue(returnValue, projectSearchPathPreferences);
		if ("".equals(returnValue)) {
			returnValue = "\"" + ".." + "\"";
		}
		returnValue = "(" + returnValue + ")";
		if (!invalidProjects.isEmpty()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					WorkbenchUtilitiesPlugin.errorDialog(WorkbenchUtilitiesPlugin.getActiveWorkbenchShell(), "NSProjectSearchPath", "The projects " + invalidProjects + " contain dashes in their paths.  This breaks NSProjectSearchPath. Rapid turnaround will be disabled.", (Throwable) null);
				}
			});
		}
		return returnValue;
	}

	/**
	 * Method addPreferencesValue.
	 * 
	 * @param aString
	 * @param projectSearchPathPreferences
	 * @return String
	 */
	private String addPreferencesValue(String aString, String projectSearchPathPreferences) {
		if (aString == null)
			return aString;
		String nsProjectSarchPath = projectSearchPathPreferences;
		if (nsProjectSarchPath == null || nsProjectSarchPath.length() == 0)
			return aString;
		if (aString.length() > 0)
			aString = aString + ",";
		return aString + nsProjectSarchPath;
	}

	/**
	 * @param theProject
	 * @param wd
	 * @return
	 * @throws CoreException
	 */
	public File getWDFolder(IProject theProject, IPath wd) throws CoreException {
		File wdFile = null;
		if (wd == null) {
			IFolder wdFolder;
			if (this.isAnt()) {
				wdFolder = theProject.getFolder("dist/" + theProject.getName() + ".woa");
			} else {
				wdFolder = theProject.getFolder("build/" + theProject.getName() + ".woa");
			}
			if (wdFolder != null || !wdFolder.exists()) {
				if (this.isAnt()) {
					wdFolder = theProject.getFolder("dist");
				} else {
					wdFolder = theProject.getFolder("build");
				}
				if (wdFolder != null || !wdFolder.exists()) {
					IResource[] members = wdFolder.members();
					for (int i = 0; i < members.length; i++) {
						IResource member = members[i];
						if (member.getType() == IResource.FOLDER && member.getName().endsWith(".woa")) {
							wdFolder = (IFolder) member;
							break;
						}
					}
				}
			}
			if (wdFolder == null || !wdFolder.exists()) {
				wdFolder = theProject.getFolder(theProject.getName() + ".woa");
			}
			if (wdFolder == null || !wdFolder.exists()) {
				IPath externalRoot = VariablesPlugin.getDefault().getProjectVariables(theProject).getExternalBuildRoot();
				if (externalRoot != null) {
					wdFile = externalRoot.append(this.getIProject().getName() + ".woa").toFile();
				}
			} else {
				wdFile = wdFolder.getLocation().toFile();
			}
		} else {
			wdFile = wd.toFile();
		}
		if (wdFile != null && !wdFile.exists()) {
			wdFile = null;
		}
		return wdFile;
	}
}
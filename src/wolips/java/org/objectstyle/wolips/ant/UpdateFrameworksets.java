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
 package org.objectstyle.wolips.ant;

import java.io.ByteArrayInputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.wo.WOVariables;

/**
 * @author mnolte
 *
 */
public class UpdateFrameworksets extends Task {

	private static Path nextRoot;
	private String projectName;
	private String frameworkIncludesfile;
	private IProject actualProject;
	private IFile frameworkListFile;

	/**
	 * Constructor for UpdateFrameworksets.
	 */
	public UpdateFrameworksets() {
		super();
	}

	public void execute() throws BuildException {
		validateAttributes();
		actualProject =
			ResourcesPlugin.getWorkspace().getRoot().getProject(
				getProjectName());

		frameworkListFile = actualProject.getFile(getFrameworkIncludesfile());

		if (actualProject.exists()) {
			System.out.println("Building framework sets...");

			if (!frameworkListFile.exists()) {
				try {
					frameworkListFile.create(
						new ByteArrayInputStream("".getBytes()),
						true,
						null);
				} catch (CoreException e) {
					System.out.println(
						"Exception while trying to create framework list file: "
							+ e.getMessage());
				}
			}

			// add wo classpath entries
			IJavaProject myJavaProject = JavaCore.create(actualProject);
			IClasspathEntry[] classPaths;
			try {
				classPaths = myJavaProject.getRawClasspath();
			} catch (JavaModelException e) {
				System.out.println(
					"Exception while trying to get classpath entries: "
						+ e.getMessage());
				return;
			}
			StringBuffer newFrameworkEntries = new StringBuffer();
			for (int i = 0; i < classPaths.length; i++) {
				if (classPaths[i].getEntryKind()
					== IClasspathEntry.CPE_LIBRARY) {
					// remove next root from path
					newFrameworkEntries.append(
						classpathEntryToFrameworkEntry(classPaths[i]));
					newFrameworkEntries.append("\n");
				}
			}

			try {
				frameworkListFile.setContents(
					new ByteArrayInputStream(
						newFrameworkEntries.toString().getBytes()),
					true,
					true,
					null);
			} catch (CoreException e) {
				System.out.println(
					"Exception while trying to write framework file: "
						+ e.getMessage());
				return;
			}
			System.out.println("done");

		} else {
			System.out.println(
				"Project " + actualProject.getName() + " does not exist");
		}
	}

	/**
	 * Method validateAttributes.
	 */
	private void validateAttributes() throws BuildException {
		if (projectName == null) {
			throw new BuildException("'projectName' attribute is missing.");
		}

		if (frameworkIncludesfile == null) {
			throw new BuildException("'frameworkIncludesfile' attribute is missing.");
		}
	}

	private String classpathEntryToFrameworkEntry(IClasspathEntry entry) {
		IPath toReturn = null;
		if (entry.getPath().matchingFirstSegments(nextRoot())
			== nextRoot().segmentCount()) {

			// remove next root from path, remove device and make relative
			toReturn =
				entry
					.getPath()
					.removeFirstSegments(nextRoot().segmentCount())
					.setDevice(null)
					.makeRelative();

			for (int i = 0; i < toReturn.segmentCount(); i++) {
				if (toReturn.segment(i).endsWith(".framework")) {
					// remove segments after framework
					toReturn =
						toReturn.removeLastSegments(
							toReturn.segmentCount() - i - 1);
					return toReturn.toOSString();
				}
			}

		}
		return toReturn.toOSString();
	}

	/**
	 * Method nextRoot. Converts next root to Path
	 * @return Path nextRoot as Path
	 */
	private static Path nextRoot() {
		if (nextRoot == null) {
			nextRoot = new Path(WOVariables.nextRoot());
		}
		return nextRoot;
	}

	/**
	 * Returns the frameworkIncludesfile.
	 * @return String
	 */
	public String getFrameworkIncludesfile() {
		return frameworkIncludesfile;
	}

	/**
	 * Returns the projectName.
	 * @return String
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the frameworkIncludesfile.
	 * @param frameworkIncludesfile The frameworkIncludesfile to set
	 */
	public void setFrameworkIncludesfile(String frameworkIncludesfile) {
		this.frameworkIncludesfile = frameworkIncludesfile;
	}

	/**
	 * Sets the projectName.
	 * @param projectName The projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
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
import java.util.HashSet;

import org.apache.tools.ant.BuildException;
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

/**
 * @author mnolte
 *
 */
public class UpdateOtherClasspathIncludeFiles extends UpdateIncludeFiles {


	/**
	 * Constructor for UpdateOtherClasspathIncludeFiles.
	 */
	public UpdateOtherClasspathIncludeFiles() {
		super();
		INCLUDES_FILE_PREFIX = "ant.classpaths";
	}

	public void execute() throws BuildException {
		validateAttributes();
		actualProject =
			ResourcesPlugin.getWorkspace().getRoot().getProject(
				getProjectName());

		//	frameworkListFile = actualProject.getFile(getFrameworkIncludesfile());

		if (actualProject.exists()) {
			System.out.println(
				"Building other classpath sets include files...");
			buildIncludeFiles();
			System.out.println("done");

		} else {
			System.out.println(
				"Project " + actualProject.getName() + " does not exist");
		}
	}

	protected void buildIncludeFiles() throws BuildException {
		// void double entries
		HashSet resolvedEntries = new HashSet();
		// add wo classpath entries
		IJavaProject myJavaProject = JavaCore.create(actualProject);
		IClasspathEntry[] classPaths;
		try {
			classPaths = myJavaProject.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			System.out.println(
				"Exception while trying to get java project classpath entries: "
					+ e.getMessage());
			return;
		}

		IFile currentClasspathListFile;

		for (int i = 0; i < ROOT_PATHS.length; i++) {

			currentClasspathListFile =
				actualProject.getFile(
					INCLUDES_FILE_PREFIX + "." + ROOT_PATHS[i]);

			if (currentClasspathListFile.exists()) {
				// delete old include file
				try {
					currentClasspathListFile.delete(true, null);
				} catch (CoreException e) {
					throw new BuildException(
						"Exception while trying to delete "
							+ currentClasspathListFile.getName()
							+ ": "
							+ e.getMessage());
				}
			}

			if (project.getProperty(ROOT_PATHS[i]) == null) {
				System.out.println(
					"Property " + ROOT_PATHS[i] + " doesn't exists");
				continue;
			}

			StringBuffer newClasspathEntries = new StringBuffer();
			String resolvedEntry;
			for (int j = 0; j < classPaths.length; j++) {
				if (classPaths[j].getEntryKind() == IClasspathEntry.CPE_LIBRARY
					|| classPaths[j].getEntryKind()
						== IClasspathEntry.CPE_VARIABLE) {

					// convert classpath entries to woproject acceptable paths
					resolvedEntry =
						classpathEntryToOtherClasspathEntry(
							classPaths[j],
							new Path(project.getProperty(ROOT_PATHS[i])));

					if (resolvedEntry != null
						&& !resolvedEntries.contains(classPaths[j])) {
						resolvedEntries.add(classPaths[j]);
						newClasspathEntries.append(resolvedEntry);
						newClasspathEntries.append("\n");
					}
				}
			}

			if (newClasspathEntries.length() > 0) {
				try {
					if (currentClasspathListFile.exists()){
						// file may be created by WOBuilder in the meantime
						// no update needed
						return;
					}else{
					// create list file if any entries found
					currentClasspathListFile.create(
						new ByteArrayInputStream(
							newClasspathEntries.toString().getBytes()),
						true,
						null);
					}
				} catch (CoreException e) {
					throw new BuildException(
						"Exception while trying to create "
							+ currentClasspathListFile.getName()
							+ ": "
							+ e.getMessage());
				}
			}
		}
	}
	/**
	 * Method validateAttributes.
	 */
	protected void validateAttributes() throws BuildException {
		if (project == null) {
			throw new BuildException("no project set");
		}
		if (getProjectName() == null) {
			throw new BuildException("'projectName' attribute is missing.");
		}
	}

	private String classpathEntryToOtherClasspathEntry(
		IClasspathEntry entry,
		Path rootDir) {
		String toReturn = null;
		IPath pathToConvert;
		if (entry.getPath().matchingFirstSegments(rootDir)
			== rootDir.segmentCount()) {

			// remove root dir from path, remove device and make relative
			pathToConvert =
				entry
					.getPath()
					.removeFirstSegments(rootDir.segmentCount())
					.setDevice(null)
					.makeRelative();

			// avoid framework entries
			for (int i = 0; i < pathToConvert.segmentCount(); i++) {
				if (pathToConvert.segment(i).endsWith(".framework")) {
					return toReturn;
				}
			}
			toReturn = pathToConvert.toOSString();
		}
		return toReturn;
	}


}
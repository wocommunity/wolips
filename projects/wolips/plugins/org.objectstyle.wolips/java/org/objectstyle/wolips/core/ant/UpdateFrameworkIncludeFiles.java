/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.core.ant;

import java.io.ByteArrayInputStream;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.project.IWOLipsProject;
import org.objectstyle.wolips.core.project.WOLipsCore;
import org.objectstyle.wolips.core.resources.IWOLipsModel;

/**
 * @author mnolte
 *  
 */
public class UpdateFrameworkIncludeFiles extends UpdateIncludeFiles {

	/**
	 * Constructor for UpdateFrameworkIncludeFiles.
	 */
	public UpdateFrameworkIncludeFiles() {
		super();
		INCLUDES_FILE_PREFIX = "ant.frameworks";
	}
	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() {
		buildIncludeFiles();
	}
	/**
	 * @see org.objectstyle.wolips.core.ant.UpdateIncludeFiles#buildIncludeFiles()
	 */
	protected synchronized void buildIncludeFiles() {
		// avoid creating two entries for one classPath Entry
		HashSet resolvedEntries = new HashSet();

		// avoid double entries
		HashSet generatedEntries = new HashSet();

		// add wo classpath entries
		IJavaProject myJavaProject = JavaCore.create(this.getIProject());
		IClasspathEntry[] classPaths;
		try {
			classPaths = myJavaProject.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			WOLipsLog.log(e.getMessage());
			return;
		}

		IFile currentFrameworkListFile;
		// use sorted root paths to ensure correct replacement of
		// classpath entries with framework entries
		for (int i = 0; i < this.getPaths().length; i++) {

			String thisPath = getPaths()[i];

			currentFrameworkListFile =
				this.getIProject().getFile(
					INCLUDES_FILE_PREFIX + "." + rootPaths[i]);

			//if (currentFrameworkListFile.exists()) {
			// delete old include file
			//try {
			//	if (false)
			//		currentFrameworkListFile.delete(true, null);
			//	
			//} catch (CoreException e) {
			//	WOLipsLog.log(e.getMessage());
			//	return;
			//}

			StringBuffer newFrameworkEntries = new StringBuffer();
			String resolvedEntry;
			for (int j = 0; j < classPaths.length; j++) {
				IClasspathEntry thisEntry = classPaths[j];
				if (thisEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY
					|| thisEntry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {

					if (!resolvedEntries.contains(thisEntry)) {
						// convert classpath entries to woproject
						// acceptable paths
						resolvedEntry =
							classpathEntryToFrameworkEntry(thisEntry, thisPath);

						//Uk was sorted
						if (resolvedEntry != null) {
							if (!generatedEntries.contains(resolvedEntry)) {
								generatedEntries.add(resolvedEntry);
								newFrameworkEntries.append(resolvedEntry);
								newFrameworkEntries.append("\n");
							}
							resolvedEntries.add(thisEntry);
						}
					}
				}
			}
			if (thisPath.toString().equals(this.getWOLocalRoot())) {
				IProject[] referencedProjects;
				try {
					referencedProjects = getIProject().getReferencedProjects();
				} catch (CoreException e1) {
					WOLipsLog.log(e1);
					referencedProjects = null;
				}
				if (referencedProjects != null) {
					for (int j = 0; j < referencedProjects.length; j++) {
						if (referencedProjects[j].isAccessible()
							&& referencedProjects[j].isOpen()) {
							try {
								IWOLipsProject referencedWOLipsProject =
									WOLipsCore.createProject(
										referencedProjects[j]);
								if (referencedWOLipsProject != null
									&& referencedWOLipsProject
										.getNaturesAccessor()
										.hasWOLipsNature()
									&& referencedWOLipsProject
										.getNaturesAccessor()
										.isFramework()) {
									newFrameworkEntries.append(
										"Library/Frameworks/");
									newFrameworkEntries.append(
										referencedWOLipsProject
											.getProject()
											.getName());
									newFrameworkEntries.append(".");
									newFrameworkEntries.append(
										IWOLipsModel.EXT_FRAMEWORK);
									newFrameworkEntries.append("\n");
								}
							} catch (CoreException e1) {
								WOLipsLog.log(e1);
							}
						}
					}
				}
			}
			if (newFrameworkEntries.length() == 0) {
				newFrameworkEntries.append(
					"An empty file result in a full filesystem scan");
				newFrameworkEntries.append("\n");
			}
			try {
				if (currentFrameworkListFile.exists()) {
					// file may be created by WOBuilder in the meantime
					// no update needed
					currentFrameworkListFile.setContents(
						new ByteArrayInputStream(
							newFrameworkEntries.toString().getBytes()),
						true,
						true,
						null);
				} else {
					// create list file if any entries found
					currentFrameworkListFile.create(
						new ByteArrayInputStream(
							newFrameworkEntries.toString().getBytes()),
						true,
						null);
				}
			} catch (CoreException e) {
				WOLipsLog.log(e.getMessage());
				return;
			}
		}
	}

	/**
	 * Method classpathEntryToFrameworkEntry.
	 * 
	 * @param entry
	 * @param rootDir
	 * @return String
	 */
	private String classpathEntryToFrameworkEntry(
		IClasspathEntry entry,
		String rootDir) {

		IPath pathToConvert;

		// determine if entry's path begins with rootDir
		// Quoting the JavaDoc:
		// "To be a prefix, this path's segments must appear
		// in the argument path in the same order, and their device ids
		// must match."
		if (!entry.getPath().toOSString().startsWith(rootDir))
			return null;

		// remove root dir from path, remove device and make relative
		pathToConvert = entry.getPath();

		for (int i = pathToConvert.segmentCount(); i > 0; i--) {
			if (pathToConvert.segment(i - 1).endsWith(".framework")) {
				// remove segments after framework
				pathToConvert =
					pathToConvert.removeLastSegments(
						pathToConvert.segmentCount() - i);
				break;
			}
		}

		String result = null;

		if (pathToConvert != null) {
			pathToConvert = pathToConvert.setDevice(null);

			//pathToConvert =
			//	pathToConvert.removeFirstSegments(rootDir.segmentCount());
			result = pathToConvert.toOSString();
			result = result.substring(rootDir.length());
			if (result.startsWith("/") || result.startsWith("\\"))
				result = result.substring(1);
		}

		return result;
	}

}
/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2004 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.jdt.ant;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.resources.types.folder.IWoprojectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.variables.VariablesPlugin;

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
		this.INCLUDES_FILE_PREFIX = "ant.frameworks";
	}

	/**
	 * 
	 */
	public void execute() {
		buildIncludeFiles();
	}

	/**
	 * @see org.objectstyle.wolips.jdt.ant.UpdateIncludeFiles#buildIncludeFiles()
	 */
	protected synchronized void buildIncludeFiles() {
		// avoid creating two entries for one classPath Entry
		Set<IClasspathEntry> resolvedEntries = new HashSet<IClasspathEntry>();

		// avoid double entries
		Set<String> generatedEntries = new HashSet<String>();

		// add wo classpath entries
		IJavaProject javaProject = JavaCore.create(this.getIProject());
		if (javaProject != null) {
			IClasspathEntry[] classPaths;
			try {
				classPaths = javaProject.getResolvedClasspath(true);
			} catch (JavaModelException e) {
				JdtPlugin.getDefault().getPluginLogger().log(e);
				return;
			}
			IProjectAdapter projectAdaptor = this.getProjectAdapter();
			if (projectAdaptor != null) {
				IWoprojectAdapter woProjectAdaptor = projectAdaptor.getWoprojectAdapter();
				if (woProjectAdaptor != null) {
					IFile currentFrameworkListFile;
					// use sorted root paths to ensure correct replacement of
					// classpath entries with framework entries
					for (int i = 0; i < this.getPaths().length; i++) {
						String thisPath = getPaths()[i].toOSString();
						currentFrameworkListFile = woProjectAdaptor.getUnderlyingFolder().getFile(this.INCLUDES_FILE_PREFIX + "." + this.rootPaths[i]);
	
						// if (currentFrameworkListFile.exists()) {
						// delete old include file
						// try {
						// if (false)
						// currentFrameworkListFile.delete(true, null);
						//	
						// } catch (CoreException e) {
						// WOLipsLog.log(e.getMessage());
						// return;
						// }
			
						StringBuffer newFrameworkEntries = new StringBuffer();
						String resolvedEntry;
						for (int j = 0; j < classPaths.length; j++) {
							IClasspathEntry thisEntry = classPaths[j];
							if (thisEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY || thisEntry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
			
								if (!resolvedEntries.contains(thisEntry)) {
									// convert classpath entries to woproject
									// acceptable paths
									resolvedEntry = classpathEntryToFrameworkEntry(thisEntry, thisPath);
			
									// Uk was sorted
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
						if (thisPath.equals(VariablesPlugin.getDefault().getLocalRoot().toOSString())) {
							IProject[] referencedProjects;
							try {
								referencedProjects = getIProject().getReferencedProjects();
							} catch (CoreException e1) {
								JdtPlugin.getDefault().getPluginLogger().log(e1);
								referencedProjects = null;
							}
							if (referencedProjects != null) {
								for (int j = 0; j < referencedProjects.length; j++) {
									if (referencedProjects[j].isAccessible() && referencedProjects[j].isOpen()) {
										IProjectAdapter referencedWOLipsProject = (IProjectAdapter) (referencedProjects[j]).getAdapter(IProjectAdapter.class);
										if (referencedWOLipsProject != null && referencedWOLipsProject.isFramework()) {
											newFrameworkEntries.append("Library/Frameworks/");
											newFrameworkEntries.append(referencedWOLipsProject.getUnderlyingProject().getName());
											newFrameworkEntries.append(".");
											newFrameworkEntries.append("framework");
											newFrameworkEntries.append("\n");
										}
									}
								}
							}
						}
						if (newFrameworkEntries.length() == 0) {
							newFrameworkEntries.append("An empty file result in a full filesystem scan");
							newFrameworkEntries.append("\n");
						}
						try {
							if (currentFrameworkListFile.exists()) {
								// file may be created by WOBuilder in the meantime
								// no update needed
								currentFrameworkListFile.setContents(new ByteArrayInputStream(newFrameworkEntries.toString().getBytes()), true, true, null);
							} else {
								// create list file if any entries found
								this.getProjectAdapter().getWoprojectAdapter().getUnderlyingFolder().create(false, true, new NullProgressMonitor());
								currentFrameworkListFile.create(new ByteArrayInputStream(newFrameworkEntries.toString().getBytes()), true, null);
							}
						} catch (CoreException e) {
							JdtPlugin.getDefault().getPluginLogger().log(e.getMessage());
							return;
						}
					}
				}
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
	private String classpathEntryToFrameworkEntry(IClasspathEntry entry, String rootDir) {

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
				pathToConvert = pathToConvert.removeLastSegments(pathToConvert.segmentCount() - i);
				break;
			}
		}

		String result = null;

		if (pathToConvert != null) {
			pathToConvert = pathToConvert.setDevice(null);

			// pathToConvert =
			// pathToConvert.removeFirstSegments(rootDir.segmentCount());
			result = pathToConvert.toOSString();
			result = result.substring(new Path(rootDir).setDevice(null).toOSString().length());
			if (result.startsWith("/") || result.startsWith("\\"))
				result = result.substring(1);
		}

		return result;
	}

}
/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.jdt.ant;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.jdt.JdtPlugin;

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
		this.INCLUDES_FILE_PREFIX = "ant.classpaths";
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
	protected void buildIncludeFiles() {
		// avoid double entries
		Set<IClasspathEntry> resolvedEntries = new HashSet<IClasspathEntry>();
		// add wo classpath entries
		IJavaProject myJavaProject = JavaCore.create(this.getIProject());
		IClasspathEntry[] classPaths;
		try {
			classPaths = myJavaProject.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			JdtPlugin.getDefault().getPluginLogger().log(e.getMessage());
			return;
		}

		IFile currentClasspathListFile;

		for (int i = 0; i < getPaths().length; i++) {

			currentClasspathListFile = this.getProjectAdapter().getWoprojectAdapter().getUnderlyingFolder().getFile(this.INCLUDES_FILE_PREFIX + "." + this.rootPaths[i]);
			// System.out.println("currentClasspathListFile: " +
			// currentClasspathListFile.toString());
			if (currentClasspathListFile.exists()) {
				// delete old include file
				try {
					if (false)
						currentClasspathListFile.delete(true, null);
				} catch (CoreException e) {
					JdtPlugin.getDefault().getPluginLogger().log(e.getMessage());
					return;
				}
			}

			StringBuffer newClasspathEntries = new StringBuffer();
			String resolvedEntry;
			for (int j = 0; j < classPaths.length; j++) {
				if (classPaths[j].getEntryKind() == IClasspathEntry.CPE_LIBRARY || classPaths[j].getEntryKind() == IClasspathEntry.CPE_VARIABLE) {

					// convert classpath entries to woproject acceptable paths
					resolvedEntry = classpathEntryToOtherClasspathEntry(classPaths[j], getPaths()[i].toOSString());

					if (resolvedEntry != null && !resolvedEntries.contains(classPaths[j])) {
						resolvedEntries.add(classPaths[j]);
						newClasspathEntries.append(resolvedEntry);
						newClasspathEntries.append("\n");
					}
				}
			}

			if (newClasspathEntries.length() == 0) {
				newClasspathEntries.append("An empty file result in a full filesystem scan");
				newClasspathEntries.append("\n");
			}
			try {
				if (currentClasspathListFile.exists()) {
					currentClasspathListFile.setContents(new ByteArrayInputStream(newClasspathEntries.toString().getBytes()), true, true, null);
				} else {
					// create list file if any entries found
					IFolder woprojectFolder = this.getProjectAdapter().getWoprojectAdapter().getUnderlyingFolder();
					if (!woprojectFolder.exists()) {
						woprojectFolder.create(false, true, new NullProgressMonitor());
					}
					currentClasspathListFile.create(new ByteArrayInputStream(newClasspathEntries.toString().getBytes()), true, null);
				}
			} catch (CoreException e) {
				JdtPlugin.getDefault().getPluginLogger().log(e.getMessage());
				return;
			}
		}
	}

	/**
	 * Method classpathEntryToOtherClasspathEntry.
	 * 
	 * @param entry
	 * @param rootDir
	 * @return String
	 */
	private String classpathEntryToOtherClasspathEntry(IClasspathEntry entry, String rootDir) {
		// System.out.println("classpathEntryToOtherClasspathEntry");
		// System.out.println("entry: " + entry);
		// System.out.println("rootDir" + rootDir);
		String toReturn = null;
		IPath pathToConvert;
		if (entry.getPath().toOSString().startsWith(rootDir)) {

			// remove root dir from path, remove device and make relative
			pathToConvert = entry.getPath();
			/*
			 * .removeFirstSegments(rootDir.segmentCount()) .setDevice(null)
			 * .makeRelative();
			 */

			// avoid framework entries
			for (int i = 0; i < pathToConvert.segmentCount(); i++) {
				if (pathToConvert.segment(i).endsWith(".framework")) {
					return toReturn;
				}
			}
			toReturn = pathToConvert.toOSString().substring(rootDir.length());
		}
		// System.out.println("toReturn: " + toReturn);
		// System.out.println("classpathEntryToOtherClasspathEntry return");
		return toReturn;
	}
}
/*
 * ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *
 */
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.wizards.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

public class EOModelImportSupport {

	/**
	 * Import EOModels to a specified IProject. The paths dictionary is keyed
	 * with the model name including extension and the values contain the full
	 * path to the source .eomodel file.
	 *
	 * @param paths
	 * @param project
	 * @throws IOException
	 */
	public static void importEOModelsToProject(HashMap<String, String> paths, IProject project) throws IOException {
		// Move any specified models over
		File path = project.getLocation().toFile();
		File resourcesPath = new File(path, "Resources");
		if (resourcesPath.exists()) {
			path = resourcesPath;
		}
		File aFile, destFile = null;

		for (String aFileName : paths.keySet()) {
			aFile = new File(paths.get(aFileName.trim()));
			destFile = new File(path, aFileName);
			IFolder existingModelFolder = project.getFolder(new Path(destFile.getAbsolutePath()));

			// don't overwrite existing eomodel
			if (existingModelFolder.exists()) {
				ErrorDialog.openError(new Shell(), "EOModel Exists", existingModelFolder.getFullPath().toOSString() + " already exists.", new Status(IStatus.ERROR, "org.objectstyle.wolips.wizards", existingModelFolder.getFullPath().toOSString() + " already exists."));
			} else if (aFile.exists()) {
				destFile.mkdirs();
				// Use CopyFilesAndFoldersOperation.copyFiles() instead??
				EOModelImportSupport.copyDirectoryFilesToDirectory(aFile, destFile, project);
			}
		}
	}

	/**
	 * @param paths
	 * @param project
	 * @param dstPath
	 * @throws IOException
	 */
	public static void importEOModelsToProject(HashMap<String, String> paths, IProject project, String dstPath) throws IOException {
		// Move any specified models over
		File path = null;
		if (dstPath == null) {
			path = project.getLocation().toFile();
			File resourcesPath = new File(path, "Resources");
			if (resourcesPath.exists()) {
				path = resourcesPath;
			}
		} else {
			path = new File(dstPath);
		}

		File aFile, destFile = null;

		for (String aFileName : paths.keySet()) {
			aFile = new File(paths.get(aFileName.trim()));
			destFile = new File(path, aFileName);
			IFolder existingModelFolder = project.getFolder(new Path(destFile.getAbsolutePath()));

			// don't overwrite existing eomodel
			if (existingModelFolder.exists()) {
				ErrorDialog.openError(new Shell(), "EOModel Exists", existingModelFolder.getFullPath().toOSString() + " already exists.", new Status(IStatus.ERROR, "org.objectstyle.wolips.wizards", existingModelFolder.getFullPath().toOSString() + " already exists."));
			} else if (aFile.exists()) {
				destFile.mkdirs();
				// Use CopyFilesAndFoldersOperation.copyFiles() instead??
				EOModelImportSupport.copyDirectoryFilesToDirectory(aFile, destFile, project);
			}
		}
	}

	/**
	 * @param src
	 * @param dst
	 * @param project
	 * @throws IOException
	 */
	public static void copyFile(File src, File dst, IProject project) throws IOException {
		FileChannel in = null, out = null;
		ByteBuffer buffer = null;
		try {
			in = new FileInputStream(src).getChannel();
			try {
				out = new FileOutputStream(dst).getChannel();
				buffer = ByteBuffer.allocate(1024);
				while (in.read(buffer) != -1) {
					buffer.flip(); // Prepare for writing
					out.write(buffer);
					buffer.clear(); // Prepare for reading
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			project.getFile(dst.getCanonicalPath());
		}
	}

	// copy files in a root directory to a destination directory
	/**
	 * @param srcRootFile
	 * @param dstRootFile
	 * @param project
	 * @throws IOException
	 */
	public static void copyDirectoryFilesToDirectory(File srcRootFile, File dstRootFile, IProject project) throws IOException {
		File[] children = srcRootFile.listFiles();
		for (File child : children) {
			if (child.isFile()) {
				EOModelImportSupport.copyFile(child, new File(dstRootFile, child.getName()), project);
				project.getFolder(new File(dstRootFile, child.getName()).getCanonicalPath());
			}
		}
	}
}

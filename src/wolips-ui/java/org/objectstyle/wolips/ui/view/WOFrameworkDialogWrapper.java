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

package org.objectstyle.wolips.ui.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.SelectFilesOperation;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * Wrapper of FileSelectionDialog to select jars from given
 * file root object. The FileSelectionDialog displays only jars
 * not already added to classpath.
 * <br>
 * @author mnolte
 */
public class WOFrameworkDialogWrapper {

	public static Log log = LogFactory.getLog(WOFrameworkDialogWrapper.class);
	//private static Path nextRootAsPath = new Path(WOVariables.nextRoot());

	private FileSelectionDialog dialog;
	private IJavaProject projectToUpdate;
	private IWorkbenchPart part;
	private IClasspathEntry[] oldClasspathEntries;
	private IPath frameworkRootPath;
	private IPath expandedClassPathVariable;
	private String classPathVariableName;

	/**
	 * Constructor for WOFrameworkDialogWrapper.
	 * @param part actual workbench part
	 * @param projectToUpdate selected webobjects project to update
	 * @param fileRoot file root for framework selection
	 */
	public WOFrameworkDialogWrapper(
		IWorkbenchPart part,
		IJavaProject projectToUpdate,
		String classPathVariableName) {
		super();
		this.part = part;
		this.classPathVariableName = classPathVariableName;
		// expand class path	
		expandedClassPathVariable =
			JavaCore.getClasspathVariable(classPathVariableName);
		if (expandedClassPathVariable == null) {
			WOLipsPlugin.handleException(
				part.getSite().getShell(),
				new Exception(
					"unable to resolve eclipse class path variable: "
						+ classPathVariableName),
				"unable to resolve eclipse class path variable: "
					+ classPathVariableName);
			return;
		}
		frameworkRootPath = expandedClassPathVariable.append("Library/Frameworks");
		// get old class path values to limit selection on FileSystemElement (see below)
		this.projectToUpdate = projectToUpdate;
		try {
			oldClasspathEntries = this.projectToUpdate.getRawClasspath();
		} catch (JavaModelException e) {
			WOLipsPlugin.handleException(part.getSite().getShell(), e, null);
			return;
		}

		FrameworkRootOperation op =
			new FrameworkRootOperation(frameworkRootPath.toFile());

		try {
			new ProgressMonitorDialog(
				part.getSite().getWorkbenchWindow().getShell()).run(
				false,
				false,
				op);
			//part.getSite().getWorkbenchWindow().run(false, false, op);
		} catch (InvocationTargetException e) {
			WOLipsPlugin.handleException(part.getSite().getShell(), e, null);
		} catch (InterruptedException e) {
			WOLipsPlugin.handleException(part.getSite().getShell(), e, null);
		}
		//MessageDialog.openInformation(this.part.getSite().getShell(), "Error", "Selection is not a folder!");
		FileSystemElement elem = op.getResult();

		dialog =
			new FileSelectionDialog(
				part.getSite().getShell(),
				elem,
				Messages.getString("WOFrameworkDialogWrapper.message"));

		dialog.setTitle(Messages.getString("WOFrameworkDialogWrapper.title"));

	}

	public void executeDialog() {

		Object[] result = null;
		dialog.open();

		if (dialog.getReturnCode() != Window.OK)
			return;
		result = dialog.getResult();

		FileSystemElement currentFileElement;
		String currentFileName;
		IPath currentNewClasspath;
		IClasspathEntry[] newClasspathEntries =
			new IClasspathEntry[oldClasspathEntries.length + result.length];

		// copy old classpath entries to new classpath entries
		for (int i = 0; i < oldClasspathEntries.length; i++) {
			newClasspathEntries[i] = oldClasspathEntries[i];
		}

		// add new classpath entries
		for (int i = 0; i < result.length; i++) {
			currentFileElement = (FileSystemElement) result[i];
			currentFileName =
				((File) currentFileElement.getFileSystemObject())
					.getAbsolutePath();
			currentNewClasspath = new Path(currentFileName);

			// determine if new class path begins with file root path
			if ((currentNewClasspath.segmentCount()
				> frameworkRootPath.segmentCount())
				&& currentNewClasspath
					.removeLastSegments(
						currentNewClasspath.segmentCount()
							- frameworkRootPath.segmentCount())
					.equals(frameworkRootPath)) {

				// replace beginning of class path with correspondending
				// class path variable
				currentNewClasspath =
					new Path(classPathVariableName).append(
						currentNewClasspath.removeFirstSegments(
							expandedClassPathVariable.segmentCount()));

				// set path as variable entry			
				newClasspathEntries[i + oldClasspathEntries.length] =
					JavaCore.newVariableEntry(currentNewClasspath, null, null);

			} else {
				newClasspathEntries[i + oldClasspathEntries.length] =
					JavaCore.newLibraryEntry(currentNewClasspath, null, null);
			}
		}

		try {
			projectToUpdate.setRawClasspath(newClasspathEntries, null);
		} catch (JavaModelException e) {
			WOLipsPlugin.handleException(part.getSite().getShell(), e, null);
		}

	}
	/**
	 * @author uli To change this generated comment edit the template variable
	 * "typecomment": Window>Preferences>Java>Templates. To enable and disable
	 * the creation of type comments go to Window>Preferences>Java>Code
	 * Generation.
	 */
	private class FrameworkRootOperation extends SelectFilesOperation {

		/**
		 * Constructor for FrameworkRootOperation.
		 * @param rootObject
		 * @param structureProvider
		 */
		public FrameworkRootOperation(File fileRoot) {
			super(fileRoot, FileSystemStructureProvider.INSTANCE);
			String[] extArray = { "jar", "zip" };
			setDesiredExtensions(extArray);

		}
		/**
		 * @see org.eclipse.ui.wizards.datatransfer.SelectFilesOperation#createElement(org.eclipse.ui.dialogs.FileSystemElement, java.lang.Object)
		 */
		protected FileSystemElement createElement(
			FileSystemElement parent,
			Object fileSystemObject)
			throws InterruptedException {
			FileSystemElement toReturn =
				super.createElement(parent, fileSystemObject);

			if (fileSystemObject == null)
				return toReturn;

			File fileToAdd = (File) fileSystemObject;

			File javaDir = null;
			File resourcesDir = null;
			File frameworkDir = null;
			String fileExtensionToAdd = getExtensionFor(fileToAdd.getName());
			if (fileToAdd.isFile()
				&& ("jar".equals(fileExtensionToAdd) || "zip".equals(fileExtensionToAdd))) {

				// ensure correct framework path
				boolean isValidPath = false;
				javaDir = fileToAdd.getParentFile();
				if (javaDir != null && "Java".equals(javaDir.getName())) {
					resourcesDir = javaDir.getParentFile();
					if (resourcesDir != null
						&& "Resources".equals(resourcesDir.getName())) {
						frameworkDir = resourcesDir.getParentFile();
						if (frameworkDir != null
							&& frameworkDir.getName().endsWith(".framework")) {
							isValidPath = true;
						}
					}
				}
				if (!isValidPath) {
					return null;
				}

				IClasspathEntry[] resolvedOldClasspathEntries;
				try {
					resolvedOldClasspathEntries =
						projectToUpdate.getResolvedClasspath(true);
				} catch (JavaModelException e) {
					WOLipsPlugin.handleException(
						part.getSite().getShell(),
						e,
						null);
					return null;
				} // now look through resolved old class path entries and deny entries already set
				for (int i = 0; i < resolvedOldClasspathEntries.length; i++) {
					if (resolvedOldClasspathEntries[i]
						.getPath()
						.toFile()
						.equals(fileToAdd)) {
						return null;
					}
				}

			}
			return toReturn;
		}
	}

}

package org.objectstyle.wolips.ui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

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
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.env.Environment;
import org.objectstyle.wolips.wizards.Messages;
import org.objectstyle.wolips.wo.WOVariables;


/**
 * Wrapper of FileSelectionDialog to select jars from given
 * file root object. The FileSelectionDialog displays only jars
 * not already added to classpath.
 * <br>
 * @author mnolte
 */
public class WOFrameworkDialogWrapper {

	private static Path nextRootAsPath = new Path(WOVariables.nextRoot());

	private FileSelectionDialog dialog;
	private IJavaProject projectToUpdate;
	private IWorkbenchPart part;
	private IClasspathEntry[] oldClasspathEntries;

	/**
	 * Constructor for WOFrameworkDialogWrapper.
	 * @param part actual workbench part
	 * @param projectToUpdate selected webobjects project to update
	 * @param fileRoot file root for framework selection
	 */
	public WOFrameworkDialogWrapper(
		IWorkbenchPart part,
		IJavaProject projectToUpdate,
		File fileRoot) {
		super();
		this.part = part;

		// get old class path values to limit selection on FileSystemElement (see below)
		this.projectToUpdate = projectToUpdate;
		try {
			oldClasspathEntries = this.projectToUpdate.getRawClasspath();
		} catch (JavaModelException e) {
			WOLipsPlugin.handleException(part.getSite().getShell(), e, null);
			return;
		}

		FrameworkRootOperation op = new FrameworkRootOperation(fileRoot);

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

		dialog.setTitle(
			Messages.getString("WOFrameworkDialogWrapper.title"));

	}

	public void executeDialog() {

		Object[] result = null;
		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			result = dialog.getResult();
		} else {
			return;
		}

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

			// determine if new class path begins with next root
			if ((currentNewClasspath.segmentCount()
				> nextRootAsPath.segmentCount())
				&& currentNewClasspath
					.removeLastSegments(
						currentNewClasspath.segmentCount()
							- nextRootAsPath.segmentCount())
					.equals(nextRootAsPath)) {

				// replace beginning of class path with next root
				currentNewClasspath =
					new Path(Environment.NEXT_ROOT).append(
						currentNewClasspath.removeFirstSegments(
							nextRootAsPath.segmentCount()));

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

	private class FrameworkRootOperation extends SelectFilesOperation {

		/**
		 * Constructor for FrameworkRootOperation.
		 * @param rootObject
		 * @param structureProvider
		 */
		public FrameworkRootOperation(File fileRoot) {
			super(fileRoot, FileSystemStructureProvider.INSTANCE);
			String[] extArray = { "jar" };
			setDesiredExtensions(extArray);

		}

		protected FileSystemElement createElement(
			FileSystemElement parent,
			Object fileSystemObject)
			throws InterruptedException {
			FileSystemElement toReturn =
				super.createElement(parent, fileSystemObject);

			if (fileSystemObject != null) {

				File fileToAdd = (File) fileSystemObject;

				if (fileToAdd.isFile()
					&& "jar".equals(getExtensionFor(fileToAdd.getName()))) {

					// must be jar (see above), ensure no web server resources are added
					if (fileToAdd
						.getParentFile()
						.getParentFile()
						.getName()
						.equals(WOVariables.webServerResourcesDirName())) {
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
					}

					// now look through resolved old class path entries and deny entries already set
					for (int i = 0; i < resolvedOldClasspathEntries.length; i++) {
						if (resolvedOldClasspathEntries[i]
							.getPath()
							.toFile()
							.equals(fileToAdd)) {
							return null;
						}
					}

				}
			}

			return toReturn;
		}
	}

}

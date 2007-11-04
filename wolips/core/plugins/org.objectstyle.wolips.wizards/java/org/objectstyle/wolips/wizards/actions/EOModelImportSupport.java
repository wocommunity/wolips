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
	 * @return
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

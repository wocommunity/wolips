package org.objectstyle.wolips.wizards.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	 * Import EOModels to a specified IProject.  The paths dictionary is keyed with the model name including extension and
	 * the values contain the full path to the source .eomodel file.
	 * @param paths
	 * @param project
	 * @return
	 */
	public static boolean importEOModelsToProject(HashMap <String, String> paths, IProject project) {
		//Move any specified models over
		String path = project.getLocation().toOSString();
		File aFile, destFile = null;

		for (String aFileName : paths.keySet()) {
			aFile = new File(paths.get(aFileName.trim()));
			destFile = new File(path+File.separator+aFileName);
			IFolder existingModelFolder = project.getFolder(new Path(destFile.getAbsolutePath()));

			//don't overwrite existing eomodel
			if (existingModelFolder.exists()) {
				ErrorDialog.openError(new Shell(),
						"EOModel Exists",
						existingModelFolder.getFullPath().toOSString()+" already exists.",
						new Status(IStatus.ERROR, "org.objectstyle.wolips.wizards" , existingModelFolder.getFullPath().toOSString()+" already exists."));
				return false;
			}

			if (aFile.exists()) {
				destFile.mkdirs();
				//Use CopyFilesAndFoldersOperation.copyFiles() instead??
				if (!copyDirectoryFilesToDirectory(aFile, destFile, project)) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean copyFile(String src, String dst, IProject project) {
		FileChannel in = null, out = null;
		ByteBuffer buffer = null;
		try {
			in = new FileInputStream(src).getChannel();
			out = new FileOutputStream(dst).getChannel();
			buffer = ByteBuffer.allocate(1024);
			while (in.read(buffer) != -1) {
				buffer.flip(); // Prepare for writing
				out.write(buffer);
				buffer.clear(); // Prepare for reading
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// swallow
			}
			project.getFile(dst);
		}
		return true;
	}

	//copy files in a root directory to a destination directory
	public static boolean copyDirectoryFilesToDirectory(File srcRootFile, File dstRootFile, IProject project) {
		String[] list = srcRootFile.list();
		for (String aFileName : list) {
			boolean result = copyFile(srcRootFile.getAbsolutePath()+File.separator+aFileName, dstRootFile.getAbsolutePath()+File.separator+aFileName, project);
			if (!result) {
				return false;
			}
			project.getFolder(dstRootFile.getAbsolutePath()+File.separator+aFileName);

		}
		return true;
	}
}

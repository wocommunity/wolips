package org.objectstyle.wolips.wizards.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;

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
			if (aFile.exists()) {
				destFile.mkdirs();
				//Use CopyFilesAndFoldersOperation.copyFiles() instead??
				if (!copyDirectoryFilesToDirectory(aFile, destFile)) {
					System.out.println("Copy failed");
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static boolean copyFile(String src, String dst) {
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
		}
		return true;
	}

	//copy files in a root directory to a destination directory
	public static boolean copyDirectoryFilesToDirectory(File srcRootFile, File dstRootFile) {
		String[] list = srcRootFile.list();
		for (String aFileName : list) {
			boolean result = copyFile(srcRootFile.getAbsolutePath()+File.separator+aFileName, dstRootFile+File.separator+aFileName);
			if (!result) {
				return false;
			}
		}
		return true;
	}
}

package org.objectstyle.wolips.baseforplugins.util;

import java.io.File;

import org.eclipse.core.resources.IResource;

/**
 * Utilities for working with IResource and its implementations.
 * 
 * @author mschrag
 */
public class ResourceUtilities {
	/**
	 * Returns the name of the given resource with its extension removed.
	 * 
	 * @param resource the resource
	 * @return the name of the resource without the extension
	 */
	public static String getFileNameWithoutExtension(IResource resource) {
		String fileName = null;
		if (resource != null) {
			fileName = ResourceUtilities.getFileNameWithoutExtension(resource.getName());
		}
		return fileName;
	}
	
	/**
	 * Returns the name of the given file with its extension removed.
	 * 
	 * @param file the file
	 * @return the name of the file without the extension
	 */
	public static String getFileNameWithoutExtension(File file) {
		String fileName = null;
		if (file != null) {
			fileName = ResourceUtilities.getFileNameWithoutExtension(file.getName());
		}
		return fileName;
	}
	
	/**
	 * Returns the name of the given file with its extension removed.
	 * 
	 * @param fileName the name of the file
	 * @return the name of the resource without the extension
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		String fileNameWithoutExtension = fileName;
		if (fileNameWithoutExtension != null) {
			int lastDotIndex = fileNameWithoutExtension.lastIndexOf('.');
			if (lastDotIndex != -1) {
				fileNameWithoutExtension = fileNameWithoutExtension.substring(0, lastDotIndex);
			}
		}
		return fileNameWithoutExtension;
	}
}

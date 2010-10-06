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
	
	/**
	 * Returns the language name for the given component file  
	 * or null if it's not in an lproj folder.
	 * 
	 * @param file the file to lookup the language for
	 * @return the language name (or null)
	 */
	public static String getLocalizationName(IResource file)
	{
		String language = null;
		if (file != null && file.exists()) {
			boolean done = false;
			IResource resource = file;
			do {
				resource = resource.getParent();
				if (resource == null) {
					done = true;
				}
				else {
					String name = resource.getName();
					if (name.endsWith(".lproj")) {
						language = name.substring(0, name.length()-".lproj".length());
						done = true;
					}
				}
			} while (!done);
		}
		return language;
	}
}

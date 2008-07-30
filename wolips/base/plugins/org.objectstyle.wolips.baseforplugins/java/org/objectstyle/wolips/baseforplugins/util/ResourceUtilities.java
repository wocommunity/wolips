package org.objectstyle.wolips.baseforplugins.util;

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
		String fileName = resource.getName();
		String fileExtension = resource.getFileExtension();
		if (fileExtension != null) {
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		return fileName;
	}
}

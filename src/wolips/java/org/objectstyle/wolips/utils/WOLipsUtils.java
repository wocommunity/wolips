package org.objectstyle.wolips.utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.IWOLipsPluginConstants;

/**
 * @author mnolte
 *
 */
public class WOLipsUtils {

	/**
	 * Constructor for WOLipsUtils.
	 */
	private WOLipsUtils() {
		super();
	}
	
	public static IContainer getProjectSourceFolder(IProject project) {
		IClasspathEntry[] classpathEntries;
		IJavaProject javaProject;

		try {
			javaProject = JavaCore.create(project);

			classpathEntries = javaProject.getRawClasspath();
		} catch (JavaModelException e) {
			WOLipsPlugin.log(e);
			return null;
		}

		for (int i = 0; i < classpathEntries.length; i++) {
			if (IClasspathEntry.CPE_SOURCE
				== classpathEntries[i].getEntryKind()) {
				// source entry found
				if (classpathEntries[i].getPath() != null
					&& classpathEntries[i].getPath().toString().indexOf(
						"." + IWOLipsPluginConstants.SUBPROJECT + "." + IWOLipsPluginConstants.SRC)
						== -1) {
					// non subproject entry found
					if (classpathEntries[i].getPath().segmentCount() > 1) {
						return project.getWorkspace().getRoot().getFolder(
							classpathEntries[i].getPath());
					}
					break;
				}
			}
		}
		// project is source container
		return project;
	}

}

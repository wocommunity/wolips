package org.objectstyle.wolips.ide;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class WOClasspathUpdater {

	/**
	 * Constructor for WOClasspathUpdater.
	 */
	private WOClasspathUpdater() {
		super();
	}

	public static void update()  {
		//IPath jarPath = new Path("$NEXT_ROOT/System/Library/Frameworks/JavaWebObjects.framework/Resources/Java/javawebobjects.jar");
		//System.out.println("jarPath: " +jarPath.toOSString());
		//JavaCore.newLibraryEntry(jarPath, null, null, true);
	}
}

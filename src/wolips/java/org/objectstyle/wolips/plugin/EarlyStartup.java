package org.objectstyle.wolips.plugin;

import java.net.URL;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.env.Environment;
import org.objectstyle.wolips.io.WOLipsLog;
import org.objectstyle.wolips.listener.JavaElementChangeListener;
import org.objectstyle.wolips.listener.ResourceChangeListener;
import org.objectstyle.wolips.workbench.WorkbenchHelper;

/**
 * @author uli
 *
* Adds listeners for resource and java classpath changes to keep
* webobjects project file synchronized.
*/
public class EarlyStartup {

	//private static IResourceChangeListener resourceChangeListener;
	//private static IElementChangedListener javaElementChangeListener;

	/**
		 * Adds listeners for resource and java classpath changes to keep
		 * webobjects project file synchronized.
		 * <br>
		 * @see org.eclipse.ui.IStartup#earlyStartup()
		 */
	public static void earlyStartup() {
		try {
			EarlyStartup.writePropertiesFileToUserHome();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			WOLipsLog.log(
				IWOLipsPluginConstants.build_user_home_properties_pde_info);
		}
		EarlyStartup.validateMandatoryAttributes();
		// add resource change listener to update project file on resource changes
		IResourceChangeListener resourceChangeListener = new ResourceChangeListener();
		WorkbenchHelper.getWorkspace().addResourceChangeListener(
			resourceChangeListener,
			IResourceChangeEvent.PRE_AUTO_BUILD);
		// add element change listener to update project file on classpath changes
		IElementChangedListener javaElementChangeListener = new JavaElementChangeListener();
		JavaCore.addElementChangedListener(
			javaElementChangeListener,
			ElementChangedEvent.PRE_AUTO_BUILD);
	}
	/**
	 * Method writePropertiesFileToUserHome.
	 */
	private static void writePropertiesFileToUserHome() throws Exception {
		AntRunner antRunner = new AntRunner();
		URL relativeBuildFile =
			new URL(
				WOLipsPlugin.baseURL(),
				IWOLipsPluginConstants.build_user_home_properties);
		URL buildFile = Platform.asLocalURL(relativeBuildFile);
		antRunner.setBuildFileLocation(buildFile.getPath());
		antRunner.run();
	}

	private static void validateMandatoryAttributes() {
		if (JavaCore.getClasspathVariable(Environment.NEXT_ROOT) == null) {
			try {
				JavaCore.setClasspathVariable(
					Environment.NEXT_ROOT,
					new Path(Environment.nextRoot()),
					null);
			} catch (JavaModelException e) {
				WOLipsLog.log(e);
			}
		}
	}
}

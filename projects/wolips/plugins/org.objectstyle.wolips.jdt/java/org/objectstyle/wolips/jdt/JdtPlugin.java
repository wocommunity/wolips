package org.objectstyle.wolips.jdt;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.jdt.listener.JavaElementChangeListener;

/**
 * The main plugin class to be used in the desktop.
 */
public class JdtPlugin extends AbstractUIPlugin implements IStartup {
	private final static String PLUGIN_ID = "org.objectstyle.wolips.jdt.JdtPlugin";
	//The shared instance.
	private static JdtPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public JdtPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.objectstyle.wolips.jdt.JdtPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static JdtPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the PluginID.
	 * @return
	 */
	public static String getPluginId() {
		if (plugin != null) {
			return getDefault().getDescriptor().getUniqueIdentifier();
		} else
			return JdtPlugin.PLUGIN_ID;
	}

	/**
	 * Prints a Status.
	 * @param e
	 */
	public static void log(IStatus status) {
		JdtPlugin.getDefault().getLog().log(status);
	}

	/**
	 * Prints a Throwable.
	 * @param e
	 */
	public static void log(Throwable e) {
		JdtPlugin.log(new Status(IStatus.ERROR, JdtPlugin.getPluginId(), IStatus.ERROR, "Internal Error", e)); //$NON-NLS-1$
	}
	
	/**
	 * Prints a message.
	 * @param message
	 */
	public static void log(String message) {
		JdtPlugin.log(
			new Status(
				IStatus.ERROR,
				JdtPlugin.getPluginId(),
				IStatus.ERROR,
				message,
				null));
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = JdtPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	/**
	 * Method baseURL.
	 * @return URL
	 */
	public static URL baseURL() {
		return JdtPlugin.getDefault().getDescriptor().getInstallURL();
	}
	

	/**
	 * Calls EarlyStartup.earlyStartup().
	 * <br>
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		// add element change listener to update project file on classpath changes
		IElementChangedListener javaElementChangeListener =
			new JavaElementChangeListener();
		JavaCore.addElementChangedListener(
			javaElementChangeListener,
			ElementChangedEvent.POST_CHANGE);
	}
}

package org.objectstyle.wolips.jdt;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class JdtPlugin extends AbstractUIPlugin {
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
	public void logError(String string) {
		this.getLog().log(		new Status(
				IStatus.ERROR,
				VariablesPlugin.getPluginId(),
				IStatus.ERROR,
				string,
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
		
}

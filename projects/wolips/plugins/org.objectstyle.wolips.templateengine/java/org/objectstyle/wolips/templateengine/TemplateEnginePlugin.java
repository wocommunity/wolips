package org.objectstyle.wolips.templateengine;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class TemplateEnginePlugin extends AbstractUIPlugin {
	//The shared instance.
	private static TemplateEnginePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 * @param descriptor
	 */
	public TemplateEnginePlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("org.objectstyle.wolips.templateengine.TemplateenginePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 * @return
	 */
	public static TemplateEnginePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 * @return
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * @param key
	 * @return
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= TemplateEnginePlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * @return
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}

package org.objectstyle.wolips.baseforplugins;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractBaseActivator {
	// The shared instance.
	private static Activator plugin;

	/**
	 * The constructor.
	 */
	public Activator() {
		super();
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}


}

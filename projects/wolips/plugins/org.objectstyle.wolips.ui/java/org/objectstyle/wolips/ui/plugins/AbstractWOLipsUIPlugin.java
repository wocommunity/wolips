package org.objectstyle.wolips.ui.plugins;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public abstract class AbstractWOLipsUIPlugin extends AbstractUIPlugin {
	// Resource bundle.
	private ResourceBundle resourceBundle;

	// The shared instance.
	private static AbstractWOLipsUIPlugin Plugin;

	private boolean debug = true;

	private String bundleID;

	/**
	 * The constructor.
	 */
	public AbstractWOLipsUIPlugin() {
		super();
		AbstractWOLipsUIPlugin.Plugin = this;
	}

	public final String getBundleID() {
		if (bundleID == null) {
			bundleID = this.getClass().getName() + "Resources";
		}
		return bundleID;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		this.resourceBundle = null;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public final static String getResourceString(String key) {
		ResourceBundle bundle = AbstractWOLipsUIPlugin.Plugin
				.getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public final ResourceBundle getResourceBundle() {
		try {
			if (this.resourceBundle == null)
				this.resourceBundle = ResourceBundle.getBundle(this
						.getBundleID());
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
		return this.resourceBundle;
	}

	/**
	 * Prints an IStatus.
	 * 
	 * @param status
	 */
	public void log(IStatus status) {
		this.getLog().log(status);
	}

	/**
	 * Prints a message.
	 * 
	 * @param message
	 */
	public void log(Object message) {
		this.log(new Status(IStatus.ERROR, this.getBundleID(), IStatus.ERROR,
				"" + message, null));
	}

	/**
	 * Prints a Throwable.
	 * 
	 * @param e
	 */
	public void log(Throwable e) {
		this.log(new Status(IStatus.ERROR, this.getBundleID(), IStatus.ERROR,
				"Internal Error", e)); //$NON-NLS-1$
	}

	/**
	 * Prints a Throwable.
	 * 
	 * @param message
	 * @param e
	 */
	public void log(Object message, Throwable e) {
		this.log(new Status(IStatus.ERROR, this.getBundleID(), IStatus.ERROR,
				"" + message, e)); //$NON-NLS-1$
	}

	/**
	 * If debug is true this method prints an Exception to the log.
	 * 
	 * @param aThrowable
	 */
	public void debug(Throwable aThrowable) {
		if (this.debug) {
			this.log(new Status(IStatus.WARNING, this.getBundleID(),
					IStatus.WARNING, aThrowable.getMessage(), aThrowable)); //$NON-NLS-1$
		}
	}

	/**
	 * @param message
	 * @param t
	 */
	public void debug(Object message, Throwable t) {
		if (this.debug) {
			this.log(new Status(IStatus.WARNING, this.getBundleID(),
					IStatus.WARNING, "" + message, t)); //$NON-NLS-1$
		}
	}

	/**
	 * @param message
	 */
	public void debug(Object message) {
		if (this.debug) {
			this.log(new Status(IStatus.WARNING, this.getBundleID(),
					IStatus.WARNING, "" + message, null)); //$NON-NLS-1$
		}
	}

}

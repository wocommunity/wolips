package org.objectstyle.wolips.maven.launching;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.core.runtime.AbstractCorePlugin;
import org.objectstyle.wolips.maven.core.mavenlaunchers.IMavenLauncher;
import org.objectstyle.wolips.maven.core.mavenlaunchers.internal.MavenLauncherWrapper;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MavenLaunchingPlugin extends AbstractCorePlugin {
	// The shared instance.
	private static MavenLaunchingPlugin plugin;

	/**
	 * The constructor.
	 */
	public MavenLaunchingPlugin() {
		super();
		plugin = this;
	}

	/**
	 * @return Returns the shared instance.
	 */
	public static MavenLaunchingPlugin getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

}

/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.maven.core;

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
public class MavenCorePlugin extends AbstractCorePlugin implements IMavenLauncher {
	// The shared instance.
	private static MavenCorePlugin plugin;

	private MavenLauncherWrapper[] mavenLauncherWrapper;

	private static final String EXTENSION_POINT_ID = "org.objectstyle.wolips.maven.core.mavenLaunchers";

	/**
	 * The constructor.
	 */
	public MavenCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * @return Returns the shared instance.
	 */
	public static MavenCorePlugin getDefault() {
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

	/**
	 * @return The mavenLauncher defined in the mavenLauncher extension point
	 * @throws CoreException
	 */
	private void loadMavenLauncherExtensionPoint() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(EXTENSION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configurationElements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < configurationElements.length; j++) {
				IConfigurationElement configurationElement = configurationElements[j];
				IMavenLauncher currentMavenLauncher = null;
				try {
					currentMavenLauncher = (IMavenLauncher) configurationElement
							.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					arrayList.add(new MavenLauncherWrapper(currentMavenLauncher, name));
				} catch (CoreException e) {
					this.log(
							"Could not create executable from configuration element: "
									+ configurationElement, e);
				}
			}
		}
		this.mavenLauncherWrapper = (MavenLauncherWrapper[]) arrayList
				.toArray(new MavenLauncherWrapper[arrayList.size()]);
	}

	public MavenLauncherWrapper[] getMavenLauncherWrapper() {
		if (this.mavenLauncherWrapper == null) {
			loadMavenLauncherExtensionPoint();
		}
		ArrayList mavenLauncherWrapperList = new ArrayList();
		for (int i = 0; i < mavenLauncherWrapper.length; i++) {
			MavenLauncherWrapper currentMavenLauncherWrapper = mavenLauncherWrapper[i];
			mavenLauncherWrapperList.add(currentMavenLauncherWrapper);
		}
		return (MavenLauncherWrapper[]) mavenLauncherWrapperList
				.toArray(new MavenLauncherWrapper[mavenLauncherWrapperList.size()]);
	}

	public void launchMavenInExternalVM(IFile buildFile, IProgressMonitor monitor, boolean captureOutput, String targets) throws CoreException {
		this.getMavenLauncherWrapper()[0].getMavenLauncher().launchMavenInExternalVM(buildFile, monitor, captureOutput, targets);
	}

}

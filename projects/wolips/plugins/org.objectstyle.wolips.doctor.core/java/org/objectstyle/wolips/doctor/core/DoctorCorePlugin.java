/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.doctor.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.objectstyle.wolips.commons.logging.PluginLogger;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author ulrich
 */
public class DoctorCorePlugin extends Plugin {
	private final static String PLUGIN_ID = "org.objectstyle.wolips.doctor.core";

	private PluginLogger pluginLogger;

	// The shared instance.
	private static DoctorCorePlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	private IDoctor[] issueProvider;

	private ArrayList issueListener;

	/**
	 * The constructor.
	 */
	public DoctorCorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.pluginLogger = new PluginLogger(DoctorCorePlugin.PLUGIN_ID, false);

		/*DoctorWorkspaceJob doctorWorkspaceJob = new DoctorWorkspaceJob();
		doctorWorkspaceJob.setRule(ResourcesPlugin.getWorkspace().getRoot());
		doctorWorkspaceJob.schedule(6000);*/
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
		issueProvider = null;
		issueListener = null;
	}

	/**
	 * @return the shared instance.
	 */
	public static DoctorCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * @param key
	 * @return Returns the string from the plugin's resource bundle, or 'key' if
	 *         not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DoctorCorePlugin.getDefault()
				.getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * @return Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle
						.getBundle("org.objectstyle.wolips.doctor.core.CorePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * @return Returns the pluginLogger.
	 */
	public PluginLogger getPluginLogger() {
		return this.pluginLogger;
	}

	/**
	 * @return The issue provider defined in the issueprovider extension point
	 * @throws CoreException
	 */
	protected IDoctor[] getIssueProvider() throws CoreException {
		if (issueProvider == null) {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(
							"org.objectstyle.wolips.doctor.core.issueProvider");
			IExtension[] extensions = extensionPoint.getExtensions();
			ArrayList arrayList = new ArrayList();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configurationElements = extensions[i]
						.getConfigurationElements();
				for (int j = 0; j < configurationElements.length; j++) {
					IDoctor issueProviderElement = (IDoctor) configurationElements[j]
							.createExecutableExtension("class");
					arrayList.add(issueProviderElement);
				}
			}
			issueProvider = (IDoctor[]) arrayList.toArray(new IDoctor[arrayList
					.size()]);
		}
		return issueProvider;
	}

	/**
	 * @return the PluginID
	 */
	public static String getPluginId() {
		return DoctorCorePlugin.PLUGIN_ID;
	}

	protected void forwardIssues(IIssue[] issues) {
		if (issueListener != null) {
			Iterator iterator = issueListener.iterator();
			while (iterator.hasNext()) {
				IIssueListener currentIssueListener = (IIssueListener)iterator.next();
				currentIssueListener.issuesChanged(issues);
			}
		}
	}

	/**
	 * @param newIssueListener
	 */
	public void addIssueListener(IIssueListener newIssueListener) {
		if (issueListener == null) {
			issueListener = new ArrayList();
		}
		this.issueListener.add(newIssueListener);
	}

	/**
	 * @param newIssueListener
	 */
	public void removeIssueListener(IIssueListener newIssueListener) {
		if (issueListener == null) {
			return;
		}
		this.issueListener.remove(newIssueListener);
	}
}

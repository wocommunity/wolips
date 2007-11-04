/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group
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

package org.objectstyle.wolips.templateengine;

import java.net.URL;
import java.util.Dictionary;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.objectstyle.wolips.baseforplugins.logging.PluginLogger;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * The main plugin class to be used in the desktop.
 */
public class TemplateEnginePlugin extends AbstractBaseUIActivator {
	private final static String PLUGIN_ID = "org.objectstyle.wolips.templateengine";

	// The shared instance.
	private static TemplateEnginePlugin plugin;

	private PluginLogger pluginLogger = null;

	/**
	 * Comment for <code>WOApplicationProject</code>
	 */
	public static final String WOApplicationProject = "WOApplicationProject";

	/**
	 * Comment for <code>WOnderApplicationProject</code>
	 */
	public static final String WOnderApplicationProject = "WOnderApplicationProject";

	/**
	 * Comment for <code>D2W_ApplicationProject</code>
	 */
	/**
	 * Comment for <code>D2W_ApplicationProject</code>
	 */
	public static final String D2W_ApplicationProject = "D2W_ApplicationProject";

	/**
	 * Comment for <code>WOFrameworkProject</code>
	 */
	public static final String WOFrameworkProject = "WOFrameworkProject";

	/**
	 * Comment for <code>JarProject</code>
	 */
	public static final String JarProject = "JarProject";

	/**
	 * Comment for <code>WOComponent</code>
	 */
	public static final String WOComponent = "WOComponent";

	/**
	 * Comment for <code>EOModel</code>
	 */
	public static final String EOModel = "EOModel";

	/**
	 * The constructor.
	 */
	public TemplateEnginePlugin() {
		super();
		plugin = this;
	}

	/**
	 * @return Returns the shared instance.
	 */
	public static TemplateEnginePlugin getDefault() {
		return plugin;
	}

	/**
	 * @return Returns the PluginID.
	 */
	public static String getPluginId() {
		if (plugin != null) {
			Dictionary dictionary = plugin.getBundle().getHeaders();
			String pluginID = (String) dictionary.get(Constants.BUNDLE_NAME);
			return pluginID;
		}
		return null;
	}

	/**
	 * Returns the workspace instance.
	 * 
	 * @return
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Method baseURL.
	 * 
	 * @return URL
	 */
	public static URL baseURL() {
		return TemplateEnginePlugin.getDefault().getDescriptor().getInstallURL();
	}

	/**
	 * @return Returns the pluginLogger.
	 */
	protected PluginLogger getPluginLogger() {
		return this.pluginLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.pluginLogger = new PluginLogger(TemplateEnginePlugin.PLUGIN_ID, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		this.pluginLogger = null;
	}
}
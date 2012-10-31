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
package org.objectstyle.wolips.jdt;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.baseforplugins.logging.PluginLogger;
import org.objectstyle.wolips.jdt.classpath.model.EclipseFrameworkModel;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class JdtPlugin extends AbstractUIPlugin {
	private final static String PLUGIN_ID = "org.objectstyle.wolips.jdt.JdtPlugin";

	// The shared instance.
	private static JdtPlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	private PluginLogger pluginLogger = null;

	private Map<String, EclipseFrameworkModel> frameworkModels;

	/**
	 * The constructor.
	 */
	public JdtPlugin() {
		super();
		plugin = this;
		this.frameworkModels = new HashMap<String, EclipseFrameworkModel>();
		try {
			this.resourceBundle = ResourceBundle.getBundle("org.objectstyle.wolips.jdt.JdtPluginResources");
		} catch (MissingResourceException x) {
			this.resourceBundle = null;
		}
	}

	/**
	 * @return the shared instance.
	 */
	public static JdtPlugin getDefault() {
		return plugin;
	}

	/**
	 * @return the string from the plugin's resource bundle, or 'key' if not
	 *         found.
	 * @param key
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
	 * @return Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return this.resourceBundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.pluginLogger = new PluginLogger(JdtPlugin.PLUGIN_ID, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		this.frameworkModels = null;
		this.pluginLogger = null;
	}

	/**
	 * @return Returns the pluginLogger.
	 */
	public PluginLogger getPluginLogger() {
		return this.pluginLogger;
	}

	public synchronized void invalidateFrameworkModel() {
		//System.out.println("JdtPlugin.invalidateFrameworkModel: invalidating");
		EclipseFrameworkModel.invalidateProjectRootCache();
		this.frameworkModels.clear();
	}
	
	/**
	 * @return Returns the classpathModel.
	 */
	public synchronized EclipseFrameworkModel getFrameworkModel(IProject project) {
		String projectName = project != null ? project.getName() : null;
		EclipseFrameworkModel frameworkModel = this.frameworkModels.get(projectName);
		if (frameworkModel == null || frameworkModel.shouldReload()) {
			frameworkModel = new EclipseFrameworkModel(project);
			this.frameworkModels.put(projectName, frameworkModel);
		}
		return frameworkModel;
	}

	/**
	 * @return
	 */
	public static String getPluginId() {
		return JdtPlugin.PLUGIN_ID;
	}
}
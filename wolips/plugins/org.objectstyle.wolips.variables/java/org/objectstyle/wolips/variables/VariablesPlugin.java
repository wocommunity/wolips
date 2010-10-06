/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group
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
package org.objectstyle.wolips.variables;

import java.util.Dictionary;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.wolips.baseforplugins.AbstractBaseActivator;
import org.objectstyle.wolips.preferences.Preferences;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * The main plugin class to be used in the desktop.
 */
public class VariablesPlugin extends AbstractBaseActivator {
	// The shared instance.
	private static VariablesPlugin plugin;

	//private WOEnvironment woEnvironment;

	/**
	 * The constructor.
	 */
	public VariablesPlugin() {
		super();
		plugin = this;
	}

	/**
	 * @return the shared instance
	 */
	public static VariablesPlugin getDefault() {
		return plugin;
	}

	public ProjectVariables getGlobalVariables() {
		return new ProjectVariables(getWOVariables(null, null));
	}

	public ProjectVariables getGlobalVariables(String wolipsPropertiesFile) {
		return new ProjectVariables(getWOVariables(null, wolipsPropertiesFile));
	}

	public ProjectVariables getProjectVariables(IProject project) {
		return new ProjectVariables(getWOVariables(project, null));
	}

	private WOVariables getWOVariables(IProject project, String wolipsPropertiesFile) {
		//if (this.woEnvironment == null) {
		//	this.woEnvironment = new WOEnvironment(null);
		//}
		Properties defaultProperties = new Properties();
		String defaultPropertiesFile = wolipsPropertiesFile;
		if (defaultPropertiesFile == null || defaultPropertiesFile.length() == 0) {
			defaultPropertiesFile = Preferences.getString(Preferences.PREF_WOLIPS_PROPERTIES_FILE);
		}
		if (defaultPropertiesFile != null && defaultPropertiesFile.length() > 0) {
			defaultProperties.put(WOVariables.WOLIPS_PROPERTIES, defaultPropertiesFile);
		}

		if (project != null) {
			BuildProperties buildPropertiesAdapter = (BuildProperties) project.getAdapter(BuildProperties.class);
			Properties buildProperties = null;
			if (buildPropertiesAdapter != null) {
				buildProperties = buildPropertiesAdapter.getProperties();
			}
			if (buildProperties != null) {
				defaultProperties.putAll(buildProperties);
			}
		}

		WOVariables variables = new WOEnvironment(defaultProperties).getWOVariables();
		return variables;
	}

	/**
	 * @return the plugin id
	 */
	public static String getPluginId() {
		if (plugin != null) {
			Dictionary dictionary = plugin.getBundle().getHeaders();
			String pluginID = (String) dictionary.get(Constants.BUNDLE_NAME);
			return pluginID;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
}
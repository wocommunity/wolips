/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractBaseUIActivator {
	public static final String CHECK_ICON = "check";

	public static final String EOMODEL_ICON = "eoModel";

	public static final String EOENTITY_ICON = "eoEntity";

	public static final String EOATTRIBUTE_ICON = "eoAttribute";

	public static final String EORELATIONSHIP_ICON = "eoRelationship";

	public static final String EOFETCHSPEC_ICON = "eoFetchSpec";

	public static final String EOSTOREDPROCEDURE_ICON = "eoStoredProcedure";

	public static final String EODATABASECONFIG_ICON = "eoDatabaseConfig";

	public static final String TO_MANY_ICON = "toMany";

	public static final String TO_ONE_ICON = "toOne";

	public static final String PRIMARY_KEY_ICON = "primaryKey";

	public static final String LOCKING_ICON = "locking";

	public static final String CLASS_PROPERTY_ICON = "classProperty";

	public static final String ALLOW_NULL_ICON = "allowNull";

	public static final String ASCENDING_ICON = "ascending";

	public static final String DESCENDING_ICON = "descending";

	public static final String SQL_ICON = "sql";

	// The plug-in ID
	public static final String PLUGIN_ID = "org.objectstyle.wolips.eomodeler";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	protected void initializeImageRegistry(ImageRegistry _reg) {
		super.initializeImageRegistry(_reg);
		_reg.put(Activator.PRIMARY_KEY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/primaryKey.gif"));
		_reg.put(Activator.LOCKING_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/locking.gif"));
		_reg.put(Activator.CLASS_PROPERTY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/classProperty.gif"));
		_reg.put(Activator.CHECK_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/check.gif"));
		_reg.put(Activator.EOMODEL_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoModel.png"));
		_reg.put(Activator.EOATTRIBUTE_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoAttribute.png"));
		_reg.put(Activator.EOENTITY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoEntity.png"));
		_reg.put(Activator.EOFETCHSPEC_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoFetchSpecification.png"));
		_reg.put(Activator.EOSTOREDPROCEDURE_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoStoredProcedure.png"));
		_reg.put(Activator.EORELATIONSHIP_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoRelationship.png"));
		_reg.put(Activator.EODATABASECONFIG_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/eoDatabaseConfig.png"));
		_reg.put(Activator.TO_MANY_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/toMany.gif"));
		_reg.put(Activator.TO_ONE_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/toOne.gif"));
		_reg.put(Activator.ASCENDING_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/ascending.png"));
		_reg.put(Activator.DESCENDING_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/descending.png"));
		_reg.put(Activator.SQL_ICON, AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/sql.gif"));
	}
}

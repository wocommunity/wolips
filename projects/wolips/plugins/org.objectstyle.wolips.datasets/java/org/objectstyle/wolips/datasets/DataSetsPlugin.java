/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
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
package org.objectstyle.wolips.datasets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
/**
 * The main plugin class to be used in the desktop.
 */
public class DataSetsPlugin extends Plugin implements IDataSetTypes {
	
	private final static String PLUGIN_ID = "org.objectstyle.wolips.datasets";
	//The shared instance.
	private static DataSetsPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	//	DataSets based on files
	protected final static String API_EXTENSION = "api";
	protected final static String WOD_EXTENSION = "wod";
	protected final static String WOO_EXTENSION = "woo";
	protected final static String HTML_EXTENSION = "html";
	//DataSets based on folder
	protected final static String WOCOMPONENT_EXTENSION = "wo";
	protected final static String SUBPROJECT_EXTENSION = "subproject";
	protected final static String EOMODEL_EXTENSION = "eomodel";
	protected final static String EOMODEL_BACKUP_EXTENSION = "eomodeld~";
	protected final static String D2WMODEL_EXTENSION = "d2wmodel";
	protected final static String FRAMEWORK_EXTENSION = "d2wmodel";
	protected final static String WOA_EXTENSION = "woa";
	protected final static String BUILD_EXTENSION = "build";
	protected final static String DIST_EXTENSION = "dist";
	private final static String[] TYPES = new String[]{
			DataSetsPlugin.API_EXTENSION, WOD_EXTENSION, WOO_EXTENSION,
			HTML_EXTENSION, WOCOMPONENT_EXTENSION, SUBPROJECT_EXTENSION,
			EOMODEL_EXTENSION, EOMODEL_BACKUP_EXTENSION, D2WMODEL_EXTENSION,
			FRAMEWORK_EXTENSION, WOA_EXTENSION, BUILD_EXTENSION, DIST_EXTENSION};
	/**
	 * The constructor.
	 */
	public DataSetsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("org.objectstyle.wolips.datasets.DataSetPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}
	/**
	 * Returns the shared instance.
	 */
	public static DataSetsPlugin getDefault() {
		if (plugin == null) {
			// ensure plugin instance is always available using id
			return new DataSetsPlugin(
				Platform.getPlugin(DataSetsPlugin.PLUGIN_ID).getDescriptor());
		}
		return plugin;
	}
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return DataSetsPlugin.getWorkspace();
	}
	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DataSetsPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null ? bundle.getString(key) : key);
		} catch (MissingResourceException e) {
			return key;
		}
	}
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	/**
	 * @param resource
	 *            return Null if the resource is null or unsupported otherwise
	 *            the IDataSet
	 */
	public IDataSet getDataSet(IResource resource) {
		if (resource == null)
			return null;
		switch (resource.getType()) {
			case IResource.FILE :
				switch (this.getAssociatedType(resource.getFileExtension())) {
					case IDataSetTypes.API :
						break;
					default :
						break;
				}
				break;
			case IResource.FOLDER :
				switch (this.getAssociatedType(resource.getFileExtension())) {
					case IDataSetTypes.WOCOMPONENT :
						break;
					default :
						break;
				}
				break;
			case IResource.PROJECT :
				break;
			default :
				break;
		}
		return null;
	}
	/**
	 * @param extension
	 *            The file extension.
	 * @return The IDataSet type.
	 */
	protected int getAssociatedType(String extension) {
		if (extension == null)
			return -1;
		for (int i = 0; i < DataSetsPlugin.TYPES.length; i++) {
			if (extension.equals(DataSetsPlugin.TYPES[i]))
				return i;
		}
		return -1;
	}
}

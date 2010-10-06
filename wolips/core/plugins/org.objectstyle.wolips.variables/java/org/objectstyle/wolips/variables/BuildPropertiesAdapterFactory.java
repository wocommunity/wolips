/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 - 2006 The ObjectStyle Group,
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.objectstyle.wolips.baseforplugins.util.WOLipsNatureUtils;

public class BuildPropertiesAdapterFactory implements IAdapterFactory {
	private Map<IProject, BuildProperties> _cache = new HashMap<IProject, BuildProperties>();

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		IProject project = (IProject) adaptableObject;
		if (!WOLipsNatureUtils.isWOLipsNature(project)) {
			return null;
		}

		BuildProperties properties = null;
		if (adapterType == BuildProperties.class) {
			properties = _cache.get(project);
			BuildProperties newProperties = new BuildProperties((IProject) adaptableObject);
			if (properties == null || properties.getModificationStamp() != newProperties.getModificationStamp()) {
				// MS: This MUST be above initializeBuildProperties or you'll infinite loop
				_cache.put(project, newProperties);
				if (properties != null) {
					newProperties._copyDefaultsFrom(properties);
				}
				else {
					BuildPropertiesAdapterFactory.initializeBuildProperties(newProperties);
				}
				properties = newProperties;
			}
		}
		return properties;
	}

	public Class[] getAdapterList() {
		return new Class[] { BuildProperties.class };
	}

	public static void initializeBuildProperties(BuildProperties buildProperties) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.objectstyle.wolips.variables.buildPropertiesInitializer");
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				try {
					IBuildPropertiesInitializer buildPropertiesInitializer = (IBuildPropertiesInitializer) configurationElement.createExecutableExtension("class");
					buildPropertiesInitializer.initialize(buildProperties);
				}
				catch (CoreException e) {
					VariablesPlugin.getDefault().log(e);
				}
			}
		}
	}

	public static void initializeBuildPropertiesDefaults(BuildProperties buildProperties) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.objectstyle.wolips.variables.buildPropertiesInitializer");
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				try {
					IBuildPropertiesInitializer buildPropertiesInitializer = (IBuildPropertiesInitializer) configurationElement.createExecutableExtension("class");
					buildPropertiesInitializer.initializeDefaults(buildProperties);
				}
				catch (CoreException e) {
					VariablesPlugin.getDefault().log(e);
				}
			}
		}
	}
}
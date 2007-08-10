/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2004 -2005 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.launching;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;
import org.objectstyle.wolips.launching.exceptionhandler.IExceptionHandler;
import org.objectstyle.wolips.launching.exceptionhandler.internal.ExceptionHandlerWrapper;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author uli
 * @author markus
 */
public class LaunchingPlugin extends AbstractBaseUIActivator {
	// The shared instance.
	private static LaunchingPlugin plugin;

	public static final String PLUGIN_ID = "org.objectstyle.wolips.launching";

	private ExceptionHandlerWrapper[] exceptionHandlerWrapper;

	private static final String EXTENSION_POINT_ID = "org.objectstyle.wolips.launching.exceptionhandlers";

	/**
	 * The constructor.
	 */
	public LaunchingPlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static LaunchingPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.objectstyle.wolips.launching", path);
	}

	/**
	 * @return The exceptionHandler defined in the exceptionHandler extension
	 *         point
	 * @throws CoreException
	 */
	private void loadExceptionHandlerExtensionPoint() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		IExtension[] extensions = extensionPoint.getExtensions();
		ArrayList<ExceptionHandlerWrapper> arrayList = new ArrayList<ExceptionHandlerWrapper>();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configurationElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configurationElements.length; j++) {
				IConfigurationElement configurationElement = configurationElements[j];
				IExceptionHandler currentBuilder = null;
				try {
					currentBuilder = (IExceptionHandler) configurationElement.createExecutableExtension("class");
					String name = configurationElement.getAttribute("name");
					arrayList.add(new ExceptionHandlerWrapper(currentBuilder, name));
				} catch (CoreException e) {
					this.log("Could not create executable from configuration element: " + configurationElement, e);
				}
			}
		}
		this.exceptionHandlerWrapper = arrayList.toArray(new ExceptionHandlerWrapper[arrayList.size()]);
	}

	public ExceptionHandlerWrapper[] getExceptionHandlerWrapper() {
		if (this.exceptionHandlerWrapper == null) {
			loadExceptionHandlerExtensionPoint();
		}
		return exceptionHandlerWrapper;
	}
}
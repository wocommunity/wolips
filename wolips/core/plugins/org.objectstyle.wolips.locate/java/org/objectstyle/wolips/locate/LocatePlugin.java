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
package org.objectstyle.wolips.locate;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.baseforplugins.AbstractBaseActivator;
import org.objectstyle.wolips.locate.cache.ComponentLocateCache;
import org.objectstyle.wolips.locate.result.JavaLocateResult;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;
import org.objectstyle.wolips.locate.scope.JavaLocateScope;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LocatePlugin extends AbstractBaseActivator {
	// The shared instance.
	private static LocatePlugin plugin;

	private ComponentLocateCache componentsLocateCache;

	/**
	 * The constructor.
	 */
	public LocatePlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		componentsLocateCache = new ComponentLocateCache();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(componentsLocateCache);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(componentsLocateCache);
		componentsLocateCache = null;
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static LocatePlugin getDefault() {
		return plugin;
	}

	public LocalizedComponentsLocateResult getLocalizedComponentsLocateResult(IResource resource) throws CoreException, LocateException {
		LocalizedComponentsLocateResult result;
		if (resource == null) {
			result = null;
		}
		else {
			result = getLocalizedComponentsLocateResult(resource.getProject(), LocatePlugin.getDefault().fileNameWithoutExtension(resource));
		}
		return result;
	}

	public LocalizedComponentsLocateResult getLocalizedComponentsLocateResult(IProject project, String filenameWithoutExtension) throws CoreException, LocateException {
		LocalizedComponentsLocateResult localizedComponentsLocateResult = componentsLocateCache.getLocalizedComponentsLocateResult(project, filenameWithoutExtension);
		if (localizedComponentsLocateResult != null) {
			return localizedComponentsLocateResult;
		}
		ComponentLocateScope componentLocateScope = ComponentLocateScope.createLocateScope(project, filenameWithoutExtension);
		localizedComponentsLocateResult = new LocalizedComponentsLocateResult();
		Locate locate = new Locate(componentLocateScope, localizedComponentsLocateResult);
		locate.locate();
		if (componentsLocateCache != null) {
			componentsLocateCache.addToCache(project, filenameWithoutExtension, localizedComponentsLocateResult);
		}
		return localizedComponentsLocateResult;
	}

	public JavaLocateResult getJavaLocateResult(String fileName, IProject project) throws CoreException, LocateException {
		JavaLocateScope javaLocateScope = JavaLocateScope.createLocateScope(fileName, project);
		JavaLocateResult javaLocateResult = new JavaLocateResult();
		Locate locate = new Locate(javaLocateScope, javaLocateResult);
		locate.locate();
		return javaLocateResult;
	}

	public String fileNameWithoutExtension(IResource file) {
		String fileName = file.getName();
		return fileNameWithoutExtension(fileName);
	}

	public String fileNameWithoutExtension(File file) {
		String fileName = file.getName();
		return fileNameWithoutExtension(fileName);
	}

	public String fileNameWithoutExtension(String fileName) {
		String fileNameWithoutExtension;
		int dotIndex = fileName.indexOf('.');
		if (dotIndex != -1) {
			fileNameWithoutExtension = fileName.substring(0, dotIndex);
		} else {
			fileNameWithoutExtension = fileName;
		}
		return fileNameWithoutExtension;
	}
}

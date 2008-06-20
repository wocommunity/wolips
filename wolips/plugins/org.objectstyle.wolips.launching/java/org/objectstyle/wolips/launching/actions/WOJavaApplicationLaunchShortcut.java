/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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

package org.objectstyle.wolips.launching.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.objectstyle.wolips.launching.delegates.WOJavaLocalApplicationLaunchConfigurationDelegate;

/**
 * @author uli
 */
public class WOJavaApplicationLaunchShortcut extends JavaApplicationLaunchShortcut {

	/**
	 * Returns the local wo java launch config type
	 */
	protected ILaunchConfigurationType getConfigurationType() {
		ILaunchConfigurationType launchConfigurationType = getLaunchManager().getLaunchConfigurationType(WOJavaLocalApplicationLaunchConfigurationDelegate.WOJavaLocalApplicationID);
		return launchConfigurationType;
	}

	protected ILaunchConfiguration createConfiguration(IType type) {
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try {
			ILaunchConfigurationType configType = getConfigurationType();
			wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(type.getJavaProject().getProject().getName()));
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, type.getFullyQualifiedName());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, type.getJavaProject().getElementName());
			wc.setMappedResources(new IResource[] { type.getJavaProject().getProject() });
			WOJavaLocalApplicationLaunchConfigurationDelegate.initConfiguration(wc);
			config = wc.doSave();
		} catch (CoreException exception) {
			MessageDialog.openError(getShell(), LauncherMessages.JavaLaunchShortcut_3, exception.getStatus().getMessage()); 
		}
		return config;
	}

	/**
	 * Returns a list of classpath URLs for the given project that should match
	 * what you'd get at runtime.
	 * 
	 * @param javaProject the java project to generate a classpath for
	 * @return a list of classpath URLs for the given project
	 */
	public static List<URL> createClasspathURLsForProject(IJavaProject javaProject) throws CoreException, MalformedURLException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationWorkingCopy wc = null;
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(WOJavaLocalApplicationLaunchConfigurationDelegate.WOJavaLocalApplicationID);
		wc = launchConfigurationType.newInstance(null, launchManager.generateUniqueLaunchConfigurationNameFrom("Entity Modeler SQL Generation"));
		// wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
		// type.getFullyQualifiedName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, javaProject.getElementName());
		wc.setMappedResources(new IResource[] { javaProject.getProject() });
		WOJavaLocalApplicationLaunchConfigurationDelegate.initConfiguration(wc);

		IRuntimeClasspathEntry[] runtimeClasspathEntries = JavaRuntime.computeUnresolvedRuntimeClasspath(wc);
		runtimeClasspathEntries = JavaRuntime.resolveRuntimeClasspath(runtimeClasspathEntries, wc);
		List<String> userEntries = new ArrayList<String>(runtimeClasspathEntries.length);
		Set<String> set = new HashSet<String>(runtimeClasspathEntries.length);
		for (int i = 0; i < runtimeClasspathEntries.length; i++) {
			if (runtimeClasspathEntries[i].getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				String location = runtimeClasspathEntries[i].getLocation();
				if (location != null) {
					if (!set.contains(location)) {
						userEntries.add(location);
						set.add(location);
					}
				}
			}
		}
		String[] classpathEntryStrings = userEntries.toArray(new String[userEntries.size()]);
		List<URL> classpathUrls = new LinkedList<URL>();
		for (String str : classpathEntryStrings) {
			classpathUrls.add(new File(str).toURL());
		}
		return classpathUrls;
	}
	
	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
}
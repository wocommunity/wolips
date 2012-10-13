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
package org.objectstyle.wolips.jdt.classpath;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.model.EclipseFrameworkModel;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author mschrag
 */
public class WOFrameworkContainerInitializer extends ClasspathContainerInitializer {
	protected Map<IEclipseFramework, WOFrameworkClasspathContainer> classpathContainerCache = new HashMap<IEclipseFramework, WOFrameworkClasspathContainer>();

	@Override
	public String getDescription(IPath containerPath, IJavaProject project) {
		String frameworkName = frameworkNameForClasspathPath(containerPath);
		return frameworkName + " WebObjects Framework";
	}

	@Override
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		return true;
	}

	@Override
	public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
		initialize(containerPath, project);
	}

	@Override
	public Object getComparisonID(IPath containerPath, IJavaProject project) {
		if (containerPath == null) {
			return null;
		}
		return frameworkNameForClasspathPath(containerPath);
	}

	@Override
	public void initialize(IPath containerPath, IJavaProject javaProject) throws CoreException {
		//new Exception(javaProject.getProject().getName()).printStackTrace(System.out);
		//System.out.println("WOFrameworkContainerInitializer.initialize: " + containerPath + " in " + javaProject.getProject().getName());
		String containerID = containerPath.segment(0);
		if (WOFrameworkClasspathContainer.ID.equals(containerID)) {
			WOFrameworkClasspathContainer frameworkContainer = classpathContainerForPath(containerPath, javaProject);
			JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { javaProject }, new IClasspathContainer[] { frameworkContainer }, null);
		}
	}
	
	protected synchronized WOFrameworkClasspathContainer classpathContainerForFramework(IEclipseFramework framework, IPath containerPath) {
		if (framework == null) {
			return null;
		}
		WOFrameworkClasspathContainer container = classpathContainerCache.get(framework);
		if (container == null) {
			container = new WOFrameworkClasspathContainer(framework, paramsForClasspathPath(containerPath));
			classpathContainerCache.put(framework, container);
		}
		return container;

	}
	
	protected WOFrameworkClasspathContainer classpathContainerForPath(IPath containerPath, IJavaProject javaProject) {
		String frameworkName = frameworkNameForClasspathPath(containerPath);
		boolean hasProjectConfig = VariablesPlugin.getDefault().hasProjectVariables(javaProject.getProject());
		EclipseFrameworkModel frameworkModel = JdtPlugin.getDefault().getFrameworkModel(hasProjectConfig ? javaProject.getProject() : null);
		IEclipseFramework framework = frameworkModel.getFrameworkWithName(frameworkName);
		return classpathContainerForFramework(framework, containerPath);
	}

	protected Map<String, String> paramsForClasspathPath(IPath path) {
		Map<String, String> params = new HashMap<String, String>();
		if (path == null) {
			return params;
		}
		for (int segmentNum = 2; segmentNum < path.segmentCount(); segmentNum++) {
			String kvPair = path.segment(segmentNum);
			int equalsIndex = kvPair.indexOf('=');
			if (equalsIndex != -1) {
				String key = kvPair.substring(0, equalsIndex);
				String value = kvPair.substring(equalsIndex + 1);
				params.put(key, value);
			}
		}
		return params;
	}

	protected String frameworkNameForClasspathPath(IPath path) {
		return path.segment(1);
	}
}
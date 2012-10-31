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
package org.objectstyle.wolips.jdt.classpath.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.objectstyle.woenvironment.frameworks.Root;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;

public class EclipseProjectRoot extends Root<IEclipseFramework> {
	private IWorkspaceRoot workspaceRoot;
	private Set<IEclipseFramework> cachedFrameworks;
	private Map<String, IEclipseFramework> cachedFrameworksByName;
	private Set<IEclipseFramework> cachedApplications;
	private Map<String, IEclipseFramework> cachedApplicationsByName;
	
	public EclipseProjectRoot(String shortName, String name, IWorkspaceRoot workspaceRoot) {
		super(shortName, name);
		this.workspaceRoot = workspaceRoot;
	}

	@Override
	public synchronized Set<IEclipseFramework> getFrameworks() {
		Set<IEclipseFramework> frameworks = this.cachedFrameworks;
		if (frameworks == null) {
			frameworks = new HashSet<IEclipseFramework>();
			Map<String, IEclipseFramework> frameworksMap = new HashMap<String, IEclipseFramework>();
			IProject[] projects = this.workspaceRoot.getProjects();
			for (IProject project : projects) {
				if (project.isAccessible()) {
					ProjectAdapter woProjectAdaptor = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
					if (woProjectAdaptor != null && woProjectAdaptor.isFramework()) {
						EclipseProjectFramework framework = new EclipseProjectFramework(this, project);
						frameworks.add(framework);
						frameworksMap.put(framework.getName(), framework);
					}
				}
			}
			this.cachedFrameworks = frameworks;
			this.cachedFrameworksByName = frameworksMap;
		}
		return frameworks;
	}

	@Override
	public synchronized Set<IEclipseFramework> getApplications() {
		Set<IEclipseFramework> applications = this.cachedApplications;
		if (applications == null) {
			applications = new HashSet<IEclipseFramework>();
			Map<String, IEclipseFramework> applicationsMap = new HashMap<String, IEclipseFramework>();
			IProject[] projects = this.workspaceRoot.getProjects();
			for (IProject project : projects) {
				if (project.isAccessible()) {
					ProjectAdapter woProjectAdaptor = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
					if (woProjectAdaptor != null && woProjectAdaptor.isApplication()) {
						EclipseProjectFramework application = new EclipseProjectFramework(this, project);
						applications.add(application);
						applicationsMap.put(application.getName(), application);
					}
				}
			}
			this.cachedApplications = applications;
			this.cachedApplicationsByName = applicationsMap;
		}
		return applications;
	}
	
	@Override
	public IEclipseFramework getApplicationWithName(String applicationName) {
		if (cachedApplicationsByName == null) {
			getApplications();
		}
		return cachedApplicationsByName.get(applicationName);
	}

	@Override
	public IEclipseFramework getFrameworkWithName(String frameworkName) {
		if (cachedFrameworksByName == null) {
			getFrameworks();
		}
		return cachedFrameworksByName.get(frameworkName);
	}
	
	@Override
	public String toString() {
		return "[EclipseProjectRoot]";
	}
}

/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2004 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.jdt.listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.adaptable.JavaProject;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.project.PBProjectUpdater;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.ant.UpdateFrameworkIncludeFiles;
import org.objectstyle.wolips.jdt.ant.UpdateOtherClasspathIncludeFiles;

/**
 * Tracking changes in resources and synchronizes webobjects project file
 */
public class ResourceChangeListener extends WorkspaceJob implements
		IResourceDeltaVisitor {
	private IResourceChangeEvent event;

	private List frameworkNames;

	private Project project;

	/**
	 * Constructor for ResourceChangeListener.
	 */
	public ResourceChangeListener() {
		super("WOLips Project Files Updates (Java)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		try {
			this.event.getDelta().accept(this);
		} catch (CoreException e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
		}
		if (frameworkNames != null) {
			PBProjectUpdater projectUpdater = PBProjectUpdater
					.instance(this.project.getIProject());
			if (projectUpdater != null)
				projectUpdater.setFrameworks(this.frameworkNames);
		}
		return new Status(IStatus.OK, DataSetsPlugin.getPluginId(), IStatus.OK,
				"Done", null);
	}

	/**
	 * @param delta
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
	 * @return
	 * @throws CoreException
	 */
	public final boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		try {
			return examineResource(resource, delta.getKind());
		} catch (CoreException e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
			return false;
		}
	}

	/**
	 * Method examineResource. Examines changed resources for added and/or
	 * removed webobjects project resources and synchronizes project file. <br>
	 * 
	 * @see #updateProjectFile(int, IResource, String, IFile)
	 * @param resource
	 * @param kindOfChange
	 * @return boolean
	 * @throws CoreException
	 */
	private final boolean examineResource(IResource resource, int kindOfChange)
			throws CoreException {
		if (!resource.isAccessible() && kindOfChange != IResourceDelta.REMOVED)
			return false;
		if (resource.isDerived())
			return false;
		switch (resource.getType()) {
		case IResource.ROOT:
			// further investigation of resource delta needed
			return true;
		case IResource.PROJECT:
			this.project = null;
			this.project = (Project) ((IProject) resource)
					.getAdapter(Project.class);
			if (this.project == null) {
				return false;
			}
			if (this.project.isWOLipsProject()) {
				return true;
			} // no webobjects project
			return false;
		case IResource.FOLDER:
			return false;
		case IResource.FILE:
			if (resource.getName().equals(".classpath")) {
				UpdateOtherClasspathIncludeFiles updateOtherClasspathIncludeFiles = new UpdateOtherClasspathIncludeFiles();
				updateOtherClasspathIncludeFiles.setIProject(this.project
						.getIProject());
				updateOtherClasspathIncludeFiles.execute();
				UpdateFrameworkIncludeFiles updateFrameworkIncludeFiles = new UpdateFrameworkIncludeFiles();
				updateFrameworkIncludeFiles.setIProject(this.project
						.getIProject());
				updateFrameworkIncludeFiles.execute();
				IJavaProject iJavaProject = JavaCore.create(this.project
						.getIProject());
				JavaProject javaProject = (JavaProject)iJavaProject.getAdapter(JavaProject.class);
				frameworkNames = javaProject.getFrameworkNames();
			}
		}
		return false;
	}

	/**
	 * @param event
	 *            The event to set.
	 */
	public void setEvent(IResourceChangeEvent event) {
		this.event = event;
	}

}
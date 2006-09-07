/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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
package org.objectstyle.wolips.builder.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.woenvironment.pb.PBXProject;
import org.objectstyle.woenvironment.pb.XcodeProjProject;
import org.objectstyle.woenvironment.pb.XcodeProject;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.builder.IFullBuilder;
import org.objectstyle.wolips.core.resources.builder.IIncrementalBuilder;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.preferences.Preferences;

/**
 * @author mschrag
 */
public class DotXcodeBuilder implements IIncrementalBuilder, IFullBuilder {
	private XcodeProject myXcodeProject;

	private XcodeProjProject myXcodeProjProject;

	private Map myXcodeProjects = new HashMap();

	private Map myXcodeProjProjects = new HashMap();

	public DotXcodeBuilder() {
		super();
	}

	private XcodeProject getXcodeProject(IProject project, boolean create) {
		String key = project.getName();
		XcodeProject xcodeproject = (XcodeProject) myXcodeProjects.get(key);
		if (xcodeproject == null && create) {
			xcodeproject = new XcodeProject();
			myXcodeProjects.put(key, xcodeproject);
		}
		return xcodeproject;
	}

	private XcodeProjProject getXcodeProjProject(IProject project, boolean create) {
		String key = project.getName();
		XcodeProjProject xcodeproject = (XcodeProjProject) myXcodeProjProjects.get(key);
		if (xcodeproject == null && create) {
			xcodeproject = new XcodeProjProject();
			myXcodeProjProjects.put(key, xcodeproject);
		}
		return xcodeproject;
	}

	public boolean buildStarted(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		boolean fullRebuild = (kind == IncrementalProjectBuilder.FULL_BUILD);
		if (false && kind == IncrementalProjectBuilder.CLEAN_BUILD) {
			// ak: this doesn't work as when we do a full build, the resources
			// are updated after the clean runs
			// and before the full build
			myXcodeProjects.remove(project.getName());
			myXcodeProjProjects.remove(project.getName());
			myXcodeProject = null;
			myXcodeProjProject = null;
		} else {
			if (Preferences.getPREF_WRITE_XCODE_ON_BUILD()) {
				boolean exists = project.getFolder(project.getName() + ".xcode").getFile("project.pbxproj").exists();
				myXcodeProject = getXcodeProject(project, true);
				fullRebuild |= !exists;
			} else {
				myXcodeProject = null;
			}

			if (Preferences.getPREF_WRITE_XCODE21_ON_BUILD()) {
				boolean exists = project.getFolder(project.getName() + ".xcodeproj").getFile("project.pbxproj").exists();
				myXcodeProjProject = getXcodeProjProject(project, true);
				fullRebuild |= !exists;
			} else {
				myXcodeProjProject = null;
			}
		}
		// System.err.println("Started: " + kind + " " + project.getName() +
		// "->" + myXcodeProjProject);
		return fullRebuild;
	}

	public boolean buildPreparationDone(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		if (kind == IncrementalProjectBuilder.FULL_BUILD || kind == IncrementalProjectBuilder.CLEAN_BUILD) {
			if (myXcodeProject != null) {
				try {
					writeXcodeProject(monitor, project, myXcodeProject, project.getName() + ".xcode");
				} catch (CoreException e) {
					BuilderPlugin.getDefault().log(e);
				}
			}

			if (myXcodeProjProject != null) {
				try {
					writeXcodeProject(monitor, project, myXcodeProjProject, project.getName() + ".xcodeproj");
				} catch (CoreException e) {
					BuilderPlugin.getDefault().log(e);
				}
			}
		}
		return false;
	}

	protected void writeXcodeProject(IProgressMonitor monitor, IProject project, PBXProject xcodeProject, String projectFolderName) throws CoreException {
		IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
		List frameworkPaths = projectAdapter.getFrameworkPaths();
		// System.err.println("Writing: " + project.getName() + "->" +
		// xcodeProject);
		Iterator frameworkPathsIter = frameworkPaths.iterator();
		while (frameworkPathsIter.hasNext()) {
			IPath frameworkPath = (IPath) frameworkPathsIter.next();
			IContainer frameworkContainer = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(frameworkPath);
			if (frameworkContainer instanceof IProject) {
				IProject frameworkProject = (IProject) frameworkContainer;
				IJavaProject frameworkJavaProject = JavaCore.create(frameworkProject);
				if (frameworkJavaProject != null) {
					IPath defaultOutputLocation = frameworkProject.getLocation().append("build");
					if (defaultOutputLocation != null) {
						IPath builtFrameworkPath = defaultOutputLocation.append(frameworkProject.getName() + ".framework");
						xcodeProject.addFrameworkReference(builtFrameworkPath.toOSString());
					}
				}
			} else {
				xcodeProject.addFrameworkReference(frameworkPath.toOSString());
			}
		}

		IFile projectFolderFile = project.getFile(projectFolderName);
		if (projectFolderFile.exists()) {
			// NTS: ??
			BuilderPlugin.getDefault().log("Specified Xcode project package (" + projectFolderName + ") is not a directory.");
		} else {
			IFolder projectFolder = project.getFolder(projectFolderName);
			if (!projectFolder.exists()) {
				projectFolder.create(false, true, monitor);
			}
			IFile pbxProjFile = projectFolder.getFile("project.pbxproj");
			xcodeProject.save(pbxProjFile.getLocation().toFile());
			pbxProjFile.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

	public void handleSourceDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// return delta.getKind() == IResourceDelta.ADDED || delta.getKind() ==
		// IResourceDelta.REMOVED;
	}

	public void handleClassesDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// return delta.getKind() == IResourceDelta.ADDED || delta.getKind() ==
		// IResourceDelta.REMOVED;
	}

	public void handleWoappResourcesDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// return delta.getKind() == IResourceDelta.ADDED || delta.getKind() ==
		// IResourceDelta.REMOVED;
	}

	public void handleWebServerResourcesDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// return delta.getKind() == IResourceDelta.ADDED || delta.getKind() ==
		// IResourceDelta.REMOVED;
	}

	public void handleOtherDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// return false;
	}

	public void classpathChanged(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// return false;
	}

	public void handleSource(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		if (myXcodeProject != null) {
			myXcodeProject.addSourceReference(resource.getLocation().toOSString());
		}
		if (myXcodeProjProject != null) {
			myXcodeProjProject.addSourceReference(resource.getLocation().toOSString());
		}
	}

	public void handleClasses(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		// do nothing
	}

	public void handleClasspath(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		// do nothing
	}

	public void handleOther(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		// do nothing
	}

	public void handleWebServerResources(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		// NTS: Do something here?
	}

	public void handleWoappResources(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		String resourcePath = resource.getLocation().toOSString();
		// System.out.println("DotXcodeBuilder.handleWoappResources: " +
		// resourcePath + ", " + (resource instanceof IFolder));
		if (resource instanceof IFolder) {
			// PJYF May 21 2006 We need to exclude the temp wrappers
			if (!resource.getName().endsWith("~")) {
				if (myXcodeProject != null) {
					myXcodeProject.addResourceFolderReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.addResourceFolderReference(resourcePath);
				}
			}
		} else if (resource instanceof IFile) {
			IContainer parent = resource.getParent();
			boolean addResourceFileReference = true;
			if (parent != null) {
				String parentName = parent.getName().toLowerCase();
				// PJYF May 21 2006 We need to exclude the temp wrappers
				if (parentName.endsWith(".eomodeld") || parentName.endsWith(".wo") || parentName.endsWith("~")) {
					addResourceFileReference = false;
				}
			}
			if (addResourceFileReference) {
				if (myXcodeProject != null) {
					myXcodeProject.addResourceFileReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.addResourceFileReference(resourcePath);
				}
			}
		}
	}
}

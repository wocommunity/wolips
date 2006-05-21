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
import org.objectstyle.wolips.core.resources.builder.ICleanBuilder;
import org.objectstyle.wolips.core.resources.builder.IDeltaBuilder;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.preferences.Preferences;

/**
 * @author mschrag
 */
public class DotXcodeBuilder implements IDeltaBuilder, ICleanBuilder {
	private XcodeProject myXcodeProject;

	private XcodeProjProject myXcodeProjProject;

	public DotXcodeBuilder() {
	}

	public boolean buildStarted(int _kind, Map _args,
			IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		boolean fullRebuild = false;
		if (Preferences.getPREF_WRITE_XCODE_ON_BUILD()) {
			myXcodeProject = new XcodeProject();
			fullRebuild = !_project.getFolder(_project.getName() + ".xcode")
					.getFile("project.pbxproj").exists();
		} else {
			myXcodeProject = null;
		}

		if (Preferences.getPREF_WRITE_XCODE21_ON_BUILD()) {
			myXcodeProjProject = new XcodeProjProject();
			fullRebuild = !_project
					.getFolder(_project.getName() + ".xcodeproj").getFile(
							"project.pbxproj").exists();
		} else {
			myXcodeProjProject = null;
		}
		return fullRebuild;
	}

	public boolean buildPreparationDone(int _kind, Map _args,
			IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		if (_kind == IncrementalProjectBuilder.FULL_BUILD
				|| _kind == IncrementalProjectBuilder.CLEAN_BUILD) {
			if (myXcodeProject != null) {
				try {
					writeXcodeProject(_monitor, _project, myXcodeProject,
							_project.getName() + ".xcode");
				} catch (CoreException e) {
					BuilderPlugin.getDefault().log(e);
				}
			}

			if (myXcodeProjProject != null) {
				try {
					writeXcodeProject(_monitor, _project, myXcodeProjProject,
							_project.getName() + ".xcodeproj");
				} catch (CoreException e) {
					BuilderPlugin.getDefault().log(e);
				}
			}
		}
		return false;
	}

	protected void writeXcodeProject(IProgressMonitor _monitor,
			IProject _project, PBXProject _xcodeProject,
			String _projectFolderName) throws CoreException {
		IProjectAdapter projectAdapter = (IProjectAdapter) _project
				.getAdapter(IProjectAdapter.class);
		List frameworkPaths = projectAdapter.getFrameworkPaths();
		Iterator frameworkPathsIter = frameworkPaths.iterator();
		while (frameworkPathsIter.hasNext()) {
			IPath frameworkPath = (IPath) frameworkPathsIter.next();
			IContainer frameworkContainer = ResourcesPlugin.getWorkspace()
					.getRoot().getContainerForLocation(frameworkPath);
			if (frameworkContainer instanceof IProject) {
				IProject frameworkProject = (IProject) frameworkContainer;
				IJavaProject frameworkJavaProject = JavaCore
						.create(frameworkProject);
				if (frameworkJavaProject != null) {
					IPath defaultOutputLocation = frameworkProject
							.getLocation().append("build");
					if (defaultOutputLocation != null) {
						IPath builtFrameworkPath = defaultOutputLocation
								.append(frameworkProject.getName()
										+ ".framework");
						_xcodeProject.addFrameworkReference(builtFrameworkPath
								.toOSString());
					}
				}
			} else {
				_xcodeProject.addFrameworkReference(frameworkPath.toOSString());
			}
		}

		IFile projectFolderFile = _project.getFile(_projectFolderName);
		if (projectFolderFile.exists()) {
			// NTS: ??
			BuilderPlugin.getDefault().log(
					"Specified Xcode project package (" + _projectFolderName
							+ ") is not a directory.");
		} else {
			IFolder projectFolder = _project.getFolder(_projectFolderName);
			if (!projectFolder.exists()) {
				projectFolder.create(false, true, _monitor);
			}
			IFile pbxProjFile = projectFolder.getFile("project.pbxproj");
			_xcodeProject.save(pbxProjFile.getLocation().toFile());
			pbxProjFile.refreshLocal(IResource.DEPTH_INFINITE, _monitor);
		}
	}

	public boolean handleSourceDelta(IResourceDelta _delta,
			IProgressMonitor monitor, Map _buildCache) {
		return _delta.getKind() == IResourceDelta.ADDED
				|| _delta.getKind() == IResourceDelta.REMOVED;
	}

	public boolean handleClassesDelta(IResourceDelta _delta,
			IProgressMonitor monitor, Map _buildCache) {
		return _delta.getKind() == IResourceDelta.ADDED
				|| _delta.getKind() == IResourceDelta.REMOVED;
	}

	public boolean handleWoappResourcesDelta(IResourceDelta _delta,
			IProgressMonitor monitor, Map _buildCache) {
		return _delta.getKind() == IResourceDelta.ADDED
				|| _delta.getKind() == IResourceDelta.REMOVED;
	}

	public boolean handleWebServerResourcesDelta(IResourceDelta _delta,
			IProgressMonitor monitor, Map _buildCache) {
		return _delta.getKind() == IResourceDelta.ADDED
				|| _delta.getKind() == IResourceDelta.REMOVED;
	}

	public boolean handleOtherDelta(IResourceDelta _delta,
			IProgressMonitor monitor, Map _buildCache) {
		return false;
	}

	public boolean classpathChanged(IResourceDelta _delta,
			IProgressMonitor monitor, Map _buildCache) {
		return false;
	}

	public void handleSource(IResource _resource,
			IProgressMonitor _progressMonitor, Map _buildCache) {
		if (myXcodeProject != null) {
			myXcodeProject.addSourceReference(_resource.getLocation()
					.toOSString());
		}
		if (myXcodeProjProject != null) {
			myXcodeProjProject.addSourceReference(_resource.getLocation()
					.toOSString());
		}
	}

	public void handleClasses(IResource _resource,
			IProgressMonitor _progressMonitor, Map _buildCache) {
	}

	public void handleClasspath(IResource _resource,
			IProgressMonitor _progressMonitor, Map _buildCache) {
	}

	public void handleOther(IResource _resource,
			IProgressMonitor _progressMonitor, Map _buildCache) {
	}

	public void handleWebServerResources(IResource _resource,
			IProgressMonitor _progressMonitor, Map _buildCache) {
		// NTS: Do something here?
	}

	public void handleWoappResources(IResource _resource,
			IProgressMonitor _progressMonitor, Map _buildCache) {
		String resourcePath = _resource.getLocation().toOSString();
		// System.out.println("DotXcodeBuilder.handleWoappResources: " +
		// resourcePath + ", " + (_resource instanceof IFolder));
		if (_resource instanceof IFolder) {
		  // PJYF May 21 2006 We need to exclude the temp wrappers
      if (! _resource.getName().endsWith("~")) {
        if (myXcodeProject != null) {
          myXcodeProject.addResourceFolderReference(resourcePath);
        }
        if (myXcodeProjProject != null) {
          myXcodeProjProject.addResourceFolderReference(resourcePath);
        }
      }
		} else if (_resource instanceof IFile) {
			IContainer parent = _resource.getParent();
			boolean addResourceFileReference = true;
			if (parent != null) {
				String parentName = parent.getName().toLowerCase();
        // PJYF May 21 2006 We need to exclude the temp wrappers
				if (parentName.endsWith(".eomodeld")
            || parentName.endsWith(".wo")
            || parentName.endsWith("~")) {
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

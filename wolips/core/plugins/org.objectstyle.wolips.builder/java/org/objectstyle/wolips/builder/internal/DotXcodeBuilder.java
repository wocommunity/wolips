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

import java.io.IOException;
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
import org.objectstyle.wolips.baseforplugins.plist.PropertyListParserException;
import org.objectstyle.wolips.builder.BuilderPlugin;
import org.objectstyle.wolips.core.resources.builder.IFullBuilder;
import org.objectstyle.wolips.core.resources.builder.IIncrementalBuilder;
import org.objectstyle.wolips.core.resources.internal.build.BuilderWrapper;
import org.objectstyle.wolips.core.resources.internal.build.FullBuildDeltaVisitor;
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
	
	private boolean myProjectChanged;

	public DotXcodeBuilder() {
		super();
	}

	private XcodeProject getXcodeProject(IProject project, boolean create) {
		String key = project.getName();
		XcodeProject xcodeproject = (XcodeProject) myXcodeProjects.get(key);
		if (xcodeproject == null && create) {
			xcodeproject = new XcodeProject();
			myXcodeProjects.put(key, xcodeproject);
			myProjectChanged = true;
		}
		return xcodeproject;
	}

	private XcodeProjProject getXcodeProjProject(IProject project, boolean create) {
		String key = project.getName();
		XcodeProjProject xcodeproject = (XcodeProjProject) myXcodeProjProjects.get(key);
		if (xcodeproject == null && create) {
			xcodeproject = new XcodeProjProject();
			myXcodeProjProjects.put(key, xcodeproject);
			myProjectChanged = true;
		}
		return xcodeproject;
	}

	public boolean buildStarted(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		boolean fullRebuild = (kind == IncrementalProjectBuilder.FULL_BUILD);
		//System.out.println("DotXcodeBuilder.buildStarted: full rebuild = " + fullRebuild);
		
		// AK: this class sucks terribly. Not only that we need to do everything twice, we also only
		// get the deltas from the "normal" builder, so when we create, we need to run a full build.
		
		if (false && kind == IncrementalProjectBuilder.CLEAN_BUILD) {
			// AK: this doesn't work as when we do a full build, the resources
			// are updated after the clean runs
			// and before the full build
			myXcodeProjects.remove(project.getName());
			myXcodeProjProjects.remove(project.getName());
			myXcodeProject = null;
			myXcodeProjProject = null;
		} else {
			if (Preferences.shouldWriteXcodeOnBuild()) {
				boolean exists = project.getFolder(project.getName() + ".xcode").getFile("project.pbxproj").exists();
				myXcodeProject = getXcodeProject(project, true);
				fullRebuild |= !exists || myProjectChanged;
				if(fullRebuild) {
					fullVisit(kind, args, monitor, project, buildCache);
				}
				//System.out.println("DotXcodeBuilder.buildStarted: xcode exists = " + exists);
			} else {
				myXcodeProject = null;
			}

			if (Preferences.shouldWriteXcodeProjOnBuild()) {
				boolean exists = project.getFolder(project.getName() + ".xcodeproj").getFile("project.pbxproj").exists();
				myXcodeProjProject = getXcodeProjProject(project, true);
				fullRebuild |= !exists || myProjectChanged;
				if(fullRebuild) {
					fullVisit(kind, args, monitor, project, buildCache);
				}
				//System.out.println("DotXcodeBuilder.buildStarted: xcodeproj exists = " + exists);
			} else {
				myXcodeProjProject = null;
			}
		}
		// System.err.println("Started: " + kind + " " + project.getName() +
		// "->" + myXcodeProjProject);
		myProjectChanged |= fullRebuild;
		// System.out.println("DotXcodeBuilder.buildStarted: rebuild? " + fullRebuild);
		return fullRebuild;
	}

	private void fullVisit(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		BuilderWrapper[] wrappers = new BuilderWrapper[1];
		wrappers[0] = new BuilderWrapper(this, "DotXCode", "dontknow");
		FullBuildDeltaVisitor fullBuildDeltaVisitor = new FullBuildDeltaVisitor(wrappers, monitor, buildCache);
		fullBuildDeltaVisitor.buildStarted(project);
		try {
			project.accept(fullBuildDeltaVisitor);
			buildPreparationDone(kind, args, monitor, project, buildCache);
			fullBuildDeltaVisitor.visitingDone();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean buildPreparationDone(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		//System.out.println("DotXcodeBuilder.buildPreparationDone: xcode " + myXcodeProject + " (" + myProjectChanged + ")");
		if (myProjectChanged) {
			myProjectChanged = false;
			if (myXcodeProject != null) {
				try {
					writeXcodeProject(monitor, project, myXcodeProject, project.getName() + ".xcode");
				} catch (Exception e) {
					BuilderPlugin.getDefault().log(e);
				}
			}

			//System.out.println("DotXcodeBuilder.buildPreparationDone: xcodeproj " + myXcodeProjProject);
			if (myXcodeProjProject != null) {
				try {
					writeXcodeProject(monitor, project, myXcodeProjProject, project.getName() + ".xcodeproj");
				} catch (Exception e) {
					BuilderPlugin.getDefault().log(e);
				}
			}
		}
		return false;
	}

	protected void writeXcodeProject(IProgressMonitor monitor, IProject project, PBXProject xcodeProject, String projectFolderName) throws CoreException, PropertyListParserException, IOException {
		IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
		List frameworkPaths = projectAdapter.getFrameworkPaths();
		//System.out.println("DotXcodeBuilder.writeXcodeProject: Writing " + project.getName() + " " + xcodeProject);
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
		//System.out.println("DotXcodeBuilder.handleSourceDelta: " + delta);
		IResource resource = delta.getResource();
		if (resource != null) {
			if (delta.getKind() == IResourceDelta.ADDED) {
				handleSource(resource, monitor, buildCache);
			} else if (delta.getKind() == IResourceDelta.REMOVED) {
				String resourcePath = resource.getLocation().toOSString();
				if (myXcodeProject != null) {
					myXcodeProject.removeSourceReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.removeSourceReference(resourcePath);
				}
				myProjectChanged = true;
			}
		}
	}

	public void handleClassesDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleClassesDelta: " + delta);
	}

	public void handleWoappResourcesDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		// System.out.println("DotXcodeBuilder.handleWoappResourcesDelta: " + delta);
		IResource resource = delta.getResource();
		if (resource != null) {
			if (delta.getKind() == IResourceDelta.ADDED) {
				handleWoappResources(resource, monitor, buildCache);
			} else if (delta.getKind() == IResourceDelta.REMOVED) {
				String resourcePath = resource.getLocation().toOSString();
				if (resource instanceof IFile) {
					if (myXcodeProject != null) {
						myXcodeProject.removeResourceFileReference(resourcePath);
					}
					if (myXcodeProjProject != null) {
						myXcodeProjProject.removeResourceFileReference(resourcePath);
					}
					myProjectChanged = true;
				} else if (resource instanceof IFolder) {
					if (myXcodeProject != null) {
						myXcodeProject.removeResourceFolderReference(resourcePath);
					}
					if (myXcodeProjProject != null) {
						myXcodeProjProject.removeResourceFolderReference(resourcePath);
					}
					myProjectChanged = true;
				}
			}
		}
	}

	public void handleWebServerResourcesDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleWebServerResourcesDelta: " + delta);
		IResource resource = delta.getResource();
		if (delta.getKind() == IResourceDelta.ADDED) {
			handleWebServerResources(resource, monitor, buildCache);
		}
		else if (delta.getKind() == IResourceDelta.REMOVED) {
			if (resource instanceof IFile) {
				String resourcePath = resource.getLocation().toOSString();
				if (myXcodeProject != null) {
					myXcodeProject.removeWSResourceFileReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.removeWSResourceFileReference(resourcePath);
				}
				myProjectChanged = true;
			} else if (resource instanceof IFolder) {
				String resourcePath = resource.getLocation().toOSString();
				if (myXcodeProject != null) {
					myXcodeProject.removeWSResourceFolderReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.removeWSResourceFolderReference(resourcePath);
				}
				myProjectChanged = true;
			}
		}
	}

	public void handleOtherDelta(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleOtherDelta: " + delta);
	}

	public void classpathChanged(IResourceDelta delta, IProgressMonitor monitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.classpathChanged: " + delta);
	}

	public void handleSource(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleSource: " + resource);
		if (resource != null) {
			String resourcePath = resource.getLocation().toOSString();
			if (myXcodeProject != null) {
				myXcodeProject.addSourceReference(resourcePath);
			}
			if (myXcodeProjProject != null) {
				myXcodeProjProject.addSourceReference(resourcePath);
			}
			myProjectChanged = true;
		}
	}

	public void handleClasses(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleClasses: " + resource);
	}

	public void handleClasspath(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleClasspath: " + resource);
	}

	public void handleOther(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleWebServerResources: " + resource);
	}

	public void handleWebServerResources(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		//System.out.println("DotXcodeBuilder.handleWebServerResources: " + resource);
		if (resource instanceof IFile) {
			String resourcePath = resource.getLocation().toOSString();
			if (myXcodeProject != null) {
				myXcodeProject.addWSResourceFileReference(resourcePath);
			}
			if (myXcodeProjProject != null) {
				myXcodeProjProject.addWSResourceFileReference(resourcePath);
			}
			myProjectChanged = true;
		} else if (resource instanceof IFolder) {
			String resourcePath = resource.getLocation().toOSString();
			if (myXcodeProject != null) {
				myXcodeProject.addWSResourceFolderReference(resourcePath);
			}
			if (myXcodeProjProject != null) {
				myXcodeProjProject.addWSResourceFolderReference(resourcePath);
			}
			myProjectChanged = true;
		}
	}

	protected boolean shouldAddResource(IResource resource) {
		boolean shouldAddResource = true;
		if (resource instanceof IFolder) {
			// PJYF/AK We need to exclude the temp wrappers
			// AK: we only add folders if they are wrappers
			shouldAddResource = false;
			if (resource.getName().endsWith(".wo") || resource.getName().endsWith(".eomodeld")) {
				shouldAddResource = true;
			}
		} else if (resource instanceof IFile) {
			IContainer parent = resource.getParent();
			if (parent != null) {
				String parentName = parent.getName().toLowerCase();
				// PJYF/AK We need to exclude the wrapper contents and temp folders
				if (parentName.endsWith(".eomodeld") || parentName.endsWith(".wo") || parentName.endsWith("~")) {
					shouldAddResource = false;
				}
			}
		}
		return shouldAddResource;
	}

	public void handleWoappResources(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		boolean shouldAddResource = shouldAddResource(resource);
		// System.out.println("DotXcodeBuilder.handleWoappResources: " + resource + " " + shouldAddResource);
		if (shouldAddResource) {
			String resourcePath = resource.getLocation().toOSString();
			if (resource instanceof IFolder) {
				if (myXcodeProject != null) {
					myXcodeProject.addResourceFolderReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.addResourceFolderReference(resourcePath);
				}
				myProjectChanged = true;
			} else if (resource instanceof IFile) {
				if (myXcodeProject != null) {
					myXcodeProject.addResourceFileReference(resourcePath);
				}
				if (myXcodeProjProject != null) {
					myXcodeProjProject.addResourceFileReference(resourcePath);
				}
				myProjectChanged = true;
			}
		}
	}
}

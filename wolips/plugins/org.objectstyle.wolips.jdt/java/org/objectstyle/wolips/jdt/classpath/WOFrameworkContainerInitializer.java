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
 *//*
 * Created on 12.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.objectstyle.wolips.jdt.classpath;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.woenvironment.frameworks.FrameworkModel;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;

/**
 * @author mschrag
 */
public class WOFrameworkContainerInitializer extends ClasspathContainerInitializer implements IResourceChangeListener {
	public static final String OLD_OLD_WOLIPS_CLASSPATH_CONTAINER_ID = "org.objectstyle.wolips.WO_CLASSPATH";

	public static final String OLD_WOLIPS_CLASSPATH_CONTAINER_ID = "org.objectstyle.wolips.ContainerInitializer";

	public WOFrameworkContainerInitializer() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
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
		String containerID = containerPath.segment(0);
		if (WOFrameworkContainerInitializer.OLD_OLD_WOLIPS_CLASSPATH_CONTAINER_ID.equals(containerID)) {
			convertOldOldClasspathContainer(javaProject);
		} else if (WOFrameworkContainerInitializer.OLD_WOLIPS_CLASSPATH_CONTAINER_ID.equals(containerID)) {
			convertOldClasspathContainer(javaProject);
		} else if (WOFrameworkClasspathContainer.ID.equals(containerID)) {
			String frameworkName = frameworkNameForClasspathPath(containerPath);
			IEclipseFramework framework = JdtPlugin.getDefault().getFrameworkModel(javaProject.getProject()).getFrameworkWithName(frameworkName);
			if (framework != null) {
				WOFrameworkClasspathContainer frameworkContainer = new WOFrameworkClasspathContainer(framework, paramsForClasspathPath(containerPath));
				JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { javaProject }, new IClasspathContainer[] { frameworkContainer }, null);
			}
		}
	}

	protected void convertOldClasspathContainer(final IJavaProject javaProject) throws JavaModelException {
		Set<String> frameworkNames = new HashSet<String>();

		FrameworkModel<IEclipseFramework> frameworkModel = JdtPlugin.getDefault().getFrameworkModel(javaProject.getProject());
		IClasspathEntry[] existingClasspathEntries = javaProject.getRawClasspath();
		final List<IClasspathEntry> newClasspathEntries = new LinkedList<IClasspathEntry>();
		for (IClasspathEntry existingClasspathEntry : existingClasspathEntries) {
			IPath existingContainerPath = existingClasspathEntry.getPath();
			if (existingContainerPath.segmentCount() > 0) {
				String existingContainerID = existingContainerPath.segment(0);
				if (WOFrameworkContainerInitializer.OLD_WOLIPS_CLASSPATH_CONTAINER_ID.equals(existingContainerID)) {
					for (int segmentNum = 3; segmentNum < existingContainerPath.segmentCount(); segmentNum += 11) {
						// 10/1/Ajax/1/nil/1/nil/1/0/1/false
						String frameworkName = existingContainerPath.segment(segmentNum);
						addFrameworkNamed(frameworkName, frameworkNames, newClasspathEntries, frameworkModel);
					}
				} else {
					newClasspathEntries.add(existingClasspathEntry);
				}
			} else {
				newClasspathEntries.add(existingClasspathEntry);
			}
		}

		convertProjectReferencesToFrameworkReferences(newClasspathEntries, frameworkModel, frameworkNames);

		updateRawClasspath(javaProject, newClasspathEntries);
	}

	protected void convertOldOldClasspathContainer(final IJavaProject javaProject) throws JavaModelException {
		Set<String> frameworkNames = new HashSet<String>();

		FrameworkModel<IEclipseFramework> frameworkModel = JdtPlugin.getDefault().getFrameworkModel(javaProject.getProject());
		IClasspathEntry[] existingClasspathEntries = javaProject.getRawClasspath();
		final List<IClasspathEntry> newClasspathEntries = new LinkedList<IClasspathEntry>();
		for (IClasspathEntry existingClasspathEntry : existingClasspathEntries) {
			IPath existingContainerPath = existingClasspathEntry.getPath();
			if (existingContainerPath.segmentCount() > 0) {
				String existingContainerID = existingContainerPath.segment(0);
				if (WOFrameworkContainerInitializer.OLD_OLD_WOLIPS_CLASSPATH_CONTAINER_ID.equals(existingContainerID)) {
					for (int segmentNum = 1; segmentNum < existingContainerPath.segmentCount(); segmentNum++) {
						String frameworkName = existingContainerPath.segment(segmentNum);
						addFrameworkNamed(frameworkName, frameworkNames, newClasspathEntries, frameworkModel);
					}
				} else {
					newClasspathEntries.add(existingClasspathEntry);
				}
			} else {
				newClasspathEntries.add(existingClasspathEntry);
			}
		}

		convertProjectReferencesToFrameworkReferences(newClasspathEntries, frameworkModel, frameworkNames);

		updateRawClasspath(javaProject, newClasspathEntries);
	}

	protected boolean addFrameworkNamed(String frameworkName, Set<String> frameworkNames, List<IClasspathEntry> newClasspathEntries, FrameworkModel<IEclipseFramework> frameworkModel) {
		return addFrameworkNamed(frameworkName, frameworkNames, newClasspathEntries, frameworkModel, -1);
	}

	protected boolean addFrameworkNamed(String frameworkName, Set<String> frameworkNames, List<IClasspathEntry> newClasspathEntries, FrameworkModel<IEclipseFramework> frameworkModel, int index) {
		boolean added = false;
		IEclipseFramework framework = frameworkModel.getFrameworkWithName(frameworkName);
		if (framework != null && !frameworkNames.contains(frameworkName)) {
			WOFrameworkClasspathContainer frameworkContainer = new WOFrameworkClasspathContainer(framework);
			IClasspathEntry frameworkClasspathEntry = JavaCore.newContainerEntry(frameworkContainer.getPath(), true);
			if (index == -1) {
				newClasspathEntries.add(frameworkClasspathEntry);
			} else {
				newClasspathEntries.set(index, frameworkClasspathEntry);
			}
			frameworkNames.add(frameworkName);
			added = true;
		} else if (index != -1) {
			newClasspathEntries.remove(index);
		}
		return added;
	}

	protected void convertProjectReferencesToFrameworkReferences(List<IClasspathEntry> newClasspathEntries, FrameworkModel<IEclipseFramework> frameworkModel, Set<String> frameworkNames) {
		for (int classpathEntryNum = newClasspathEntries.size() - 1; classpathEntryNum >= 0; classpathEntryNum--) {
			IClasspathEntry newClasspathEntry = newClasspathEntries.get(classpathEntryNum);
			if (newClasspathEntry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				String projectName = newClasspathEntry.getPath().segment(0);
				// NTS: LOOKUP FRAMEWORK NAME FOR PROJECT NAME
				String frameworkName = projectName;
				IEclipseFramework framework = frameworkModel.getFrameworkWithName(frameworkName);
				if (framework != null) {
					addFrameworkNamed(frameworkName, frameworkNames, newClasspathEntries, frameworkModel, classpathEntryNum);
				}
			}
		}
	}

	protected void updateRawClasspath(final IJavaProject javaProject, final List<IClasspathEntry> newClasspathEntries) {
		final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IClasspathEntry[] newClasspathEntriesArray = newClasspathEntries.toArray(new IClasspathEntry[newClasspathEntries.size()]);
				Arrays.sort(newClasspathEntriesArray, new WOFrameworkClasspathComparator(newClasspathEntriesArray));
				javaProject.setRawClasspath(newClasspathEntriesArray, monitor);
			}
		};
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					PlatformUI.getWorkbench().getProgressService().run(true, true, new WorkbenchRunnableAdapter(runnable));
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected synchronized void woFrameworkChanged(String frameworkName) throws CoreException {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isAccessible()) {
				IProjectAdapter woProjectAdaptor = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
				if (woProjectAdaptor != null) {
					IJavaProject javaProject = JavaCore.create(project);
					if (javaProject != null) {
						IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
						for (IClasspathEntry classpathEntry : classpathEntries) {
							if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
								IPath entryPath = classpathEntry.getPath();
								if (entryPath.segmentCount() >= 2 && entryPath.segment(0).equals(WOFrameworkClasspathContainer.ID) && entryPath.segment(1).equals(frameworkName)) {
									IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(entryPath, javaProject);
									requestClasspathContainerUpdate(entryPath, javaProject, classpathContainer);
								}
							}
						}
					}
				}
			}
		}
	}

	protected Map<String, String> paramsForClasspathPath(IPath path) {
		Map<String, String> params = new HashMap<String, String>();
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

	protected String frameworkNameForProject(IProject project) {
		return project.getName();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		// don't bother looking at delta if selection not applicable
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			IResourceDelta[] resourceDeltas = delta.getAffectedChildren(IResourceDelta.CHANGED | IResourceDelta.ADDED | IResourceDelta.REMOVED);
			for (int i = 0; i < resourceDeltas.length; ++i) {
				IResourceDelta resourceDelta = resourceDeltas[i];
				// System.out.println("WOFrameworkContainerInitializer.resourceChanged:
				// " + resourceDelta);
				IResource resource = resourceDelta.getResource();
				if (resource instanceof IProject) {
					if ((resourceDelta.getFlags() & IResourceDelta.OPEN) != 0) {
						IProject project = (IProject) resource;
						//System.out.println("WOFrameworkContainerInitializer.resourceChanged: opened or closed " + project);
						try {
							woFrameworkChanged(frameworkNameForProject(project));
						} catch (CoreException e) {
							e.printStackTrace();
						}
					} else if (resourceDelta.getKind() == IResourceDelta.REMOVED) {
						IProject project = (IProject) resource;
						//System.out.println("WOFrameworkContainerInitializer.resourceChanged: removed " + project);
						try {
							woFrameworkChanged(frameworkNameForProject(project));
						} catch (CoreException e) {
							e.printStackTrace();
						}
					} else if (resourceDelta.getKind() == IResourceDelta.ADDED) {
						IProject project = (IProject) resource;
						//System.out.println("WOFrameworkContainerInitializer.resourceChanged: added " + project);
						try {
							woFrameworkChanged(frameworkNameForProject(project));
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
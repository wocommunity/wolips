package org.objectstyle.wolips.jdt.classpath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.ProjectFrameworkAdapter;
import org.objectstyle.wolips.jdt.classpath.model.EclipseProjectFramework;

public class WOFrameworkResourceListener implements IResourceChangeListener {
	protected synchronized void woFrameworkChanged(String frameworkName) throws CoreException {
		JdtPlugin.getDefault().invalidateFrameworkModel();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isAccessible()) {
				ProjectAdapter woProjectAdaptor = (ProjectAdapter) project.getAdapter(ProjectAdapter.class);
				if (woProjectAdaptor != null) {
					IJavaProject javaProject = JavaCore.create(project);
					if (javaProject != null) {
						IClasspathEntry[] classpathEntries = javaProject.getRawClasspath();
						for (IClasspathEntry classpathEntry : classpathEntries) {
							if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
								IPath entryPath = classpathEntry.getPath();
								if (entryPath.segmentCount() >= 2 && entryPath.segment(0).equals(WOFrameworkClasspathContainer.ID) && entryPath.segment(1).equals(frameworkName)) {
									IClasspathContainer classpathContainer = JavaCore.getClasspathContainer(entryPath, javaProject);
									JavaCore.getClasspathContainerInitializer(WOFrameworkClasspathContainer.ID).requestClasspathContainerUpdate(entryPath, javaProject, classpathContainer);
								}
							}
						}
					}
				}
			}
		}
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
						final IProject project = (IProject) resource;
						if (project.isOpen()) {
							final ProjectFrameworkAdapter projectFrameworkAdapter = (ProjectFrameworkAdapter) project.getAdapter(ProjectFrameworkAdapter.class);
							if (projectFrameworkAdapter != null) {
								new WorkspaceJob("Initialize WOLips Project") {
									
									@Override
									public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
										projectFrameworkAdapter.initializeProject();
										return Status.OK_STATUS;
									}
								}.schedule();
							}
						}
						try {
							woFrameworkChanged(EclipseProjectFramework.frameworkNameForProject(project));
						} catch (CoreException e) {
							e.printStackTrace();
						}
					} else if (resourceDelta.getKind() == IResourceDelta.REMOVED) {
						IProject project = (IProject) resource;
						//System.out.println("WOFrameworkContainerInitializer.resourceChanged: removed " + project);
						try {
							woFrameworkChanged(EclipseProjectFramework.frameworkNameForProject(project));
						} catch (CoreException e) {
							e.printStackTrace();
						}
					} else if (resourceDelta.getKind() == IResourceDelta.ADDED) {
						IProject project = (IProject) resource;
						//System.out.println("WOFrameworkContainerInitializer.resourceChanged: added " + project);
						try {
							woFrameworkChanged(EclipseProjectFramework.frameworkNameForProject(project));
						} catch (CoreException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	

}

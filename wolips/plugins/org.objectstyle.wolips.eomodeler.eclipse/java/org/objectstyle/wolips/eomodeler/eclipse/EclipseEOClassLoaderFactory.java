package org.objectstyle.wolips.eomodeler.eclipse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOClassLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.utils.URLUtils;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class EclipseEOClassLoaderFactory extends AbstractEOClassLoader {
	@Override
	protected void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception {
		// AK: we don't want to re-jar each time we make a change....
		String workSpacePath = VariablesPlugin.getDefault().getWOProjectDevelopmentPath();
		if (workSpacePath != null) {
			URL classUrl = new URL("file://" + workSpacePath + "wolips/core/plugins/org.objectstyle.wolips.eomodeler.core/bin/");
			classpathUrls.add(classUrl);
		}
	}
	
	@Override
	protected void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception {
		if (model.getProject() == null) {
			URL modelURL = model.getModelURL();
			IContainer[] modelContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(URLUtils.cheatAndTurnIntoFile(modelURL).getAbsolutePath()));
			for (int modelContainerNum = 0; modelContainerNum < modelContainers.length; modelContainerNum++) {
				IContainer modelContainer = modelContainers[modelContainerNum];
				IProject modelProject = modelContainer.getProject();
				fillInProjectClasspath(modelProject, classpathUrls);
			}
		} else {
			fillInProjectClasspath(model.getProject(), classpathUrls);
		}
	}

	protected void fillInProjectClasspath(IProject project, Set<URL> classpathUrls) throws MalformedURLException, JavaModelException {
		IJavaProject javaProject = JavaCore.create(project);
		if (javaProject != null) {
			IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
			for (int classpathEntryNum = 0; classpathEntryNum < classpathEntries.length; classpathEntryNum++) {
				IClasspathEntry entry = classpathEntries[classpathEntryNum];
				fillInClasspath(entry, classpathUrls, javaProject);
			}
		}
	}

	protected void fillInClasspath(IClasspathEntry entry, Set<URL> classpathUrls, IJavaProject project) throws MalformedURLException, JavaModelException {
		if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY && entry.getContentKind() == IPackageFragmentRoot.K_BINARY) {
			IPath path = entry.getPath();
			File externalFile = path.toFile();
			if (externalFile.exists()) {
				classpathUrls.add(externalFile.toURL());
			} else {
				IFile projectJarFile = project.getProject().getWorkspace().getRoot().getFile(path);
				if (projectJarFile.exists()) {
					classpathUrls.add(projectJarFile.getLocation().toFile().toURL());
				}
			}
		} else if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
			IProject dependencyProject = project.getProject().getWorkspace().getRoot().getProject(entry.getPath().lastSegment());
			fillInProjectClasspath(dependencyProject, classpathUrls);
		} else if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
			IPath outputLocation = entry.getOutputLocation();
			if (outputLocation == null || outputLocation.isEmpty()) {
				outputLocation = project.getOutputLocation();
			}
			IPath srcPath = project.getProject().getWorkspace().getRoot().getLocation().append(outputLocation);
			classpathUrls.add(srcPath.toFile().toURL());
		}
	}
}

package org.objectstyle.wolips.eomodeler.eclipse;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.runtime.InternalPlatform;
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
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.IEOClassLoaderFactory;
import org.objectstyle.wolips.eomodeler.core.utils.URLUtils;
import org.objectstyle.wolips.variables.VariablesPlugin;
import org.osgi.framework.Bundle;

public class EclipseEOClassLoaderFactory implements IEOClassLoaderFactory {
	private static Map<Set<URL>, Reference<ClassLoader>> CLASSLOADER_CACHE;

	static {
		EclipseEOClassLoaderFactory.CLASSLOADER_CACHE = new HashMap<Set<URL>, Reference<ClassLoader>>();
	}

	public ClassLoader createClassLoaderForModel(EOModel model) throws EOModelException {
		try {
			Set<URL> classpathSet = new LinkedHashSet<URL>();
			if (model.getProject() == null) {
				URL modelURL = model.getModelURL();
				IContainer[] modelContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(URLUtils.cheatAndTurnIntoFile(modelURL).getAbsolutePath()));
				for (int modelContainerNum = 0; modelContainerNum < modelContainers.length; modelContainerNum++) {
					IContainer modelContainer = modelContainers[modelContainerNum];
					IProject modelProject = modelContainer.getProject();
					fillInClasspath(modelProject, classpathSet);
				}
			} else {
				fillInClasspath(model.getProject(), classpathSet);
			}
			// AK: we don't want to re-jar each time we make a change....
			String workSpacePath = VariablesPlugin.getDefault().getWOProjectDevelopmentPath();
			if (workSpacePath != null) {
				URL classUrl = new URL("file://" + workSpacePath + "wolips/core/plugins/org.objectstyle.wolips.eomodeler/bin/");
				classpathSet.add(classUrl);
			}
			Bundle bundle = InternalPlatform.getDefault().getBundle("org.objectstyle.wolips.eomodeler");
			URL sqlJarUrl = bundle.getEntry("/lib/EntityModelerSQL.jar");
			if (sqlJarUrl != null) {
				classpathSet.add(sqlJarUrl);
			}
			StringBuffer webobjectsClasspath = new StringBuffer();
			Iterator classpathIter = classpathSet.iterator();
			while (classpathIter.hasNext()) {
				URL classpathUrl = (URL) classpathIter.next();
				webobjectsClasspath.append(File.pathSeparator);
				webobjectsClasspath.append(classpathUrl.getPath());
				// System.out.println("ClasspathUtils.createEOModelClassLoader:
				// " +
				// classpathUrl);
			}
			System.setProperty("com.webobjects.classpath", webobjectsClasspath.toString());
			ClassLoader eomodelClassLoader = createEOModelClassLoader(classpathSet);
			return eomodelClassLoader;
		} catch (Exception e) {
			throw new EOModelException("Failed to create EOF class loader.", e);
		}
	}

	protected synchronized ClassLoader createEOModelClassLoader(Set<URL> classpathUrlSet) {
		ClassLoader classLoader = null;
		Reference classLoaderReference = EclipseEOClassLoaderFactory.CLASSLOADER_CACHE.get(classpathUrlSet);
		if (classLoaderReference != null) {
			classLoader = (ClassLoader) classLoaderReference.get();
		}
		if (classLoader == null) {
			URL[] classpathUrls = classpathUrlSet.toArray(new URL[classpathUrlSet.size()]);
			classLoader = URLClassLoader.newInstance(classpathUrls, EclipseEOClassLoaderFactory.class.getClassLoader());
			classLoader = URLClassLoader.newInstance(classpathUrls);
			EclipseEOClassLoaderFactory.CLASSLOADER_CACHE.put(classpathUrlSet, new SoftReference<ClassLoader>(classLoader));
		}
		return classLoader;
	}

	protected void fillInClasspath(IProject project, Set<URL> classpathUrls) throws MalformedURLException, JavaModelException {
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
			fillInClasspath(dependencyProject, classpathUrls);
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

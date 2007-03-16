package org.objectstyle.wolips.eomodeler.utils;

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
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.variables.VariablesPlugin;
import org.osgi.framework.Bundle;

public class ClasspathUtils {
	private static Map CLASSLOADER_CACHE;

	static {
		CLASSLOADER_CACHE = new HashMap();
	}

	public static ClassLoader createEOModelClassLoader(IProject _project) throws JavaModelException, MalformedURLException {
		Set classpathUrlsSet = new LinkedHashSet();
		ClasspathUtils.fillInClasspath(_project, classpathUrlsSet);
		return createEOModelClassLoader(classpathUrlsSet);
	}

	public static synchronized ClassLoader createEOModelClassLoader(Set _classpathUrlSet) {
		ClassLoader classLoader = null;
		Reference classLoaderReference = (Reference) ClasspathUtils.CLASSLOADER_CACHE.get(_classpathUrlSet);
		if (classLoaderReference != null) {
			classLoader = (ClassLoader) classLoaderReference.get();
		}
		if (classLoader == null) {
			URL[] classpathUrls = (URL[]) _classpathUrlSet.toArray(new URL[_classpathUrlSet.size()]);
			classLoader = URLClassLoader.newInstance(classpathUrls, ClasspathUtils.class.getClassLoader());
			classLoader = URLClassLoader.newInstance(classpathUrls);
			ClasspathUtils.CLASSLOADER_CACHE.put(_classpathUrlSet, new SoftReference(classLoader));
		}
		return classLoader;
	}

	public static void fillInClasspath(IProject _project, Set _classpathUrls) throws MalformedURLException, JavaModelException {
		IJavaProject javaProject = JavaCore.create(_project);
		if (javaProject != null) {
			IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
			for (int classpathEntryNum = 0; classpathEntryNum < classpathEntries.length; classpathEntryNum++) {
				IClasspathEntry entry = classpathEntries[classpathEntryNum];
				ClasspathUtils.fillInClasspath(entry, _classpathUrls, javaProject);
			}
		}
	}

	protected static void fillInClasspath(IClasspathEntry _entry, Set _classpathUrls, IJavaProject _project) throws MalformedURLException, JavaModelException {
		if (_entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY && _entry.getContentKind() == IPackageFragmentRoot.K_BINARY) {
			IPath path = _entry.getPath();
			File externalFile = path.toFile();
			if (externalFile.exists()) {
				_classpathUrls.add(externalFile.toURL());
			} else {
				IFile projectJarFile = _project.getProject().getWorkspace().getRoot().getFile(path);
				if (projectJarFile.exists()) {
					_classpathUrls.add(projectJarFile.getLocation().toFile().toURL());
				}
			}
		} else if (_entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
			IProject dependencyProject = _project.getProject().getWorkspace().getRoot().getProject(_entry.getPath().lastSegment());
			ClasspathUtils.fillInClasspath(dependencyProject, _classpathUrls);
		} else if (_entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
			IPath outputLocation = _entry.getOutputLocation();
			if (outputLocation == null || outputLocation.isEmpty()) {
				outputLocation = _project.getOutputLocation();
			}
			IPath srcPath = _project.getProject().getWorkspace().getRoot().getLocation().append(outputLocation);
			_classpathUrls.add(srcPath.toFile().toURL());
		}
	}

	public static ClassLoader createEOModelClassLoader(EOModel _model) throws MalformedURLException, JavaModelException {
		Set classpathSet = new LinkedHashSet();
		if(_model.getProject() == null) {
			URL modelURL = _model.getModelURL();
			IContainer[] modelContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(URLUtils.cheatAndTurnIntoFile(modelURL).getAbsolutePath()));
			for (int modelContainerNum = 0; modelContainerNum < modelContainers.length; modelContainerNum++) {
				IContainer modelContainer = modelContainers[modelContainerNum];
				IProject modelProject = modelContainer.getProject();
				ClasspathUtils.fillInClasspath(modelProject, classpathSet);
			}
		}
		else {
			ClasspathUtils.fillInClasspath(_model.getProject(), classpathSet);
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
			// System.out.println("ClasspathUtils.createEOModelClassLoader: " +
			// classpathUrl);
		}
		System.setProperty("com.webobjects.classpath", webobjectsClasspath.toString());
		ClassLoader eomodelClassLoader = ClasspathUtils.createEOModelClassLoader(classpathSet);
		return eomodelClassLoader;
	}
}

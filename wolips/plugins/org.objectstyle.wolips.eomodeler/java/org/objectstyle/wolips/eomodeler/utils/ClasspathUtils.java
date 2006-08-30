package org.objectstyle.wolips.eomodeler.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import org.osgi.framework.Bundle;

public class ClasspathUtils {
  public static ClassLoader createEOModelClassLoader(IProject _project) throws JavaModelException, MalformedURLException {
    Set classpathUrlsSet = new LinkedHashSet();
    ClasspathUtils.fillInClasspath(_project, classpathUrlsSet);
    return createEOModelClassLoader(classpathUrlsSet);
  }

  public static ClassLoader createEOModelClassLoader(Set _classpathUrlSet) {
    URL[] classpathUrls = (URL[]) _classpathUrlSet.toArray(new URL[_classpathUrlSet.size()]);
    URLClassLoader classLoader = URLClassLoader.newInstance(classpathUrls, ClasspathUtils.class.getClassLoader());
    classLoader = URLClassLoader.newInstance(classpathUrls);
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
      }
      else {
        IFile projectJarFile = _project.getProject().getWorkspace().getRoot().getFile(path);
        if (projectJarFile.exists()) {
          _classpathUrls.add(projectJarFile.getLocation().toFile().toURL());
        }
      }
    }
    else if (_entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
      IProject dependencyProject = _project.getProject().getWorkspace().getRoot().getProject(_entry.getPath().lastSegment());
      ClasspathUtils.fillInClasspath(dependencyProject, _classpathUrls);
    }
    else if (_entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
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
    File modelFolder = _model.getModelFolder();
    IContainer[] modelContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(modelFolder.getAbsolutePath()));
    for (int modelContainerNum = 0; modelContainerNum < modelContainers.length; modelContainerNum++) {
      IContainer modelContainer = modelContainers[modelContainerNum];
      IProject modelProject = modelContainer.getProject();
      ClasspathUtils.fillInClasspath(modelProject, classpathSet);
    }

    // AK: yeah, this sucks. This is just an example on how to get rapid turnaournd to work...
    URL classUrl = new URL("file:///Users/ak/extras/woproject/wolips/plugins/org.objectstyle.wolips.eomodeler/bin/");
    if (classUrl != null) {
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
      //System.out.println("ClasspathUtils.createEOModelClassLoader: " + classpathUrl);
    }
    System.setProperty("com.webobjects.classpath", webobjectsClasspath.toString());
    ClassLoader eomodelClassLoader = ClasspathUtils.createEOModelClassLoader(classpathSet);
    return eomodelClassLoader;
  }
}

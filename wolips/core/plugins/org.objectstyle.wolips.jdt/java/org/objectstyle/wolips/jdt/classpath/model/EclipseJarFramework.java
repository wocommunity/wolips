package org.objectstyle.wolips.jdt.classpath.model;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.objectstyle.woenvironment.frameworks.AbstractJarFramework;
import org.objectstyle.woenvironment.frameworks.Root;

public class EclipseJarFramework extends AbstractJarFramework implements IEclipseFramework {
	private IClasspathEntry[] cachedClasspathEntries;

	public EclipseJarFramework(Root<?> root, File jarFile) {
		super(root, jarFile);
	}

	public IClasspathEntry[] getClasspathEntries() {
		if(cachedClasspathEntries == null) {
			IPath jarPath = new Path(getJarFile().getAbsolutePath());
			IClasspathEntry entry = JavaCore.newLibraryEntry(jarPath, null, null, ClasspathEntry.NO_ACCESS_RULES, ClasspathEntry.NO_EXTRA_ATTRIBUTES, false);
			cachedClasspathEntries = new IClasspathEntry[] {entry};
		}
		return cachedClasspathEntries;
	}
}

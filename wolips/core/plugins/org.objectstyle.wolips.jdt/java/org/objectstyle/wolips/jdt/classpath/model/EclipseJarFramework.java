package org.objectstyle.wolips.jdt.classpath.model;

import java.io.File;

import org.eclipse.jdt.core.IClasspathEntry;
import org.objectstyle.woenvironment.frameworks.AbstractJarFramework;
import org.objectstyle.woenvironment.frameworks.Root;

public class EclipseJarFramework extends AbstractJarFramework implements IEclipseFramework {
	public EclipseJarFramework(Root<?> root, File jarFile) {
		super(root, jarFile);
	}

	public IClasspathEntry[] getClasspathEntries() {
		return null;
	}
}

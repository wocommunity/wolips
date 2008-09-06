package org.objectstyle.wolips.jdt.classpath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;

/**
 * @author mschrag
 */
public class WOFrameworkClasspathContainer implements IClasspathContainer {
	public static final String ID = "WOFramework";

	private IEclipseFramework framework;

	private Map<String, String> params;

	public WOFrameworkClasspathContainer(IEclipseFramework framework) {
		this(framework, new HashMap<String, String>());
	}

	public WOFrameworkClasspathContainer(IEclipseFramework framework, Map<String, String> params) {
		this.framework = framework;
		this.params = params;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public IEclipseFramework getFramework() {
		return this.framework;
	}

	public String getDescription() {
		return getFramework().getName() + " Framework";
	}

	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	public IPath getPath() {
		IPath path = new Path(WOFrameworkClasspathContainer.ID + "/" + getFramework().getName());
		for (Map.Entry<String, String> param : this.params.entrySet()) {
			path = path.append(param.getKey() + "=" + param.getValue());
		}
		return path;
	}

	public IClasspathEntry[] getClasspathEntries() {
		List<IClasspathEntry> classpathEntries = this.framework.getClasspathEntries();
		return classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]);
	}

	public static WOFrameworkClasspathContainer getFrameworkClasspathContainer(IJavaProject project, IClasspathEntry classpathEntry) throws JavaModelException {
		WOFrameworkClasspathContainer frameworkContainer = null;
		IClasspathContainer container = JavaCore.getClasspathContainer(classpathEntry.getPath(), project);
		if (container instanceof WOFrameworkClasspathContainer) {
			frameworkContainer = (WOFrameworkClasspathContainer) container;
		}
		return frameworkContainer;
	}

	public static boolean isFrameworkClasspathEntry(IJavaProject project, IClasspathEntry classpathEntry) throws JavaModelException {
		IClasspathContainer container = JavaCore.getClasspathContainer(classpathEntry.getPath(), project);
		return container instanceof WOFrameworkClasspathContainer;
	}
}

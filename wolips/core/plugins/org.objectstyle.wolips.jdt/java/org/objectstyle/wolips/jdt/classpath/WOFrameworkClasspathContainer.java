package org.objectstyle.wolips.jdt.classpath;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.jdt.JdtPlugin;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;

/**
 * @author mschrag
 */
public class WOFrameworkClasspathContainer implements IClasspathContainer {
	public static final String ID = "WOFramework";

	private IEclipseFramework framework;

	private Map<String, String> params;
	
	private String _name;

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
		if (_name == null) {
			_name = getFramework().getName() + " Framework";
		}
		return _name;
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
		return this.framework.getClasspathEntries();
	}

	public static WOFrameworkClasspathContainer getFrameworkClasspathContainer(IJavaProject project, IClasspathEntry classpathEntry) {
		WOFrameworkClasspathContainer frameworkContainer = null;
		try {
			IClasspathContainer container = JavaCore.getClasspathContainer(classpathEntry.getPath(), project);
			if (container instanceof WOFrameworkClasspathContainer) {
				frameworkContainer = (WOFrameworkClasspathContainer) container;
			}
		}
		catch (Exception e) {
			JdtPlugin.getDefault().getPluginLogger().debug(e);
		}
		return frameworkContainer;
	}

	public static boolean isFrameworkClasspathEntry(IJavaProject project, IClasspathEntry classpathEntry) throws JavaModelException {
		IClasspathContainer container = JavaCore.getClasspathContainer(classpathEntry.getPath(), project);
		return container instanceof WOFrameworkClasspathContainer;
	}
}

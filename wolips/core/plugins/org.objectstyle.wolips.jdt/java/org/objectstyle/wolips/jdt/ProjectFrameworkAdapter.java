package org.objectstyle.wolips.jdt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.woenvironment.frameworks.FrameworkModel;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.jdt.classpath.model.IEclipseFramework;

public class ProjectFrameworkAdapter {
	private IProject _project;

	public ProjectFrameworkAdapter(IProject project) {
		_project = project;
	}

	public IProject getProject() {
		return _project;
	}

	public void addFrameworkNamed(String frameworkName) throws JavaModelException {
		addFrameworksNamed(frameworkName);
	}

	public void addFrameworksNamed(List<String> frameworkNames) throws JavaModelException {
		addFrameworksNamed(frameworkNames.toArray(new String[frameworkNames.size()]));
	}

	public void addFrameworksNamed(String... frameworkNames) throws JavaModelException {
		IProject project = getProject();
		FrameworkModel<IEclipseFramework> frameworkModel = JdtPlugin.getDefault().getFrameworkModel(project);
		List<IEclipseFramework> frameworks = new LinkedList<IEclipseFramework>();
		for (String frameworkName : frameworkNames) {
			IEclipseFramework framework = frameworkModel.getFrameworkWithName(frameworkName);
			if (framework == null) {
				throw new NoSuchElementException("There is no framework named '" + frameworkName + "'.");
			}
			frameworks.add(framework);
		}
		IJavaProject javaProject = JavaCore.create(project);
		IEclipseFramework.Utility.addFrameworksToProject(frameworks, javaProject, true);
	}
	
	public void addFrameworks(IEclipseFramework... frameworks) throws JavaModelException {
		List<IEclipseFramework> frameworksList = new LinkedList<IEclipseFramework>();
		for (IEclipseFramework framework : frameworks) {
			frameworksList.add(framework);
		}
		IProject project = getProject();
		IJavaProject javaProject = JavaCore.create(project);
		IEclipseFramework.Utility.addFrameworksToProject(frameworksList, javaProject, true);
	}

	public void removeFrameworkNamed(String frameworkName) throws JavaModelException {
		removeFrameworksNamed(frameworkName);
	}

	public void removeFrameworksNamed(List<String> frameworkNames) throws JavaModelException {
		removeFrameworksNamed(frameworkNames.toArray(new String[frameworkNames.size()]));
	}

	public void removeFrameworksNamed(String... frameworkNames) throws JavaModelException {
		IProject project = getProject();
		IJavaProject javaProject = JavaCore.create(project);

		FrameworkModel<IEclipseFramework> frameworkModel = JdtPlugin.getDefault().getFrameworkModel(project);
		List<IEclipseFramework> frameworks = new LinkedList<IEclipseFramework>();
		for (String frameworkName : frameworkNames) {
			IEclipseFramework framework = frameworkModel.getFrameworkWithName(frameworkName);
			if (framework == null) {
				throw new NoSuchElementException("There is no framework named '" + frameworkName + "'.");
			}
			frameworks.add(framework);
		}

		IEclipseFramework.Utility.removeFrameworksFromProject(frameworks, javaProject, true);
	}

	public List<IEclipseFramework> getFrameworks() throws JavaModelException {
		IProject project = getProject();
		IJavaProject javaProject = JavaCore.create(project);
		return IEclipseFramework.Utility.getFrameworks(javaProject);
	}

	public Set<String> getFrameworkNames() throws JavaModelException {
		Set<String> frameworkNames = new HashSet<String>();
		for (IEclipseFramework framework : getFrameworks()) {
			frameworkNames.add(framework.getName());
		}
		return frameworkNames;
	}

	public boolean isLinkedToFrameworkNamed(String frameworkName) throws JavaModelException {
		return getFrameworkNames().contains(frameworkName);
	}

	public Map<String, IEclipseFramework> getPluginFrameworks() {
		Map<String, IEclipseFramework> pluginFrameworks = new TreeMap<String, IEclipseFramework>(); 
		Pattern pluginPattern = Pattern.compile("(.*)PlugIn");
		FrameworkModel<IEclipseFramework> frameworkModel = JdtPlugin.getDefault().getFrameworkModel(getProject());
		for (IEclipseFramework framework : frameworkModel.getAllFrameworks()) {
			String frameworkName = framework.getName();
			Matcher matcher = pluginPattern.matcher(frameworkName);
			if (matcher.matches()) {
				String pluginName = matcher.group(1);
				pluginFrameworks.put(pluginName, framework);
			}
		}
		return pluginFrameworks;
	}

	public Map<String, IEclipseFramework> getAdaptorFrameworks() {
		Map<String, IEclipseFramework> adaptorFrameworks = new TreeMap<String, IEclipseFramework>(); 
		Pattern adaptorPattern = Pattern.compile("Java(.*)Adaptor");
		FrameworkModel<IEclipseFramework> frameworkModel = JdtPlugin.getDefault().getFrameworkModel(getProject());
		for (IEclipseFramework framework : frameworkModel.getAllFrameworks()) {
			String frameworkName = framework.getName();
			Matcher matcher = adaptorPattern.matcher(frameworkName);
			if (matcher.matches()) {
				String adaptorName = matcher.group(1);
				adaptorFrameworks.put(adaptorName, framework);
			}
		}
		return adaptorFrameworks;
	}

	/** OLD FRAMEWORK API'S -- SHOULD BE REWRITTEN USING NEW FRAMEWORK API'S **/
	public List<IPath> getFrameworkPaths() {
//		for (IEclipseFramework framework : getFrameworks()) {
//			
//		}
		ArrayList<IPath> list = new ArrayList<IPath>();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isFrameworkReference(projects[i])) {
				list.add(projects[i].getLocation());
			}
		}
		try {
			IJavaProject javaProject = JavaCore.create(getProject());
			list.addAll(toFrameworkPaths(javaProject.getResolvedClasspath(false)));
		} catch (JavaModelException e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
		}
		return list;
	}

	private List<IPath> toFrameworkPaths(IClasspathEntry[] classpathEntries) {
		ArrayList<IPath> arrayList = new ArrayList<IPath>();
		for (int i = 0; i < classpathEntries.length; i++) {
			IPath path = classpathEntries[i].getPath();
			IPath choppedFrameworkPath = null;
			int count = path.segmentCount();
			for (int pathElementNum = 0; pathElementNum < count && choppedFrameworkPath == null; pathElementNum++) {
				String segment = path.segment(pathElementNum);
				if (segment.endsWith("." + "framework")) {
					choppedFrameworkPath = path.removeLastSegments(count - pathElementNum - 1);
				}
			}
			if (choppedFrameworkPath != null && !choppedFrameworkPath.lastSegment().startsWith("JavaVM")) {
				arrayList.add(choppedFrameworkPath);
			}
		}
		return arrayList;
	}

	public boolean isFrameworkReference(IProject iProject) {
		boolean isFrameworkReference;
		IJavaProject javaProject = null;
		try {
			javaProject = JavaCore.create(getProject());
			if (javaProject == null) {
				isFrameworkReference = false;
			} else {
				IProjectAdapter project = (IProjectAdapter) iProject.getAdapter(IProjectAdapter.class);
				isFrameworkReference = project != null && project.isFramework() && ProjectFrameworkAdapter.isProjectReferencedByProject(iProject, javaProject.getProject());
			}
		} catch (Exception e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
			isFrameworkReference = false;
		}
		return isFrameworkReference;
	}

	public static boolean isProjectReferencedByProject(IProject child, IProject mother) {
		IProject[] projects;
		try {
			projects = mother.getReferencedProjects();
		} catch (Exception e) {
			JdtPlugin.getDefault().getPluginLogger().log(e);
			return false;
		}
		for (IProject project : projects) {
			if (project.equals(child)) {
				return true;
			}
		}
		return false;
	}

}

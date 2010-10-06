package org.objectstyle.wolips.launching.classpath;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.objectstyle.woenvironment.frameworks.DependencyOrdering;
import org.objectstyle.wolips.jdt.classpath.model.EclipseDependency;

public class EclipseDependencyOrdering extends DependencyOrdering<EclipseDependency> {
	private Set<IPath> _allProjectArchiveEntries;

	private IProject _project;
	  
	public EclipseDependencyOrdering(IProject project, boolean includeProjectDependency) {
		super(includeProjectDependency);
		_project = project;
	}

	protected void initialize() {
		super.initialize();
		_allProjectArchiveEntries = new HashSet<IPath>();
	}

	protected void addWOProject(EclipseDependency dependency) {
		IPath projectArchive = dependency.getWOJavaArchive();
		if (!_allProjectArchiveEntries.contains(projectArchive)) {
			pendingResult.add(dependency);
			IRuntimeClasspathEntry resolvedEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(projectArchive);
			pendingResult.add(new EclipseDependency(_project, resolvedEntry));
			_allProjectArchiveEntries.add(projectArchive);
		}
	}
}

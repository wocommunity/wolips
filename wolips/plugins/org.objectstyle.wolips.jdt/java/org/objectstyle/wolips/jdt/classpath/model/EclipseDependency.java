package org.objectstyle.wolips.jdt.classpath.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.objectstyle.woenvironment.frameworks.Dependency;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;
import org.objectstyle.wolips.variables.ProjectVariables;
import org.objectstyle.wolips.variables.VariablesPlugin;

public class EclipseDependency extends Dependency {
	private IProject _project;

	private IRuntimeClasspathEntry _classpathEntry;
	
	private ProjectVariables _variables;

	public EclipseDependency(IProject project, IRuntimeClasspathEntry classpathEntry) {
		_project = project;
		_variables = VariablesPlugin.getDefault().getProjectVariables(project);
		_classpathEntry = classpathEntry;
	}

	@Override
	public String getRawPath() {
		return _classpathEntry.getPath().toString();
	}

	public IRuntimeClasspathEntry getClasspathEntry() {
		return _classpathEntry;
	}

	public boolean isProject() {
		return IRuntimeClasspathEntry.PROJECT == _classpathEntry.getType();
	}

	public boolean isWOProject() {
		return getWOJavaArchive() != null;
	}

	// MS: This is a total hack ... It should use
	// the WOLips API to framework name.  For most, I think it works
	// out, and in particular, for Wonder it does. 
	public String getProjectFrameworkName() {
		IProject project = (IProject) _classpathEntry.getResource();
		String frameworkName = project.getName() + ".framework";
		return frameworkName;
	}

	@Override
	public String getLocation() {
		return _classpathEntry.getLocation();
	}

	@Override
	public String getSystemRoot() {
		return _variables.getSystemRoot().toString();
	}

	public IPath getWOJavaArchive() {
		try {
			IPath woJavaArchivePath = null;
			if (isProject()) {
				IProject project = (IProject) _classpathEntry.getResource();
				IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
				if (projectAdapter != null) {
					woJavaArchivePath = projectAdapter.getWOJavaArchive();
				} else {
					woJavaArchivePath = null;
				}
			}
			return woJavaArchivePath;
		} catch (CoreException e) {
			throw new RuntimeException("Failed to retrieve WO project metadata.", e);
		}
	}
}

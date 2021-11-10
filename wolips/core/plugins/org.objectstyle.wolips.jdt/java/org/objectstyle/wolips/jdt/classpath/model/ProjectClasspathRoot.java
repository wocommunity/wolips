package org.objectstyle.wolips.jdt.classpath.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.woenvironment.frameworks.Root;
import org.objectstyle.wolips.baseforplugins.util.WOLipsNatureUtils;

public class ProjectClasspathRoot extends Root<IEclipseFramework> {
	private IProject project;
	
	private Set<IEclipseFramework> frameworks;
	
	public ProjectClasspathRoot(String shortName, String name, IProject project) {
		super(shortName, name);
		this.project = project;
	}

	@Override
	public Set<IEclipseFramework> getFrameworks() {
		if(frameworks == null) {
			Set<IEclipseFramework> result =  new HashSet<IEclipseFramework>();
			boolean isMaven = WOLipsNatureUtils.isMavenNature(this.project);
			if(isMaven) {
				IJavaProject javaProject = JavaCore.create(this.project);
				try {
					IClasspathEntry[] entries = javaProject.getResolvedClasspath(false);
					for(IClasspathEntry entry : entries) {
						if(IClasspathEntry.CPE_LIBRARY == entry.getEntryKind()) {
							EclipseJarFramework jarFramework =  new EclipseJarFramework(this, entry.getPath().toFile());
							if(jarFramework.getInfoPlist() != null) {
								result.add(jarFramework);
							}
						}
					}
				} catch (JavaModelException e) {
					throw new RuntimeException(e);
				}
			}
			frameworks = result;
		}
		return frameworks;
	}

	@Override
	public Set<IEclipseFramework> getApplications() {
		return new HashSet<IEclipseFramework>();
	}

}

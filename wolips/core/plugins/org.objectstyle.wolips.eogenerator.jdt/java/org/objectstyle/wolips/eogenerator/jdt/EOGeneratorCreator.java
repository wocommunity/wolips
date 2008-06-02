package org.objectstyle.wolips.eogenerator.jdt;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;

public class EOGeneratorCreator {
	public static EOGeneratorModel createDefaultModel(IProject project) {
		EOGeneratorModel model = new EOGeneratorModel(project);
		model.setJava(Boolean.TRUE);
		model.setPackageDirs(Boolean.TRUE);
		model.setVerbose(Boolean.TRUE);
		model.setLoadModelGroup(Boolean.TRUE);
		// model.setEOGeneratorPath(Preferences.getEOGeneratorPath());
		// model.setJavaTemplate(Preferences.getEOGeneratorJavaTemplate());
		// model.setTemplateDir(Preferences.getEOGeneratorTemplateDir());
		// model.setSubclassJavaTemplate(Preferences.getEOGeneratorSubclassJavaTemplate());
		try {
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject != null) {
				IClasspathEntry[] classpathEntry = javaProject.getRawClasspath();
				for (int classpathEntryNum = 0; classpathEntryNum < classpathEntry.length; classpathEntryNum++) {
					IClasspathEntry entry = classpathEntry[classpathEntryNum];
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath path = entry.getPath();
						if (path != null) {
							IFolder sourceFolder = project.getWorkspace().getRoot().getFolder(path);
							IPath projectRelativePath = sourceFolder.getProjectRelativePath();
							String projectRelativePathStr = projectRelativePath.toPortableString();
							model.setDestination(projectRelativePathStr);
							model.setSubclassDestination(projectRelativePathStr);
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return model;
	}
}

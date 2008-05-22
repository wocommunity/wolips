package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class WOMojo extends AbstractMojo {

	public final static String MAVEN_WEBOBJECTS_GROUP_ID = "com.apple.webobjects";

	public WOMojo() {
		super();
	}

	public abstract String getProductExtension();

	public abstract MavenProject getProject();

	protected String getProjectFolder() {
		String projectFolder = getProject().getFile().getPath().substring(0, getProject().getFile().getPath().length() - 7);
		return projectFolder;
	}

	protected String getWOProjectFolder() {
		File file = new File(this.getProjectFolder() + "woproject");
		if (file.exists()) {
			return file.getPath();
		}
		return null;
	}

	protected boolean isWebObjectAppleGroup(String dependencyGroup) {
		if (dependencyGroup == null) {
			return false;
		}

		String normalizedGroup = FilenameUtils.separatorsToUnix(dependencyGroup);

		boolean returnValue = MAVEN_WEBOBJECTS_GROUP_ID.equals(normalizedGroup);

		getLog().info("WOMojo: the group " + normalizedGroup + " is " + (returnValue ? "" : "NOT ") + "an Apple group.");

		return returnValue;
	}
}

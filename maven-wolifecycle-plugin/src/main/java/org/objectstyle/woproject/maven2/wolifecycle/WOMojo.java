package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class WOMojo extends AbstractMojo {

	public final static String MAVEN_WEBOBJECTS_GROUP_ID = "webobjects/apple";

	public WOMojo() {
		super();
	}

	private boolean containsDot(String text) {
		return text.indexOf('.') >= 0;
	}

	public abstract String getProductExtension();

	public abstract MavenProject getProject();

	protected String getProjectFolder() {
		String projectFolder = this.getProject().getFile().getPath().substring(0, this.getProject().getFile().getPath().length() - 7);
		return projectFolder;
	}

	protected String getWOProjectFolder() {
		File file = new File(this.getProjectFolder() + "woproject");
		if (file.exists()) {
			return file.getPath();
		}
		return null;
	}

	protected boolean isWebobjectAppleGroup(String dependencyGroup) {
		if (dependencyGroup == null) {
			return false;
		}

		if (containsDot(dependencyGroup)) {
			throw new IllegalArgumentException("Dependency group cannot contains '.' (dot).");
		}

		String normalizedGroup = FilenameUtils.separatorsToUnix(dependencyGroup);

		boolean returnValue = MAVEN_WEBOBJECTS_GROUP_ID.equals(normalizedGroup);

		getLog().debug("WOMojo: isWebobjectAppleGroup: " + normalizedGroup + " return value " + returnValue);

		return returnValue;
	}
}

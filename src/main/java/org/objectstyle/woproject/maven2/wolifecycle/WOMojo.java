package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class WOMojo extends AbstractMojo {

	public WOMojo() {
		super();
	}

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

	public abstract String getProductExtension();

	public abstract MavenProject getProject();
}

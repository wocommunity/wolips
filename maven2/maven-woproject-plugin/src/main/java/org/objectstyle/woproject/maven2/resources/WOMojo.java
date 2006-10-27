package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class WOMojo extends AbstractMojo {

	public WOMojo() throws MojoExecutionException {
		super();
	}

	protected String getProjectFolder() {
		String projectFolder = this.getProject().getFile().getPath().substring(0, this.getProject().getFile().getPath().length() - 7);
		return projectFolder;
	}

	private String getWOProjectFolder() {
		File file = new File(this.getProjectFolder() + "woproject");
		if (file.exists()) {
			return file.getPath();
		}
		return null;
	}

	public abstract String getProductExtension();

	public abstract MavenProject getProject();
}

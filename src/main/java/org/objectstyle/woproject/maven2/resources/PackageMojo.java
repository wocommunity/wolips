package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

public abstract class PackageMojo extends WOMojo {

	public PackageMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Defining wo resources");
		String fileName = /*"this.getProjectFolder() + */this.getArtifactFileName();
		getLog().info("Defining artifact filename: " + fileName);
		this.getProject().getArtifact().setFile( new File( fileName ) );
	}

	protected abstract String getArtifactFileName();
}

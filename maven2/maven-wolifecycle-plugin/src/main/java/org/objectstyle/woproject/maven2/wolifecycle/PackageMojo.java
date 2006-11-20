package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class PackageMojo extends WOMojo {

	public PackageMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Package wo");
		String artifactFileName = this.getArtifactFileName();
		if (artifactFileName != null) {
			String fileName = this.getProjectFolder() + artifactFileName;
			getLog().info("Defining artifact filename: " + fileName);
			this.getProject().getArtifact().setFile(new File(fileName));
		}
	}

	protected abstract String getArtifactFileName();
}

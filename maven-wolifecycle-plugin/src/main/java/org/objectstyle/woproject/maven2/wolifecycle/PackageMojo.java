package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class PackageMojo extends WOMojo {

	public PackageMojo() {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Package wo");

		String artifactFileName = this.getArtifactFileName();

		BufferedWriter artifactWriter = null;

		try {
			getLog().debug("Package wo ... writing artifact to: " + artifactFileName);

			artifactWriter = new BufferedWriter(new FileWriter(new File(artifactFileName)));
			artifactWriter.write("\n");
		} catch (IOException ioe) {
			new MojoExecutionException("could not write " + artifactFileName, ioe);
		} finally {
			if (null != artifactWriter) {
				try {
					artifactWriter.close();
				} catch (IOException ioe) {
					// Ignore exception
				}
			}
		}

		if (artifactFileName != null) {
			String fileName = artifactFileName;
			getLog().debug("Defining artifact filename: " + fileName);
			this.getProject().getArtifact().setFile(new File(fileName));
		}
	}

	protected abstract String getArtifactFileName();
}

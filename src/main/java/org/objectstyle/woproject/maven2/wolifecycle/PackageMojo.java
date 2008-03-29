package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.*;

import org.apache.commons.io.*;
import org.apache.maven.plugin.*;

public abstract class PackageMojo extends WOMojo {

	public PackageMojo() {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Starting to package WebObject project...");

		String artifactFileName = getArtifactFileName();

		BufferedWriter artifactWriter = null;

		getLog().debug("Packaging WebObject project: Writing artifact to " + artifactFileName);

		try {
			artifactWriter = new BufferedWriter(new FileWriter(new File(artifactFileName)));

			artifactWriter.write("This is an empty file created beacuse of the Maven extension mechanism.\n");

		} catch (IOException ioe) {
			new MojoExecutionException("Packaging WebObject project: Could not package the WebObjects project. Error writing" + artifactFileName, ioe);
		} finally {
			IOUtils.closeQuietly(artifactWriter);
		}

		if (artifactFileName == null) {
			return;
		}

		String fileName = artifactFileName;

		getLog().debug("Packaging WebObject project: Defining artifact filename as " + fileName);

		this.getProject().getArtifact().setFile(new File(fileName));
	}

	protected abstract String getArtifactFileName();
}

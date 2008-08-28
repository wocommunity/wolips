package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;

public abstract class AbstractPackageMojo extends AbstractWOMojo {
	/**
	 * Classifier to add to the artifact generated. If given, the artifact will
	 * be an attachment instead.
	 * 
	 * @parameter
	 */
	private String classifier;

	/**
	 * The name of the generated package (framework or application).
	 * 
	 * @parameter expression="${project.build.finalName}"
	 * @required
	 */
	private String finalName;

	/**
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	public AbstractPackageMojo() {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Starting to package WebObject project...");

		File artifactFile = getArtifactFile();

		BufferedWriter artifactWriter = null;

		getLog().debug("Writing artifact to " + artifactFile.getAbsolutePath());

		try {
			artifactWriter = new BufferedWriter(new FileWriter(artifactFile));

			artifactWriter.write("This is an empty file created beacuse of the Maven extension mechanism.\n");

		} catch (IOException ioe) {
			new MojoExecutionException("Could not package the WebObjects project. Error writing" + artifactFile.getAbsolutePath(), ioe);
		} finally {
			IOUtils.closeQuietly(artifactWriter);
		}

		if (artifactFile == null) {
			return;
		}

		getLog().debug("Defining artifact filename as " + artifactFile.getName());

		getProject().getArtifact().setFile(artifactFile);
	}

	protected File getArtifactFile() {
		return new File(getBuildDirectory(), getFinalName() + getClassifierAsString() + "." + getProductExtension());
	}

	public String getClassifier() {

		return classifier;
	}

	protected String getClassifierAsString() {
		return getClassifier() == null ? "" : "-" + getClassifier();
	}

	public String getFinalName() {
		return finalName;
	}

	public MavenProjectHelper getProjectHelper() {
		return projectHelper;
	}

	public void setClassifier(final String classifier) {
		this.classifier = classifier;
	}

	public void setFinalName(final String finalName) {
		this.finalName = finalName;
	}

	public void setProjectHelper(final MavenProjectHelper projectHelper) {
		this.projectHelper = projectHelper;
	}
}

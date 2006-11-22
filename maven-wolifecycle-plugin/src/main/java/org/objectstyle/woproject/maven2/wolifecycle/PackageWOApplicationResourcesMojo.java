package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal package-woapplication
 * @requiresDependencyResolution compile
 * @author uli
 * @since 2.0
 */
public class PackageWOApplicationResourcesMojo extends PackageMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
	 * @required
	 * @readonly
	 */
	private ArtifactFactory artifactFactory;

	public PackageWOApplicationResourcesMojo() throws MojoExecutionException {
		super();
	}

	public MavenProject getProject() {
		return project;
	}

	public String getProductExtension() {
		return "woa";
	}

	protected String getArtifactFileName() {
		return "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication";
	}

	protected String getWOApplicationFileName() {
		return "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication.tar.gz";
	}

	private String getWOWeberverResourcesArtifactFileName() {
		return "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".wowebserverresources.tar.gz";
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Package wo");

		BufferedWriter woapplicationWriter = null;
		String filename = getArtifactFileName();
		try {
			woapplicationWriter = new BufferedWriter(new FileWriter(new File(filename)));
			woapplicationWriter.write("\n");
		} catch (IOException ioe) {
			new MojoExecutionException("could not write " + filename, ioe);
		} finally {
			if (null != woapplicationWriter) {
				try {
					woapplicationWriter.close();
				} catch (IOException ioe) {
					// Ignore exception
				}
			}
		}
		String artifactFileName = this.getArtifactFileName();
		if (artifactFileName != null) {
			String fileName = this.getProjectFolder() + artifactFileName;
			getLog().info("Defining artifact filename: " + fileName);
			this.getProject().getArtifact().setFile(new File(fileName));
		}
		Artifact artifact = artifactFactory.createBuildArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), "woapplication.tar.gz");

		artifact.setFile(new File(this.getWOApplicationFileName()));
		getLog().info("Attaching artifact: " + this.getWOWeberverResourcesArtifactFileName());
		project.addAttachedArtifact(artifact);
		artifact = artifactFactory.createBuildArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), "wowebserverresources.tar.gz");

		artifact.setFile(new File(this.getWOWeberverResourcesArtifactFileName()));
		getLog().info("Attaching artifact: " + this.getWOWeberverResourcesArtifactFileName());
		project.addAttachedArtifact(artifact);
	}
}

package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

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
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication";
	}

	protected String getWOApplicationFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication.tar.gz";
	}

	private String getWOWeberverResourcesArtifactFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".wowebserverresources.tar.gz";
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		Artifact artifact = artifactFactory.createBuildArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), "woapplication.tar.gz");

		artifact.setFile(new File(this.getWOApplicationFileName()));
		getLog().info("Attaching artifact: " + this.getWOApplicationFileName());
		project.addAttachedArtifact(artifact);

		artifact = artifactFactory.createBuildArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), "wowebserverresources.tar.gz");
		artifact.setFile(new File(this.getWOWeberverResourcesArtifactFileName()));
		getLog().info("Attaching artifact: " + this.getWOWeberverResourcesArtifactFileName());
		project.addAttachedArtifact(artifact);
	}
}

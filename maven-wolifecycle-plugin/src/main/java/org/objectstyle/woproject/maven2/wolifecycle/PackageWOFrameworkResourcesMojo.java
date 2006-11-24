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
 * @goal package-woframework
 * @author uli
 * @since 2.0
 */
public class PackageWOFrameworkResourcesMojo extends PackageMojo {

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

	public PackageWOFrameworkResourcesMojo() {
		super();
	}

	public MavenProject getProject() {
		return project;
	}

	public String getProductExtension() {
		return "framework";
	}

	protected String getArtifactFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woframework";
	}

	protected String getWOFrameworkFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".jar";
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		Artifact artifact = artifactFactory.createBuildArtifact(project.getGroupId(), project.getArtifactId(), project.getVersion(), "jar");
		artifact.setFile(new File(this.getWOFrameworkFileName()));
		getLog().info("Attaching artifact: " + this.getWOFrameworkFileName());
		project.addAttachedArtifact(artifact);
	}

}

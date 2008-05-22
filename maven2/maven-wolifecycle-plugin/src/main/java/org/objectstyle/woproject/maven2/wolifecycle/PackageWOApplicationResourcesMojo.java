package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal package-woapplication
 * @requiresDependencyResolution compile
 * @author uli
 * @author hprange
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
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	public PackageWOApplicationResourcesMojo() {
		super();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		getLog().debug("Attaching artifact: " + this.getWOApplicationFileName());

		projectHelper.attachArtifact(project, "woapplication.tar.gz", new File(this.getWOApplicationFileName()));

		getLog().debug("Attaching artifact: " + this.getWOWebServerResourcesArtifactFileName());

		projectHelper.attachArtifact(project, "wowebserverresources.tar.gz", new File(this.getWOWebServerResourcesArtifactFileName()));
	}

	@Override
	protected String getArtifactFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication";
	}

	@Override
	public String getProductExtension() {
		return "woa";
	}

	@Override
	public MavenProject getProject() {
		return project;
	}

	protected String getWOApplicationFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication.tar.gz";
	}

	private String getWOWebServerResourcesArtifactFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".wowebserverresources.tar.gz";
	}
}

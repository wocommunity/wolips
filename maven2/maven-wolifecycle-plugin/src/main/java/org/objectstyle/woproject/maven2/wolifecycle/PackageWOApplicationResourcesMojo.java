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
public class PackageWOApplicationResourcesMojo extends AbstractPackageMojo {

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

		File woapplicationFile = getWOApplicationFile();

		getLog().info("Attaching artifact: " + woapplicationFile.getAbsolutePath());

		projectHelper.attachArtifact(project, "woapplication.tar.gz", woapplicationFile);

		File webServerResourcesArtifactFile = getWOWebServerResourcesArtifactFile();

		getLog().info("Attaching artifact: " + webServerResourcesArtifactFile.getAbsolutePath());

		projectHelper.attachArtifact(project, "wowebserverresources.tar.gz", webServerResourcesArtifactFile);
	}

	@Override
	protected File getArtifactFile() {
		return new File(getBuildFolder(), getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woapplication");
	}

	@Override
	public String getProductExtension() {
		return "woa";
	}

	@Override
	public MavenProject getProject() {
		return project;
	}

	protected File getWOApplicationFile() {
		return new File(getBuildFolder(), getProject().getArtifactId() + "-" + getProject().getVersion() + ".woapplication.tar.gz");
	}

	private File getWOWebServerResourcesArtifactFile() {
		return new File(getBuildFolder(), getProject().getArtifactId() + "-" + getProject().getVersion() + ".wowebserverresources.tar.gz");
	}
}

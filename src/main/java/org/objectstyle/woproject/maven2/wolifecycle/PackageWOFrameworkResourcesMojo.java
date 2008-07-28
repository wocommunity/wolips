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
 * @goal package-woframework
 * @author uli
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @since 2.0
 */
public class PackageWOFrameworkResourcesMojo extends AbstractPackageMojo {

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

	public PackageWOFrameworkResourcesMojo() {
		super();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		File frameworkFile = getWOFrameworkFile();

		getLog().info("Attaching artifact " + frameworkFile.getAbsolutePath());

		projectHelper.attachArtifact(getProject(), "jar", frameworkFile);
	}

	@Override
	protected File getArtifactFile() {
		return new File(getBuildFolder(), getProject().getArtifactId() + "-" + getProject().getVersion() + ".woframework");
	}

	@Override
	public String getProductExtension() {
		return "framework";
	}

	@Override
	public MavenProject getProject() {
		return project;
	}

	protected File getWOFrameworkFile() {
		return new File(getBuildFolder(), getProject().getArtifactId() + "-" + getProject().getVersion() + ".jar");
	}

}

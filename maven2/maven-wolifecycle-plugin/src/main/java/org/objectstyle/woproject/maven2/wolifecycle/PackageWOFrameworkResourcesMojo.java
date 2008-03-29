package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.*;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal package-woframework
 * @author uli
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
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
	 * @component
	 */
	private MavenProjectHelper projectHelper;

	public PackageWOFrameworkResourcesMojo() {
		super();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		getLog().info("Packaging WebObject project: attaching artifact " + this.getWOFrameworkFileName());

		File woFrameworkJar = new File(getWOFrameworkFileName());

		projectHelper.attachArtifact(getProject(), "jar", woFrameworkJar);
	}

	@Override
	protected String getArtifactFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woframework";
	}

	@Override
	public String getProductExtension() {
		return "framework";
	}

	@Override
	public MavenProject getProject() {
		return project;
	}

	protected String getWOFrameworkFileName() {
		return this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".jar";
	}

}

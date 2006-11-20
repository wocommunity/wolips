package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

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
		return "target" + File.separator + this.getProject().getArtifactId()
				+ "-" + this.getProject().getVersion() + ".woapplication.tar.gz";
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Package wo");
		String artifactFileName = this.getArtifactFileName();
		if (artifactFileName != null) {
			String fileName = this.getProjectFolder() + artifactFileName;
			getLog().info("Defining artifact filename: " + fileName);
			this.getProject().getArtifact().setFile(new File(fileName));
		}
	}
}

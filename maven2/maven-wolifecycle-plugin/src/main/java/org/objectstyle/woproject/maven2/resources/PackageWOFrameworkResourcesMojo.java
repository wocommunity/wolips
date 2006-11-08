package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
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

	public PackageWOFrameworkResourcesMojo() throws MojoExecutionException {
		super();
	}

	public MavenProject getProject() {
		return project;
	}
	
	public String getProductExtension() {
		return "framework";
	}

	protected String getArtifactFileName() {
		return "target" + File.separator + this.getProject().getArtifactId()
				+ "-" + this.getProject().getVersion() + ".jar";
	}
}

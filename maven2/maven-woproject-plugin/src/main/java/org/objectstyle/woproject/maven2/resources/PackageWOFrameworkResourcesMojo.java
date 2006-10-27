package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.IOException;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
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
public class PackageWOFrameworkResourcesMojo extends DefineResourcesMojo {

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
				+ "-" + this.getProject().getVersion() + "-woframework.jar";
	}
}

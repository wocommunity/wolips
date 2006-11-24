package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal define-woframework-resources
 * @author uli
 * @since 2.0
 */
public class DefineWOFrameworkResourcesMojo extends DefineResourcesMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Read patternsets.
	 * 
	 * @parameter expression="readPatternsets"
	 */
	private Boolean readPatternsets;

	public DefineWOFrameworkResourcesMojo() {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
	}

	public MavenProject getProject() {
		return project;
	}

	public String getProductExtension() {
		return "framework";
	}

	public boolean hasContentsFolder() {
		return false;
	}

	protected Boolean readPatternsets() {
		return readPatternsets;
	}

	public boolean includesVersionInArtifactName() {
		return false;
	}
}

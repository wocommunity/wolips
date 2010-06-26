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
public class DefineWOFrameworkResourcesMojo extends AbstractDefineResourcesMojo {

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
	super.execute();
    }

    @Override
    public MavenProject getProject() {
	return project;
    }

    @Override
    public String getProductExtension() {
	return "framework";
    }

    @Override
    public boolean hasContentsFolder() {
	return false;
    }

    @Override
    protected Boolean readPatternsets() {
	return readPatternsets;
    }

    @Override
    public boolean includesVersionInArtifactName() {
	return false;
    }
}

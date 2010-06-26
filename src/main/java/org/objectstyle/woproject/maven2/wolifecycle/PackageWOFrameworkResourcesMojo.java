package org.objectstyle.woproject.maven2.wolifecycle;

// org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal package-woframework
 * @phase package
 * @requiresProject
 * @requiresDependencyResolution compile
 * @author uli
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
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

    public PackageWOFrameworkResourcesMojo() {
	super();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
	super.execute();
    }

    @Override
    public String getProductExtension() {
	return "jar";
    }

    @Override
    public MavenProject getProject() {
	return project;
    }

    protected File getWOFrameworkFile() {
	return new File(getBuildDirectory(), getFinalName() + ".jar");
    }
}

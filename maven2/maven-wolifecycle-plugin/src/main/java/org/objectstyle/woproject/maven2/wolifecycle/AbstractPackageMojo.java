package org.objectstyle.woproject.maven2.wolifecycle;

// org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;

public abstract class AbstractPackageMojo extends AbstractWOMojo {
    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    public AbstractPackageMojo() {
	super();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
	getLog().info("Packaging WebObjects project");
    }

    protected File getArtifactFile() {
	return new File(getBuildDirectory(), getFinalName()
		+ getClassifierAsString() + "." + getProductExtension());
    }

    public MavenProjectHelper getProjectHelper() {
	return projectHelper;
    }

    public void setProjectHelper(final MavenProjectHelper projectHelper) {
	this.projectHelper = projectHelper;
    }
}

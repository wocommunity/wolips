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
 * @phase package
 * @requiresProject
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

    public PackageWOApplicationResourcesMojo() {
	super();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
	super.execute();

	File woapplicationFile = getWOApplicationFile();

	getLog().info(
		"Attaching artifact: " + woapplicationFile.getAbsolutePath());

	getProjectHelper().attachArtifact(project, "woapplication.tar.gz",
		getClassifier(), woapplicationFile);

	File webServerResourcesArtifactFile = getWOWebServerResourcesArtifactFile();

	getLog().info(
		"Attaching artifact: "
			+ webServerResourcesArtifactFile.getAbsolutePath());

	getProjectHelper().attachArtifact(project,
		"wowebserverresources.tar.gz", getClassifier(),
		webServerResourcesArtifactFile);
    }

    @Override
    public String getProductExtension() {
	return "woapplication";
    }

    @Override
    public MavenProject getProject() {
	return project;
    }

    protected File getWOApplicationFile() {
	return new File(getBuildDirectory(), getFinalName()
		+ ".woapplication.tar.gz");
    }

    private File getWOWebServerResourcesArtifactFile() {
	return new File(getBuildDirectory(), getFinalName()
		+ ".wowebserverresources.tar.gz");
    }
}

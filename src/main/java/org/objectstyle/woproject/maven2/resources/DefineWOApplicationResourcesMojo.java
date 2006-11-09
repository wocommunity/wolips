package org.objectstyle.woproject.maven2.resources;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * resources goal for WebObjects projects.
 * 
 * @goal define-woapplication-resources
 * @requiresDependencyResolution compile
 * @author uli
 * @since 2.0
 */
public class DefineWOApplicationResourcesMojo extends DefineResourcesMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The set of dependencies required by the project
	 * 
	 * @parameter default-value="${project.dependencies}"
	 * @required
	 * @readonly
	 */
	private java.util.ArrayList dependencies;

	/**
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;
	
	/**
     * Read patternsets.
     * @parameter expression="readPatternsets"
     */
    private Boolean readPatternsets;

	public DefineWOApplicationResourcesMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		this.defineProperties();
		this.defineClasspath();
	}

	private void defineProperties() throws MojoExecutionException {
		String fileName = this.getProjectFolder() + "target" + File.separator
				+ "wobuild.properties";
		getLog().info("Defining wo properties: writing to file: " + fileName);
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write("maven.localRepository.baseDir = "
					+ localRepository.getBasedir());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write wo properties", e);
		}
	}

	private void defineClasspath() throws MojoExecutionException {
		getLog().info("Defining wo classpath: dependencies from parameter");
		Iterator dependenciesIterator = dependencies.iterator();
		StringBuffer classPath = new StringBuffer();
		while (dependenciesIterator.hasNext()) {
			Dependency dependency = (Dependency) dependenciesIterator.next();
			String depenendencyGroup = dependency.getGroupId();
			if (depenendencyGroup != null) {
				depenendencyGroup = depenendencyGroup.replace('.',
						File.separatorChar);
			}
			String depenendencyArtifact = dependency.getArtifactId();
			String depenendencyVersion = dependency.getVersion();
			String dependencyPath = depenendencyGroup + File.separator
					+ depenendencyArtifact + File.separator
					+ depenendencyVersion + File.separator
					+ depenendencyArtifact + "-" + depenendencyVersion + ".jar";
			getLog().info(
					"Defining wo classpath: dependencyPath: " + dependencyPath);
			classPath.append(dependencyPath + "\n");
		}
		String fileName = this.getProjectFolder() + "target" + File.separator
				+ "classpath.txt";
		getLog().info("Defining wo classpath: writing to file: " + fileName);
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(classPath.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write classpath", e);
		}
	}

	public MavenProject getProject() {
		return project;
	}

	public String getProductExtension() {
		return "woa";
	}

	public boolean hasContentsFolder() {
		return true;
	}

	protected Boolean readPatternsets() {
		return readPatternsets;
	}
}

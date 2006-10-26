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
 * @goal define-resources
 * @author uli
 * @since 2.0
 */
public abstract class DefineResourcesMojo extends AbstractMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	public DefineResourcesMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Defining wo resources");
		this.executePatternsetFiles();
		this.executeFolders();
	}

	private void executePatternsetFiles() throws MojoExecutionException,
			MojoFailureException {
		getLog().info("Defining wo resources: loading patternsets");
		String woProjectFolder = getWOProjectFolder();
		File woProjectFile = new File(woProjectFolder);
		if (woProjectFile.exists()) {
			getLog()
					.info(
							"Defining wo resources: \"woproject\" folder found within project. Reading patternsets...");
		} else {
			getLog()
					.info(
							"Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		String[] resourcesIncludeFromAntPatternsetFiles = this
				.getResourcesInclude();
		String[] resourcesExcludeFromAntPatternsetFiles = this
				.getResourcesExclude();
		if (resourcesIncludeFromAntPatternsetFiles != null
				&& resourcesExcludeFromAntPatternsetFiles != null
				&& (resourcesIncludeFromAntPatternsetFiles.length > 0 || resourcesExcludeFromAntPatternsetFiles.length > 0)) {
			Resource resourcesFromAntPatternsetFiles = this.createResources(
					resourcesIncludeFromAntPatternsetFiles,
					resourcesExcludeFromAntPatternsetFiles, ".", "Resources");
			this.project.addResource(resourcesFromAntPatternsetFiles);
		}
		String[] webserverResourcesIncludeFromAntPatternsetFiles = this
				.getWebserverResourcesInclude();
		String[] webserverResourcesExcludeFromAntPatternsetFiles = this
				.getWebserverResourcesExclude();
		if (webserverResourcesIncludeFromAntPatternsetFiles != null
				&& webserverResourcesExcludeFromAntPatternsetFiles != null
				&& (webserverResourcesIncludeFromAntPatternsetFiles.length > 0 || webserverResourcesExcludeFromAntPatternsetFiles.length > 0)) {
			Resource webserverResourcesFromAntPatternsetFiles = this
					.createResources(
							webserverResourcesIncludeFromAntPatternsetFiles,
							webserverResourcesExcludeFromAntPatternsetFiles,
							".", "WebServerResources");
			this.project.addResource(webserverResourcesFromAntPatternsetFiles);
		}
	}

	private void executeFolders() throws MojoExecutionException,
			MojoFailureException {
		getLog().info("Defining wo resources: defining default folder");
		String resourcesPath = getProjectFolder() + "Resources";
		File resourcesFile = new File(resourcesPath);
		if (resourcesFile.exists()) {
			getLog()
					.info(
							"Defining wo resources: \"Resources\" folder found within project. Adding include...");
			Resource resourcesFromResourcesFolder = this.createResources(null,
					null, "Resources", "Resources");
			this.project.addResource(resourcesFromResourcesFolder);
		} else {
			getLog()
					.info(
							"Defining wo resources: No \"Resources\" folder found within project. Skipping include...");
		}
		String webServerResourcesPath = getProjectFolder()
				+ "WebServerResources/**";
		File webServerResourcesFile = new File(webServerResourcesPath);
		if (webServerResourcesFile.exists()) {
			getLog()
					.info(
							"Defining wo resources: \"WebServerResources\" folder found within project. Adding include...");
			Resource webServerResourcesFromWebServerResourcesFolder = this
					.createResources(null, null, "WebServerResources",
							"WebServerResources");
			this.project
					.addResource(webServerResourcesFromWebServerResourcesFolder);
		} else {
			getLog()
					.info(
							"Defining wo resources: No \"WebServerResources\" folder found within project. Skipping include...");
		}
	}

	private Resource createResources(String[] resourcesInclude,
			String[] resourcesExclude, String directory, String targetPath) {
		Resource resource = new Resource();
		resource.setDirectory(directory);
		if (resourcesInclude != null) {
			for (int i = 0; i < resourcesInclude.length; i++) {
				String string = resourcesInclude[i];
				resource.addInclude(string);
			}
		}
		if (resourcesExclude != null) {
			for (int i = 0; i < resourcesExclude.length; i++) {
				String string = resourcesExclude[i];
				resource.addExclude(string);
			}
			resource.addExclude("build/**");
			resource.addExclude("dist/**");
		}
		resource.setTargetPath("../" + targetPath);
		return resource;
	}

	private String[] readPatternset(String patternsetFileName) {
		getLog()
				.info(
						"Defining wo resources: loading \""
								+ patternsetFileName + "\"");

		String woProjectFolder = this.getWOProjectFolder();
		File file = new File(woProjectFolder + File.separator
				+ patternsetFileName);
		PatternsetReader patternsetReader;
		String[] pattern = null;
		try {
			patternsetReader = new PatternsetReader(file);
			pattern = patternsetReader.getPattern();
		} catch (IOException e) {
			getLog().info(
					"Defining wo resources: exception while loading \""
							+ patternsetFileName + "\"", e);
		}
		return pattern;
	}

	private String[] getWebserverResourcesExclude() {
		String patternsetFileName = "wsresources.exclude.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String[] getWebserverResourcesInclude() {
		String patternsetFileName = "wsresources.include.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String[] getResourcesInclude() {
		String patternsetFileName = "resources.include.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String[] getResourcesExclude() {
		String patternsetFileName = "resources.exclude.patternset";
		return this.readPatternset(patternsetFileName);
	}

	private String getProjectFolder() {
		String projectFolder = this.project.getFile().getPath().substring(0,
				this.project.getFile().getPath().length() - 7);
		return projectFolder;
	}

	private String getWOProjectFolder() {
		File file = new File(this.getProjectFolder() + "woproject");
		if (file.exists()) {
			return file.getPath();
		}
		return null;
	}
}

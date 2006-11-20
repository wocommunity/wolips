package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class DefineResourcesMojo extends WOMojo {

	public DefineResourcesMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Creating folder");
		this.executeCreateFolders();
		getLog().info("Defining wo resources");
		this.executeExistingResources();
		this.executeExistingWebServerResources();
		Boolean readPatternsets = this.readPatternsets();
		if (readPatternsets != null && readPatternsets.booleanValue()) {
			this.executeResourcesPatternsetFiles();
			this.executeWebServerResourcesPatternsetFiles();
		}
		this.executeFolders();
	}

	protected abstract Boolean readPatternsets();

	private void executeExistingWebServerResources() {
		this.executePatchResources("Resources", this.getFullTargetPath("Resources"));
	}

	private void executeExistingResources() {
		this.executePatchResources("WebServerResources", this.getFullTargetPath("WebServerResources"));
	}

	private void executePatchResources(String existingTargetPath, String newTargetPath) {
		List list = this.getProject().getResources();
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			Resource resource = (Resource) iterator.next();
			if (resource.getTargetPath() != null && resource.getTargetPath().equals(existingTargetPath)) {
				getLog().info("Defining wo resources:  Patching target path of resource: " + resource);
				resource.setTargetPath(newTargetPath);
			}
		}

	}

	private void executeCreateFolders() {
		File target = new File(this.getProjectFolder() + File.separator + "target" + File.separator + "classes");
		if (!target.exists()) {
			target.mkdirs();
		}
	}

	private void executeResourcesPatternsetFiles() throws MojoExecutionException, MojoFailureException {
		getLog().info("Defining wo resources: loading patternsets");
		String woProjectFolder = getWOProjectFolder();
		if (woProjectFolder == null) {
			getLog().info("Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		File woProjectFile = new File(woProjectFolder);
		if (woProjectFile.exists()) {
			getLog().info("Defining wo resources: \"woproject\" folder found within project. Reading patternsets...");
		} else {
			getLog().info("Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		String[] resourcesIncludeFromAntPatternsetFiles = this.getResourcesInclude();
		String[] resourcesExcludeFromAntPatternsetFiles = this.getResourcesExclude();
		if (resourcesIncludeFromAntPatternsetFiles != null && resourcesExcludeFromAntPatternsetFiles != null && (resourcesIncludeFromAntPatternsetFiles.length > 0 || resourcesExcludeFromAntPatternsetFiles.length > 0)) {
			Resource resourcesFromAntPatternsetFiles = this.createResources(resourcesIncludeFromAntPatternsetFiles, resourcesExcludeFromAntPatternsetFiles, ".", "Resources");
			this.getProject().addResource(resourcesFromAntPatternsetFiles);
		}
	}

	private void executeWebServerResourcesPatternsetFiles() throws MojoExecutionException, MojoFailureException {
		getLog().info("Defining wo webserverresources: loading patternsets");
		String woProjectFolder = getWOProjectFolder();
		if (woProjectFolder == null) {
			getLog().info("Defining wo resources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		File woProjectFile = new File(woProjectFolder);
		if (woProjectFile.exists()) {
			getLog().info("Defining wo webserverresources: \"woproject\" folder found within project. Reading patternsets...");
		} else {
			getLog().info("Defining wo webserverresources:  No \"woproject\" folder found within project. Skipping patternsets...");
			return;
		}
		String[] webserverResourcesIncludeFromAntPatternsetFiles = this.getWebserverResourcesInclude();
		String[] webserverResourcesExcludeFromAntPatternsetFiles = this.getWebserverResourcesExclude();
		if (webserverResourcesIncludeFromAntPatternsetFiles != null && webserverResourcesExcludeFromAntPatternsetFiles != null && (webserverResourcesIncludeFromAntPatternsetFiles.length > 0 || webserverResourcesExcludeFromAntPatternsetFiles.length > 0)) {
			Resource webserverResourcesFromAntPatternsetFiles = this.createResources(webserverResourcesIncludeFromAntPatternsetFiles, webserverResourcesExcludeFromAntPatternsetFiles, ".", "WebServerResources");
			this.getProject().addResource(webserverResourcesFromAntPatternsetFiles);
		}
	}

	private void executeFolders() {
		getLog().info("Defining wo resources: defining default folder");
		String resourcesPath = getProjectFolder() + "Resources";
		File resourcesFile = new File(resourcesPath);
		if (resourcesFile.exists()) {
			getLog().info("Defining wo resources: \"Resources\" folder found within project. Adding include...");
			Resource resourcesFromResourcesFolder = this.createResources(null, null, "Resources", "Resources");
			this.getProject().addResource(resourcesFromResourcesFolder);
		} else {
			getLog().info("Defining wo resources: No \"Resources\" folder found within project. Skipping include...");
		}
		String webServerResourcesPath = getProjectFolder() + "WebServerResources";
		File webServerResourcesFile = new File(webServerResourcesPath);
		if (webServerResourcesFile.exists()) {
			getLog().info("Defining wo webserverresources: \"WebServerResources\" folder found within project. Adding include...");
			Resource webServerResourcesFromWebServerResourcesFolder = this.createResources(null, null, "WebServerResources", "WebServerResources");
			this.getProject().addResource(webServerResourcesFromWebServerResourcesFolder);
		} else {
			getLog().info("Defining wo webserverresources: No \"WebServerResources\" folder found within project. Skipping include...");
		}
	}

	private Resource createResources(String[] resourcesInclude, String[] resourcesExclude, String directory, String targetPath) {
		Resource resource = new Resource();
		resource.setDirectory(this.getProjectFolder() + directory);
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
			resource.addExclude("target/**");
		}
		String fullTargetPath = this.getFullTargetPath(targetPath);
		resource.setTargetPath(fullTargetPath);
		return resource;
	}

	private String getFullTargetPath(String targetPath) {
		String fullTargetPath = "../" + this.getProject().getArtifactId();
		if(this.includesVersionInArtifactName()) {
			fullTargetPath = fullTargetPath + "-" + this.getProject().getVersion();
		}
		fullTargetPath = fullTargetPath + "." + getProductExtension();
		if (this.hasContentsFolder()) {
			fullTargetPath = fullTargetPath + File.separator + "Contents";
		}
		fullTargetPath = fullTargetPath + File.separator + targetPath;
		return fullTargetPath;
	}

	public abstract boolean hasContentsFolder();
	
	public abstract boolean includesVersionInArtifactName();

	private String[] readPatternset(String patternsetFileName) {
		getLog().info("Defining wo resources: loading \"" + patternsetFileName + "\"");

		String woProjectFolder = this.getWOProjectFolder();
		File file = new File(woProjectFolder + File.separator + patternsetFileName);
		if (!file.exists()) {
			return null;
		}
		PatternsetReader patternsetReader;
		String[] pattern = null;
		try {
			patternsetReader = new PatternsetReader(file);
			pattern = patternsetReader.getPattern();
		} catch (IOException e) {
			getLog().info("Defining wo resources: exception while loading \"" + patternsetFileName + "\"", e);
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
}

package org.objectstyle.woproject.maven2.wolifecycle;

//org.apache.maven.plugins:maven-compiler-plugin:compile
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
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
	 * Changes back slashes '\' on Windows to '/'.
	 * 
	 * @param path
	 *            Any file path
	 * @return Returns a file path without back slashes
	 */
	public static String normalizedPath(String path) {
		return FilenameUtils.separatorsToUnix(path);
	}

	private String[][] dependencyPaths;

	/**
	 * include JavaClientClasses in WebServerResources.
	 * 
	 * @parameter expression="includeJavaClientClassesInWebServerResources"
	 */
	private Boolean includeJavaClientClassesInWebServerResources;

	/**
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;

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

	/**
	 * skip webobjects frameworks from apple.
	 * 
	 * @parameter expression="skipAppleProvidedFrameworks"
	 */
	private Boolean skipAppleProvidedFrameworks;

	public DefineWOApplicationResourcesMojo() {
		super();
	}

	protected String artifactDescription(Artifact artifact) {
		return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
	}

	private void copyJarEntryToFile(String jarFileName, File destinationFolder, JarEntry jarEntry) throws IOException, FileNotFoundException {
		// getLog().info("Copy webserverresources: jarFileName " + jarFileName +
		// " destinationFolder " + destinationFolder);
		String name = jarEntry.getName();
		if (this.includeJavaClientClassesInWebServerResources == null || this.includeJavaClientClassesInWebServerResources.booleanValue() == false) {

			String prefix = "WebServerResources/Java";
			if (name.startsWith(prefix)) {
				return;
			}
		}
		String destinationFolderWithPathFromJarEntry = destinationFolder + File.separator + name.substring(0, name.lastIndexOf('/'));
		String destinationName = name.substring(name.lastIndexOf('/') + 1);
		// getLog().info("Copy webserverresources:
		// destinationFolderWithPathFromJarEntry " +
		// destinationFolderWithPathFromJarEntry + " destinationName " +
		// destinationName);
		File file = new File(destinationFolderWithPathFromJarEntry, destinationName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		// getLog().info("Copy webserverresources: file " + file);
		file.createNewFile();

		InputStream is = null;
		OutputStream os = null;
		try {
			is = new JarFile(jarFileName).getInputStream(jarEntry);
			os = new FileOutputStream(file);

			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0) {
				os.write(buf, 0, len);
			}

		} finally {
			if (is != null) {
				is.close();
			}

			if (os != null) {
				os.close();
			}
		}
	}

	private void defineClasspath() throws MojoExecutionException {
		getLog().debug("Defining wo classpath: dependencies from parameter");

		Collection<String> classpathLines = populateClasspath(false);

		classpathLines.addAll(populateClasspath(true));

		Artifact artifact = project.getArtifact();

		classpathLines.add(artifact.getArtifactId() + "-" + artifact.getVersion() + (artifact.getClassifier() != null ? artifact.getClassifier() : "") + ".jar");

		File classpathTxtFile = new File(project.getBuild().getDirectory(), "classpath.txt");

		getLog().info("Defining wo classpath: writing to file: " + classpathTxtFile.getAbsolutePath());

		try {
			FileUtils.writeLines(classpathTxtFile, classpathLines);
		} catch (IOException exception) {
			throw new MojoExecutionException("Could not write classpath.txt", exception);
		}

		File classpathPropertiesFile = new File(project.getBuild().getDirectory(), "classpath.properties");

		getLog().info("Defining wo classpath: writing to file: " + classpathPropertiesFile);

		try {
			FileUtils.writeStringToFile(classpathPropertiesFile, "dependencies.lib = " + normalizedPath(project.getBuild().getDirectory()) + "/lib");
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write classpath.properties", e);
		}
	}

	void defineProperties() throws MojoExecutionException {
		String fileName = getProjectFolder() + "target" + File.separator + "wobuild.properties";

		getLog().debug("Defining wo properties: writing to file: " + fileName);

		File file = new File(fileName);

		FileWriter fileWriter;

		try {
			fileWriter = new FileWriter(file);

			fileWriter.write("classpath.localRepository.baseDir = " + normalizedPath(localRepository.getBasedir()));

			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write wo properties", e);
		}
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		try {
			this.defineProperties();
			this.defineClasspath();
			this.executeCopyWebServerResources();
		} finally {
			dependencyPaths = null;
		}
	}

	private void executeCopyWebServerResources() throws MojoExecutionException {
		getLog().info("Copy webserverresources");

		Set<Artifact> artifacts = project.getArtifacts();

		for (Artifact artifact : artifacts) {

			if (skipAppleProvidedFrameworks != null && skipAppleProvidedFrameworks.booleanValue() && isWebObjectAppleGroup(artifact.getGroupId())) {
				getLog().info("Skipping artifact: " + artifactDescription(artifact) + " (Apple provided)");

				continue;
			}

			FileInputStream fileInputStream;
			try {
				File jarFile = artifact.getFile();

				if (!isArtifactDeployed(jarFile)) {
					getLog().info("Skipping artifact: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion() + " (not installed in the local repository)");

					continue;
				}

				fileInputStream = new FileInputStream(jarFile);
				JarInputStream jarInputStream = new JarInputStream(fileInputStream);
				int counter = 0;
				JarEntry jarEntry = null;
				while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
					if (!jarEntry.isDirectory()) {
						String jarEntryName = jarEntry.getName();
						String prefix = "WebServerResources";
						String frameworksFolderName = this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woa" + File.separator + "Contents" + File.separator + "Frameworks" + File.separator;
						if (jarEntryName != null && jarEntryName.length() > prefix.length() && jarEntryName.startsWith(prefix)) {
							File destinationFolder = new File(frameworksFolderName + jarFile.getName() + ".framework");
							this.copyJarEntryToFile(jarFile.getAbsolutePath(), destinationFolder, jarEntry);
							counter++;
						}
					}
				}
				getLog().debug("Copy webserverresources: extracted " + counter + " webserverresources from  jar named " + jarFile.getName());
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException("Could not open file input stream", e);
			} catch (IOException e) {
				throw new MojoExecutionException("Could not open jar input stream", e);
			}
		}
	}

	@Override
	public String getProductExtension() {
		return "woa";
	}

	@Override
	public MavenProject getProject() {
		return project;
	}

	@Override
	public boolean hasContentsFolder() {
		return true;
	}

	@Override
	public boolean includesVersionInArtifactName() {
		return true;
	}

	private boolean isArtifactDeployed(File file) {
		return file.isFile();
	}

	private Collection<String> populateClasspath(boolean populateWebObjectsLibraries) throws MojoExecutionException {
		if (populateWebObjectsLibraries && BooleanUtils.isTrue(skipAppleProvidedFrameworks)) {
			getLog().info("Defining wo classpath: Skipping WebObjects libraries");

			return Collections.emptyList();
		}

		Set<Artifact> artifacts = project.getArtifacts();

		Collection<String> classpathLines = new ArrayList<String>();

		ScopeArtifactFilter providedFilter = new ScopeArtifactFilter(Artifact.SCOPE_PROVIDED);

		for (Artifact artifact : artifacts) {
			if (populateWebObjectsLibraries) {
				if (!isWebObjectAppleGroup(artifact.getGroupId())) {
					continue;
				}
			} else if (isWebObjectAppleGroup(artifact.getGroupId())) {
				continue;
			} else if (providedFilter.include(artifact)) {
				continue;
			}

			getLog().info("Defining wo classpath: dependencyPath: " + artifact.getArtifactId());

			String groupPath = StringUtils.replace(artifact.getGroupId(), ".", "/");
			String artifactPath = artifact.getArtifactId() + "/" + artifact.getVersion();
			String filename = artifact.getArtifactId() + "-" + artifact.getVersion() + (artifact.getClassifier() != null ? artifact.getClassifier() : "") + ".jar";

			String classpathEntry = groupPath + "/" + artifactPath + "/" + filename;

			classpathLines.add(classpathEntry);

			File sourceFile = artifact.getFile();

			getLog().info("Source file: " + sourceFile.getAbsolutePath());

			File destinationFile = new File(project.getBuild().getDirectory(), "lib/" + classpathEntry);

			getLog().info("Destination file: " + destinationFile.getAbsolutePath());

			try {
				FileUtils.copyFile(sourceFile, destinationFile);
			} catch (IOException exception) {
				throw new MojoExecutionException("Cannot copy the dependency " + artifactDescription(artifact), exception);
			}
		}

		return classpathLines;
	}

	@Override
	protected Boolean readPatternsets() {
		return readPatternsets;
	}
}

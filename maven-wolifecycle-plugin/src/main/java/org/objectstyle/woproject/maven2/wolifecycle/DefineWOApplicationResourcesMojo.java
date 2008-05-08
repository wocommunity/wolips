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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
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

	/**
	 * @component
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	private ArtifactResolver artifactResolver;

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
	 * @component
	 */
	private ArtifactMetadataSource metadataSource;

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
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 */
	private List remoteRepositories;

	/**
	 * skip webobjects frameworks from apple.
	 * 
	 * @parameter expression="skipAppleProvidedFrameworks"
	 */
	private Boolean skipAppleProvidedFrameworks;

	public DefineWOApplicationResourcesMojo() {
		super();
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
		String[][] classpathEntries = this.getDependencyPaths();
		StringBuffer classPath = new StringBuffer();
		for (int k = 0; k < 2; k++) {
			for (int i = 0; i < classpathEntries[0].length; i++) {
				getLog().debug("Defining wo classpath: dependencyPath: " + classpathEntries[0][i]);
				if (k == 0 && this.isWebobjectAppleGroup(classpathEntries[2][i])) {
					continue;
				}
				if (k == 1 && !this.isWebobjectAppleGroup(classpathEntries[2][i])) {
					continue;
				}
				if (skipAppleProvidedFrameworks != null && skipAppleProvidedFrameworks.booleanValue() && this.isWebobjectAppleGroup(classpathEntries[2][i])) {
					getLog().debug("Defining wo classpath: dependencyPath: " + classpathEntries[0][i] + "is in the apple group skipping");
					continue;
				}
				classPath.append(classpathEntries[0][i] + "\n");
			}
		}
		String classpathTxtFileName = this.getProjectFolder() + "target" + File.separator + "classpath.txt";
		getLog().debug("Defining wo classpath: writing to file: " + classpathTxtFileName);
		File classpathTxtFile = new File(classpathTxtFileName);
		FileWriter classpathTxtFileWriter;
		try {
			classpathTxtFileWriter = new FileWriter(classpathTxtFile);
			classpathTxtFileWriter.write(classPath.toString());
			classpathTxtFileWriter.flush();
			classpathTxtFileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write classpath.txt", e);
		}
		String classpathPropertiesFileName = this.getProjectFolder() + "target" + File.separator + "classpath.properties";
		getLog().debug("Defining wo classpath: writing to file: " + classpathPropertiesFileName);
		File classpathPropertiesFile = new File(classpathPropertiesFileName);
		FileWriter classpathPropertiesFileWriter;
		try {
			classpathPropertiesFileWriter = new FileWriter(classpathPropertiesFile);
			classpathPropertiesFileWriter.write("classpath.localRepository.baseDir " + localRepository.getBasedir());
			classpathPropertiesFileWriter.flush();
			classpathPropertiesFileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write classpath.properties", e);
		}
	}

	private void defineProperties() throws MojoExecutionException {
		String fileName = this.getProjectFolder() + "target" + File.separator + "wobuild.properties";
		getLog().debug("Defining wo properties: writing to file: " + fileName);
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write("maven.localRepository.baseDir = " + normalizedPath(localRepository.getBasedir()));
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

			if (skipAppleProvidedFrameworks != null && skipAppleProvidedFrameworks.booleanValue() && isWebobjectAppleGroup(artifact.getGroupId())) {
				getLog().debug("Skipping artifact: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());

				continue;
			}

			FileInputStream fileInputStream;
			try {

				File jarFile = artifact.getFile();

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

	private String[][] getDependencyPaths() throws MojoExecutionException {
		if (dependencyPaths == null) {
			// List dependencies = project.getDependencies();
			// Set dependencyArtifacts = null;
			// ArtifactResolutionResult result = null;
			// try {
			// dependencyArtifacts =
			// MavenMetadataSource.createArtifacts(artifactFactory,
			// dependencies, null, null, null);
			// result =
			// artifactResolver.resolveTransitively(dependencyArtifacts,
			// this.project.getArtifact(), Collections.EMPTY_MAP,
			// localRepository, remoteRepositories, metadataSource, null,
			// Collections.EMPTY_LIST);
			//
			// } catch (InvalidDependencyVersionException e) {
			// throw new MojoExecutionException("error while resolving
			// dependencies", e);
			// } catch (ArtifactResolutionException e) {
			// throw new MojoExecutionException("error while resolving
			// dependencies", e);
			// } catch (ArtifactNotFoundException e) {
			// throw new MojoExecutionException("error while resolving
			// dependencies", e);
			// }
			Set artifacts = project.getArtifacts();
			ArrayList classPathEntriesArray = new ArrayList();
			ArrayList artfactNameEntriesArray = new ArrayList();
			ArrayList artfactGroupEntriesArray = new ArrayList();
			Iterator dependenciesIterator = artifacts.iterator();
			while (dependenciesIterator.hasNext()) {
				Artifact artifact = (Artifact) dependenciesIterator.next();

				if (Artifact.SCOPE_PROVIDED.equals(artifact.getScope())) {
					continue;
				}

				String depenendencyGroup = artifact.getGroupId();
				if (depenendencyGroup != null) {
					depenendencyGroup = depenendencyGroup.replace('.', File.separatorChar);
				}
				String depenendencyArtifact = artifact.getArtifactId();
				String depenendencyVersion = artifact.getVersion();
				String depenendencyBaseVersion = artifact.getBaseVersion();
				if (artifact.isSnapshot()) {
					depenendencyBaseVersion = depenendencyBaseVersion.substring(0, depenendencyBaseVersion.indexOf('-') + 1) + "SNAPSHOT";
				}
				String dependencyPath = depenendencyGroup + File.separator + depenendencyArtifact + File.separator + depenendencyBaseVersion + File.separator + depenendencyArtifact + "-" + depenendencyVersion + ".jar";
				classPathEntriesArray.add(dependencyPath);
				artfactNameEntriesArray.add(depenendencyArtifact);
				artfactGroupEntriesArray.add(depenendencyGroup);
			}
			dependencyPaths = new String[3][];
			dependencyPaths[0] = (String[]) classPathEntriesArray.toArray(new String[classPathEntriesArray.size()]);
			dependencyPaths[1] = (String[]) artfactNameEntriesArray.toArray(new String[artfactNameEntriesArray.size()]);
			dependencyPaths[2] = (String[]) artfactGroupEntriesArray.toArray(new String[artfactGroupEntriesArray.size()]);
		}
		return dependencyPaths;
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

	@Override
	protected Boolean readPatternsets() {
		return readPatternsets;
	}
}

package org.objectstyle.woproject.maven2.wobootstrap;

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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.objectstyle.woproject.maven2.wobootstrap.mojos.AbstractWOMojo;

public class WebServerResourcesFromJarExtractorMojo extends AbstractWOMojo {

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
	 * 
	 * @parameter expression="readPatternsets"
	 */
	private Boolean readPatternsets;

	private String[][] dependencyPaths;

	public WebServerResourcesFromJarExtractorMojo() throws MojoExecutionException {
		super();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			this.executeCopyWebServerResources();
		} finally {
			dependencyPaths = null;
		}
	}

	private void executeCopyWebServerResources() throws MojoExecutionException {
		getLog().info("Copy webserverresources");
		String[][] classpathEntries = this.getDependencyPaths();
		for (int i = 0; i < classpathEntries[1].length; i++) {
			FileInputStream fileInputStream;
			try {
				String jarFileName = localRepository.getBasedir() + File.separator + classpathEntries[0][i];
				getLog().info("Copy webserverresources: looking into jar named " + jarFileName);
				fileInputStream = new FileInputStream(jarFileName);
				JarInputStream jarInputStream = new JarInputStream(fileInputStream);
				int counter = 0;
				JarEntry jarEntry = null;
				while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
					if (!jarEntry.isDirectory()) {
						String jarEntryName = jarEntry.getName();
						String prefix = "WebServerResources";
						String frameworksFolderName = this.getProjectFolder() + "target" + File.separator + this.getProject().getArtifactId() + "-" + this.getProject().getVersion() + ".woa" + File.separator + "Contents" + File.separator + "Frameworks" + File.separator;
						if (jarEntryName != null && jarEntryName.length() > prefix.length() && jarEntryName.startsWith(prefix)) {
							File destinationFolder = new File(frameworksFolderName + classpathEntries[1][i] + ".framework");
							this.copyJarEntryToFile(jarFileName, destinationFolder, jarEntry);
							counter++;
						}
					}
				}
				getLog().info("Copy webserverresources: extracted " + counter + " webserverresources from  jar named " + jarFileName);
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException("Could not open file input stream", e);
			} catch (IOException e) {
				throw new MojoExecutionException("Could not open jar input stream", e);
			}
		}
	}

	private void copyJarEntryToFile(String jarFileName, File destinationFolder, JarEntry jarEntry) throws IOException, FileNotFoundException {
		//getLog().info("Copy webserverresources: jarFileName " + jarFileName + " destinationFolder " + destinationFolder);
		String name = jarEntry.getName();
		String destinationFolderWithPathFromJarEntry = destinationFolder + File.separator + name.substring(0, name.lastIndexOf('/'));
		String destinationName = name.substring(name.lastIndexOf('/') + 1);
		//getLog().info("Copy webserverresources: destinationFolderWithPathFromJarEntry " + destinationFolderWithPathFromJarEntry + " destinationName " + destinationName);
		File file = new File(destinationFolderWithPathFromJarEntry, destinationName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		//getLog().info("Copy webserverresources: file " + file);
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

	private void defineProperties() throws MojoExecutionException {
		String fileName = this.getProjectFolder() + "target" + File.separator + "wobuild.properties";
		getLog().info("Defining wo properties: writing to file: " + fileName);
		File file = new File(fileName);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write("maven.localRepository.baseDir = " + localRepository.getBasedir());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write wo properties", e);
		}
	}

	private void defineClasspath() throws MojoExecutionException {
		getLog().info("Defining wo classpath: dependencies from parameter");
		String[] classpathEntries = this.getDependencyPaths()[0];
		StringBuffer classPath = new StringBuffer();
		for (int i = 0; i < classpathEntries.length; i++) {
			getLog().info("Defining wo classpath: dependencyPath: " + classpathEntries[i]);
			classPath.append(classpathEntries[i] + "\n");
		}
		String fileName = this.getProjectFolder() + "target" + File.separator + "classpath.txt";
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

	private String[][] getDependencyPaths() {
		if (dependencyPaths == null) {
			ArrayList classPathEntriesArray = new ArrayList();
			ArrayList artfactNameEntriesArray = new ArrayList();
			Iterator dependenciesIterator = dependencies.iterator();
			while (dependenciesIterator.hasNext()) {
				Dependency dependency = (Dependency) dependenciesIterator.next();
				String depenendencyGroup = dependency.getGroupId();
				if (depenendencyGroup != null) {
					depenendencyGroup = depenendencyGroup.replace('.', File.separatorChar);
				}
				String depenendencyArtifact = dependency.getArtifactId();
				String depenendencyVersion = dependency.getVersion();
				String dependencyPath = depenendencyGroup + File.separator + depenendencyArtifact + File.separator + depenendencyVersion + File.separator + depenendencyArtifact + "-" + depenendencyVersion + ".jar";
				classPathEntriesArray.add(dependencyPath);
				artfactNameEntriesArray.add(depenendencyArtifact);
			}
			dependencyPaths = new String[2][];
			dependencyPaths[0] = (String[]) classPathEntriesArray.toArray(new String[classPathEntriesArray.size()]);
			dependencyPaths[1] = (String[]) artfactNameEntriesArray.toArray(new String[artfactNameEntriesArray.size()]);
		}
		return dependencyPaths;
	}
}

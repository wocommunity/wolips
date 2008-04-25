/*
 * ==================================================================== The
 * ObjectStyle Group Software License, Version 1.0 Copyright (c) 2006 The
 * ObjectStyle Group, and individual authors of the software. All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may appear
 * in the software itself, if and wherever such third-party acknowlegements
 * normally appear. 4. The names "ObjectStyle Group" and "Cayenne" must not be
 * used to endorse or promote products derived from this software without prior
 * written permission. For written permission, please contact
 * andrus@objectstyle.org. 5. Products derived from this software may not be
 * called "ObjectStyle" nor may "ObjectStyle" appear in their names without
 * prior written permission of the ObjectStyle Group. THIS SOFTWARE IS PROVIDED
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR ITS
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ==================================================================== This
 * software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 */
package org.objectstyle.woproject.maven2.wobootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.objectstyle.woproject.maven2.wobootstrap.locator.CustomWebObjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.locator.MacOsWebObjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.locator.UnixWebObjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.locator.WebObjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.locator.WindowsWebObjectsLocator;
import org.objectstyle.woproject.maven2.wobootstrap.utils.PomGenerator;
import org.objectstyle.woproject.maven2.wobootstrap.utils.WebObjectsUtils;

/**
 * <code>AbstractBootstrapMojo</code> is an abstract class that prepares the
 * WebObjects artifacts to be imported into a Maven repository. Any class that
 * extends <code>AbstractBootstrapMojo</code> must implement the method
 * {@link AbstractBootstrapMojo#executeGoal(File, Artifact)} and provide a way
 * to deploy or install these WebObjects artifacts.
 * 
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @since 2.0
 */
public abstract class AbstractBootstrapMojo extends AbstractMojo {
	/**
	 * A factory for Maven artifacts
	 * 
	 * @component
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * Properties of the current artifact being used during the Mojo execution
	 */
	Properties artifactProperties;

	/**
	 * The Maven local repository.
	 * 
	 * @parameter expression="${localRepository}"
	 */
	protected ArtifactRepository localRepository;

	/**
	 * Locator for WebObjects resources
	 */
	WebObjectsLocator locator;

	/**
	 * Mapping between default WebObjects jar names and woproject naming
	 * convention
	 */
	private Map<String, String> namesMap;

	/**
	 * Properties defined in bootstrap.properties file
	 */
	private final Properties pluginProperties = new Properties();

	/**
	 * The path to the WebObjects lib folder. Retrieved automatically in
	 * accordance with the type of operational system.
	 * 
	 * @parameter expression="${webObjectsLibFolder}"
	 */
	protected String webObjectsLibFolder;

	/**
	 * The WebObjects version. Retrieved automatically from the version.plist
	 * file if not provided.
	 * 
	 * @parameter expression="${webObjectsVersion}"
	 */
	protected String webObjectsVersion;

	/**
	 * Verify the OS platform and load the properties of this plug-in.
	 * 
	 * @throws MojoExecutionException
	 *             if any problem occurs during bootstrap creation
	 */
	public AbstractBootstrapMojo() throws MojoExecutionException {
		super();
	}

	/**
	 * This constructor is only for testing purpose.
	 * 
	 * @param locator
	 *            A locator for WebObjects resources
	 * @throws MojoExecutionException
	 *             if any problem occurs during bootstrap creation
	 */
	AbstractBootstrapMojo(WebObjectsLocator locator) throws MojoExecutionException {
		this.locator = locator;

		initialize();
	}

	private Artifact createArtifact(Properties properties) {
		Artifact artifact = artifactFactory.createArtifactWithClassifier(properties.getProperty("groupId"), properties.getProperty("artifactId"), properties.getProperty("version"), properties.getProperty("packaging"), null);

		File pomFile = new File(properties.getProperty("pomFile"));

		ArtifactMetadata metadata = new ProjectArtifactMetadata(artifact, pomFile);

		artifact.addMetadata(metadata);

		return artifact;
	}

	/**
	 * Prepares the WebObjects artifacts to be imported into a Maven repository.
	 * Call the abstract method {@link #executeGoal(File, Artifact)} for each
	 * WebObjects artifact found.
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		initializeLocator();
		initialize();

		File[] jars = WebObjectsUtils.getWebObjectsJars(locator);

		if (jars == null) {
			throw new MojoExecutionException("WebObjects lib folder is missing. Maybe WebObjects isn't installed.");
		}

		for (int i = 0; i < jars.length; i++) {
			Properties properties = fillProperties(jars[i]);

			if (properties == null) {
				getLog().warn("Cannot import the following jar: " + jars[i].getName());

				continue;
			}

			Artifact artifact = createArtifact(properties);

			File file = new File(properties.getProperty("file"));

			executeGoal(file, artifact);
		}
	}

	/**
	 * Subclass must implement this method and provide a way to install or
	 * deploy the given <code>Artifact</code>.
	 * 
	 * @param file
	 *            The jar file of the WebObjects library
	 * @param artifact
	 *            The Maven Artifact representing the WebObjects library
	 * @throws MojoExecutionException
	 *             If an exception occur during the goal execution
	 */
	protected abstract void executeGoal(File file, Artifact artifact) throws MojoExecutionException;

	/**
	 * Fill the contents of a properties based on a JAR file. Probably could be
	 * done directly without a <code>Properties</code>.
	 * 
	 * @param jar
	 *            The JAR file
	 * @return Returns the populated properties or <code>null</code> if cannot
	 *         map the JAR file
	 */
	protected Properties fillProperties(File jar) {
		String artifactId = getArtifactIdForJar(jar);

		if (artifactId == null) {
			return null;
		}

		artifactProperties.setProperty("file", jar.getAbsolutePath());
		artifactProperties.setProperty("artifactId", artifactId);

		try {
			File tempPom = File.createTempFile("pom-", ".xml");

			tempPom.deleteOnExit();

			PomGenerator generator = new PomGenerator(artifactProperties);

			generator.writeModel(tempPom);

			artifactProperties.setProperty("pomFile", tempPom.getAbsolutePath());
		} catch (IOException exception) {
			getLog().info("Cannot create a pom file for " + artifactId);
		}

		return artifactProperties;
	}

	/**
	 * Resolve the artifactId for a specific JAR file.
	 * 
	 * @param jar
	 *            The JAR file
	 * @return Returns the artifatId or <code>null</code> if cannot find a key
	 *         that match the JAR file
	 */
	protected String getArtifactIdForJar(File jar) {
		if (jar == null) {
			return null;
		}

		loadNamesMap();

		String jarName = FilenameUtils.getBaseName(jar.getAbsolutePath());

		return namesMap.get(jarName.toLowerCase());
	}

	private String getWebObjectsVersion() {
		if (webObjectsVersion != null) {
			return webObjectsVersion;
		}

		return WebObjectsUtils.getWebObjectsVersion(locator);
	}

	private boolean illegalSetOfParameters() {
		return webObjectsLibFolder != null || webObjectsVersion != null;
	}

	void initialize() throws MojoExecutionException {
		InputStream propertiesInputStream = AbstractBootstrapMojo.class.getResourceAsStream("/bootstrap.properties");

		try {
			pluginProperties.load(propertiesInputStream);
		} catch (IOException exception) {
			throw new MojoExecutionException("Cannot load plug-in properties.");
		}

		artifactProperties = new Properties();

		artifactProperties.setProperty("groupId", pluginProperties.getProperty("woproject.convention.group"));

		String version = getWebObjectsVersion();

		if (version == null) {
			throw new MojoExecutionException("WebObjects version is missing. Maybe WebObjects isn't installed.");
		}

		artifactProperties.setProperty("version", version);
		artifactProperties.setProperty("packaging", "jar");
	}

	void initializeLocator() throws MojoExecutionException {
		if (locator != null) {
			return;
		}

		if (webObjectsLibFolder != null && webObjectsVersion != null) {
			locator = new CustomWebObjectsLocator(webObjectsLibFolder);
		} else if (illegalSetOfParameters()) {
			throw new MojoExecutionException("You must provide both webObjectsLibFolder and webObjectsVersion to use a custom locator for WebObjects libraries");
		} else if (SystemUtils.IS_OS_MAC_OSX) {
			locator = new MacOsWebObjectsLocator();
		} else if (SystemUtils.IS_OS_WINDOWS) {
			locator = new WindowsWebObjectsLocator();
		} else if (SystemUtils.IS_OS_UNIX) {
			locator = new UnixWebObjectsLocator();
		} else {
			throw new MojoExecutionException("Unsupported OS platform.");
		}
	}

	private void loadNamesMap() {
		if (namesMap != null) {
			return;
		}

		String defaultNames = pluginProperties.getProperty("webobjects.default.names");

		String originalNames[] = StringUtils.split(defaultNames, ",");

		String conventionedNames = pluginProperties.getProperty("woproject.convention.names");

		String artifactIds[] = StringUtils.split(conventionedNames, ",");

		namesMap = new HashMap<String, String>();

		for (int i = 0; i < originalNames.length; i++) {
			namesMap.put(originalNames[i].toLowerCase(), artifactIds[i]);
		}
	}
}

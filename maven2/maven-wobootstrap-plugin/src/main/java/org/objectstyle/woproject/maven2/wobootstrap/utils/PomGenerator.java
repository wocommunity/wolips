/**
 * Criado em 22/11/2006
 */
package org.objectstyle.woproject.maven2.wobootstrap.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.IOUtil;

/**
 * This class generates maven project description (POM) for an artifact. The
 * generated model includes the transitive dependencies configured into
 * bootstrap.properties file.
 * 
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @since 2.0
 */
public class PomGenerator {

	/**
	 * Properties that contains the transitive dependencies.
	 */
	protected static Properties dependencyProperties;

	/**
	 * The version of the pom model.
	 */
	private static final String MODEL_VERSION = "4.0.0";

	/**
	 * Lazy initialization for <code>dependencieProperties</code>.
	 */
	private static void loadDependecyProperties() {

		if (dependencyProperties != null) {
			return;
		}

		dependencyProperties = new Properties();

		try {
			dependencyProperties.load(PomGenerator.class.getResourceAsStream("/bootstrap.properties"));
		} catch (IOException exception) {
			// TODO: hprange, write an info to log instead
			exception.printStackTrace();
		}
	}

	/**
	 * The artifact id.
	 */
	protected final String artifactId;

	/**
	 * The group id.
	 */
	protected final String groupId;

	/**
	 * The version of the artifact.
	 */
	protected final String version;

	/**
	 * Creates a new model generator based on some artifact properties.
	 * 
	 * @param properties
	 *            The artifact properties
	 */
	public PomGenerator(Properties properties) {

		if (properties == null) {
			throw new IllegalArgumentException("The properties must not be null");
		}

		groupId = properties.getProperty("groupId");
		artifactId = properties.getProperty("artifactId");
		version = properties.getProperty("version");
	}

	/**
	 * Generates the model (POM) for the defined artifact including its
	 * transitive dependencies.
	 * 
	 * @return Returns a <code>Model</code> object
	 */
	public Model generateModel() {

		Model model = new Model();

		model.setGroupId(groupId);
		model.setArtifactId(artifactId);
		model.setVersion(version);
		model.setModelVersion(MODEL_VERSION);

		loadDependecyProperties();

		String dependenciesList = dependencyProperties.getProperty("transitive.dependencies." + model.getArtifactId());

		if (dependenciesList == null) {
			return model;
		}

		String[] dependencies = StringUtils.split(dependenciesList, ",");

		for (int i = 0; i < dependencies.length; i++) {
			String dependencyArtifactId = dependencies[i];

			Dependency dependency = new Dependency();

			dependency.setGroupId(model.getGroupId());
			dependency.setArtifactId(StringUtils.trim(dependencyArtifactId));
			dependency.setVersion(model.getVersion());

			model.addDependency(dependency);
		}

		return model;
	}

	/**
	 * Writes the model (POM) into the specified file.
	 * 
	 * @param file
	 *            The file
	 */
	public void writeModel(File file) {

		if (file == null) {
			throw new NullPointerException("Cannot write to a null file.");
		}

		FileWriter writer = null;

		try {
			writer = new FileWriter(file);

			new MavenXpp3Writer().write(writer, generateModel());
		} catch (IOException exception) {
			// TODO: hprange, write an info to log instead
			exception.printStackTrace();
		} finally {
			IOUtil.close(writer);
		}
	}
}

/**
 * Criado em 22/11/2006
 */
package org.objectstyle.woproject.maven2.wobootstrap.utils;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.custommonkey.xmlunit.XMLTestCase;

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class TestPomGenerator extends XMLTestCase {
	protected PomGenerator generator;

	protected static final String ARTIFACT_ID = "java-eo-access";

	protected static final String GROUP_ID = "webobjects.apple";

	protected static final String VERSION = "5.3";

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		Properties properties = new Properties();

		properties.setProperty("artifactId", ARTIFACT_ID);
		properties.setProperty("groupId", GROUP_ID);
		properties.setProperty("version", VERSION);

		generator = new PomGenerator(properties);
	}

	public void testModelGeneration() throws Exception {
		Model model = generator.generateModel();

		assertEquals(ARTIFACT_ID, model.getArtifactId());
		assertEquals(GROUP_ID, model.getGroupId());
		assertEquals(VERSION, model.getVersion());
		assertEquals("4.0.0", model.getModelVersion());
	}

	public void testModelWithDependencies() throws Exception {
		Model model = generator.generateModel();

		List dependencies = model.getDependencies();

		assertEquals(2, dependencies.size());

		String[] expectedDependencies = { "java-foundation", "java-eo-control" };

		for (int i = 0; i < expectedDependencies.length; i++) {
			Dependency dependency = (Dependency) dependencies.get(i);

			assertEquals(GROUP_ID, dependency.getGroupId());
			assertEquals(expectedDependencies[i], dependency.getArtifactId());
			assertEquals(VERSION, dependency.getVersion());
		}
	}

	public void testNullProperties() throws Exception {

		try {
			generator = new PomGenerator(null);

			fail("Must throw an IllegalArgumentException");
		} catch (IllegalArgumentException exception) {
			assertEquals("The properties must not be null", exception.getMessage());
		}
	}

	public void testWriteToFile() throws Exception {

		File expectedFile = FileUtils.toFile(getClass().getResource("/example.pom"));

		File createdFile = File.createTempFile("pom-", "xml");

		createdFile.deleteOnExit();

		generator.writeModel(createdFile);

		assertXMLEqual(FileUtils.readFileToString(expectedFile, null), FileUtils.readFileToString(createdFile, null));
	}

	public void testWriteToNullFile() throws Exception {
		try {
			generator.writeModel(null);

			fail("Writing to a null file. Must throw an exception.");
		} catch (NullPointerException exception) {
			assertEquals("Cannot write to a null file.", exception.getMessage());
		}
	}
}

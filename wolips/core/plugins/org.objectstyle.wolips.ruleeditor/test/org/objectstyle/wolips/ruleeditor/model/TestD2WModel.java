package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Test;

public class TestD2WModel {
	protected D2WModel model;

	protected File tempFile;

	@Test
	public void copyNullRuleDoNothing() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		model.copyRule(null);

		assertThat(model.getRules().size(), is(0));
	}

	@Test
	public void copyRule() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		Rule ruleToCopy = new Rule();

		model.copyRule(ruleToCopy);

		assertThat(model.getRules().size(), is(1));

		for (Rule rule : model.getRules()) {
			assertThat(rule == ruleToCopy, is(false));
		}
	}

	@Test
	public void createD2WModelWithExistingFile() throws Exception {
		loadModelWithFile("empty-file.d2wmodel");

		assertThat(model.getModelPath(), containsString("/resources/empty-file.d2wmodel"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createD2WModelWithNullFile() throws Exception {
		new D2WModel(null);
	}

	private void createTempFile() throws Exception {
		File tempFolder = SystemUtils.getJavaIoTmpDir();

		tempFile = new File(tempFolder, "temp.d2wmodel");

		tempFile.createNewFile();
	}

	@Test
	public void firePropertyChangeEventAfterSaveChanges() throws Exception {
		loadModelWithFile("empty-file.d2wmodel");

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		model.addPropertyChangeListener(listener);

		// Assume it is a modified model
		model.hasUnsavedChanges = true;
		model.saveChanges();

		assertThat(listener.firedEventsCount(), is(1));
	}

	@Test
	public void firePropertyChangeEventOnChange() throws Exception {
		loadModelWithFile("empty-file.d2wmodel");

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		model.addPropertyChangeListener(listener);

		assertThat(listener.firedEventsCount(), is(0));

		MockRule rule = new MockRule();

		model.addRule(rule);

		assertThat(listener.firedEventsCount(), is(1));

		// Assume it is a not changed model
		model.hasUnsavedChanges = false;

		model.removeRule(rule);

		assertThat(listener.firedEventsCount(), is(2));
	}

	@Test
	public void firePropertyChangeEventOnExistingRulesChange() throws Exception {
		loadModelWithFile("one-rule.d2wmodel");

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		model.addPropertyChangeListener(listener);

		assertThat(listener.firedEventsCount(), is(0));

		Rule rule = (Rule) model.getRules().toArray()[0];

		rule.setAuthor("200");

		assertThat(listener.firedEventsCount(), is(1));
	}

	private File getFile(String filename) throws Exception {
		URL fileURL = getClass().getResource("/resources/" + filename);

		return new File(fileURL.toURI());
	}

	@Test
	public void hasUnsavedChangesAfterRuleChange() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		Rule rule = new Rule();

		model.addRule(rule);

		// Assume it is a model without changes
		model.hasUnsavedChanges = false;

		rule.setAuthor("200");

		assertThat(model.hasUnsavedChanges(), is(true));
	}

	@Test
	public void hasUnsavedChangesFalseAfterSaveChanges() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		// Assume it is a model with changes
		model.hasUnsavedChanges = true;

		model.saveChanges();

		assertThat(model.hasUnsavedChanges(), is(false));
	}

	@Test
	public void loadEmptyRuleFile() throws Exception {
		loadModelWithFile("empty-file.d2wmodel");

		Collection<Rule> rules = model.getRules();

		assertThat(rules.size(), is(0));
	}

	private void loadModelWithFile(String filename) throws Exception {
		model = new D2WModel(getFile(filename));
	}

	@Test
	public void loadNotQualifierRuleFile() throws Exception {
		loadModelWithFile("rule-with-not-qualifier.d2wmodel");

		Collection<Rule> rules = model.getRules();

		assertThat(rules.size(), is(1));
	}

	@Test
	public void loadOneRuleFile() throws Exception {
		loadModelWithFile("one-rule.d2wmodel");

		Collection<Rule> rules = model.getRules();

		assertThat(rules.size(), is(1));
	}

	@Test
	public void loadTwentyRuleFile() throws Exception {
		loadModelWithFile("twenty-rule.d2wmodel");

		Collection<Rule> rules = model.getRules();

		assertThat(rules.size(), is(20));
	}

	@Test
	public void saveEmptyModel() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		model.saveChanges();

		File expectedFile = getFile("no-rules.d2wmodel");

		assertThat(FileUtils.readFileToString(tempFile), is(FileUtils.readFileToString(expectedFile)));
	}

	@Test
	public void saveModelWithComplexQualifiers() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		Rule rule = model.createEmptyRule();

		LeftHandSide lhs = rule.getLeftHandSide();

		lhs.setConditions("(task = 'edit' and (entity.name = 'Entity1' or entity.name = 'Entity2') and propertyKey = 'property')");

		RightHandSide rhs = rule.getRightHandSide();

		rhs.setKeyPath("key");
		rhs.setValue("value");

		model.saveChanges();

		File expectedFile = getFile("lhs-example.d2wmodel");

		assertThat(FileUtils.readFileToString(tempFile), is(FileUtils.readFileToString(expectedFile)));
	}

	@Test
	public void saveModelWithOneRule() throws Exception {
		createTempFile();

		model = new D2WModel(tempFile);

		Rule rule = model.createEmptyRule();

		LeftHandSide lhs = rule.getLeftHandSide();

		lhs.setConditions("(task = 'edit')");

		RightHandSide rhs = rule.getRightHandSide();

		rhs.setKeyPath("key");
		rhs.setValue("value");

		model.saveChanges();

		File expectedFile = getFile("one-rule.d2wmodel");

		assertThat(FileUtils.readFileToString(tempFile), is(FileUtils.readFileToString(expectedFile)));
	}

	@After
	public void tearDown() {
		if (tempFile != null) {
			tempFile.delete();
		}
	}
}

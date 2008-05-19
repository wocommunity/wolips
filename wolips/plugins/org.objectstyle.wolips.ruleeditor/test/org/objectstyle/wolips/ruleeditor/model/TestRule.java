package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestRule {
	protected Rule rule;

	@Test
	public void emptyRuleToMap() throws Exception {
		rule = new Rule();

		Map<String, Object> result = rule.toMap();

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is(Rule.DEFAULT_ASSIGNMENT_CLASS_NAME));
		assertThat((String) result.get(Rule.AUTHOR_KEY), is(Rule.DEFAULT_AUTHOR));
		assertThat(result.get(Rule.LHS_KEY), nullValue());
		assertThat(result.get(Rule.RHS_KEY), notNullValue());
	}

	@Test
	public void existingRuleInitialization() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("author", "100");
		properties.put("class", "com.webobjects.directtoweb.Rule");

		Map<String, Object> lhsProperties = new HashMap<String, Object>();

		lhsProperties.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		lhsProperties.put("key", "task");
		lhsProperties.put("value", "edit");
		lhsProperties.put("selectorName", "isEqualTo");

		properties.put("lhs", lhsProperties);

		Map<String, Object> rhsProperties = new HashMap<String, Object>();

		rhsProperties.put("class", "com.webobjects.directtoweb.Assignment");
		rhsProperties.put("keyPath", "key");
		rhsProperties.put("value", "value");

		properties.put("rhs", rhsProperties);

		rule = new Rule(properties);

		assertThat(rule.getAuthor(), is("100"));
		assertThat(rule.getAssignmentClassName(), is("com.webobjects.directtoweb.Rule"));

		LeftHandSide lhs = rule.getLeftHandSide();

		assertThat(lhs, notNullValue());

		RightHandSide rhs = rule.getRightHandSide();

		assertThat(rhs, notNullValue());
	}

	@Test
	public void firePropertyChangeEventOnChange() throws Exception {
		rule = new Rule();

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		rule.addPropertyChangeListener(listener);

		assertThat(listener.firedEventsCount(), is(0));

		rule.setAuthor("200");

		assertThat(listener.firedEventsCount(), is(1));
	}

	@Test
	public void firePropertyChangeEventOnLhsChange() throws Exception {
		rule = new Rule();

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		rule.addPropertyChangeListener(listener);

		assertThat(listener.firedEventsCount(), is(0));

		LeftHandSide lhs = rule.getLeftHandSide();

		lhs.setConditions("(task = 'edit')");

		// In this case we have 2 events fired because of the assigmentClassName
		// change
		assertThat(listener.firedEventsCount(), is(2));
	}

	@Test
	public void firePropertyChangeEventOnRhsChange() throws Exception {
		rule = new Rule();

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		rule.addPropertyChangeListener(listener);

		assertThat(listener.firedEventsCount(), is(0));

		RightHandSide rhs = rule.getRightHandSide();

		rhs.setKeyPath("key");

		assertThat(listener.firedEventsCount(), is(1));
	}

	@Test
	public void newRuleInitialization() throws Exception {
		rule = new Rule();

		assertThat(rule.getLeftHandSide(), notNullValue());
		assertThat(rule.getRightHandSide(), notNullValue());
		assertThat(rule.getAuthor(), is(Rule.DEFAULT_AUTHOR));
		assertThat(rule.getAssignmentClassName(), is(Rule.DEFAULT_ASSIGNMENT_CLASS_NAME));
	}

	@Test
	public void newRuleWithoutLhs() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("author", "100");
		properties.put("class", "com.webobjects.directtoweb.Rule");

		Map<String, Object> rhsProperties = new HashMap<String, Object>();

		rhsProperties.put("class", "com.webobjects.directtoweb.Assignment");
		rhsProperties.put("keyPath", "key");
		rhsProperties.put("value", "value");

		properties.put("rhs", rhsProperties);

		rule = new Rule(properties);

		LeftHandSide lhs = rule.getLeftHandSide();

		assertThat(lhs, notNullValue());
	}
}

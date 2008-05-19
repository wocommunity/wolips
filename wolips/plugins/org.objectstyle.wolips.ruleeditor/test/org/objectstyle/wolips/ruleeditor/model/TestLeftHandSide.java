package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestLeftHandSide {
	protected LeftHandSide lhs;

	private Map<String, Object> createSimpleMap() {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		properties.put("key", "task");
		properties.put("value", "edit");
		properties.put("selectorName", "isEqualTo");

		return properties;
	}

	@Test
	public void displaStringForLeftHandSideWithQualifiers() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", "com.webobjects.eocontrol.EOAndQualifier");

		Collection<Map<String, Object>> qualifiers = new ArrayList<Map<String, Object>>();

		Map<String, Object> qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		qualifierMap.put("key", "task");
		qualifierMap.put("selectorName", "isEqualTo");
		qualifierMap.put("value", "edit");

		qualifiers.add(qualifierMap);

		qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		qualifierMap.put("key", "entity.name");
		qualifierMap.put("selectorName", "isEqualTo");
		qualifierMap.put("value", "Entity");

		qualifiers.add(qualifierMap);

		properties.put("qualifiers", qualifiers);

		lhs = new LeftHandSide(properties);

		assertThat(lhs.toString(), is("(task = 'edit' and entity.name = 'Entity')"));
	}

	@Test
	public void displayStringForEmptyLeftHandSide() throws Exception {
		lhs = new LeftHandSide();

		assertThat(lhs.toString(), is("*true*"));
	}

	@Test
	public void displayStringForSimpleLeftHandSide() throws Exception {
		Map<String, Object> properties = createSimpleMap();

		lhs = new LeftHandSide(properties);

		assertThat(lhs.toString(), is("task = 'edit'"));
	}

	@Test
	public void emptyLhs() throws Exception {
		lhs = new LeftHandSide();

		assertThat(lhs.isEmpty(), is(true));
	}

	@Test
	public void emptyLhsToMap() throws Exception {
		lhs = new LeftHandSide();

		assumeTrue(lhs.isEmpty());

		assertThat(lhs.toMap(), nullValue());
	}

	@Test
	public void existingLhsInitializationWithQualifiers() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", "com.webobjects.eocontrol.EOAndQualifier");

		Collection<Map<String, Object>> qualifiersArray = new ArrayList<Map<String, Object>>();

		Map<String, Object> qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		qualifierMap.put("key", "task");
		qualifierMap.put("selectorName", "isEqualTo");
		qualifierMap.put("value", "edit");

		qualifiersArray.add(qualifierMap);

		qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		qualifierMap.put("key", "entity.name");
		qualifierMap.put("selectorName", "isEqualTo");
		qualifierMap.put("value", "Entity");

		qualifiersArray.add(qualifierMap);

		properties.put("qualifiers", qualifiersArray);

		lhs = new LeftHandSide(properties);

		assertThat(lhs.getKey(), nullValue());
		assertThat(lhs.getSelectorName(), nullValue());
		assertThat(lhs.getValue(), nullValue());
		assertThat(lhs.getAssignmentClassName(), is("com.webobjects.eocontrol.EOAndQualifier"));

		Collection<QualifierElement> qualifiers = lhs.getQualifiers();

		assertThat(qualifiers.size(), is(2));
	}

	@Test
	public void existingLhsSimpleInitialization() throws Exception {
		Map<String, Object> properties = createSimpleMap();

		lhs = new LeftHandSide(properties);

		assertThat(lhs.getAssignmentClassName(), is("com.webobjects.eocontrol.EOKeyValueQualifier"));
		assertThat(lhs.getKey(), is("task"));
		assertThat(lhs.getValue(), is("edit"));
		assertThat(lhs.getSelectorName(), is("isEqualTo"));
		assertThat(lhs.getQualifiers(), nullValue());
	}

	@Test
	public void initializationWithQualifiersElements() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", "com.webobjects.eocontrol.EOAndQualifier");

		Collection<QualifierElement> qualifiersArray = new ArrayList<QualifierElement>();

		Map<String, Object> qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		qualifierMap.put("key", "task");
		qualifierMap.put("selectorName", "isEqualTo");
		qualifierMap.put("value", "edit");

		qualifiersArray.add(new QualifierElement(qualifierMap));

		qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", "com.webobjects.eocontrol.EOKeyValueQualifier");
		qualifierMap.put("key", "entity.name");
		qualifierMap.put("selectorName", "isEqualTo");
		qualifierMap.put("value", "Entity");

		qualifiersArray.add(new QualifierElement(qualifierMap));

		properties.put("qualifiers", qualifiersArray);

		lhs = new LeftHandSide(properties);

		assertThat(lhs.getKey(), nullValue());
		assertThat(lhs.getSelectorName(), nullValue());
		assertThat(lhs.getValue(), nullValue());
		assertThat(lhs.getAssignmentClassName(), is("com.webobjects.eocontrol.EOAndQualifier"));

		Collection<QualifierElement> qualifiers = lhs.getQualifiers();

		assertThat(qualifiers.size(), is(2));
	}

	@Test
	public void newLhsInitialization() throws Exception {
		lhs = new LeftHandSide();

		assertThat(lhs.getQualifiers(), nullValue());
		assertThat(lhs.getAssignmentClassName(), nullValue());
		assertThat(lhs.getKey(), nullValue());
		assertThat(lhs.getSelectorName(), nullValue());
		assertThat(lhs.getValue(), nullValue());
	}

	@Test
	public void notEmptyLhs() throws Exception {
		lhs = new LeftHandSide();

		lhs.setAssignmentClassName("teste");

		assertThat(lhs.isEmpty(), is(false));
	}

	@Test
	public void oneQualifierLhsToMap() throws Exception {
		lhs = new LeftHandSide();

		lhs.setConditions("(task = 'edit')");

		Map<String, Object> result = lhs.toMap();

		assertThat(result.get(AbstractQualifierElement.QUALIFIERS_KEY), nullValue());
		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOKeyValueQualifier"));
		assertThat((String) result.get(AbstractQualifierElement.KEY_KEY), is("task"));
		assertThat((String) result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), is("isEqualTo"));
		assertThat((String) result.get(AbstractQualifierElement.VALUE_KEY), is("edit"));
	}

	@Test
	public void setNullConditions() throws Exception {
		lhs = new LeftHandSide();

		lhs.setKey("test");
		lhs.setValue("test");
		lhs.setSelectorName("test");
		lhs.setAssignmentClassName("test");
		lhs.setQualifiers(new ArrayList<QualifierElement>());

		lhs.setConditions(null);

		assertThat(lhs.isEmpty(), is(true));
	}

	@Test
	public void setOneCondition() throws Exception {
		lhs = new LeftHandSide();

		lhs.setConditions("(task = 'edit')");

		assertThat(lhs.getAssignmentClassName(), is("com.webobjects.eocontrol.EOKeyValueQualifier"));
		assertThat(lhs.getKey(), is("task"));
		assertThat(lhs.getValue(), is("edit"));
		assertThat(lhs.getSelectorName(), is("isEqualTo"));
		assertThat(lhs.getQualifiers(), nullValue());
	}

	@Test
	public void twoQualifiersLhsToMap() throws Exception {
		lhs = new LeftHandSide();

		lhs.setConditions("(task = 'edit' and entity.name = 'Entity')");

		Map<String, Object> result = lhs.toMap();

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOAndQualifier"));
		assertThat(result.get(AbstractQualifierElement.KEY_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.VALUE_KEY), nullValue());

		Collection qualifiers = (Collection) result.get(AbstractQualifierElement.QUALIFIERS_KEY);

		assertThat(qualifiers, notNullValue());
		assertThat(qualifiers.size(), is(2));
	}
}

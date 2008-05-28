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

	@Test
	public void createLhsForMapWithNotQualifier() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", Qualifier.NOT.getClassName());

		Map<String, Object> qualifierMap = new HashMap<String, Object>();

		qualifierMap.put("class", Qualifier.KEY_VALUE.getClassName());
		qualifierMap.put("key", "relationship.isToMany");
		qualifierMap.put("selectorName", Selector.EQUAL.getSelectorName());
		qualifierMap.put("value", "test");

		// Note that EONotQualifier have only one qualifier
		properties.put("qualifier", qualifierMap);

		lhs = new LeftHandSide(properties);

		assertThat(lhs.getKey(), nullValue());
		assertThat(lhs.getSelectorName(), nullValue());
		assertThat(lhs.getValue(), nullValue());
		assertThat(lhs.getAssignmentClassName(), is(Qualifier.NOT.getClassName()));

		QualifierElement qualifier = lhs.getQualifier();

		assertThat(qualifier.getAssignmentClassName(), is(Qualifier.KEY_VALUE.getClassName()));
		assertThat(qualifier.getKey(), is("relationship.isToMany"));
		assertThat(qualifier.getSelectorName(), is(Selector.EQUAL.getSelectorName()));
		assertThat((String) qualifier.getValue().value, is("test"));
	}

	@Test
	public void createLhsForMapWithNumber() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", Qualifier.KEY_VALUE.getClassName());

		Map<String, Object> numberMap = new HashMap<String, Object>();

		numberMap.put("class", "java.lang.Number");
		numberMap.put("value", 1);

		properties.put("value", numberMap);

		lhs = new LeftHandSide(properties);

		assertThat((Integer) lhs.getValue().value, is(1));
	}

	private LeftHandSide createLhsWithNotQualifier() {
		LeftHandSide lhs = new LeftHandSide();

		lhs.setAssignmentClassName(Qualifier.NOT.getClassName());

		QualifierElement qualifier = new QualifierElement();

		qualifier.setAssignmentClassName(Qualifier.KEY_VALUE.getClassName());
		qualifier.setKey("task");
		qualifier.setSelectorName(Selector.EQUAL.getSelectorName());
		qualifier.setValue("edit");

		lhs.setQualifier(qualifier);

		return lhs;
	}

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
	public void displayStringForNotQualifier() throws Exception {
		lhs = createLhsWithNotQualifier();

		assertThat(lhs.toString(), is("not (task = 'edit')"));
	}

	@Test
	public void displayStringForNotQualifierWithAndQualifier() throws Exception {
		lhs = new LeftHandSide();

		lhs.setAssignmentClassName(Qualifier.AND.getClassName());

		Collection<QualifierElement> qualifiers = new ArrayList<QualifierElement>();

		QualifierElement qualifier = new QualifierElement();

		qualifier.setAssignmentClassName(Qualifier.KEY_VALUE.getClassName());
		qualifier.setKey("property");
		qualifier.setSelectorName(Selector.EQUAL.getSelectorName());
		qualifier.setValue("test");

		qualifiers.add(qualifier);

		qualifier = new QualifierElement();

		qualifier.setAssignmentClassName(Qualifier.KEY_VALUE.getClassName());
		qualifier.setKey("task");
		qualifier.setSelectorName(Selector.EQUAL.getSelectorName());
		qualifier.setValue("edit");

		QualifierElement notQualifier = new QualifierElement();

		notQualifier.setAssignmentClassName(Qualifier.NOT.getClassName());
		notQualifier.setQualifier(qualifier);

		qualifiers.add(notQualifier);

		lhs.setQualifiers(qualifiers);

		assertThat(lhs.toString(), is("(property = 'test' and not (task = 'edit'))"));
	}

	@Test
	public void displayStringForQualifierWithNumber() throws Exception {
		lhs = new LeftHandSide();

		lhs.setAssignmentClassName(Qualifier.KEY_VALUE.getClassName());
		lhs.setKey("relationship.isToMany");
		lhs.setSelectorName(Selector.EQUAL.getSelectorName());
		lhs.setValue("1");

		assertThat(lhs.toString(), is("relationship.isToMany = 1"));
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
		assertThat((String) lhs.getValue().value, is("edit"));
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

		lhs = new LeftHandSide();

		lhs.setQualifier(new QualifierElement());

		assertThat(lhs.isEmpty(), is(false));
	}

	@Test
	public void notQualifierToMap() throws Exception {
		lhs = createLhsWithNotQualifier();

		Map<String, Object> result = lhs.toMap();

		assertThat(result.get(AbstractQualifierElement.QUALIFIERS_KEY), nullValue());
		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is(Qualifier.NOT.getClassName()));

		Map<String, Object> qualifierMap = (Map<String, Object>) result.get(AbstractQualifierElement.QUALIFIER_KEY);

		assertThat((String) qualifierMap.get(AbstractRuleElement.CLASS_KEY), is(Qualifier.KEY_VALUE.getClassName()));
		assertThat((String) qualifierMap.get(AbstractQualifierElement.KEY_KEY), is("task"));
		assertThat((String) qualifierMap.get(AbstractQualifierElement.SELECTOR_NAME_KEY), is("isEqualTo"));
		assertThat((String) qualifierMap.get(AbstractQualifierElement.VALUE_KEY), is("edit"));
	}

	@Test
	public void numberToMap() throws Exception {
		lhs = new LeftHandSide();

		lhs.setValue("1");

		Map<String, String> result = (Map<String, String>) lhs.toMap().get(AbstractQualifierElement.VALUE_KEY);

		assertThat(result.get("class"), is(Number.class.getName()));
		assertThat(result.get("value"), is("1"));
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
		assertThat((String) lhs.getValue().value, is("edit"));
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

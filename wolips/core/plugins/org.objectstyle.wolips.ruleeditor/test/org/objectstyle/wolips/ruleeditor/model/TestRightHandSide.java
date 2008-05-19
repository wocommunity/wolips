package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestRightHandSide {
	protected RightHandSide rhs;

	@Test
	public void emptyRhsToMap() throws Exception {
		rhs = new RightHandSide();

		Map<String, Object> result = rhs.toMap();

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is(RightHandSide.DEFAULT_ASSIGNMENT_CLASS_NAME));
		assertThat((String) result.get(RightHandSide.KEY_PATH_KEY), is(""));
		assertThat((String) result.get(RightHandSide.VALUE_KEY), is(""));
	}

	@Test
	public void existingRhsInitialization() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", "com.webobjects.directtoweb.Assignment");
		properties.put("keyPath", "key");
		properties.put("value", "string");

		rhs = new RightHandSide(properties);

		assertThat(rhs.getAssignmentClassName(), is("com.webobjects.directtoweb.Assignment"));
		assertThat(rhs.getKeyPath(), is("key"));
		assertThat(rhs.getValue(), is("string"));
	}

	@Test
	public void firePropertyChangeEventOnChange() throws Exception {
		rhs = new RightHandSide();

		MockPropertyChangeListener listener = new MockPropertyChangeListener();

		rhs.addPropertyChangeListener(listener);

		assertThat(listener.firedEventsCount(), is(0));

		rhs.setKeyPath("key");

		assertThat(listener.firedEventsCount(), is(1));

		rhs.setValue("value");

		assertThat(listener.firedEventsCount(), is(2));

		rhs.setAssignmentClassName("a class name");

		assertThat(listener.firedEventsCount(), is(3));
	}

	@Test
	public void getNullValue() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put("class", "com.webobjects.directtoweb.Assignment");
		properties.put("keyPath", "key");

		rhs = new RightHandSide(properties);

		assertThat(rhs.getAssignmentClassName(), is("com.webobjects.directtoweb.Assignment"));
		assertThat(rhs.getKeyPath(), is("key"));
		assertThat(rhs.getValue(), nullValue());
	}

	@Test
	public void newRhsInitialization() throws Exception {
		rhs = new RightHandSide();

		assertThat(rhs.getValue(), is(""));
		assertThat(rhs.getKeyPath(), is(""));
		assertThat(rhs.getAssignmentClassName(), is(RightHandSide.DEFAULT_ASSIGNMENT_CLASS_NAME));
	}

	@Test
	public void rhsValueAsArray() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		Collection<String> arrayOfValues = new ArrayList<String>();

		arrayOfValues.add("string1");
		arrayOfValues.add("string2");

		properties.put("value", arrayOfValues);

		rhs = new RightHandSide(properties);

		assertThat(rhs.getValue(), is("( \"string1\", \"string2\" )"));
	}

	@Test
	public void rhsValueAsDictionary() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		Map<String, String> mapOfValues = new HashMap<String, String>();

		mapOfValues.put("key1", "value1");
		mapOfValues.put("key2", "value2");

		properties.put("value", mapOfValues);

		rhs = new RightHandSide(properties);

		assertThat(rhs.getValue(), is("{ \"key1\" = \"value1\"; \"key2\" = \"value2\"; }"));
	}

	@Test
	public void rhsValueAsDictionaryWithNumbers() throws Exception {
		Map<String, Object> properties = new HashMap<String, Object>();

		Map<Object, Object> mapOfValues = new HashMap<Object, Object>();

		mapOfValues.put(10, 100);
		mapOfValues.put(20, 200);

		properties.put("value", mapOfValues);

		rhs = new RightHandSide(properties);

		assertThat(rhs.getValue(), is("{ \"10\" = \"100\"; \"20\" = \"200\"; }"));
	}

	@Test
	public void setRhsValueWithArray() throws Exception {
		rhs = new RightHandSide();

		rhs.setValue("( \"value1\", \"value2\" )");

		Collection<String> arrayOfValues = (Collection<String>) rhs.value;

		assertThat(arrayOfValues.size(), is(2));
		assertThat(arrayOfValues, hasItems("value1", "value2"));
	}

	@Test
	public void setRhsValueWithDictionary() throws Exception {
		rhs = new RightHandSide();

		rhs.setValue("{ \"key1\" = \"value1\"; \"key2\" = \"value2\"; }");

		Map<String, String> mapOfValues = (Map<String, String>) rhs.value;

		assertThat(mapOfValues.size(), is(2));
		assertThat(mapOfValues.keySet(), hasItems("key1", "key2"));
		assertThat(mapOfValues.values(), hasItems("value1", "value2"));
	}

	@Test
	public void setRhsValueWithString() throws Exception {
		rhs = new RightHandSide();

		rhs.setValue("value1");

		assertThat(rhs.getValue(), is("value1"));
	}
}

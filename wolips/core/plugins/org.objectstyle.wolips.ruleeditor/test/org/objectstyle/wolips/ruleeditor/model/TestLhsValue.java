package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestLhsValue {
	protected LhsValue value;

	private Map<String, Object> createMapForNullRepresentation() {
		Map<String, Object> valueMap = new HashMap<String, Object>();

		valueMap.put("class", "com.webobjects.foundation.NSKeyValueCoding$Null");

		return valueMap;
	}

	private Map<String, Object> createNumberMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(LhsValue.CLASS_PROPERTY, Number.class.getName());
		map.put(LhsValue.VALUE_PROPERTY, 1);

		return map;
	}

	@Test(expected = IllegalArgumentException.class)
	public void createValueForMapWihtoutClassProperty() throws Exception {
		Map<String, String> valueMap = new HashMap<String, String>();

		valueMap.put(LhsValue.VALUE_PROPERTY, "1");

		value = new LhsValue(valueMap);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createValueForMapWihtoutValueProperty() throws Exception {
		Map<String, String> valueMap = new HashMap<String, String>();

		valueMap.put(LhsValue.CLASS_PROPERTY, Number.class.getName());

		value = new LhsValue(valueMap);
	}

	@Test
	public void createValueForMapWithNullRepresentation() throws Exception {

		value = new LhsValue(createMapForNullRepresentation());

		assertThat(value.value, nullValue());
	}

	@Test
	public void createValueForMapWithNumber() throws Exception {
		Map<String, Object> valueMap = createNumberMap();

		value = new LhsValue(valueMap);

		assertThat(value.value, notNullValue());
		assertThat(value.value, instanceOf(Number.class));
		assertThat((Integer) value.value, is(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void createValueForNullMap() throws Exception {
		new LhsValue((Map) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createValueForNullString() throws Exception {
		new LhsValue((String) null);
	}

	@Test
	public void createValueForNumberAsString() throws Exception {
		value = new LhsValue("1");

		assertThat(value.value, notNullValue());
		assertThat(value.value, instanceOf(Number.class));
		assertThat((Integer) value.value, is(1));
	}

	@Test
	public void createValueForObject() throws Exception {
		value = new LhsValue((Object) "test");

		assertThat(value.value, notNullValue());
	}

	@Test
	public void createValueForString() throws Exception {
		value = new LhsValue("edit");

		assertThat(value.value, notNullValue());
		assertThat(value.value, instanceOf(String.class));
		assertThat((String) value.value, is("edit"));
	}

	@Test
	public void getValueForNull() throws Exception {
		value = new LhsValue(createMapForNullRepresentation());

		assertThat(value.getValue(), is("null"));
	}

	@Test
	public void getValueForNumber() throws Exception {
		value = new LhsValue(createNumberMap());

		assertThat(value.getValue(), is("1"));
	}

	@Test
	public void getValueForString() throws Exception {
		value = new LhsValue("test");

		assertThat(value.getValue(), is("test"));
	}

	@Test
	public void toMapForNullValue() throws Exception {
		value = new LhsValue(createMapForNullRepresentation());

		Object result = value.toMap();

		assertThat(result, instanceOf(Map.class));

		Map map = (Map) result;

		assertThat((String) map.get(LhsValue.CLASS_PROPERTY), is(LhsValue.NULL_VALUE_CLASS));
		assertThat(map.get(LhsValue.VALUE_PROPERTY), nullValue());
	}

	@Test
	public void toMapForNumberValue() throws Exception {
		value = new LhsValue(createNumberMap());

		Object result = value.toMap();

		assertThat(value.toMap(), instanceOf(Map.class));

		Map map = (Map) result;

		assertThat((String) map.get(LhsValue.CLASS_PROPERTY), is(Number.class.getName()));
		assertThat((String) map.get(LhsValue.VALUE_PROPERTY), is("1"));
	}

	@Test
	public void toMapForStringValue() throws Exception {
		value = new LhsValue("edit");

		assertThat((String) value.toMap(), is("edit"));
	}

	@Test
	public void toStringForNullValue() throws Exception {
		value = new LhsValue(createMapForNullRepresentation());

		assertThat(value.toString(), is("null"));
	}

	@Test
	public void toStringForNumberValue() throws Exception {
		value = new LhsValue(createNumberMap());

		assertThat(value.toString(), is("1"));
	}

	@Test
	public void toStringForStringValue() throws Exception {
		value = new LhsValue("edit");

		assertThat(value.toString(), is("'edit'"));
	}
}

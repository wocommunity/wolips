package org.objectstyle.wolips.ruleeditor.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
class LhsValue implements Value {

	protected static final String NULL_VALUE_CLASS = "com.webobjects.foundation.NSKeyValueCoding$Null";

	protected static final String CLASS_PROPERTY = "class";

	protected static final String VALUE_PROPERTY = "value";

	private static Object initializeComposedValue(final Map<String, ?> map) {
		if (map == null) {
			throw new IllegalArgumentException("The left hand side value is a composed value and cannot be null (probably you have a left hand side with value = null)");
		}

		String classProperty = (String) map.get(CLASS_PROPERTY);

		if (NULL_VALUE_CLASS.equals(classProperty)) {
			return null;
		}

		if (!Number.class.getName().equals(classProperty)) {
			throw new IllegalArgumentException("The class property " + classProperty + " is invalid or unsupported for a composed value (probably you have a left hand side with value = { class = \"" + classProperty + "\"; ... }");
		}

		Object mappedValue = map.get(VALUE_PROPERTY);

		if (mappedValue == null) {
			throw new IllegalArgumentException("The value property cannot be null for a composed value");
		}

		return Integer.valueOf(mappedValue.toString());
	}

	private static Object initializeStringValue(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("The left hand side value cannot be null");
		}

		if (NumberUtils.isNumber(value)) {
			return Integer.valueOf(value);
		}

		return value;
	}

	final Object value;

	protected LhsValue(final Map<String, ?> valueMap) {
		this.value = initializeComposedValue(valueMap);
	}

	protected LhsValue(final Object value) {
		if (value instanceof String) {
			this.value = initializeStringValue((String) value);
		} else {
			this.value = initializeComposedValue((Map) value);
		}
	}

	protected LhsValue(final String value) {
		this.value = initializeStringValue(value);
	}

	public String getValue() {
		if (value == null) {
			return "null";
		}

		return value.toString();
	}

	public Object toMap() {
		if (value == null) {
			Map<String, String> nullMap = new HashMap<String, String>();

			nullMap.put(CLASS_PROPERTY, NULL_VALUE_CLASS);

			return nullMap;
		}

		if (value instanceof Number) {
			Map<String, String> numberMap = new HashMap<String, String>();

			numberMap.put(CLASS_PROPERTY, Number.class.getName());
			numberMap.put(VALUE_PROPERTY, value.toString());

			return numberMap;
		}

		return value;
	}

	@Override
	public String toString() {
		if (value == null) {
			return "null";
		}

		if (value instanceof Number) {
			return value.toString();
		}

		return "'" + value + "'";
	}
}

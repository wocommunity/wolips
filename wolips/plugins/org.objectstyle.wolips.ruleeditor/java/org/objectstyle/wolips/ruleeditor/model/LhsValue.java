package org.objectstyle.wolips.ruleeditor.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
class LhsValue implements Value {

	protected static final String CLASS_PROPERTY = "class";

	protected static final String VALUE_PROPERTY = "value";

	private static Object initializeMapValue(Map<String, ?> map) {
		if (map == null) {
			throw new IllegalArgumentException("The left hand side value Map cannot be null");
		}

		String classProperty = (String) map.get(CLASS_PROPERTY);

		if (!Number.class.getName().equals(classProperty)) {
			throw new IllegalArgumentException("The class Map property " + classProperty + " is invalid or unsupported");
		}

		Object mappedValue = map.get(VALUE_PROPERTY);

		if (mappedValue == null) {
			throw new IllegalArgumentException("The value Map property " + mappedValue + " cannot be null");
		}

		return Integer.valueOf(mappedValue.toString());
	}

	private static Object initializeStringValue(String value) {
		if (value == null) {
			throw new IllegalArgumentException("The left hand side value cannot be null");
		}

		if (NumberUtils.isNumber(value)) {
			return Integer.valueOf(value);
		}

		return value;
	}

	final Object value;

	protected LhsValue(Map<String, ?> valueMap) {
		this.value = initializeMapValue(valueMap);
	}

	protected LhsValue(Object value) {
		if (value instanceof String) {
			this.value = initializeStringValue((String) value);
		} else {
			this.value = initializeMapValue((Map) value);
		}
	}

	protected LhsValue(String value) {
		this.value = initializeStringValue(value);
	}

	public String getValue() {
		return value.toString();
	}

	public Object toMap() {
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
		if (value instanceof Number) {
			return value.toString();
		}

		return "'" + value + "'";
	}
}

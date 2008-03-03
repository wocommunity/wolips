package org.objectstyle.wolips.eomodeler.core.model.qualifier;

public class EOKeyValueQualifier extends EOQualifier {
	private String _key;

	private EOQualifier.Comparison _comparison;

	private Object _value;

	public EOKeyValueQualifier() {
		// DO NOTHING
	}

	public EOKeyValueQualifier(String key, String comparison, Object value) {
		this(key, new EOQualifier.Comparison(comparison), value);
	}

	public EOKeyValueQualifier(String key, EOQualifier.Comparison comparison, Object value) {
		_key = key;
		_comparison = comparison;
		_value = value;
	}

	public String getKey() {
		return _key;
	}

	public EOQualifier.Comparison getComparison() {
		return _comparison;
	}

	public Object getValue() {
		return _value;
	}

	public String toString(int depth) {
		StringBuffer sb = new StringBuffer();
		sb.append(_key);
		sb.append(" ");
		sb.append(_comparison);
		sb.append(" ");
		if (_value instanceof String) {
			sb.append("'");
			String escapedValue = (String) _value;
			escapedValue = escapedValue.replaceAll("\\\\", "\\\\\\\\");
			escapedValue = escapedValue.replaceAll("'", "\\\\'");
			sb.append(escapedValue);
			sb.append("'");
		} else {
			sb.append(_value);
		}
		return sb.toString();
	}
}

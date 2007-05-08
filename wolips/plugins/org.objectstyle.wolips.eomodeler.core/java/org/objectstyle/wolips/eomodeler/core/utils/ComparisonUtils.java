package org.objectstyle.wolips.eomodeler.core.utils;

public class ComparisonUtils {
	public static boolean equals(Object _o1, Object _o2) {
		boolean equals;
		if (_o1 == null) {
			equals = (_o2 == null);
		} else {
			equals = (_o1 == _o2);
			if (!equals) {
				equals = _o1.equals(_o2);
			}
		}
		return equals;
	}

	public static boolean equals(String _o1, String _o2, boolean _blankIsNull) {
		boolean equals = ComparisonUtils.equals(_o1, _o2);
		if (!equals && _blankIsNull) {
			equals = ((_o1 == null || _o1.length() == 0) && (_o2 == null || _o2.length() == 0));
		}
		return equals;
	}
	
	public static boolean equalsIgnoreCase(String _o1, String _o2) {
		boolean equals;
		if (_o1 == null) {
			equals = (_o2 == null);
		} else {
			equals = (_o1 == _o2);
			if (!equals) {
				equals = _o1.equalsIgnoreCase(_o2);
			}
		}
		return equals;
	}

	public static boolean equalsIgnoreCase(String _o1, String _o2, boolean _blankIsNull) {
		boolean equals = ComparisonUtils.equalsIgnoreCase(_o1, _o2);
		if (!equals && _blankIsNull) {
			equals = ((_o1 == null || _o1.length() == 0) && (_o2 == null || _o2.length() == 0));
		}
		return equals;
	}
}

package org.objectstyle.wolips.baseforplugins.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

	private static Object deepCopyVanilla(Object original) {
		if (original instanceof Map) {
			Map<Object, Object> newMap = new HashMap<Object, Object>();
			Iterator iterator = ((Map) original).entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				newMap.put(entry.getKey(), deepCopyVanilla(entry.getValue()));
			}
			return newMap;
		} else if (original instanceof Set) {
			Set<Object> newSet = new HashSet<Object>();
			Iterator iterator = ((Set) original).iterator();
			while (iterator.hasNext()) {
				newSet.add(deepCopyVanilla(iterator.next()));
			}
			return newSet;
		} else if (original instanceof Number) {
			return original.toString();
		} else {
			return original;
		}
	}

	public static boolean deepEquals(Object one, Object two) {
		if (one == null && two == null) {
			return true;
		} else if (one == null || two == null) {
			return false;
		} else {
			return deepCopyVanilla(one).equals(deepCopyVanilla(two));
		}
	}
}

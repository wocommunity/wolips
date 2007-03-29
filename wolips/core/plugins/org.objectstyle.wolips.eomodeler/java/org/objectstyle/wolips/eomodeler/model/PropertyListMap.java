package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;
import java.util.TreeMap;

public class PropertyListMap<U, V> extends TreeMap<U, V> {
	public PropertyListMap() {
		super(PropertyListComparator.AscendingPropertyListComparator);
	}

	public PropertyListMap(Map<U, V> _map) {
		this();
		putAll(_map);
	}
}

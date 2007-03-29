package org.objectstyle.wolips.eomodeler.model;

import java.util.Set;
import java.util.TreeSet;

public class PropertyListSet<T> extends TreeSet<T> {
	public PropertyListSet() {
		super(PropertyListComparator.AscendingPropertyListComparator);
	}

	public PropertyListSet(Set<T> _set) {
		this();
		addAll(_set);
	}
}

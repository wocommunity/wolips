package org.objectstyle.wolips.eomodeler.model;

import java.util.Set;
import java.util.TreeSet;

public class PropertyListSet extends TreeSet {
	public PropertyListSet() {
		super(PropertyListComparator.AscendingPropertyListComparator);
	}

	public PropertyListSet(Set _set) {
		this();
		addAll(_set);
	}
}

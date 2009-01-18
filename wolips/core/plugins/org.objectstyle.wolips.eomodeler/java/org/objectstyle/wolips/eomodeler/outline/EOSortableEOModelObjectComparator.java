package org.objectstyle.wolips.eomodeler.outline;

import java.util.Comparator;

import org.objectstyle.wolips.eomodeler.core.model.ISortableEOModelObject;

public class EOSortableEOModelObjectComparator implements Comparator<Object> {
	public int compare(Object _o1, Object _o2) {
		int comparison;
		if (_o1 == null) {
			if (_o2 == null) {
				comparison = 0;
			} else {
				comparison = -1;
			}
		} else if (_o2 == null) {
			comparison = 1;
		} else if (_o1 instanceof ISortableEOModelObject && _o2 instanceof ISortableEOModelObject) {
			String n1 = ((ISortableEOModelObject) _o1).getName();
			String n2 = ((ISortableEOModelObject) _o2).getName();
			if (n1 == null) {
				if (n2 == null) {
					comparison = 0;
				} else {
					comparison = -1;
				}
			} else if (n2 == null) {
				comparison = 1;
			} else {
				comparison = n1.compareTo(n2);
			}
		} else {
			comparison = -1;
		}
		return comparison;
	}

}

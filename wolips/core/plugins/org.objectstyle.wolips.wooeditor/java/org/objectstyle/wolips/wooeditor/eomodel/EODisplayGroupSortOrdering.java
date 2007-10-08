package org.objectstyle.wolips.wooeditor.eomodel;

import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOSortOrdering;

public class EODisplayGroupSortOrdering extends EOSortOrdering {
	public static final String SELECTOR_ASCENDING = "compareAscending:";

	public static final String SELECTOR_DESCENDING = "compareDescending:";

	@Override
	public void loadFromMap(final EOModelMap map) {
		super.loadFromMap(map);
		String mySelectorName = getSelectorName();
		if (SELECTOR_ASCENDING.equals(mySelectorName)) {
			setSelectorName(EOSortOrdering.SELECTOR_ASCENDING);
		}
		if (SELECTOR_DESCENDING.equals(mySelectorName)) {
			setSelectorName(EOSortOrdering.SELECTOR_DESCENDING);
		}
	}

	@Override
	public EOModelMap toMap() {
		String mySelectorName = getSelectorName();
		if (EOSortOrdering.SELECTOR_ASCENDING.equals(mySelectorName)) {
			setSelectorName(SELECTOR_ASCENDING);
		}
		if (EOSortOrdering.SELECTOR_DESCENDING.equals(mySelectorName)) {
			setSelectorName(SELECTOR_DESCENDING);
		}
		EOModelMap myModelMap = super.toMap();
		setSelectorName(mySelectorName);
		return myModelMap;
	}
}

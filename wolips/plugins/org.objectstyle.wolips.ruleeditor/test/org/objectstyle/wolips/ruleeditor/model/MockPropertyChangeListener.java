package org.objectstyle.wolips.ruleeditor.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MockPropertyChangeListener implements PropertyChangeListener {

	private int count = 0;

	public int firedEventsCount() {
		return count;
	}

	public void propertyChange(PropertyChangeEvent event) {
		count++;
	}

}

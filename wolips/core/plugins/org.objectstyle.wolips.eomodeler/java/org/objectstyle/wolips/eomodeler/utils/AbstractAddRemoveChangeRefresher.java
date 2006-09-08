package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractAddRemoveChangeRefresher implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent _event) {
		Set oldValues = (Set) _event.getOldValue();
		Set newValues = (Set) _event.getNewValue();
		if (newValues != null && oldValues != null) {
			if (newValues.size() > oldValues.size()) {
				List newList = new LinkedList(newValues);
				newList.removeAll(oldValues);
				objectsAdded(newList);
			} else if (newValues.size() < oldValues.size()) {
				List oldList = new LinkedList(oldValues);
				oldList.removeAll(newValues);
				objectsRemoved(oldList);
			}
		}
	}

	protected abstract void objectsAdded(List _addedObjects);

	protected abstract void objectsRemoved(List _removedObjects);
}

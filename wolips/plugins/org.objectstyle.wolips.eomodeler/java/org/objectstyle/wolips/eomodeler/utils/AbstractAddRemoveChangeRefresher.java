package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class AbstractAddRemoveChangeRefresher<T> implements PropertyChangeListener {
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent _event) {
		Set<T> oldValues = (Set<T>) _event.getOldValue();
		Set<T> newValues = (Set<T>) _event.getNewValue();
		if (newValues != null && oldValues != null) {
			if (newValues.size() > oldValues.size()) {
				List<T> newList = new LinkedList<T>(newValues);
				newList.removeAll(oldValues);
				objectsAdded(newList);
			} else if (newValues.size() < oldValues.size()) {
				List<T> oldList = new LinkedList<T>(oldValues);
				oldList.removeAll(newValues);
				objectsRemoved(oldList);
			}
		}
	}

	protected abstract void objectsAdded(List<T> _addedObjects);

	protected abstract void objectsRemoved(List<T> _removedObjects);
}

package org.objectstyle.wolips.eomodeler.core.model.history;

import java.util.LinkedList;
import java.util.List;

public class ModelEvents {
	private List<IModelEvent> _events;

	public ModelEvents() {
		_events = new LinkedList<IModelEvent>();
	}

	public synchronized void addEvent(IModelEvent newEvent) {
		boolean done = false;
		boolean shouldAdd = true;
		for (int i = _events.size() - 1; !done && i >= 0; i--) {
			IModelEvent existingEvent = _events.get(i);
			if (newEvent.isEncompassedBy(existingEvent)) {
				done = true;
				shouldAdd = false;
			}
			if (existingEvent.isReplacedBy(newEvent)) {
				_events.remove(i);
			}
		}

		if (shouldAdd) {
			_events.add(newEvent);
		}
	}

	public synchronized List<IModelEvent> getEvents() {
		return new LinkedList<IModelEvent>(_events);
	}
}

package org.objectstyle.wolips.eomodeler.core.model.history;

import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;

public class EOAttributeDeletedEvent extends AbstractModelEvent {
	private EOAttribute _attribute;

	public EOAttributeDeletedEvent(EOAttribute attribute) {
		_attribute = attribute;
	}

	public EOAttribute getAttribute() {
		return _attribute;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return event instanceof EOAttributeAddedEvent && ((EOAttributeAddedEvent) event).getAttribute().equals(_attribute);
	}

	public boolean isReplacedBy(IModelEvent event) {
		return event instanceof EOEntityDeletedEvent && ((EOEntityDeletedEvent) event).getEntity().equals(_attribute.getEntity());
	}
}

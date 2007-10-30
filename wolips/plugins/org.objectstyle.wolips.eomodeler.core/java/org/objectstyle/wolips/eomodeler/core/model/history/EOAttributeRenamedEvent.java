package org.objectstyle.wolips.eomodeler.core.model.history;

import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;

public class EOAttributeRenamedEvent extends AbstractModelEvent {
	private EOAttribute _attribute;

	public EOAttributeRenamedEvent(EOAttribute attribute) {
		_attribute = attribute;
	}

	public EOAttribute getAttribute() {
		return _attribute;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return event instanceof EOEntityAddedEvent && ((EOEntityAddedEvent) event).getEntity().equals(_attribute.getEntity());
	}

	public boolean isReplacedBy(IModelEvent event) {
		return (event instanceof EOAttributeDeletedEvent && ((EOAttributeDeletedEvent) event).getAttribute().equals(_attribute)) || ((event instanceof EOAttributeRenamedEvent && ((EOAttributeRenamedEvent) event).getAttribute().equals(_attribute)));
	}
}

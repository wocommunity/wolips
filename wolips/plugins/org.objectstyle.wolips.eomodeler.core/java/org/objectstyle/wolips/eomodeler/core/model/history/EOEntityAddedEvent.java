package org.objectstyle.wolips.eomodeler.core.model.history;

import org.objectstyle.wolips.eomodeler.core.model.EOEntity;

public class EOEntityAddedEvent extends AbstractModelEvent {
	private EOEntity _entity;

	public EOEntityAddedEvent(EOEntity entity) {
		_entity = entity;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return false;
	}

	public boolean isReplacedBy(IModelEvent event) {
		return event instanceof EOEntityDeletedEvent && ((EOEntityDeletedEvent) event).getEntity().equals(_entity);
	}
}

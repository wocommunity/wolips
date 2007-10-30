package org.objectstyle.wolips.eomodeler.core.model.history;

import org.objectstyle.wolips.eomodeler.core.model.EOEntity;

public class EOEntityDeletedEvent extends AbstractModelEvent {
	private EOEntity _entity;

	public EOEntityDeletedEvent(EOEntity entity) {
		_entity = entity;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public boolean isEncompassedBy(IModelEvent event) {
		return event instanceof EOEntityAddedEvent && ((EOEntityAddedEvent) event).getEntity().equals(_entity);
	}

	public boolean isReplacedBy(IModelEvent event) {
		return false;
	}
}

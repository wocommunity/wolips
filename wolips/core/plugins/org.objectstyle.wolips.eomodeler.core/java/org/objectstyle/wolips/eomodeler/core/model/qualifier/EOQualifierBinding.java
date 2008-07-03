package org.objectstyle.wolips.eomodeler.core.model.qualifier;

import org.objectstyle.wolips.eomodeler.core.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.ISortableEOModelObject;

/**
 * EOQualifierBindings represents the binding of a $qualifierVariable to a
 * particular keypath of an entity.
 * 
 * @author mschrag
 */
public class EOQualifierBinding implements ISortableEOModelObject {
	private EOEntity _entity;

	private String _name;

	private String _keyPath;

	public EOQualifierBinding(EOEntity entity, String name, String keyPath) {
		_entity = entity;
		_name = name;
		_keyPath = keyPath;
	}

	public EOEntity getEntity() {
		return _entity;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getKeyPath() {
		return _keyPath;
	}

	public AbstractEOAttributePath getAttributePath() {
		return _entity.resolveKeyPath(_keyPath);
	}
	
	public String getJavaClassName() {
		AbstractEOAttributePath attributePath = getAttributePath();
		if (attributePath == null) {
			throw new IllegalStateException("The binding name '" + _name + "' refers to a keypath '" + _keyPath + " that could not be resolved on the entity '" + _entity.getName() + "'.");
		}
		return attributePath.getChildClassName();
	}

	public String toString() {
		return "[EOQualifierBinding: " + _keyPath + " = $" + _name + "]";
	}
}

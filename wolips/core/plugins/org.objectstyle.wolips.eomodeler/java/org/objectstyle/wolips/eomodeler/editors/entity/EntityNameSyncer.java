package org.objectstyle.wolips.eomodeler.editors.entity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;

public class EntityNameSyncer implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent _evt) {
		EOEntity entity = (EOEntity) _evt.getSource();
		String propertyName = _evt.getPropertyName();
		if (EOEntity.NAME.equals(propertyName)) {
			String oldName = (String) _evt.getOldValue();
			String newName = (String) _evt.getNewValue();
			if (ComparisonUtils.equals(oldName, entity.getExternalName(), true)) {
				entity.setExternalName(newName);
			}
			String className = entity.getClassName();
			if (ComparisonUtils.equals(oldName, className, true)) {
				entity.setClassName(newName);
			} else if (className != null && className.endsWith("." + oldName)) {
				String oldPackage = className.substring(0, className.lastIndexOf('.') + 1);
				entity.setClassName(oldPackage + newName);
			}
		}
	}

}

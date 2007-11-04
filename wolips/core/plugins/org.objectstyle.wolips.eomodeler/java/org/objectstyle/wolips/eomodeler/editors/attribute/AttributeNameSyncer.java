package org.objectstyle.wolips.eomodeler.editors.attribute;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;

public class AttributeNameSyncer implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent _evt) {
		AbstractEOArgument argument = (AbstractEOArgument) _evt.getSource();
		if (argument instanceof EOAttribute) {
			EOAttribute attribute = (EOAttribute) argument;
			String propertyName = _evt.getPropertyName();
			if (AbstractEOArgument.NAME.equals(propertyName)) {
				String oldName = (String) _evt.getOldValue();
				if (ComparisonUtils.equals(oldName, attribute.getColumnName(), true)) {
					String newName = (String) _evt.getNewValue();
					attribute.setColumnName(newName);
				}
			}
		}
	}

}

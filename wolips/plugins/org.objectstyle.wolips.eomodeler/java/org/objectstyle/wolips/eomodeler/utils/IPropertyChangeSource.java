package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeListener;

public interface IPropertyChangeSource {
	public void addPropertyChangeListener(PropertyChangeListener _listener);

	public void addPropertyChangeListener(String _propertyName, PropertyChangeListener _listener);

	public void removePropertyChangeListener(PropertyChangeListener _listener);

	public void removePropertyChangeListener(String _propertyName, PropertyChangeListener _listener);

}

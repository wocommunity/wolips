package org.objectstyle.wolips.eomodeler.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.IAdaptable;

public class EOModelObject implements IAdaptable {
  private PropertyChangeSupport myPropertyChangeSupport = new PropertyChangeSupport(this);

  public EOModelObject() {
    myPropertyChangeSupport = new PropertyChangeSupport(this);
  }

  public void addPropertyChangeListener(PropertyChangeListener _listener) {
    myPropertyChangeSupport.addPropertyChangeListener(_listener);
  }

  public void addPropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
    myPropertyChangeSupport.addPropertyChangeListener(_propertyName, _listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener _listener) {
    myPropertyChangeSupport.removePropertyChangeListener(_listener);
  }

  public void removePropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
    myPropertyChangeSupport.removePropertyChangeListener(_propertyName, _listener);
  }

  protected void firePropertyChange(String _propertyName, Object _oldValue, Object _newValue) {
    myPropertyChangeSupport.firePropertyChange(_propertyName, _oldValue, _newValue);
  }

  protected void firePropertyChange(String _propertyName, int _oldValue, int _newValue) {
    myPropertyChangeSupport.firePropertyChange(_propertyName, _oldValue, _newValue);
  }

  protected void firePropertyChange(String _propertyName, boolean _oldValue, boolean _newValue) {
    myPropertyChangeSupport.firePropertyChange(_propertyName, _oldValue, _newValue);
  }

  public Object getAdapter(Class _adapter) {
    return null;
  }

}

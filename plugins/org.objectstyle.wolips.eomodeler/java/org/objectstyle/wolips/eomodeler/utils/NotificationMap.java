package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NotificationMap implements Map {
  public static final String CONTENTS = "__contents__"; //$NON-NLS-1$

  private Map myMap;
  private PropertyChangeSupport myPropertyChangeSupport;

  public NotificationMap() {
    this(null);
  }

  public NotificationMap(Map _map) {
    if (_map == null) {
      myMap = new HashMap();
    }
    else {
      myMap = _map;
    }
    myPropertyChangeSupport = new PropertyChangeSupport(this);
  }

  public void clear() {
    myMap.clear();
    myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
  }

  public boolean containsKey(Object _key) {
    return myMap.containsKey(_key);
  }

  public boolean containsValue(Object _value) {
    return myMap.containsValue(_value);
  }

  public Set entrySet() {
    return myMap.entrySet();
  }

  public boolean equals(Object _o) {
    return myMap.equals(_o);
  }

  public Object get(Object _key) {
    return myMap.get(_key);
  }

  public int hashCode() {
    return myMap.hashCode();
  }

  public boolean isEmpty() {
    return myMap.isEmpty();
  }

  public Set keySet() {
    return myMap.keySet();
  }

  public Object put(Object _key, Object _value) {
    Object oldValue = myMap.put(_key, _value);
    if (_key instanceof String) {
      myPropertyChangeSupport.firePropertyChange((String) _key, oldValue, _value);
      if (oldValue == null) {
        myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
      }
    }
    else {
      myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
    }
    return oldValue;
  }

  public void putAll(Map _t) {
    myMap.putAll(_t);
    myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
  }

  public Object remove(Object _key) {
    Object oldValue = myMap.remove(_key);
    if (_key instanceof String) {
      myPropertyChangeSupport.firePropertyChange((String) _key, oldValue, null);
      myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
    }
    else {
      myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
    }
    return oldValue;
  }

  public int size() {
    return myMap.size();
  }

  public Collection values() {
    return myMap.values();
  }

  public void addPropertyChangeListener(PropertyChangeListener _listener) {
    myPropertyChangeSupport.addPropertyChangeListener(_listener);
  }

  public void addPropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
    myPropertyChangeSupport.addPropertyChangeListener(_propertyName, _listener);
  }

  public PropertyChangeListener[] getPropertyChangeListeners() {
    return myPropertyChangeSupport.getPropertyChangeListeners();
  }

  public PropertyChangeListener[] getPropertyChangeListeners(String _propertyName) {
    return myPropertyChangeSupport.getPropertyChangeListeners(_propertyName);
  }

  public boolean hasListeners(String _propertyName) {
    return myPropertyChangeSupport.hasListeners(_propertyName);
  }

  public void removePropertyChangeListener(PropertyChangeListener _listener) {
    myPropertyChangeSupport.removePropertyChangeListener(_listener);
  }

  public void removePropertyChangeListener(String _propertyName, PropertyChangeListener _listener) {
    myPropertyChangeSupport.removePropertyChangeListener(_propertyName, _listener);
  }

  public String toString() {
    return myMap.toString();
  }
}

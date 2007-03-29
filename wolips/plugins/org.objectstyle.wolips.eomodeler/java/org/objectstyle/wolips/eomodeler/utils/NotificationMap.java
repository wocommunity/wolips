package org.objectstyle.wolips.eomodeler.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NotificationMap<U, V> implements Map<U, V> {
	public static final String CONTENTS = "__contents__";

	private Map<U, V> myMap;

	private PropertyChangeSupport myPropertyChangeSupport;

	public NotificationMap() {
		this(null);
	}

	public NotificationMap(Map<U, V> _map) {
		if (_map == null) {
			myMap = new HashMap<U, V>();
		} else {
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

	public Set<Map.Entry<U, V>> entrySet() {
		return myMap.entrySet();
	}

	public boolean equals(Object _o) {
		return myMap.equals(_o);
	}

	public V get(Object _key) {
		return myMap.get(_key);
	}

	public int hashCode() {
		return myMap.hashCode();
	}

	public boolean isEmpty() {
		return myMap.isEmpty();
	}

	public Set<U> keySet() {
		return myMap.keySet();
	}

	public V put(U _key, V _value) {
		V oldValue = myMap.put(_key, _value);
		if (_key instanceof String) {
			myPropertyChangeSupport.firePropertyChange((String) _key, oldValue, _value);
			if (oldValue == null) {
				myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
			}
		} else {
			myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
		}
		return oldValue;
	}

	public void putAll(Map<? extends U, ? extends V> _t) {
		myMap.putAll(_t);
		myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
	}

	public V remove(Object _key) {
		V oldValue = myMap.remove(_key);
		if (_key instanceof String) {
			myPropertyChangeSupport.firePropertyChange((String) _key, oldValue, null);
			myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
		} else {
			myPropertyChangeSupport.firePropertyChange(NotificationMap.CONTENTS, null, null);
		}
		return oldValue;
	}

	public int size() {
		return myMap.size();
	}

	public Collection<V> values() {
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

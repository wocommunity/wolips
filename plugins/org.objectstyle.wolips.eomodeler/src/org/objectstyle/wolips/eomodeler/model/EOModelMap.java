package org.objectstyle.wolips.eomodeler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EOModelMap implements Map {
  private Map myBackingMap;

  public EOModelMap() {
    this(new HashMap());
  }

  public EOModelMap(Map _backingMap) {
    myBackingMap = _backingMap;
  }

  public EOModelMap cloneModelMap() {
    return new EOModelMap(new HashMap(myBackingMap));
  }

  public Map getBackingMap() {
    return myBackingMap;
  }

  public void setBoolean(String _key, Boolean _value) {
    if (_value == null) {
      myBackingMap.remove(_value);
    }
    else {
      myBackingMap.put(_key, _value.booleanValue() ? "Y" : "N");
    }
  }

  public Boolean getBoolean(String _key) {
    Object value = get(_key);
    Boolean boolValue;
    if (value == null) {
      boolValue = null;
    }
    else if (value instanceof Boolean) {
      boolValue = (Boolean) value;
    }
    else if (value instanceof String) {
      String strValue = getString(_key, true);
      if (strValue != null) {
        boolValue = Boolean.valueOf("y".equalsIgnoreCase(strValue) || "yes".equalsIgnoreCase(strValue) || "true".equalsIgnoreCase(strValue) || "on".equalsIgnoreCase(strValue));
      }
      else {
        boolValue = null;
      }
    }
    else {
      throw new IllegalArgumentException("Unknown boolean value '" + value + "' for '" + _key + "'");
    }
    return boolValue;
  }

  public void setMap(String _key, Map _map) {
    if (_map != null) {
      myBackingMap.put(_key, _map);
    }
    else {
      myBackingMap.remove(_key);
    }
  }

  public Map getMap(String _key) {
    return (Map) myBackingMap.get(_key);
  }

  public Map getMap(String _key, boolean _clone) {
    Map map = (Map) myBackingMap.get(_key);
    if (map != null) {
      map = new HashMap(map);
    }
    return map;
  }

  public void setList(String _key, List _list) {
    if (_list == null) {
      myBackingMap.remove(_key);
    }
    else {
      myBackingMap.put(_key, _list);
    }
  }

  public List getList(String _key) {
    return (List) myBackingMap.get(_key);
  }

  public List getList(String _key, boolean _clone) {
    List list = (List) myBackingMap.get(_key);
    if (list != null) {
      list = new LinkedList(list);
    }
    return list;
  }

  public void setString(String _key, String _value, boolean _emptyIsNull) {
    if (_value != null && (!_emptyIsNull || _value.trim().length() > 0)) {
      myBackingMap.put(_key, _value);
    }
    else {
      myBackingMap.remove(_key);
    }
  }

  public String getString(String _key, boolean _emptyIsNull) {
    String value = (String) myBackingMap.get(_key);
    if (_emptyIsNull && value != null && value.trim().length() == 0) {
      value = null;
    }
    return value;
  }

  public void setInteger(String _key, Integer _value) {
    if (_value == null) {
      myBackingMap.remove(_key);
    }
    else {
      myBackingMap.put(_key, _value);
    }
  }

  public Integer getInteger(String _key) {
    Object value = get(_key);
    Integer integerValue;
    if (value == null) {
      integerValue = null;
    }
    else if (value instanceof Integer) {
      integerValue = (Integer) value;
    }
    else if (value instanceof String) {
      String strValue = getString(_key, true);
      integerValue = Integer.valueOf(strValue);
    }
    else {
      throw new IllegalArgumentException("Unknown integer value '" + value + "' for '" + _key + "'");
    }
    return integerValue;
  }

  public void clear() {
    myBackingMap.clear();
  }

  public boolean containsKey(Object _key) {
    return myBackingMap.containsKey(_key);
  }

  public boolean containsValue(Object _value) {
    return myBackingMap.containsValue(_value);
  }

  public Set entrySet() {
    return myBackingMap.entrySet();
  }

  public boolean equals(Object _o) {
    return myBackingMap.equals(_o);
  }

  public Object get(Object _key) {
    return myBackingMap.get(_key);
  }

  public int hashCode() {
    return myBackingMap.hashCode();
  }

  public boolean isEmpty() {
    return myBackingMap.isEmpty();
  }

  public Set keySet() {
    return myBackingMap.keySet();
  }

  public Object put(Object arg0, Object arg1) {
    return myBackingMap.put(arg0, arg1);
  }

  public void putAll(Map arg0) {
    myBackingMap.putAll(arg0);
  }

  public Object remove(Object _key) {
    return myBackingMap.remove(_key);
  }

  public int size() {
    return myBackingMap.size();
  }

  public Collection values() {
    return myBackingMap.values();
  }

}

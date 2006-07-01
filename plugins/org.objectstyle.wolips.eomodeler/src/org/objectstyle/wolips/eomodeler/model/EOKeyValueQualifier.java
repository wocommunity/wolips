package org.objectstyle.wolips.eomodeler.model;

import java.util.List;
import java.util.Map;

public class EOKeyValueQualifier implements IEOQualifier {
  private String myKey;
  private Object myValue;
  private String mySelectorName;

  public EOKeyValueQualifier() {
  }

  public String getKey() {
    return myKey;
  }

  public void setKey(String _key) {
    myKey = _key;
  }

  public String getSelectorName() {
    return mySelectorName;
  }

  public void setSelectorName(String _selectorName) {
    mySelectorName = _selectorName;
  }

  public Object getValue() {
    return myValue;
  }

  public void setValue(Object _value) {
    myValue = _value;
  }

  public void loadFromMap(EOModelMap _map) {
    myKey = _map.getString("key", true);
    mySelectorName = _map.getString("selectorName", true);
    Object value = _map.get("value");
    if (value instanceof Map) {
      Map valueMap = (Map) value;
      String clazz = (String) valueMap.get("class");
      if ("EONull".equals(clazz)) {
        myValue = null;
      }
      else {
        throw new IllegalArgumentException("Unknown class " + clazz);
      }
    }
    else {
      myValue = value;
    }
  }

  public EOModelMap toMap() {
    EOModelMap qualifierMap = new EOModelMap();
    qualifierMap.setString("class", "EOKeyValueQualifier", true);
    qualifierMap.setString("key", myKey, true);
    qualifierMap.setString("selectorName", mySelectorName, true);
    if (myValue == null) {
      EOModelMap nullMap = new EOModelMap();
      nullMap.setString("class", "EONull", true);
      qualifierMap.setMap("value", nullMap);
    }
    else {
      qualifierMap.put("value", myValue);
    }
    return qualifierMap;
  }

  public void verify(List _failures) {
    // TODO
  }
}

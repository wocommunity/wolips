package org.objectstyle.wolips.eomodeler.model;

import java.util.List;

public class EOSortOrdering {
  private String myKey;
  private String mySelectorName;

  public EOSortOrdering() {
  }

  public void setKey(String _key) {
    myKey = _key;
  }

  public String getKey() {
    return myKey;
  }

  public void setSelectorName(String _selectorName) {
    mySelectorName = _selectorName;
  }

  public String getSelectorName() {
    return mySelectorName;
  }

  public void loadFromMap(EOModelMap _map) {
    myKey = _map.getString("key", true);
    mySelectorName = _map.getString("selectorName", true);
  }

  public EOModelMap toMap() {
    EOModelMap sortOrderingMap = new EOModelMap();
    sortOrderingMap.setString("class", "EOSortOrdering", true);
    sortOrderingMap.setString("key", myKey, true);
    sortOrderingMap.setString("selectorName", mySelectorName, true);
    return sortOrderingMap;
  }
  
  public void verify(List _failures) {
    // TODO
  }
}

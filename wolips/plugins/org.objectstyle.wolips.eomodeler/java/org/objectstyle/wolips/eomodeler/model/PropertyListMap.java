package org.objectstyle.wolips.eomodeler.model;

import java.util.Map;
import java.util.TreeMap;

public class PropertyListMap extends TreeMap {
  public PropertyListMap() {
    super(PropertyListComparator.AscendingPropertyListComparator);
  }

  public PropertyListMap(Map _map) {
    this();
    putAll(_map);
  }
}

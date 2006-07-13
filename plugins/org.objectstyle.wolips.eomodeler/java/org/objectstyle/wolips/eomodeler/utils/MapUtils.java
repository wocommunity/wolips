package org.objectstyle.wolips.eomodeler.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtils {
  public static Map toStringMap(Map _map) {
    Map strMap;
    if (_map == null) {
      strMap = null;
    }
    else {
      strMap = new HashMap();
      Iterator entrySetIter = _map.entrySet().iterator();
      while (entrySetIter.hasNext()) {
        Map.Entry entry = (Map.Entry) entrySetIter.next();
        Object key = entry.getKey();
        String strKey = null;
        if (key != null) {
          strKey = key.toString();
        }
        Object value = entry.getValue();
        strMap.put(strKey, value);
      }
    }
    return strMap;
  }
}

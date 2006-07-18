package org.objectstyle.wolips.eomodeler.model;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PropertyListComparator implements Comparator {

  public static final PropertyListComparator AscendingPropertyListComparator = new PropertyListComparator();

  public int compare(Object arg0, Object arg1) {
    if (arg0 == null) {
      return (arg1 == null) ? 0 : -1;
    }
    else if (arg1 == null) {
      return 1;
    }
    else if (arg0 instanceof String && arg1 instanceof String) {
      return ((String) arg0).compareTo(arg1);
    }
    else if (arg0 instanceof Number && arg1 instanceof Number) {
      double d0 = ((Number) arg0).doubleValue();
      double d1 = ((Number) arg1).doubleValue();
      if (d0 > d1) {
        return 1;
      }
      else if (d0 < d1) {
        return -1;
      }
      return 0;
    }
    else if (arg0 instanceof Timestamp && arg1 instanceof Timestamp) {
      return ((Timestamp) arg0).compareTo(arg1);
    }
    else if (arg0 instanceof Map && arg1 instanceof Map) {
      Map dic0 = (Map) arg0;
      Map dic1 = (Map) arg1;
      Object key0 = dic0.get("name"); //$NON-NLS-1$
      Object key1 = dic1.get("name"); //$NON-NLS-1$
      if (key0 != null && key1 != null) {
        return compare(key0, key1);
      }
      else if (key0 != key1) {
        throw new IllegalArgumentException("no 'name' key for either: " + arg0 + " or " + arg1);
      }
      // if no "name" keys are present, compare the keys and values
      Set allKeys0 = dic0.keySet();
      Set allKeys1 = dic1.keySet();
      Iterator allKeys0Iter = allKeys0.iterator();
      Iterator allKeys1Iter = allKeys1.iterator();
      while (allKeys0Iter.hasNext() && allKeys1Iter.hasNext()) {
        key0 = allKeys0Iter.next();
        key1 = allKeys1Iter.next();
        int compareResult = compare(key0, key1);
        if (compareResult != 0) {
          return compareResult;
        }
        compareResult = compare(dic0.get(key0), dic1.get(key1));
        if (compareResult != 0) {
          return compareResult;
        }
      }
      return compare(new Integer(allKeys0.size()), new Integer(allKeys1.size()));
    }
    else {
      throw new IllegalArgumentException("unhandled classes '" + arg0.getClass().getName() + "' and '" + arg1.getClass().getName() + "'");
    }
  }

}

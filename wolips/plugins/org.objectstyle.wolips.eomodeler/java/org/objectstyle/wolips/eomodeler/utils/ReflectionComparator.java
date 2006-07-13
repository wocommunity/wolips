package org.objectstyle.wolips.eomodeler.utils;

import java.lang.reflect.Method;
import java.util.Comparator;

public class ReflectionComparator implements Comparator {
  private Method myComparisonMethod;

  public ReflectionComparator(Class _class, String _methodName) {
    try {
      myComparisonMethod = _class.getMethod(_methodName, null);
    }
    catch (Throwable t) {
      throw new RuntimeException("Unable to compare " + _methodName + " of " + _class.getName() + ".", t);
    }
  }

  public int compare(Object _o1, Object _o2) {
    try {
      Object value1 = null;
      if (_o1 != null) {
        value1 = myComparisonMethod.invoke(_o1, null);
      }
      Object value2 = null;
      if (_o2 != null) {
        value2 = myComparisonMethod.invoke(_o2, null);
      }
      int results;
      if (value1 == null) {
        if (value2 == null) {
          results = 0;
        }
        else if (value2 instanceof Comparable) {
          results = ((Comparable) value2).compareTo(value2);
        }
        else {
          throw new IllegalArgumentException(myComparisonMethod.getName() + " did not return a comparable value from " + _o2);
        }
      }
      else if (value1 instanceof Comparable) {
        results = ((Comparable) value1).compareTo(value2);
      }
      else {
        throw new IllegalArgumentException(myComparisonMethod.getName() + " did not return a comparable value from " + _o1);
      }
      return results;
    }
    catch (Throwable t) {
      throw new RuntimeException("Failed to retrieve value of " + myComparisonMethod.getName() + " on " + _o1 + " or " + _o2 + ".", t);
    }
  }

}

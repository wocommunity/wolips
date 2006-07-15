package org.objectstyle.wolips.eomodeler.utils;

public class ComparisonUtils {
  public static boolean equals(Object _o1, Object _o2) {
    boolean equals;
    if (_o1 == null) {
      equals = (_o2 == null);
    }
    else {
      equals = (_o1 == _o2);
      if (!equals) {
        equals = _o1.equals(_o2);
      }
    }
    return equals;
  }
}

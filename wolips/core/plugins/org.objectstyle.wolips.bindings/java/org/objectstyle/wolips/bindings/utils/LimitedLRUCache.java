package org.objectstyle.wolips.bindings.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LimitedLRUCache<U, V> extends LinkedHashMap<U, V> {
  private int _maxSize;

  public LimitedLRUCache(int maxSize) {
    super(12, 0.75f, true);
    _maxSize = maxSize;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<U, V> eldest) {
    return size() > _maxSize;
  }
}

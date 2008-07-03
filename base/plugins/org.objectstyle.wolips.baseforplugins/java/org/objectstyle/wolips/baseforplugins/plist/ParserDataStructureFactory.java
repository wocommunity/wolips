package org.objectstyle.wolips.baseforplugins.plist;

import java.util.Collection;
import java.util.Map;

public interface ParserDataStructureFactory {
  public Map<Object, Object> createMap(String keyPath);
  
  public Collection<Object> createCollection(String keyPath);
}

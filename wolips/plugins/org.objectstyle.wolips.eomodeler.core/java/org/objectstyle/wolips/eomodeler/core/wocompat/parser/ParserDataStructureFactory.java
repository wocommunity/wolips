package org.objectstyle.wolips.eomodeler.core.wocompat.parser;

import java.util.Collection;
import java.util.Map;

public interface ParserDataStructureFactory {
  public Map<Object, Object> createMap(String keyPath);
  
  public Collection<Object> createCollection(String keyPath);
}

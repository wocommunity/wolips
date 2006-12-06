package org.objectstyle.wolips.eomodeler.wocompat.parser;

import java.util.Collection;
import java.util.Map;

public interface ParserDataStructureFactory {
  public Map createMap(String keyPath);
  
  public Collection createCollection(String keyPath);
}

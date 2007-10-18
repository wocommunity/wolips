package org.objectstyle.wolips.eomodeler.core.wocompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleParserDataStructureFactory implements ParserDataStructureFactory {
  public Collection<Object> createCollection(String keyPath) {
    return new ArrayList<Object>();
  }

  public Map<Object, Object> createMap(String keyPath) {
    return new HashMap<Object, Object>();
  }
}

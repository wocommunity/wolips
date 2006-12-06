package org.objectstyle.wolips.eomodeler.wocompat.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleParserDataStructureFactory implements ParserDataStructureFactory {
  public Collection createCollection(String keyPath) {
    return new ArrayList();
  }

  public Map createMap(String keyPath) {
    return new HashMap();
  }
}

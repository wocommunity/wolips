package org.objectstyle.wolips.eomodeler.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.objectstyle.cayenne.wocompat.parser.ParserDataStructureFactory;

public class EOModelParserDataStructureFactory implements ParserDataStructureFactory {

  public Collection createCollection(String _keyPath) {
    return new LinkedList();
  }

  public Map createMap(String _keyPath) {
    return new TreeMap(PropertyListComparator.AscendingPropertyListComparator);
  }

}

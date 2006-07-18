package org.objectstyle.wolips.eomodeler.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.objectstyle.cayenne.wocompat.parser.ParserDataStructureFactory;

public class EOModelParserDataStructureFactory implements ParserDataStructureFactory {

  public Collection createCollection(String _keyPath) {
    boolean createSortedSet = false;
    if ("root.attributes".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.attributesUsedForLocking".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.classProperties".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.entities".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.sharedObjectFetchSpecificationNames".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.internalInfo._clientClassPropertyNames".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.internalInfo._deletedEntityNamesInObjectStore".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.primaryKeyAttributes".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.relationships".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if ("root.relationships.joins".equals(_keyPath)) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if (_keyPath.startsWith("root.connectionDictionary.jdbc2Info.typeInfo.") && _keyPath.endsWith(".defaultJDBCType")) { //$NON-NLS-1$ //$NON-NLS-2$
      createSortedSet = true;
    }
    else if (_keyPath.endsWith(".prefetchingRelationshipKeyPaths")) { //$NON-NLS-1$
      createSortedSet = true;
    }
    else if (_keyPath.endsWith(".rawRowKeyPaths")) { //$NON-NLS-1$
      createSortedSet = true;
    }
    Collection collection;
    if (createSortedSet) {
      collection = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    }
    else {
      collection = new LinkedList();
    }
    return collection;
  }

  public Map createMap(String _keyPath) {
    return new TreeMap(PropertyListComparator.AscendingPropertyListComparator);
  }

}

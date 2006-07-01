package org.objectstyle.wolips.eomodeler.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EOFetchSpecification {
  private EOEntity myEntity;
  private String myName;
  private String myClass;
  private Integer myFetchLimit;
  private Boolean myDeep;
  private Boolean myLocksObjects;
  private List myPrefetchingRelationshipKeyPaths;
  private Boolean myPromptsAfterFetchLimit;
  private List myRawRowKeyPaths;
  private Boolean myRefreshesRefetchedObjects;
  private Boolean myRequiresAllQualifierBindingVariables;
  private Boolean myUsesDistinct;
  private List mySortOrderings;
  private IEOQualifier myQualifier;
  private EOModelMap myFetchSpecMap;

  public EOFetchSpecification(EOEntity _entity, String _name) {
    myEntity = _entity;
    myName = _name;
    myClass = "EOFetchSpecification";
    mySortOrderings = new LinkedList();
  }

  public void setName(String _name) {
    myName = _name;
  }

  public String getName() {
    return myName;
  }

  public void addSortOrdering(EOSortOrdering _sortOrdering) {
    mySortOrderings.add(_sortOrdering);
  }

  public void removeSortOrdering(EOSortOrdering _sortOrdering) {
    mySortOrderings.remove(_sortOrdering);
  }

  public void clearSortOrderings() {
    mySortOrderings.clear();
  }

  public List getSortOrderings() {
    return mySortOrderings;
  }

  public void setQualifier(IEOQualifier _qualifier) {
    myQualifier = _qualifier;
  }

  public IEOQualifier getQualifier() {
    return myQualifier;
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public void loadFromMap(EOModelMap _map) {
    myFetchSpecMap = _map;
    // "entityName" = myEntity
    myClass = _map.getString("class", true);
    myFetchLimit = _map.getInteger("fetchLimit");
    myDeep = _map.getBoolean("isDeep");
    myLocksObjects = _map.getBoolean("locksObjects");
    myPrefetchingRelationshipKeyPaths = _map.getList("prefetchingRelationshipKeyPaths", true);
    myPromptsAfterFetchLimit = _map.getBoolean("prompsAfterFetchLimit");

    Map qualifierMap = _map.getMap("qualifier");
    if (qualifierMap != null) {
      myQualifier = EOQualifierFactory.qualifierForMap(new EOModelMap(qualifierMap));
    }
    myRawRowKeyPaths = _map.getList("rawRowKeyPaths", true);
    myRefreshesRefetchedObjects = _map.getBoolean("refreshesRefetchedObjects");
    myRequiresAllQualifierBindingVariables = _map.getBoolean("requiresAllQualifierBindingVariables");
    myUsesDistinct = _map.getBoolean("usesDistinct");

    List sortOrderings = _map.getList("sortOrderings");
    if (sortOrderings != null) {
      Iterator sortOrderingsIter = sortOrderings.iterator();
      while (sortOrderingsIter.hasNext()) {
        EOModelMap sortOrderingMap = new EOModelMap((Map) sortOrderingsIter.next());
        EOSortOrdering sortOrdering = new EOSortOrdering();
        sortOrdering.loadFromMap(sortOrderingMap);
        addSortOrdering(sortOrdering);
      }
    }
  }

  public EOModelMap toMap() {
    EOModelMap fetchSpecMap = myFetchSpecMap.cloneModelMap();
    fetchSpecMap.setString("entityName", myEntity.getName(), true);
    fetchSpecMap.setString("class", myClass, true);
    fetchSpecMap.setInteger("fetchLimit", myFetchLimit);
    fetchSpecMap.setBoolean("isDeep", myDeep);
    fetchSpecMap.setBoolean("locksObjects", myLocksObjects);
    fetchSpecMap.setList("prefetchingRelationshipKeyPaths", myPrefetchingRelationshipKeyPaths);
    fetchSpecMap.setBoolean("prompsAfterFetchLimit", myPromptsAfterFetchLimit);
    if (myQualifier == null) {
      fetchSpecMap.setMap("qualifier", null);
    }
    else {
      fetchSpecMap.setMap("qualifier", myQualifier.toMap());
    }
    fetchSpecMap.setList("rawRowKeyPaths", myRawRowKeyPaths);
    fetchSpecMap.setBoolean("refreshesRefetchedObjects", myRefreshesRefetchedObjects);
    fetchSpecMap.setBoolean("requiresAllQualifierBindingVariables", myRequiresAllQualifierBindingVariables);
    fetchSpecMap.setBoolean("usesDistinct", myUsesDistinct);

    List sortOrderings = new LinkedList();
    Iterator sortOrderingsIter = mySortOrderings.iterator();
    while (sortOrderingsIter.hasNext()) {
      EOSortOrdering sortOrdering = (EOSortOrdering) sortOrderingsIter.next();
      EOModelMap sortOrderingMap = sortOrdering.toMap();
      sortOrderings.add(sortOrderingMap);
    }
    fetchSpecMap.setList("sortOrderings", sortOrderings);
    return fetchSpecMap;
  }

  public void verify(List _failures) {
    // TODO
    if (myQualifier != null) {
      myQualifier.verify(_failures);
    }
    Iterator sortOrderingsIter = mySortOrderings.iterator();
    while (sortOrderingsIter.hasNext()) {
      EOSortOrdering sortOrdering = (EOSortOrdering) sortOrderingsIter.next();
      sortOrdering.verify(_failures);
    }
  }
}

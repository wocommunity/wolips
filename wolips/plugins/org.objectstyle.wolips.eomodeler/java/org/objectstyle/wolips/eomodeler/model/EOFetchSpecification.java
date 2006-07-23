/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.objectstyle.cayenne.exp.parser.Node;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public class EOFetchSpecification extends UserInfoableEOModelObject implements IEOEntityRelative, ISortableEOModelObject {
  public static final String NAME = "name";
  public static final String SORT_ORDERINGS = "sortOrderings";
  public static final String QUALIFIER = "qualifier";
  public static final String ENTITY = "entity";
  public static final String FETCH_LIMIT = "fetchLimit";
  public static final String DEEP = "deep";
  public static final String LOCKS_OBJECTS = "locksObjects";
  public static final String PREFETCHING_RELATIONSHIP_KEY_PATHS = "prefetchingRelationshipKeyPaths";
  public static final String PROMPTS_AFTER_FETCH_LIMIT = "promptsAfterFetchLimit";
  public static final String RAW_ROW_KEY_PATHS = "rawRowKeyPaths";
  public static final String REFRESHES_REFETCHED_OBJECTS = "refreshesRefetchedObjects";
  public static final String REQUIRES_ALL_QUALIFIER_BINDING_VARIABLES = "requiresAllQualifierBindingVariables";
  public static final String USES_DISTINCT = "usesDistinct";
  public static final String SHARES_OBJECTS = "sharesObjects";

  private EOEntity myEntity;
  private String myName;
  private String myClass;
  private Integer myFetchLimit;
  private Boolean myDeep;
  private Boolean myLocksObjects;
  private Set myPrefetchingRelationshipKeyPaths;
  private Boolean myPromptsAfterFetchLimit;
  private Set myRawRowKeyPaths;
  private Boolean myRefreshesRefetchedObjects;
  private Boolean myRequiresAllQualifierBindingVariables;
  private Boolean myUsesDistinct;
  private List mySortOrderings;
  private Node myQualifier;
  private EOModelMap myFetchSpecMap;
  private Boolean mySharesObjects;

  public EOFetchSpecification(String _name) {
    myName = _name;
    myClass = "EOFetchSpecification";
    mySortOrderings = new LinkedList();
    myFetchSpecMap = new EOModelMap();
  }

  public EOFetchSpecification cloneFetchSpecification() {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(myName);
    fetchSpec.myClass = myClass;
    fetchSpec.myFetchLimit = myFetchLimit;
    fetchSpec.myDeep = myDeep;
    fetchSpec.myLocksObjects = myLocksObjects;
    if (myPrefetchingRelationshipKeyPaths != null) {
      fetchSpec.myPrefetchingRelationshipKeyPaths = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
      fetchSpec.myPrefetchingRelationshipKeyPaths.addAll(myPrefetchingRelationshipKeyPaths);
    }
    fetchSpec.myPromptsAfterFetchLimit = myPromptsAfterFetchLimit;
    if (myRawRowKeyPaths != null) {
      fetchSpec.myRawRowKeyPaths = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
      fetchSpec.myRawRowKeyPaths.addAll(myRawRowKeyPaths);
    }
    fetchSpec.myRefreshesRefetchedObjects = myRefreshesRefetchedObjects;
    fetchSpec.myRequiresAllQualifierBindingVariables = myRequiresAllQualifierBindingVariables;
    fetchSpec.myUsesDistinct = myUsesDistinct;
    fetchSpec.mySortOrderings.addAll(mySortOrderings);
    if (myQualifier != null) {
      fetchSpec.myQualifier = EOQualifierFactory.createNodeFromQualifierMap(EOQualifierFactory.createQualifierMapFromNode(myQualifier));
    }
    fetchSpec.mySharesObjects = mySharesObjects;
    return fetchSpec;
  }

  public Set getReferenceFailures() {
    return new HashSet();
  }
  
  public void _setEntity(EOEntity _entity) {
    myEntity = _entity;
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    if (myEntity != null) {
      myEntity._fetchSpecificationChanged(this);
    }
  }

  public int hashCode() {
    return ((myEntity == null) ? 1 : myEntity.hashCode()) * ((myName == null) ? super.hashCode() : myName.hashCode());
  }

  public boolean equals(Object _obj) {
    boolean equals = false;
    if (_obj instanceof EOFetchSpecification) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) _obj;
      equals = (fetchSpec == this) || (ComparisonUtils.equals(fetchSpec.myEntity, myEntity) && ComparisonUtils.equals(fetchSpec.myName, myName));
    }
    return equals;
  }

  public void setName(String _name) throws DuplicateFetchSpecNameException {
    setName(_name, true);
  }

  public void setName(String _name, boolean _fireEvents) throws DuplicateFetchSpecNameException {
    if (myEntity != null) {
      myEntity._checkForDuplicateFetchSpecName(this, _name, null);
    }
    String oldName = myName;
    myName = _name;
    if (_fireEvents) {
      firePropertyChange(EOFetchSpecification.NAME, oldName, myName);
    }
  }

  public String getName() {
    return myName;
  }

  public void addSortOrdering(EOSortOrdering _sortOrdering, boolean _fireEvents) {
    if (_fireEvents) {
      List oldSortOrderings = mySortOrderings;
      mySortOrderings = new LinkedList(mySortOrderings);
      mySortOrderings.add(_sortOrdering);
      firePropertyChange(EOFetchSpecification.SORT_ORDERINGS, oldSortOrderings, mySortOrderings);
    }
    else {
      mySortOrderings.add(_sortOrdering);
    }
  }

  public void removeSortOrdering(EOSortOrdering _sortOrdering, boolean _fireEvents) {
    mySortOrderings.remove(_sortOrdering);
    if (_fireEvents) {
      List oldSortOrderings = mySortOrderings;
      mySortOrderings = new LinkedList(mySortOrderings);
      mySortOrderings.remove(_sortOrdering);
      firePropertyChange(EOFetchSpecification.SORT_ORDERINGS, oldSortOrderings, mySortOrderings);
    }
    else {
      mySortOrderings.remove(_sortOrdering);
    }
  }

  public void setSortOrderings(List _sortOrderings, boolean _fireEvents) {
    if (_fireEvents) {
      List oldSortOrderings = mySortOrderings;
      firePropertyChange(EOFetchSpecification.SORT_ORDERINGS, oldSortOrderings, mySortOrderings);
    }
    else {
      mySortOrderings = _sortOrderings;
    }
  }

  public void clearSortOrderings() {
    mySortOrderings.clear();
    firePropertyChange(EOFetchSpecification.SORT_ORDERINGS, null, null);
  }

  public List getSortOrderings() {
    return mySortOrderings;
  }

  public Boolean getSharesObjects() {
    return isSharesObjects();
  }

  public Boolean isSharesObjects() {
    return mySharesObjects;
  }

  public void setSharesObjects(Boolean _sharesObjects) {
    setSharesObjects(_sharesObjects, true);
  }

  public void setSharesObjects(Boolean _sharesObjects, boolean _fireEvents) {
    Boolean oldSharesObjects = mySharesObjects;
    mySharesObjects = _sharesObjects;
    if (_fireEvents) {
      firePropertyChange(EOFetchSpecification.SHARES_OBJECTS, oldSharesObjects, mySharesObjects);
    }
  }

  public void setQualifier(Node _qualifier) {
    Node oldQualifier = myQualifier;
    myQualifier = _qualifier;
    firePropertyChange(EOFetchSpecification.QUALIFIER, oldQualifier, myQualifier);
  }

  public Node getQualifier() {
    return myQualifier;
  }

  public void setEntity(EOEntity _entity) {
    EOEntity oldEntity = myEntity;
    _setEntity(_entity);
    firePropertyChange(EOFetchSpecification.ENTITY, oldEntity, myEntity);
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public Boolean isDeep() {
    return myDeep;
  }

  public void setDeep(Boolean _deep) {
    Boolean oldDeep = myDeep;
    myDeep = _deep;
    firePropertyChange(EOFetchSpecification.DEEP, oldDeep, myDeep);
  }

  public Integer getFetchLimit() {
    return myFetchLimit;
  }

  public void setFetchLimit(Integer _fetchLimit) {
    Integer oldFetchLimit = myFetchLimit;
    myFetchLimit = _fetchLimit;
    firePropertyChange(EOFetchSpecification.FETCH_LIMIT, oldFetchLimit, myFetchLimit);
  }

  public Boolean isLocksObjects() {
    return myLocksObjects;
  }

  public void setLocksObjects(Boolean _locksObjects) {
    Boolean oldLocksObjects = myLocksObjects;
    myLocksObjects = _locksObjects;
    firePropertyChange(EOFetchSpecification.LOCKS_OBJECTS, oldLocksObjects, myLocksObjects);
  }

  public Collection getPrefetchingRelationshipKeyPaths() {
    return myPrefetchingRelationshipKeyPaths;
  }

  public void setPrefetchingRelationshipKeyPaths(Set _prefetchingRelationshipKeyPaths) {
    myPrefetchingRelationshipKeyPaths = _prefetchingRelationshipKeyPaths;
    firePropertyChange(EOFetchSpecification.PREFETCHING_RELATIONSHIP_KEY_PATHS, null, null);
  }

  public Boolean isPromptsAfterFetchLimit() {
    return myPromptsAfterFetchLimit;
  }

  public void setPromptsAfterFetchLimit(Boolean _promptsAfterFetchLimit) {
    Boolean oldPromptsAfterFetchLimit = myPromptsAfterFetchLimit;
    myPromptsAfterFetchLimit = _promptsAfterFetchLimit;
    firePropertyChange(EOFetchSpecification.PROMPTS_AFTER_FETCH_LIMIT, oldPromptsAfterFetchLimit, myPromptsAfterFetchLimit);
  }

  public Collection getRawRowKeyPaths() {
    return myRawRowKeyPaths;
  }

  public void setRawRowKeyPaths(Set _rawRowKeyPaths) {
    myRawRowKeyPaths = _rawRowKeyPaths;
    firePropertyChange(EOFetchSpecification.RAW_ROW_KEY_PATHS, null, null);
  }

  public Boolean isRefreshesRefetchedObjects() {
    return myRefreshesRefetchedObjects;
  }

  public void setRefreshesRefetchedObjects(Boolean _refreshesRefetchedObjects) {
    Boolean oldRefreshesRefetchedObjects = myRefreshesRefetchedObjects;
    myRefreshesRefetchedObjects = _refreshesRefetchedObjects;
    firePropertyChange(EOFetchSpecification.REFRESHES_REFETCHED_OBJECTS, oldRefreshesRefetchedObjects, myRefreshesRefetchedObjects);
  }

  public Boolean isRequiresAllQualifierBindingVariables() {
    return myRequiresAllQualifierBindingVariables;
  }

  public void setRequiresAllQualifierBindingVariables(Boolean _requiresAllQualifierBindingVariables) {
    Boolean oldRequiresAllQualifierBindingVariables = myRequiresAllQualifierBindingVariables;
    myRequiresAllQualifierBindingVariables = _requiresAllQualifierBindingVariables;
    firePropertyChange(EOFetchSpecification.REQUIRES_ALL_QUALIFIER_BINDING_VARIABLES, oldRequiresAllQualifierBindingVariables, myRequiresAllQualifierBindingVariables);
  }

  public Boolean isUsesDistinct() {
    return myUsesDistinct;
  }

  public void setUsesDistinct(Boolean _usesDistinct) {
    Boolean oldUsesDistinct = myUsesDistinct;
    myUsesDistinct = _usesDistinct;
    firePropertyChange(EOFetchSpecification.USES_DISTINCT, oldUsesDistinct, myUsesDistinct);
  }

  public void loadFromMap(EOModelMap _map, Set _failures) throws EOModelException {
    myFetchSpecMap = _map;
    // "entityName" = myEntity
    myClass = _map.getString("class", true);
    myFetchLimit = _map.getInteger("fetchLimit");
    myDeep = _map.getBoolean("isDeep");
    myLocksObjects = _map.getBoolean("locksObjects");
    myPrefetchingRelationshipKeyPaths = _map.getSet("prefetchingRelationshipKeyPaths", true);
    myPromptsAfterFetchLimit = _map.getBoolean("prompsAfterFetchLimit");

    Map qualifierMap = _map.getMap("qualifier");
    if (qualifierMap != null) {
      myQualifier = EOQualifierFactory.createNodeFromQualifierMap(new EOModelMap(qualifierMap));
    }
    myRawRowKeyPaths = _map.getSet("rawRowKeyPaths", true);
    myRefreshesRefetchedObjects = _map.getBoolean("refreshesRefetchedObjects");
    myRequiresAllQualifierBindingVariables = _map.getBoolean("requiresAllQualifierBindingVariables");
    myUsesDistinct = _map.getBoolean("usesDistinct");
    setUserInfo(_map.getMap("userInfo", true), false);

    List sortOrderings = _map.getList("sortOrderings");
    if (sortOrderings != null) {
      Iterator sortOrderingsIter = sortOrderings.iterator();
      while (sortOrderingsIter.hasNext()) {
        EOModelMap sortOrderingMap = new EOModelMap((Map) sortOrderingsIter.next());
        EOSortOrdering sortOrdering = new EOSortOrdering();
        sortOrdering.loadFromMap(sortOrderingMap);
        addSortOrdering(sortOrdering, false);
      }
    }
  }

  public EOModelMap toMap() {
    EOModelMap fetchSpecMap = myFetchSpecMap.cloneModelMap();
    fetchSpecMap.setString("entityName", myEntity.getName(), true);
    fetchSpecMap.setString("class", myClass, true);
    fetchSpecMap.setInteger("fetchLimit", myFetchLimit);
    fetchSpecMap.setBoolean("isDeep", myDeep, EOModelMap.YESNO);
    fetchSpecMap.setBoolean("locksObjects", myLocksObjects, EOModelMap.YESNO);
    fetchSpecMap.setSet("prefetchingRelationshipKeyPaths", myPrefetchingRelationshipKeyPaths, true);
    fetchSpecMap.setBoolean("prompsAfterFetchLimit", myPromptsAfterFetchLimit, EOModelMap.YESNO);
    if (myQualifier == null) {
      fetchSpecMap.setMap("qualifier", null, true);
    }
    else {
      fetchSpecMap.setMap("qualifier", EOQualifierFactory.createQualifierMapFromNode(myQualifier), true);
    }
    fetchSpecMap.setSet("rawRowKeyPaths", myRawRowKeyPaths, false);
    fetchSpecMap.setBoolean("refreshesRefetchedObjects", myRefreshesRefetchedObjects, EOModelMap.YESNO);
    fetchSpecMap.setBoolean("requiresAllQualifierBindingVariables", myRequiresAllQualifierBindingVariables, EOModelMap.YESNO);
    fetchSpecMap.setBoolean("usesDistinct", myUsesDistinct, EOModelMap.YESNO);
    fetchSpecMap.setMap("userInfo", getUserInfo(), true);

    List sortOrderings = new LinkedList();
    Iterator sortOrderingsIter = mySortOrderings.iterator();
    while (sortOrderingsIter.hasNext()) {
      EOSortOrdering sortOrdering = (EOSortOrdering) sortOrderingsIter.next();
      EOModelMap sortOrderingMap = sortOrdering.toMap();
      sortOrderings.add(sortOrderingMap);
    }
    fetchSpecMap.setList("sortOrderings", sortOrderings, true);
    return fetchSpecMap;
  }

  public void resolve(Set _failures) {
    // TODO
  }

  public void verify(Set _failures) {
    // TODO
    //    if (myQualifier != null) {
    //      myQualifier.verify(_failures);
    //    }
    Iterator sortOrderingsIter = mySortOrderings.iterator();
    while (sortOrderingsIter.hasNext()) {
      EOSortOrdering sortOrdering = (EOSortOrdering) sortOrderingsIter.next();
      sortOrdering.verify(_failures);
    }
  }
}

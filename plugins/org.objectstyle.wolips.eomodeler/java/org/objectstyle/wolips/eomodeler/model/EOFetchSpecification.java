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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.internal.databinding.provisional.observable.list.WritableList;
import org.objectstyle.cayenne.exp.parser.Node;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public class EOFetchSpecification extends UserInfoableEOModelObject implements IEOEntityRelative, ISortableEOModelObject {
  public static final String NAME = "name"; //$NON-NLS-1$
  public static final String SORT_ORDERINGS = "sortOrderings"; //$NON-NLS-1$
  public static final String QUALIFIER = "qualifier"; //$NON-NLS-1$
  public static final String ENTITY = "entity"; //$NON-NLS-1$
  public static final String FETCH_LIMIT = "fetchLimit"; //$NON-NLS-1$
  public static final String DEEP = "deep"; //$NON-NLS-1$
  public static final String LOCKS_OBJECTS = "locksObjects"; //$NON-NLS-1$
  public static final String PREFETCHING_RELATIONSHIP_KEY_PATHS = "prefetchingRelationshipKeyPaths"; //$NON-NLS-1$
  public static final String PROMPTS_AFTER_FETCH_LIMIT = "promptsAfterFetchLimit"; //$NON-NLS-1$
  public static final String RAW_ROW_KEY_PATHS = "rawRowKeyPaths"; //$NON-NLS-1$
  public static final String REFRESHES_REFETCHED_OBJECTS = "refreshesRefetchedObjects"; //$NON-NLS-1$
  public static final String REQUIRES_ALL_QUALIFIER_BINDING_VARIABLES = "requiresAllQualifierBindingVariables"; //$NON-NLS-1$
  public static final String USES_DISTINCT = "usesDistinct"; //$NON-NLS-1$
  public static final String SHARES_OBJECTS = "sharesObjects"; //$NON-NLS-1$

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

  public EOFetchSpecification(EOEntity _entity, String _name) {
    myEntity = _entity;
    myName = _name;
    myClass = "EOFetchSpecification"; //$NON-NLS-1$
    mySortOrderings = new WritableList(EOSortOrdering.class);
    myFetchSpecMap = new EOModelMap();
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    myEntity._fetchSpecificationChanged(this);
  }

  public int hashCode() {
    return myEntity.hashCode() * ((myName == null) ? super.hashCode() : myName.hashCode());
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
    myEntity._checkForDuplicateFetchSpecName(this, _name, null);
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
    mySortOrderings.add(_sortOrdering);
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
    myEntity = _entity;
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
    myClass = _map.getString("class", true); //$NON-NLS-1$
    myFetchLimit = _map.getInteger("fetchLimit"); //$NON-NLS-1$
    myDeep = _map.getBoolean("isDeep"); //$NON-NLS-1$
    myLocksObjects = _map.getBoolean("locksObjects"); //$NON-NLS-1$
    myPrefetchingRelationshipKeyPaths = _map.getSet("prefetchingRelationshipKeyPaths", true); //$NON-NLS-1$
    myPromptsAfterFetchLimit = _map.getBoolean("prompsAfterFetchLimit"); //$NON-NLS-1$

    Map qualifierMap = _map.getMap("qualifier"); //$NON-NLS-1$
    if (qualifierMap != null) {
      myQualifier = EOQualifierFactory.createNodeFromQualifierMap(new EOModelMap(qualifierMap));
    }
    System.out.println("EOFetchSpecification.loadFromMap: qualifier = " + myQualifier);
    myRawRowKeyPaths = _map.getSet("rawRowKeyPaths", true); //$NON-NLS-1$
    myRefreshesRefetchedObjects = _map.getBoolean("refreshesRefetchedObjects"); //$NON-NLS-1$
    myRequiresAllQualifierBindingVariables = _map.getBoolean("requiresAllQualifierBindingVariables"); //$NON-NLS-1$
    myUsesDistinct = _map.getBoolean("usesDistinct"); //$NON-NLS-1$
    setUserInfo(_map.getMap("userInfo", true), false); //$NON-NLS-1$

    List sortOrderings = _map.getList("sortOrderings"); //$NON-NLS-1$
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
    fetchSpecMap.setString("entityName", myEntity.getName(), true); //$NON-NLS-1$
    fetchSpecMap.setString("class", myClass, true); //$NON-NLS-1$
    fetchSpecMap.setInteger("fetchLimit", myFetchLimit); //$NON-NLS-1$
    fetchSpecMap.setBoolean("isDeep", myDeep); //$NON-NLS-1$
    fetchSpecMap.setBoolean("locksObjects", myLocksObjects); //$NON-NLS-1$
    fetchSpecMap.setSet("prefetchingRelationshipKeyPaths", myPrefetchingRelationshipKeyPaths, true); //$NON-NLS-1$
    fetchSpecMap.setBoolean("prompsAfterFetchLimit", myPromptsAfterFetchLimit); //$NON-NLS-1$
    if (myQualifier == null) {
      fetchSpecMap.setMap("qualifier", null, true); //$NON-NLS-1$
    }
    else {
      fetchSpecMap.setMap("qualifier", EOQualifierFactory.createQualifierMapFromNode(myQualifier), true); //$NON-NLS-1$
    }
    fetchSpecMap.setSet("rawRowKeyPaths", myRawRowKeyPaths, true); //$NON-NLS-1$
    fetchSpecMap.setBoolean("refreshesRefetchedObjects", myRefreshesRefetchedObjects); //$NON-NLS-1$
    fetchSpecMap.setBoolean("requiresAllQualifierBindingVariables", myRequiresAllQualifierBindingVariables); //$NON-NLS-1$
    fetchSpecMap.setBoolean("usesDistinct", myUsesDistinct); //$NON-NLS-1$
    fetchSpecMap.setMap("userInfo", getUserInfo(), true); //$NON-NLS-1$

    List sortOrderings = new LinkedList();
    Iterator sortOrderingsIter = mySortOrderings.iterator();
    while (sortOrderingsIter.hasNext()) {
      EOSortOrdering sortOrdering = (EOSortOrdering) sortOrderingsIter.next();
      EOModelMap sortOrderingMap = sortOrdering.toMap();
      sortOrderings.add(sortOrderingMap);
    }
    fetchSpecMap.setList("sortOrderings", sortOrderings, true); //$NON-NLS-1$
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

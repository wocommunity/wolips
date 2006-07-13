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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.internal.databinding.provisional.observable.list.WritableList;
import org.objectstyle.cayenne.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.eomodeler.utils.MapUtils;

public class EOEntity extends UserInfoableEOModelObject implements IEOEntityRelative {
  private static final String FETCH_ALL = "FetchAll";
  public static final String ATTRIBUTE = "attribute"; //$NON-NLS-1$
  public static final String RELATIONSHIP = "relationship"; //$NON-NLS-1$
  public static final String FETCH_SPECIFICATION = "fetchSpecification"; //$NON-NLS-1$
  public static final String NAME = "name"; //$NON-NLS-1$
  public static final String CLASS_NAME = "className"; //$NON-NLS-1$
  public static final String PARENT = "parent"; //$NON-NLS-1$
  public static final String EXTERNAL_QUERY = "externalQuery"; //$NON-NLS-1$
  public static final String MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH = "maxNumberOfInstancesToBatchFetch"; //$NON-NLS-1$
  public static final String READ_ONLY = "readOnly"; //$NON-NLS-1$
  public static final String EXTERNAL_NAME = "externalName"; //$NON-NLS-1$
  public static final String ABSTRACT_ENTITY = "abstractEntity"; //$NON-NLS-1$
  public static final String CACHES_OBJECTS = "cachesObjects"; //$NON-NLS-1$
  public static final String RESTRICTING_QUALIFIER = "restrictingQualifier"; //$NON-NLS-1$
  public static final String FETCH_SPECIFICATIONS = "fetchSpecifications"; //$NON-NLS-1$
  public static final String ATTRIBUTES = "attributes"; //$NON-NLS-1$
  public static final String RELATIONSHIPS = "relationships"; //$NON-NLS-1$

  private EOModel myModel;
  private EOEntity myParent;
  private String myName;
  private String myExternalName;
  private String myClassName;
  private String myRestrictingQualifier;
  private String myExternalQuery;
  private Boolean myCachesObjects;
  private Boolean myAbstractEntity;
  private Boolean myReadOnly;
  private Integer myMaxNumberOfInstancesToBatchFetch;
  private List myAttributes;
  private List myRelationships;
  private List myFetchSpecs;
  private EOModelMap myEntityMap;
  private EOModelMap myFetchSpecsMap;

  public EOEntity(EOModel _model) {
    myModel = _model;
    myAttributes = new WritableList(EOAttribute.class);
    myRelationships = new WritableList(EORelationship.class);
    myFetchSpecs = new WritableList(EOFetchSpecification.class);
    myEntityMap = new EOModelMap();
    myFetchSpecsMap = new EOModelMap();
  }

  public EOEntity(EOModel _model, String _name) {
    this(_model);
    myName = _name;
  }
  
  public EOEntity getEntity() {
    return this;
  }

  public EORelationship addBlankRelationship(String _name) throws DuplicateRelationshipNameException {
    String newRelationshipNameBase = _name;
    String newRelationshipName = newRelationshipNameBase;
    int newRelationshipNum = 0;
    while (getRelationshipNamed(newRelationshipName) != null) {
      newRelationshipNum++;
      newRelationshipName = newRelationshipNameBase + newRelationshipNum;
    }
    EORelationship relationship = new EORelationship(this, newRelationshipName);
    addRelationship(relationship);
    return relationship;
  }

  public EOAttribute addBlankAttribute(String _name) throws DuplicateAttributeNameException {
    String newAttributeNameBase = _name;
    String newAttributeName = newAttributeNameBase;
    int newAttributeNum = 0;
    while (getAttributeNamed(newAttributeName) != null) {
      newAttributeNum++;
      newAttributeName = newAttributeNameBase + newAttributeNum;
    }
    EOAttribute attribute = new EOAttribute(this, newAttributeName);
    addAttribute(attribute);
    return attribute;
  }

  public EOFetchSpecification addBlankFetchSpec(String _name) throws DuplicateFetchSpecNameException {
    String newFetchSpecNameBase = _name;
    String newFetchSpecName = newFetchSpecNameBase;
    int newFetchSpecNum = 0;
    while (getFetchSpecNamed(newFetchSpecName) != null) {
      newFetchSpecNum++;
      newFetchSpecName = newFetchSpecNameBase + newFetchSpecNum;
    }
    EOFetchSpecification fetchSpec = new EOFetchSpecification(this, newFetchSpecName);
    addFetchSpecification(fetchSpec);
    return fetchSpec;
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    myModel._entityChanged(this);
  }

  public boolean hasSharedObjects() {
    boolean hasSharedObjects = false;
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (!hasSharedObjects && fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      hasSharedObjects = fetchSpec.isSharesObjects() != null && fetchSpec.isSharesObjects().booleanValue();
    }
    return hasSharedObjects;
  }

  public void shareNoObjects() {
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      fetchSpec.setSharesObjects(Boolean.FALSE);
    }
  }

  public boolean isSharesAllObjectsOnly() {
    boolean sharesAllObjects = false;
    int sharedFetchSpecCount = 0;
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      if (fetchSpec.isSharesObjects() != null && fetchSpec.isSharesObjects().booleanValue()) {
        sharedFetchSpecCount++;
        if (EOEntity.FETCH_ALL.equals(fetchSpec.getName())) {
          sharesAllObjects = true;
        }
      }
    }
    return sharesAllObjects && sharedFetchSpecCount == 1;
  }

  public void shareAllObjects() throws DuplicateFetchSpecNameException {
    EOFetchSpecification fetchAllFetchSpec = getFetchSpecNamed(EOEntity.FETCH_ALL);
    if (fetchAllFetchSpec != null) {
      fetchAllFetchSpec.setSharesObjects(Boolean.TRUE);
    }
    else {
      fetchAllFetchSpec = new EOFetchSpecification(this, EOEntity.FETCH_ALL);
      fetchAllFetchSpec.setSharesObjects(Boolean.TRUE, false);
      addFetchSpecification(fetchAllFetchSpec);
    }
  }

  public Object getAdapter(Class _adapter) {
    Object adapter = null;
    //    if (_adapter == IPropertySource.class) {
    //      adapter = null; 
    //    }
    return adapter;
  }

  public EOModel getModel() {
    return myModel;
  }

  public boolean isPrototype() {
    return myName != null && myName.startsWith("EO") && myName.endsWith("Prototypes"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public String getExternalQuery() {
    return myExternalQuery;
  }

  public void setExternalQuery(String _externalQuery) {
    String oldExternalQuery = myExternalQuery;
    myExternalQuery = _externalQuery;
    firePropertyChange(EOEntity.EXTERNAL_QUERY, oldExternalQuery, myExternalQuery);
  }

  public Integer getMaxNumberOfInstancesToBatchFetch() {
    return myMaxNumberOfInstancesToBatchFetch;
  }

  public void setMaxNumberOfInstancesToBatchFetch(Integer _maxNumberOfInstancesToBatchFetch) {
    Integer oldMaxNumberOfInstancesToBatchFetch = myMaxNumberOfInstancesToBatchFetch;
    myMaxNumberOfInstancesToBatchFetch = _maxNumberOfInstancesToBatchFetch;
    firePropertyChange(EOEntity.MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH, oldMaxNumberOfInstancesToBatchFetch, myMaxNumberOfInstancesToBatchFetch);
  }

  public Boolean getReadOnly() {
    return isReadOnly();
  }

  public Boolean isReadOnly() {
    return myReadOnly;
  }

  public void setReadOnly(Boolean _readOnly) {
    Boolean oldReadOnly = myReadOnly;
    myReadOnly = _readOnly;
    firePropertyChange(EOEntity.READ_ONLY, oldReadOnly, myReadOnly);
  }

  public String getName() {
    return myName;
  }

  public void setName(String _name) throws DuplicateEntityNameException {
    setName(_name, true);
  }

  public void setName(String _name, boolean _fireEvents) throws DuplicateEntityNameException {
    String oldName = myName;
    myModel._checkForDuplicateEntityName(this, _name, null);
    myModel._entityNameChanged(myName);
    myName = _name;
    if (_fireEvents) {
      firePropertyChange(EOEntity.NAME, oldName, myName);
    }
  }

  public String getClassName() {
    return myClassName;
  }

  public void setClassName(String _className) {
    String oldClassName = myClassName;
    myClassName = _className;
    firePropertyChange(EOEntity.CLASS_NAME, oldClassName, myClassName);
  }

  public String getExternalName() {
    return myExternalName;
  }

  public void setExternalName(String _externalName) {
    String oldExternalName = myExternalName;
    myExternalName = _externalName;
    firePropertyChange(EOEntity.EXTERNAL_NAME, oldExternalName, myExternalName);
  }

  public int hashCode() {
    return myName.hashCode();
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof EOEntity && (_obj == this || ((EOEntity) _obj).myName.equals(myName)));
  }

  public List getReferencingRelationships() {
    List referencingRelationships = new LinkedList();
    Iterator modelsIter = getModel().getModelGroup().getModels().iterator();
    while (modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      Iterator entitiesIter = model.getEntities().iterator();
      while (entitiesIter.hasNext()) {
        EOEntity entity = (EOEntity) entitiesIter.next();
        if (!entity.equals(this)) {
          Iterator relationshipsIter = entity.getRelationships().iterator();
          while (relationshipsIter.hasNext()) {
            EORelationship relationship = (EORelationship) relationshipsIter.next();
            if (relationship.isRelatedTo(this)) {
              referencingRelationships.add(relationship);
            }
          }
        }
      }
    }
    return referencingRelationships;
  }

  public List getChildrenEntities() {
    List children = new LinkedList();
    Iterator modelsIter = myModel.getModelGroup().getModels().iterator();
    while (modelsIter.hasNext()) {
      EOModel model = (EOModel) modelsIter.next();
      Iterator entitiesIter = model.getEntities().iterator();
      while (entitiesIter.hasNext()) {
        EOEntity entity = (EOEntity) entitiesIter.next();
        if (entity.getParent() == this) {
          children.add(entity);
        }
      }
    }
    return children;
  }

  public EOEntity getParent() {
    return myParent;
  }

  public void setParent(EOEntity _parent) {
    EOEntity oldParent = myParent;
    myParent = _parent;
    firePropertyChange(EOEntity.PARENT, oldParent, myParent);
  }

  public Boolean getAbstractEntity() {
    return isAbstractEntity();
  }

  public Boolean isAbstractEntity() {
    return myAbstractEntity;
  }

  public void setAbstractEntity(Boolean _abstractEntity) {
    Boolean oldAbstractEntity = myAbstractEntity;
    myAbstractEntity = _abstractEntity;
    firePropertyChange(EOEntity.ABSTRACT_ENTITY, oldAbstractEntity, myAbstractEntity);
  }

  public Boolean getCachesObjects() {
    return isCachesObjects();
  }

  public Boolean isCachesObjects() {
    return myCachesObjects;
  }

  public void setCachesObjects(Boolean _cachesObjects) {
    Boolean oldCachesObjects = myCachesObjects;
    myCachesObjects = _cachesObjects;
    firePropertyChange(EOEntity.CACHES_OBJECTS, oldCachesObjects, myCachesObjects);
  }

  public String getRestrictingQualifier() {
    return myRestrictingQualifier;
  }

  public void setRestrictingQualifier(String _restrictingQualifier) {
    String oldRestrictingQualifier = myRestrictingQualifier;
    myRestrictingQualifier = _restrictingQualifier;
    firePropertyChange(EOEntity.RESTRICTING_QUALIFIER, oldRestrictingQualifier, myRestrictingQualifier);
  }

  public void setAttributes(List _attributes) {
    myAttributes = _attributes;
    firePropertyChange(EOEntity.ATTRIBUTES, null, null);
  }

  public List getAttributes() {
    return myAttributes;
  }

  public String[] getAttributeNames() {
    List attributes = getAttributes();
    String[] attributeNames = new String[attributes.size()];
    Iterator attributeIter = attributes.iterator();
    for (int attributeNum = 0; attributeIter.hasNext(); attributeNum++) {
      EOAttribute attribute = (EOAttribute) attributeIter.next();
      attributeNames[attributeNum] = attribute.getName();
    }
    return attributeNames;
  }

  public List getRelationships() {
    return myRelationships;
  }

  public List getFetchSpecs() {
    return myFetchSpecs;
  }

  public void _checkForDuplicateAttributeName(EOAttribute _attribute, String _newName, Set _failures) throws DuplicateAttributeNameException {
    EOAttribute existingAttribute = getAttributeNamed(_newName);
    if (existingAttribute != null && existingAttribute != _attribute) {
      if (_failures == null) {
        throw new DuplicateAttributeNameException(_newName, this);
      }

      boolean unusedNameFound = false;
      String unusedName = null;
      for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
        unusedName = _newName + dupeNameNum;
        EOAttribute renameAttribute = getAttributeNamed(unusedName);
        unusedNameFound = (renameAttribute == null);
      }
      existingAttribute.setName(unusedName, false);
      _failures.add(new DuplicateAttributeFailure(this, _newName, unusedName));
    }
  }

  public EOFetchSpecification getFetchSpecNamed(String _name) {
    EOFetchSpecification matchingFetchSpec = null;
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (matchingFetchSpec == null && fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      if (fetchSpec.getName().equals(_name)) {
        matchingFetchSpec = fetchSpec;
      }
    }
    return matchingFetchSpec;
  }

  public void _checkForDuplicateFetchSpecName(EOFetchSpecification _fetchSpec, String _newName, Set _failures) throws DuplicateFetchSpecNameException {
    EOFetchSpecification existingFetchSpec = getFetchSpecNamed(_newName);
    if (existingFetchSpec != null && existingFetchSpec != _fetchSpec) {
      if (_failures == null) {
        throw new DuplicateFetchSpecNameException(_newName, this);
      }

      boolean unusedNameFound = false;
      String unusedName = null;
      for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
        unusedName = _newName + dupeNameNum;
        EOFetchSpecification renameFetchSpec = getFetchSpecNamed(unusedName);
        unusedNameFound = (renameFetchSpec == null);
      }
      existingFetchSpec.setName(unusedName, false);
      _failures.add(new DuplicateFetchSpecFailure(this, _newName, unusedName));
    }
  }

  public void addFetchSpecification(EOFetchSpecification _fetchSpecification) throws DuplicateFetchSpecNameException {
    addFetchSpecification(_fetchSpecification, true, null);
  }

  public void addFetchSpecification(EOFetchSpecification _fetchSpecification, boolean _fireEvents, Set _failures) throws DuplicateFetchSpecNameException {
    _checkForDuplicateFetchSpecName(_fetchSpecification, _fetchSpecification.getName(), _failures);
    myFetchSpecs.add(_fetchSpecification);
    if (_fireEvents) {
      firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, null, null);
    }
  }

  public void removeFetchSpecification(EOFetchSpecification _fetchSpecification) {
    myFetchSpecs.remove(_fetchSpecification);
    firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, null, null);
  }

  public void addAttribute(EOAttribute _attribute) throws DuplicateAttributeNameException {
    addAttribute(_attribute, true, null);
  }

  public void addAttribute(EOAttribute _attribute, boolean _fireEvents, Set _failures) throws DuplicateAttributeNameException {
    _checkForDuplicateAttributeName(_attribute, _attribute.getName(), _failures);
    myAttributes.add(_attribute);
    if (_fireEvents) {
      firePropertyChange(EOEntity.ATTRIBUTES, null, null);
    }
  }

  public void removeAttribute(EOAttribute _attribute) {
    myAttributes.remove(_attribute);
    firePropertyChange(EOEntity.ATTRIBUTES, null, null);
  }

  public IEOAttribute _getAttributeNamed(String _name) {
    IEOAttribute attribute = getAttributeNamed(_name);
    if (attribute == null) {
      attribute = getRelationshipNamed(_name);
    }
    return attribute;
  }

  public EOAttribute getAttributeNamed(String _name) {
    EOAttribute matchingAttribute = null;
    Iterator attributesIter = myAttributes.iterator();
    while (matchingAttribute == null && attributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributesIter.next();
      String attributeName = attribute.getName();
      if (attributeName != null && attributeName.equals(_name)) {
        matchingAttribute = attribute;
      }
    }
    return matchingAttribute;
  }

  public void _checkForDuplicateRelationshipName(EORelationship _relationship, String _newName, Set _failures) throws DuplicateRelationshipNameException {
    EORelationship existingRelationship = getRelationshipNamed(_newName);
    if (existingRelationship != null && existingRelationship != _relationship) {
      if (_failures == null) {
        throw new DuplicateRelationshipNameException(_newName, this);
      }

      boolean unusedNameFound = false;
      String unusedName = null;
      for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
        unusedName = _newName + dupeNameNum;
        EORelationship renameRelationship = getRelationshipNamed(unusedName);
        unusedNameFound = (renameRelationship == null);
      }
      existingRelationship.setName(unusedName, false);
      _failures.add(new DuplicateRelationshipFailure(this, _newName, unusedName));
    }
  }

  protected void _attributeChanged(EOAttribute _attribute) {
    firePropertyChange(EOEntity.ATTRIBUTE, null, _attribute);
  }

  protected void _relationshipChanged(EORelationship _relationship) {
    firePropertyChange(EOEntity.RELATIONSHIP, null, _relationship);
  }

  protected void _fetchSpecificationChanged(EOFetchSpecification _fetchSpecification) {
    firePropertyChange(EOEntity.FETCH_SPECIFICATION, null, _fetchSpecification);
  }

  public void addRelationship(EORelationship _relationship) throws DuplicateRelationshipNameException {
    addRelationship(_relationship, true, null);
  }

  public void addRelationship(EORelationship _relationship, boolean _fireEvents, Set _failures) throws DuplicateRelationshipNameException {
    _checkForDuplicateRelationshipName(_relationship, _relationship.getName(), _failures);
    myRelationships.add(_relationship);
    if (_fireEvents) {
      firePropertyChange(EOEntity.RELATIONSHIPS, null, null);
    }
  }

  public void removeRelationship(EORelationship _relationship) {
    myRelationships.remove(_relationship);
    firePropertyChange(EOEntity.RELATIONSHIPS, null, null);
  }

  public EORelationship getRelationshipNamed(String _name) {
    EORelationship matchingRelationship = null;
    Iterator relationshipsIter = myRelationships.iterator();
    while (matchingRelationship == null && relationshipsIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipsIter.next();
      if (relationship.getName().equals(_name)) {
        matchingRelationship = relationship;
      }
    }
    return matchingRelationship;
  }

  public void loadFromFile(File _entityFile, File _fetchSpecFile, Set _failures) throws IOException, EOModelException {
    try {
      if (_entityFile.exists()) {
        EOModelMap entityMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(_entityFile));
        loadFromMap(entityMap, _failures);
      }
    }
    catch (EOModelException e) {
      throw new EOModelException("Failed to load entity from '" + _entityFile + "'.", e);
    }
    try {
      if (_fetchSpecFile.exists()) {
        EOModelMap fspecMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(_fetchSpecFile));
        loadFetchSpecsFromMap(fspecMap, _failures);
      }
    }
    catch (EOModelException e) {
      throw new EOModelException("Failed to load fetch specifications from '" + _fetchSpecFile + "'.", e);
    }
  }

  public void loadFromMap(EOModelMap _entityMap, Set _failures) throws DuplicateRelationshipNameException, DuplicateAttributeNameException {
    myEntityMap = _entityMap;
    myName = _entityMap.getString("name", true); //$NON-NLS-1$
    myExternalName = _entityMap.getString("externalName", true); //$NON-NLS-1$
    myClassName = _entityMap.getString("className", true); //$NON-NLS-1$
    myCachesObjects = _entityMap.getBoolean("cachesObjects"); //$NON-NLS-1$
    myAbstractEntity = _entityMap.getBoolean("isAbstractEntity"); //$NON-NLS-1$
    myReadOnly = _entityMap.getBoolean("readOnly"); //$NON-NLS-1$
    myRestrictingQualifier = _entityMap.getString("restrictingQualifier", true); //$NON-NLS-1$
    myExternalQuery = _entityMap.getString("externalQuery", true); //$NON-NLS-1$
    myMaxNumberOfInstancesToBatchFetch = _entityMap.getInteger("maxNumberOfInstancesToBatchFetch"); //$NON-NLS-1$
    setUserInfo(MapUtils.toStringMap(_entityMap.getMap("userInfo", true)), false); //$NON-NLS-1$

    //Map fetchSpecifications = _entityMap.getMap("fetchSpecificationDictionary");
    // TODO: Fetch Specs

    List attributeList = _entityMap.getList("attributes"); //$NON-NLS-1$
    if (attributeList != null) {
      Iterator attributeIter = attributeList.iterator();
      while (attributeIter.hasNext()) {
        EOModelMap attributeMap = new EOModelMap((Map) attributeIter.next());
        EOAttribute attribute = new EOAttribute(this);
        attribute.loadFromMap(attributeMap, _failures);
        addAttribute(attribute, false, _failures);
      }
    }

    List relationshipList = _entityMap.getList("relationships"); //$NON-NLS-1$
    if (relationshipList != null) {
      Iterator relationshipIter = relationshipList.iterator();
      while (relationshipIter.hasNext()) {
        EOModelMap relationshipMap = new EOModelMap((Map) relationshipIter.next());
        EORelationship relationship = new EORelationship(this);
        relationship.loadFromMap(relationshipMap, _failures);
        addRelationship(relationship, false, _failures);
      }
    }

    List attributesUsedForLocking = _entityMap.getList("attributesUsedForLocking"); //$NON-NLS-1$
    if (attributesUsedForLocking != null) {
      Iterator attributesUsedForLockingIter = attributesUsedForLocking.iterator();
      while (attributesUsedForLockingIter.hasNext()) {
        String attributeName = (String) attributesUsedForLockingIter.next();
        EOAttribute attribute = getAttributeNamed(attributeName);
        if (attribute != null) {
          attribute.setUsedForLocking(Boolean.TRUE, false);
        }
      }
    }

    List classProperties = _entityMap.getList("classProperties"); //$NON-NLS-1$
    if (classProperties != null) {
      Iterator classPropertiesIter = classProperties.iterator();
      while (classPropertiesIter.hasNext()) {
        String attributeName = (String) classPropertiesIter.next();
        IEOAttribute attribute = _getAttributeNamed(attributeName);
        if (attribute != null) {
          attribute.setClassProperty(Boolean.TRUE, false);
        }
      }
    }

    List primaryKeyAttributes = _entityMap.getList("primaryKeyAttributes"); //$NON-NLS-1$
    if (primaryKeyAttributes != null) {
      Iterator primaryKeyAttributesIter = primaryKeyAttributes.iterator();
      while (primaryKeyAttributesIter.hasNext()) {
        String attributeName = (String) primaryKeyAttributesIter.next();
        EOAttribute attribute = getAttributeNamed(attributeName);
        if (attribute != null) {
          attribute.setPrimaryKey(Boolean.TRUE, false);
        }
      }
    }

    Map internalInfo = _entityMap.getMap("internalInfo"); //$NON-NLS-1$
    if (internalInfo != null) {
      List clientClassPropertyNames = _entityMap.getList("_clientClassPropertyNames"); //$NON-NLS-1$
      if (clientClassPropertyNames != null) {
        Iterator clientClassPropertyNameIter = clientClassPropertyNames.iterator();
        while (clientClassPropertyNameIter.hasNext()) {
          String attributeName = (String) clientClassPropertyNameIter.next();
          EOAttribute attribute = getAttributeNamed(attributeName);
          if (attribute != null) {
            attribute.setClientClassProperty(Boolean.TRUE);
          }
        }
      }
    }
  }

  public void loadFetchSpecsFromMap(EOModelMap _map, Set _failures) throws EOModelException {
    myFetchSpecsMap = _map;
    List sharedObjectFetchSpecificationNames = myEntityMap.getList("sharedObjectFetchSpecificationNames"); //$NON-NLS-1$
    Iterator fetchSpecIter = _map.entrySet().iterator();
    while (fetchSpecIter.hasNext()) {
      Map.Entry fetchSpecEntry = (Map.Entry) fetchSpecIter.next();
      String fetchSpecName = (String) fetchSpecEntry.getKey();
      EOModelMap fetchSpecMap = new EOModelMap((Map) fetchSpecEntry.getValue());
      EOFetchSpecification fetchSpec = new EOFetchSpecification(this, fetchSpecName);
      fetchSpec.loadFromMap(fetchSpecMap, _failures);
      if (sharedObjectFetchSpecificationNames != null && sharedObjectFetchSpecificationNames.contains(fetchSpecName)) {
        fetchSpec.setSharesObjects(Boolean.TRUE, false);
      }
      addFetchSpecification(fetchSpec, false, _failures);
    }
  }

  public EOModelMap toEntityMap() {
    EOModelMap entityMap = myEntityMap.cloneModelMap();
    entityMap.setString("name", myName, true); //$NON-NLS-1$
    entityMap.setString("externalName", myExternalName, true); //$NON-NLS-1$
    entityMap.setString("className", myClassName, true); //$NON-NLS-1$
    if (myParent != null) {
      entityMap.setString("parent", myParent.getName(), true); //$NON-NLS-1$
    }
    entityMap.setBoolean("cachesObjects", myCachesObjects); //$NON-NLS-1$
    entityMap.setBoolean("isAbstractEntity", myAbstractEntity); //$NON-NLS-1$
    entityMap.setBoolean("readOnly", myReadOnly); //$NON-NLS-1$
    entityMap.setString("restrictingQualifier", myRestrictingQualifier, true); //$NON-NLS-1$
    entityMap.setString("externalQuery", myExternalQuery, true); //$NON-NLS-1$
    entityMap.setInteger("maxNumberOfInstancesToBatchFetch", myMaxNumberOfInstancesToBatchFetch); //$NON-NLS-1$

    //Map fetchSpecifications = _entityMap.getMap("fetchSpecificationDictionary");
    // TODO: ???

    List classProperties = new LinkedList();
    List primaryKeyAttributes = new LinkedList();
    List attributesUsedForLocking = new LinkedList();
    List clientClassProperties = new LinkedList();
    List attributes = new LinkedList();
    Iterator attributeIter = myAttributes.iterator();
    while (attributeIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributeIter.next();
      EOModelMap attributeMap = attribute.toMap();
      attributes.add(attributeMap);
      if (attribute.isClassProperty() != null && attribute.isClassProperty().booleanValue()) {
        classProperties.add(attribute.getName());
      }
      if (attribute.isPrimaryKey() != null && attribute.isPrimaryKey().booleanValue()) {
        primaryKeyAttributes.add(attribute.getName());
      }
      if (attribute.isUsedForLocking() != null && attribute.isUsedForLocking().booleanValue()) {
        attributesUsedForLocking.add(attribute.getName());
      }
      if (attribute.isClientClassProperty() != null && attribute.isClientClassProperty().booleanValue()) {
        clientClassProperties.add(attribute.getName());
      }
    }
    entityMap.setList("attributes", attributes, true); //$NON-NLS-1$

    List relationships = new LinkedList();
    Iterator relationshipIter = myRelationships.iterator();
    while (relationshipIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipIter.next();
      EOModelMap relationshipMap = relationship.toMap();
      relationships.add(relationshipMap);
      if (relationship.isClassProperty() != null && relationship.isClassProperty().booleanValue()) {
        classProperties.add(relationship.getName());
      }
    }
    entityMap.setList("relationships", relationships, true); //$NON-NLS-1$
    entityMap.setList("attributesUsedForLocking", attributesUsedForLocking, true); //$NON-NLS-1$
    entityMap.setList("classProperties", classProperties, true); //$NON-NLS-1$
    entityMap.setList("primaryKeyAttributes", primaryKeyAttributes, true); //$NON-NLS-1$

    List sharedObjectFetchSpecificationNames = new LinkedList();
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      if (fetchSpec.isSharesObjects() != null && fetchSpec.isSharesObjects().booleanValue()) {
        sharedObjectFetchSpecificationNames.add(fetchSpec.getName());
      }
    }
    entityMap.setList("sharedObjectFetchSpecificationNames", sharedObjectFetchSpecificationNames, true); //$NON-NLS-1$

    Map internalInfoMap = entityMap.getMap("internalInfo"); //$NON-NLS-1$
    if (internalInfoMap == null) {
      internalInfoMap = new HashMap();
      entityMap.setMap("internalInfo", internalInfoMap, false); //$NON-NLS-1$
    }
    if (!clientClassProperties.isEmpty()) {
      internalInfoMap.put("_clientClassPropertyNames", clientClassProperties); //$NON-NLS-1$
    }

    entityMap.setMap("userInfo", getUserInfo(), true); //$NON-NLS-1$

    return entityMap;
  }

  public EOModelMap toFetchSpecsMap() {
    EOModelMap fetchSpecsMap = myFetchSpecsMap.cloneModelMap();
    fetchSpecsMap.clear();
    Iterator fetchSpecIter = myFetchSpecs.iterator();
    while (fetchSpecIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
      EOModelMap fetchSpecMap = fetchSpec.toMap();
      fetchSpecsMap.setMap(fetchSpec.getName(), fetchSpecMap, true);
    }
    return fetchSpecsMap;
  }

  public void saveToFile(File _entityFile, File _fetchSpecFile) {
    EOModelMap entityMap = toEntityMap();
    PropertyListSerialization.propertyListToFile(_entityFile, entityMap);

    if (myFetchSpecs.size() == 0) {
      _fetchSpecFile.delete();
    }
    else {
      EOModelMap fetchSpecMap = toFetchSpecsMap();
      PropertyListSerialization.propertyListToFile(_fetchSpecFile, fetchSpecMap);
    }
  }

  public void resolve(Set _failures) {
    String parentName = myEntityMap.getString("parent", true); //$NON-NLS-1$
    if (parentName != null) {
      myParent = myModel.getModelGroup().getEntityNamed(parentName);
      if (myParent == null) {
        _failures.add(new MissingEntityFailure(parentName));
      }
    }

    Iterator attributeIter = myAttributes.iterator();
    while (attributeIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributeIter.next();
      attribute.resolve(_failures);
    }

    Iterator relationshipIter = myRelationships.iterator();
    while (relationshipIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipIter.next();
      relationship.resolve(_failures);
    }

    Iterator fetchSpecIter = myFetchSpecs.iterator();
    while (fetchSpecIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
      fetchSpec.resolve(_failures);
    }
  }

  public void verify(Set _failures) {
    Iterator attributeIter = myAttributes.iterator();
    while (attributeIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributeIter.next();
      attribute.verify(_failures);
    }

    Iterator relationshipIter = myRelationships.iterator();
    while (relationshipIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipIter.next();
      relationship.verify(_failures);
    }

    Iterator fetchSpecIter = myFetchSpecs.iterator();
    while (fetchSpecIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
      fetchSpec.verify(_failures);
    }
  }

  public String toString() {
    return "[EOEntity: name = " + myName + "; attributes = " + myAttributes + "; relationships = " + myRelationships + "; fetchSpecs = " + myFetchSpecs + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
  }
}

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.StringUtils;

public class EOEntity extends UserInfoableEOModelObject implements IEOEntityRelative, ISortableEOModelObject {
  private static final String FETCH_ALL = "FetchAll";
  public static final String ATTRIBUTE = "attribute";
  public static final String RELATIONSHIP = "relationship";
  public static final String FETCH_SPECIFICATION = "fetchSpecification";
  public static final String NAME = "name";
  public static final String CLASS_NAME = "className";
  public static final String PARENT = "parent";
  public static final String EXTERNAL_QUERY = "externalQuery";
  public static final String MAX_NUMBER_OF_INSTANCES_TO_BATCH_FETCH = "maxNumberOfInstancesToBatchFetch";
  public static final String READ_ONLY = "readOnly";
  public static final String EXTERNAL_NAME = "externalName";
  public static final String ABSTRACT_ENTITY = "abstractEntity";
  public static final String CACHES_OBJECTS = "cachesObjects";
  public static final String RESTRICTING_QUALIFIER = "restrictingQualifier";
  public static final String FETCH_SPECIFICATIONS = "fetchSpecifications";
  public static final String ATTRIBUTES = "attributes";
  public static final String RELATIONSHIPS = "relationships";

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
  private Set myAttributes;
  private Set myRelationships;
  private Set myFetchSpecs;
  private EOModelMap myEntityMap;
  private EOModelMap myFetchSpecsMap;

  public EOEntity() {
    myAttributes = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    myRelationships = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    myFetchSpecs = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    myEntityMap = new EOModelMap();
    myFetchSpecsMap = new EOModelMap();
  }

  public EOEntity(String _name) {
    this();
    myName = _name;
  }

  public IEOAttribute resolveKeyPath(String _keyPath) {
    IEOAttribute targetAttribute = resolveKeyPath(_keyPath, new HashSet());
    return targetAttribute;
  }

  public IEOAttribute resolveKeyPath(String _keyPath, Set _visitedRelationships) {
    IEOAttribute targetAttribute = null;
    if (_keyPath != null && _keyPath.length() > 0) {
      int dotIndex = _keyPath.indexOf('.');
      if (dotIndex == -1) {
        targetAttribute = getAttributeOrRelationshipNamed(_keyPath);
      }
      else {
        EORelationship relationship = getRelationshipNamed(_keyPath.substring(0, dotIndex));
        if (relationship != null) {
          if (_visitedRelationships.contains(relationship)) {
            System.out.println("EOEntity.resolveKeyPath: you have an invalid flattened relationship '" + _keyPath + "' which creates a loop.");
            //throw new IllegalStateException("The definition '" + _keyPath + "' results in a loop in " + getName() + ".");
          }
          else {
            _visitedRelationships.add(relationship);
            EOEntity destination = relationship.getDestination();
            if (destination != null) {
              targetAttribute = destination.resolveKeyPath(_keyPath.substring(dotIndex + 1), _visitedRelationships);
            }
          }
        }
      }
    }
    return targetAttribute;
  }

  public void pasted() throws DuplicateNameException {
    Iterator attributesIter = getAttributes().iterator();
    while (attributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributesIter.next();
      attribute.pasted();
    }

    Iterator relationshipsIter = getRelationships().iterator();
    while (relationshipsIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipsIter.next();
      relationship.pasted();
    }
  }

  public EOEntity cloneEntity() throws DuplicateNameException {
    EOEntity entity = _cloneJustEntity();
    entity._cloneAttributesAndRelationshipsFrom(this, false);
    entity._cloneFetchSpecificationsFrom(this, false);
    return entity;
  }

  protected EOEntity _cloneJustEntity() {
    EOEntity entity = new EOEntity(myName);
    entity.myParent = myParent;
    entity.myExternalName = myExternalName;
    entity.myClassName = myClassName;
    entity.myRestrictingQualifier = myRestrictingQualifier;
    entity.myExternalQuery = myExternalQuery;
    entity.myCachesObjects = myCachesObjects;
    entity.myAbstractEntity = myAbstractEntity;
    entity.myReadOnly = myReadOnly;
    entity.myMaxNumberOfInstancesToBatchFetch = myMaxNumberOfInstancesToBatchFetch;
    return entity;
  }

  public EOEntity subclass(String _subclassName, InheritanceType _inheritanceType) throws DuplicateNameException {
    EOEntity subclassEntity;
    if (_inheritanceType == InheritanceType.HORIZONTAL) {
      subclassEntity = _horizontalSubclass(_subclassName);
    }
    else if (_inheritanceType == InheritanceType.SINGLE_TABLE) {
      subclassEntity = _singleTableSubclass(_subclassName);
    }
    else if (_inheritanceType == InheritanceType.VERTICAL) {
      subclassEntity = _verticalSubclass(_subclassName);
    }
    else {
      throw new IllegalArgumentException("Unknown inheritance type " + _inheritanceType + ".");
    }
    return subclassEntity;
  }

  protected String _toSubclassName(String _subclassName) {
    String className = getClassName();
    if (className != null) {
      int lastDotIndex = className.lastIndexOf('.');
      className = className.substring(0, lastDotIndex + 1) + _subclassName;
    }
    return className;
  }

  public EOEntity _horizontalSubclass(String _subclassName) throws DuplicateNameException {
    EOEntity subclassEntity = _cloneJustEntity();
    subclassEntity.setName(_subclassName, false);
    subclassEntity.myClassName = _toSubclassName(_subclassName);
    subclassEntity.myParent = this;
    subclassEntity.myExternalName = _subclassName;
    if (subclassEntity.isAbstractEntity() != null) {
      subclassEntity.myAbstractEntity = Boolean.FALSE;
    }
    subclassEntity._cloneAttributesAndRelationshipsFrom(this, false);
    return subclassEntity;
  }

  public EOEntity _singleTableSubclass(String _subclassName) throws DuplicateNameException {
    EOEntity subclassEntity = _cloneJustEntity();
    subclassEntity.setName(_subclassName, false);
    subclassEntity.myClassName = _toSubclassName(_subclassName);
    subclassEntity.myParent = this;
    if (subclassEntity.isAbstractEntity() != null) {
      subclassEntity.myAbstractEntity = Boolean.FALSE;
    }
    subclassEntity._cloneAttributesAndRelationshipsFrom(this, false);
    return subclassEntity;
  }

  public EOEntity _verticalSubclass(String _subclassName) throws DuplicateNameException {
    EOEntity subclassEntity = _cloneJustEntity();
    subclassEntity.setName(_subclassName, false);
    subclassEntity.myClassName = _toSubclassName(_subclassName);
    subclassEntity.myParent = this;
    subclassEntity.myExternalName = _subclassName;
    if (subclassEntity.isAbstractEntity() != null) {
      subclassEntity.myAbstractEntity = Boolean.FALSE;
    }

    EORelationship superclassRelationship = subclassEntity.addBlankRelationship(subclassEntity.findUnusedRelationshipName(StringUtils.toLowercaseFirstLetter(getName())));
    superclassRelationship.setToMany(Boolean.FALSE);
    superclassRelationship.setDestination(this);
    superclassRelationship.setClassProperty(Boolean.FALSE, false);
    Set primaryKeyAttributes = getPrimaryKeyAttributes();
    Iterator primaryKeyAttributesIter = primaryKeyAttributes.iterator();
    while (primaryKeyAttributesIter.hasNext()) {
      EOAttribute superclassPrimaryKeyAttribute = (EOAttribute) primaryKeyAttributesIter.next();
      EOAttribute subclassPrimaryKeyAttribute = superclassPrimaryKeyAttribute.cloneAttribute();
      EOJoin superclassJoin = new EOJoin();
      superclassJoin.setSourceAttribute(subclassPrimaryKeyAttribute);
      superclassJoin.setDestinationAttribute(superclassPrimaryKeyAttribute);
      superclassRelationship.addJoin(superclassJoin, false);
      subclassEntity.addAttribute(subclassPrimaryKeyAttribute);
    }

    Iterator attributesIter = getAttributes().iterator();
    while (attributesIter.hasNext()) {
      EOAttribute inheritedAttribute = (EOAttribute) attributesIter.next();
      if (!primaryKeyAttributes.contains(inheritedAttribute) && BooleanUtils.isTrue(inheritedAttribute.isClassProperty())) {
        subclassEntity.addBlankAttribute(new EOAttributePath(new EORelationshipPath(null, superclassRelationship), inheritedAttribute));
      }
    }

    Iterator relationshipsIter = getRelationships().iterator();
    while (relationshipsIter.hasNext()) {
      EORelationship inheritedRelationship = (EORelationship) relationshipsIter.next();
      if (BooleanUtils.isTrue(inheritedRelationship.isClassProperty())) {
        subclassEntity.addBlankRelationship(new EORelationshipPath(new EORelationshipPath(null, superclassRelationship), inheritedRelationship));
      }
    }

    return subclassEntity;
  }

  protected void _cloneFetchSpecificationsFrom(EOEntity _entity, boolean _skipExistingNames) throws DuplicateNameException {
    Iterator fetchSpecsIter = _entity.getFetchSpecs().iterator();
    while (fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      if (!_skipExistingNames || getFetchSpecNamed(fetchSpec.getName()) == null) {
        EOFetchSpecification clonedFetchSpec = fetchSpec.cloneFetchSpecification();
        clonedFetchSpec.setName(findUnusedFetchSpecificationName(clonedFetchSpec.getName()));
        addFetchSpecification(clonedFetchSpec);
      }
    }
  }

  protected void _cloneAttributesAndRelationshipsFrom(EOEntity _entity, boolean _skipExistingNames) throws DuplicateNameException {
    Iterator attributesIter = _entity.getAttributes().iterator();
    while (attributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributesIter.next();
      if (!_skipExistingNames || getAttributeNamed(attribute.getName()) == null) {
        EOAttribute clonedAttribute = attribute.cloneAttribute();
        clonedAttribute.setName(findUnusedAttributeName(clonedAttribute.getName()));
        addAttribute(clonedAttribute);
      }
    }

    Iterator relationshipsIter = _entity.getRelationships().iterator();
    while (relationshipsIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipsIter.next();
      if (!_skipExistingNames || getRelationshipNamed(relationship.getName()) == null) {
        EORelationship clonedRelationship = relationship.cloneRelationship();
        clonedRelationship.setName(findUnusedRelationshipName(clonedRelationship.getName()));
        addRelationship(clonedRelationship);
      }
    }
  }

  public EOEntity getEntity() {
    return this;
  }

  public IEOAttribute addBlankIEOAttribute(IEOAttributePath _flattenAttribute) throws DuplicateNameException {
    if (_flattenAttribute instanceof EORelationshipPath) {
      return addBlankRelationship((EORelationshipPath) _flattenAttribute);
    }
    else if (_flattenAttribute instanceof EOAttributePath) {
      return addBlankAttribute((EOAttributePath) _flattenAttribute);
    }
    else {
      throw new IllegalArgumentException("Unknown attribute path: " + _flattenAttribute);
    }
  }

  public EORelationship addBlankRelationship(String _name) throws DuplicateNameException {
    return addBlankRelationship(_name, null);
  }

  public EORelationship addBlankRelationship(EORelationshipPath _flattenRelationship) throws DuplicateNameException {
    return addBlankRelationship(_flattenRelationship.toKeyPath().replace('.', '_'), _flattenRelationship);
  }

  public EORelationship addBlankRelationship(String _name, EORelationshipPath _flattenRelationship) throws DuplicateNameException {
    String newRelationshipNameBase = _name;
    String newRelationshipName = newRelationshipNameBase;
    int newRelationshipNum = 0;
    while (getRelationshipNamed(newRelationshipName) != null) {
      newRelationshipNum++;
      newRelationshipName = newRelationshipNameBase + newRelationshipNum;
    }
    EORelationship relationship;
    if (_flattenRelationship != null) {
      relationship = new EORelationship(newRelationshipName, _flattenRelationship.toKeyPath());
    }
    else {
      relationship = new EORelationship(newRelationshipName);
    }
    relationship.setClassProperty(Boolean.TRUE);
    addRelationship(relationship);
    return relationship;
  }

  public EOAttribute addBlankAttribute(String _name) throws DuplicateNameException {
    return addBlankAttribute(_name, null);
  }

  public EOAttribute addBlankAttribute(EOAttributePath _flattenAttribute) throws DuplicateNameException {
    return addBlankAttribute(_flattenAttribute.toKeyPath().replace('.', '_'), _flattenAttribute);
  }

  public EOAttribute addBlankAttribute(String _name, EOAttributePath _flattenAttribute) throws DuplicateNameException {
    String newAttributeNameBase = _name;
    String newAttributeName = newAttributeNameBase;
    int newAttributeNum = 0;
    while (getAttributeNamed(newAttributeName) != null) {
      newAttributeNum++;
      newAttributeName = newAttributeNameBase + newAttributeNum;
    }
    EOAttribute attribute;
    if (_flattenAttribute != null) {
      attribute = new EOAttribute(newAttributeName, _flattenAttribute.toKeyPath());
    }
    else {
      attribute = new EOAttribute(newAttributeName);
    }
    attribute.setClassProperty(Boolean.TRUE);
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
    EOFetchSpecification fetchSpec = new EOFetchSpecification(newFetchSpecName);
    addFetchSpecification(fetchSpec);
    return fetchSpec;
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    if (myModel != null) {
      myModel._entityChanged(this);
    }
  }

  public boolean hasSharedObjects() {
    boolean hasSharedObjects = false;
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (!hasSharedObjects && fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      hasSharedObjects = BooleanUtils.isTrue(fetchSpec.isSharesObjects());
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
      if (BooleanUtils.isTrue(fetchSpec.isSharesObjects())) {
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
      fetchAllFetchSpec = new EOFetchSpecification(EOEntity.FETCH_ALL);
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

  public void _setModel(EOModel _model) {
    myModel = _model;
  }

  public EOModel getModel() {
    return myModel;
  }

  public boolean isPrototype() {
    return myName != null && myName.startsWith("EO") && myName.endsWith("Prototypes");
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
    if (_name == null) {
      throw new NullPointerException(Messages.getString("EOEntity.noBlankEntityNames"));
    }
    String oldName = myName;
    if (myModel != null) {
      myModel._checkForDuplicateEntityName(this, _name, null);
      myModel._entityNameChanged(myName);
    }
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
    return (myName == null) ? super.hashCode() : myName.hashCode();
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof EOEntity && ((_obj == this) || ComparisonUtils.equals(myName, ((EOEntity) _obj).myName)));
  }

  public Set getReferenceFailures() {
    Set referenceFailures = new HashSet();
    Iterator referencingEntitiesIter = getChildrenEntities().iterator();
    while (referencingEntitiesIter.hasNext()) {
      EOEntity referencingEntity = (EOEntity) referencingEntitiesIter.next();
      referenceFailures.add(new EOEntityParentReferenceFailure(this, referencingEntity));
    }

    Iterator referencingRelationshipsIter = getReferencingRelationships().iterator();
    while (referencingRelationshipsIter.hasNext()) {
      EORelationship referencingRelationship = (EORelationship) referencingRelationshipsIter.next();
      referenceFailures.add(new EOEntityRelationshipReferenceFailure(this, referencingRelationship));
    }
    return referenceFailures;
  }

  public Set getReferencingRelationships() {
    Set referencingRelationships = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
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

  public Set getChildrenEntities() {
    Set children = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    if (myModel != null) {
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

  public void inheritParentAttributesAndRelationships() throws DuplicateNameException {
    EOEntity parent = getParent();
    if (parent != null) {
      _cloneAttributesAndRelationshipsFrom(parent, true);
    }
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

  public void setAttributes(Set _attributes) {
    myAttributes = _attributes;
    firePropertyChange(EOEntity.ATTRIBUTES, null, null);
  }

  public Set getPrimaryKeyAttributes() {
    Set primaryKeyAttributes = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Iterator attributesIter = myAttributes.iterator();
    while (attributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributesIter.next();
      Boolean primaryKey = attribute.isPrimaryKey();
      if (BooleanUtils.isTrue(primaryKey)) {
        primaryKeyAttributes.add(attribute);
      }
    }
    return primaryKeyAttributes;
  }

  public Set getAttributes() {
    return myAttributes;
  }

  public String[] getAttributeNames() {
    Set attributes = getAttributes();
    String[] attributeNames = new String[attributes.size()];
    Iterator attributeIter = attributes.iterator();
    for (int attributeNum = 0; attributeIter.hasNext(); attributeNum++) {
      EOAttribute attribute = (EOAttribute) attributeIter.next();
      attributeNames[attributeNum] = attribute.getName();
    }
    return attributeNames;
  }

  public Set getRelationships() {
    return myRelationships;
  }

  public Set getFetchSpecs() {
    return myFetchSpecs;
  }

  public String findUnusedAttributeName(String _newName) {
    boolean unusedNameFound = (getAttributeOrRelationshipNamed(_newName) == null);
    String unusedName = _newName;
    for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
      unusedName = _newName + dupeNameNum;
      IEOAttribute renameAttribute = getAttributeOrRelationshipNamed(unusedName);
      unusedNameFound = (renameAttribute == null);
    }
    return unusedName;
  }

  public IEOAttribute getAttributeOrRelationshipNamed(String _name) {
    IEOAttribute attribute = getAttributeNamed(_name);
    if (attribute == null) {
      attribute = getRelationshipNamed(_name);
    }
    return attribute;
  }

  public void _checkForDuplicateAttributeName(EOAttribute _attribute, String _newName, Set _failures) throws DuplicateNameException {
    IEOAttribute existingAttribute = getAttributeOrRelationshipNamed(_newName);
    if (existingAttribute != null && existingAttribute != _attribute) {
      if (_failures == null) {
        throw new DuplicateAttributeNameException(_newName, this);
      }

      String unusedName = findUnusedAttributeName(_newName);
      existingAttribute.setName(unusedName, false);
      _failures.add(new DuplicateAttributeFailure(this, _newName, unusedName));
    }
  }

  public EOFetchSpecification getFetchSpecNamed(String _name) {
    EOFetchSpecification matchingFetchSpec = null;
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (matchingFetchSpec == null && fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      if (ComparisonUtils.equals(fetchSpec.getName(), _name)) {
        matchingFetchSpec = fetchSpec;
      }
    }
    return matchingFetchSpec;
  }

  public String findUnusedFetchSpecificationName(String _newName) {
    boolean unusedNameFound = (getFetchSpecNamed(_newName) == null);
    String unusedName = _newName;
    for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
      unusedName = _newName + dupeNameNum;
      EOFetchSpecification renameFetchSpec = getFetchSpecNamed(unusedName);
      unusedNameFound = (renameFetchSpec == null);
    }
    return unusedName;
  }

  public void _checkForDuplicateFetchSpecName(EOFetchSpecification _fetchSpec, String _newName, Set _failures) throws DuplicateFetchSpecNameException {
    EOFetchSpecification existingFetchSpec = getFetchSpecNamed(_newName);
    if (existingFetchSpec != null && existingFetchSpec != _fetchSpec) {
      if (_failures == null) {
        throw new DuplicateFetchSpecNameException(_newName, this);
      }

      String unusedName = findUnusedFetchSpecificationName(_newName);
      existingFetchSpec.setName(unusedName, false);
      _failures.add(new DuplicateFetchSpecFailure(this, _newName, unusedName));
    }
  }

  public void addFetchSpecification(EOFetchSpecification _fetchSpecification) throws DuplicateFetchSpecNameException {
    addFetchSpecification(_fetchSpecification, true, null);
  }

  public void addFetchSpecification(EOFetchSpecification _fetchSpecification, boolean _fireEvents, Set _failures) throws DuplicateFetchSpecNameException {
    _fetchSpecification._setEntity(this);
    _checkForDuplicateFetchSpecName(_fetchSpecification, _fetchSpecification.getName(), _failures);
    Set oldFetchSpecs = null;
    if (_fireEvents) {
      oldFetchSpecs = myFetchSpecs;
      Set newFetchSpecs = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
      newFetchSpecs.addAll(myFetchSpecs);
      newFetchSpecs.add(_fetchSpecification);
      myFetchSpecs = newFetchSpecs;
      firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, oldFetchSpecs, myFetchSpecs);
    }
    else {
      myFetchSpecs.add(_fetchSpecification);
    }
  }

  public void removeFetchSpecification(EOFetchSpecification _fetchSpecification) {
    myFetchSpecs.remove(_fetchSpecification);
    firePropertyChange(EOEntity.FETCH_SPECIFICATIONS, null, null);
  }

  public void addAttribute(EOAttribute _attribute) throws DuplicateNameException {
    addAttribute(_attribute, true, null);
  }

  public synchronized void addAttribute(EOAttribute _attribute, boolean _fireEvents, Set _failures) throws DuplicateNameException {
    _attribute._setEntity(this);
    _checkForDuplicateAttributeName(_attribute, _attribute.getName(), _failures);
    _attribute.pasted();
    Set oldAttributes = null;
    if (_fireEvents) {
      oldAttributes = myAttributes;
      Set newAttributes = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
      newAttributes.addAll(myAttributes);
      newAttributes.add(_attribute);
      myAttributes = newAttributes;
      firePropertyChange(EOEntity.ATTRIBUTES, oldAttributes, myAttributes);
    }
    else {
      myAttributes.add(_attribute);
    }
  }

  public void removeAttribute(EOAttribute _attribute, boolean _removeFromSubclasses) {
    String attributeName = _attribute.getName();
    myAttributes.remove(_attribute);
    firePropertyChange(EOEntity.ATTRIBUTES, null, null);
    if (_removeFromSubclasses) {
      Iterator childrenEntitiesIter = getChildrenEntities().iterator();
      while (childrenEntitiesIter.hasNext()) {
        EOEntity childEntity = (EOEntity) childrenEntitiesIter.next();
        EOAttribute childAttribute = childEntity.getAttributeNamed(attributeName);
        childEntity.removeAttribute(childAttribute, _removeFromSubclasses);
      }
    }
    _attribute._setEntity(null);
  }

  public EOAttribute getAttributeNamed(String _name) {
    EOAttribute matchingAttribute = null;
    Iterator attributesIter = myAttributes.iterator();
    while (matchingAttribute == null && attributesIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributesIter.next();
      if (ComparisonUtils.equals(attribute.getName(), _name)) {
        matchingAttribute = attribute;
      }
    }
    return matchingAttribute;
  }

  public String findUnusedRelationshipName(String _newName) {
    boolean unusedNameFound = (getAttributeOrRelationshipNamed(_newName) == null);
    String unusedName = _newName;
    for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
      unusedName = _newName + dupeNameNum;
      IEOAttribute renameRelationship = getAttributeOrRelationshipNamed(unusedName);
      unusedNameFound = (renameRelationship == null);
    }
    return unusedName;
  }

  public void _checkForDuplicateRelationshipName(EORelationship _relationship, String _newName, Set _failures) throws DuplicateNameException {
    IEOAttribute existingRelationship = getAttributeOrRelationshipNamed(_newName);
    if (existingRelationship != null && existingRelationship != _relationship) {
      if (_failures == null) {
        throw new DuplicateRelationshipNameException(_newName, this);
      }

      String unusedName = findUnusedRelationshipName(_newName);
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

  public void addRelationship(EORelationship _relationship) throws DuplicateNameException {
    addRelationship(_relationship, true, null);
  }

  public void addRelationship(EORelationship _relationship, boolean _fireEvents, Set _failures) throws DuplicateNameException {
    _relationship._setEntity(this);
    _checkForDuplicateRelationshipName(_relationship, _relationship.getName(), _failures);
    _relationship.pasted();
    Set oldRelationships = null;
    if (_fireEvents) {
      oldRelationships = myRelationships;
      Set newRelationships = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
      newRelationships.addAll(myRelationships);
      newRelationships.add(_relationship);
      myRelationships = newRelationships;
      firePropertyChange(EOEntity.RELATIONSHIPS, oldRelationships, myRelationships);
    }
    else {
      myRelationships.add(_relationship);
    }
  }

  public void removeRelationship(EORelationship _relationship, boolean _removeFromSubclasses) {
    String relationshipName = _relationship.getName();
    myRelationships.remove(_relationship);
    firePropertyChange(EOEntity.RELATIONSHIPS, null, null);
    if (_removeFromSubclasses) {
      Iterator childrenEntitiesIter = getChildrenEntities().iterator();
      while (childrenEntitiesIter.hasNext()) {
        EOEntity childEntity = (EOEntity) childrenEntitiesIter.next();
        EORelationship childRelationship = childEntity.getRelationshipNamed(relationshipName);
        childEntity.removeRelationship(childRelationship, _removeFromSubclasses);
      }
    }
    _relationship._setEntity(null);
  }

  public EORelationship getRelationshipNamed(String _name) {
    EORelationship matchingRelationship = null;
    Iterator relationshipsIter = myRelationships.iterator();
    while (matchingRelationship == null && relationshipsIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipsIter.next();
      if (ComparisonUtils.equals(relationship.getName(), _name)) {
        matchingRelationship = relationship;
      }
    }
    return matchingRelationship;
  }

  public void loadFromMap(EOModelMap _entityMap, Set _failures) throws DuplicateNameException {
    myEntityMap = _entityMap;
    myName = _entityMap.getString("name", true);
    myExternalName = _entityMap.getString("externalName", true);
    myClassName = _entityMap.getString("className", true);
    myCachesObjects = _entityMap.getBoolean("cachesObjects");
    myAbstractEntity = _entityMap.getBoolean("isAbstractEntity");
    myReadOnly = _entityMap.getBoolean("isReadOnly");
    myRestrictingQualifier = _entityMap.getString("restrictingQualifier", true);
    myExternalQuery = _entityMap.getString("externalQuery", true);
    myMaxNumberOfInstancesToBatchFetch = _entityMap.getInteger("maxNumberOfInstancesToBatchFetch");
    setUserInfo(_entityMap.getMap("userInfo", true), false);

    //Map fetchSpecifications = _entityMap.getMap("fetchSpecificationDictionary");
    // TODO: Fetch Specs

    Set attributeList = _entityMap.getSet("attributes");
    if (attributeList != null) {
      Iterator attributeIter = attributeList.iterator();
      while (attributeIter.hasNext()) {
        EOModelMap attributeMap = new EOModelMap((Map) attributeIter.next());
        EOAttribute attribute = new EOAttribute();
        attribute.loadFromMap(attributeMap, _failures);
        addAttribute(attribute, false, _failures);
      }
    }

    Set relationshipList = _entityMap.getSet("relationships");
    if (relationshipList != null) {
      Iterator relationshipIter = relationshipList.iterator();
      while (relationshipIter.hasNext()) {
        EOModelMap relationshipMap = new EOModelMap((Map) relationshipIter.next());
        EORelationship relationship = new EORelationship();
        relationship.loadFromMap(relationshipMap, _failures);
        addRelationship(relationship, false, _failures);
      }
    }

    Set attributesUsedForLocking = _entityMap.getSet("attributesUsedForLocking");
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
  }

  public void loadFetchSpecsFromMap(EOModelMap _map, Set _failures) throws EOModelException {
    myFetchSpecsMap = _map;
    Set sharedObjectFetchSpecificationNames = myEntityMap.getSet("sharedObjectFetchSpecificationNames");
    Iterator fetchSpecIter = _map.entrySet().iterator();
    while (fetchSpecIter.hasNext()) {
      Map.Entry fetchSpecEntry = (Map.Entry) fetchSpecIter.next();
      String fetchSpecName = (String) fetchSpecEntry.getKey();
      EOModelMap fetchSpecMap = new EOModelMap((Map) fetchSpecEntry.getValue());
      EOFetchSpecification fetchSpec = new EOFetchSpecification(fetchSpecName);
      fetchSpec.loadFromMap(fetchSpecMap, _failures);
      if (sharedObjectFetchSpecificationNames != null && sharedObjectFetchSpecificationNames.contains(fetchSpecName)) {
        fetchSpec.setSharesObjects(Boolean.TRUE, false);
      }
      addFetchSpecification(fetchSpec, false, _failures);
    }
  }

  public EOModelMap toEntityMap() {
    EOModelMap entityMap = myEntityMap.cloneModelMap();
    entityMap.setString("name", myName, true);
    entityMap.setString("externalName", myExternalName, true);
    entityMap.setString("className", myClassName, true);
    if (myParent != null) {
      entityMap.setString("parent", myParent.getName(), true);
    }
    entityMap.setBoolean("cachesObjects", myCachesObjects, EOModelMap.YN);
    entityMap.setBoolean("isAbstractEntity", myAbstractEntity, EOModelMap.YN);
    entityMap.setBoolean("isReadOnly", myReadOnly, EOModelMap.YN);
    entityMap.setString("restrictingQualifier", myRestrictingQualifier, true);
    entityMap.setString("externalQuery", myExternalQuery, true);
    entityMap.setInteger("maxNumberOfInstancesToBatchFetch", myMaxNumberOfInstancesToBatchFetch);

    //Map fetchSpecifications = _entityMap.getMap("fetchSpecificationDictionary");
    // TODO: ???

    Set classProperties = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Set primaryKeyAttributes = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Set attributesUsedForLocking = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Set clientClassProperties = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Set attributes = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Iterator attributeIter = myAttributes.iterator();
    while (attributeIter.hasNext()) {
      EOAttribute attribute = (EOAttribute) attributeIter.next();
      EOModelMap attributeMap = attribute.toMap();
      attributes.add(attributeMap);
      if (BooleanUtils.isTrue(attribute.isClassProperty())) {
        classProperties.add(attribute.getName());
      }
      if (BooleanUtils.isTrue(attribute.isPrimaryKey())) {
        primaryKeyAttributes.add(attribute.getName());
      }
      if (BooleanUtils.isTrue(attribute.isUsedForLocking())) {
        attributesUsedForLocking.add(attribute.getName());
      }
      if (BooleanUtils.isTrue(attribute.isClientClassProperty())) {
        clientClassProperties.add(attribute.getName());
      }
    }
    entityMap.setSet("attributes", attributes, true);

    Set relationships = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Iterator relationshipIter = myRelationships.iterator();
    while (relationshipIter.hasNext()) {
      EORelationship relationship = (EORelationship) relationshipIter.next();
      EOModelMap relationshipMap = relationship.toMap();
      relationships.add(relationshipMap);
      if (BooleanUtils.isTrue(relationship.isClassProperty())) {
        classProperties.add(relationship.getName());
      }
      if (BooleanUtils.isTrue(relationship.isClientClassProperty())) {
        clientClassProperties.add(relationship.getName());
      }
    }
    entityMap.setSet("relationships", relationships, true);
    entityMap.setSet("attributesUsedForLocking", attributesUsedForLocking, true);
    entityMap.setSet("classProperties", classProperties, true);
    entityMap.setSet("primaryKeyAttributes", primaryKeyAttributes, true);

    Set sharedObjectFetchSpecificationNames = new TreeSet(PropertyListComparator.AscendingPropertyListComparator);
    Iterator fetchSpecsIter = myFetchSpecs.iterator();
    while (fetchSpecsIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecsIter.next();
      if (BooleanUtils.isTrue(fetchSpec.isSharesObjects())) {
        sharedObjectFetchSpecificationNames.add(fetchSpec.getName());
      }
    }
    entityMap.setSet("sharedObjectFetchSpecificationNames", sharedObjectFetchSpecificationNames, true);

    Map internalInfoMap = entityMap.getMap("internalInfo");
    if (internalInfoMap == null) {
      internalInfoMap = new HashMap();
    }
    if (!clientClassProperties.isEmpty()) {
      internalInfoMap.put("_clientClassPropertyNames", clientClassProperties);
    }
    else {
      internalInfoMap.remove("_clientClassPropertyNames");
    }
    entityMap.setMap("internalInfo", internalInfoMap, false);

    entityMap.setMap("userInfo", getUserInfo(), true);

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

  public void loadFromFile(File _entityFile, File _fetchSpecFile, Set _failures) throws IOException, EOModelException {
    try {
      if (_entityFile.exists()) {
        EOModelMap entityMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(_entityFile, new EOModelParserDataStructureFactory()));
        loadFromMap(entityMap, _failures);
      }
    }
    catch (EOModelException e) {
      throw new EOModelException("Failed to load entity from '" + _entityFile + "'.", e);
    }
    try {
      if (_fetchSpecFile.exists()) {
        EOModelMap fspecMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(_fetchSpecFile, new EOModelParserDataStructureFactory()));
        loadFetchSpecsFromMap(fspecMap, _failures);
      }
    }
    catch (EOModelException e) {
      throw new EOModelException("Failed to load fetch specifications from '" + _fetchSpecFile + "'.", e);
    }
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
    String parentName = myEntityMap.getString("parent", true);
    if (parentName != null) {
      if (myModel != null) {
        myParent = myModel.getModelGroup().getEntityNamed(parentName);
      }
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

    Set classProperties = myEntityMap.getSet("classProperties");
    if (classProperties != null) {
      Iterator classPropertiesIter = classProperties.iterator();
      while (classPropertiesIter.hasNext()) {
        String attributeName = (String) classPropertiesIter.next();
        IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
        if (attribute != null) {
          attribute.setClassProperty(Boolean.TRUE, false);
        }
      }
    }

    Set primaryKeyAttributes = myEntityMap.getSet("primaryKeyAttributes");
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

    Map internalInfo = myEntityMap.getMap("internalInfo");
    if (internalInfo != null) {
      EOModelMap internalInfoModelMap = new EOModelMap(internalInfo);
      Set clientClassPropertyNames = internalInfoModelMap.getSet("_clientClassPropertyNames");
      if (clientClassPropertyNames != null) {
        Iterator clientClassPropertyNameIter = clientClassPropertyNames.iterator();
        while (clientClassPropertyNameIter.hasNext()) {
          String attributeName = (String) clientClassPropertyNameIter.next();
          IEOAttribute attribute = getAttributeOrRelationshipNamed(attributeName);
          if (attribute != null) {
            attribute.setClientClassProperty(Boolean.TRUE, false);
          }
        }
      }
    }
  }

  public void verify(Set _failures) {
    if (!StringUtils.isUppercaseFirstLetter(myName)) {
      _failures.add(new EOModelVerificationFailure("Entity names should be capitalized, but " + myModel.getName() + "/" + myName + " is not."));
    }
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

    //    EOEntity parent = getParent();
    //    if (parent != null && getRestrictingQualifier() == null) {
    //      if (ComparisonUtils.equals(getExternalName(), parent.getExternalName()) {
    //        _failures.add(new EOModelVerificationFailure(myModel.getName() + "/" + getName() + " is a subclass of " + getParent().getName() + " but does not have a restricting qualifier."));
    //      }
    //    }
  }

  public String toString() {
    return "[EOEntity: name = " + myName + "; attributes = " + myAttributes + "; relationships = " + myRelationships + "; fetchSpecs = " + myFetchSpecs + "]"; //$NON-NLS-4$ //$NON-NLS-5$
  }
}

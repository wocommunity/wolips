package org.objectstyle.wolips.eomodeler.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectstyle.cayenne.wocompat.PropertyListSerialization;

public class EOEntity {
  private EOModel myModel;
  private String myName;
  private String myExternalName;
  private String myClassName;
  private String myParent;
  private String myRestrictingQualifier;
  private String myExternalQuery;
  private Boolean myCachesObjects;
  private Boolean myAbstractEntity;
  private Boolean myReadOnly;
  private Integer myMaxNumberOfInstancesToBatchFetch;
  private List myAttributes;
  private List myRelationships;
  private List myFetchSpecs;
  private Map myUserInfo;
  private EOModelMap myEntityMap;
  private EOModelMap myFetchSpecsMap;

  public EOEntity(EOModel _model) {
    myModel = _model;
    myAttributes = new LinkedList();
    myRelationships = new LinkedList();
    myFetchSpecs = new LinkedList();
    myEntityMap = new EOModelMap();
    myFetchSpecsMap = new EOModelMap();
  }

  public EOModel getModel() {
    return myModel;
  }

  public String getExternalQuery() {
    return myExternalQuery;
  }

  public void setExternalQuery(String _externalQuery) {
    myExternalQuery = _externalQuery;
  }

  public Integer getMaxNumberOfInstancesToBatchFetch() {
    return myMaxNumberOfInstancesToBatchFetch;
  }

  public void setMaxNumberOfInstancesToBatchFetch(Integer _maxNumberOfInstancesToBatchFetch) {
    myMaxNumberOfInstancesToBatchFetch = _maxNumberOfInstancesToBatchFetch;
  }

  public Boolean isReadOnly() {
    return myReadOnly;
  }

  public void setReadOnly(Boolean _readOnly) {
    myReadOnly = _readOnly;
  }

  public String getName() {
    return myName;
  }

  public void setName(String _name) {
    myModel._checkForDuplicateEntityName(this, _name);
    myModel._entityNameChanged(_name, myName);
    myName = _name;
  }

  public String getClassName() {
    return myClassName;
  }

  public void setClassName(String _className) {
    myClassName = _className;
  }

  public String getExternalName() {
    return myExternalName;
  }

  public void setExternalName(String _externalName) {
    myExternalName = _externalName;
  }

  public EOEntity getParent() {
    return myModel.getModelGroup().getEntityNamed(myParent);
  }

  public void setParent(EOEntity _entity) {
    if (_entity == null) {
      myParent = null;
    }
    else {
      myParent = _entity.getName();
    }
  }

  public Boolean isAbstractEntity() {
    return myAbstractEntity;
  }

  public void setAbstractEntity(Boolean _abstractEntity) {
    myAbstractEntity = _abstractEntity;
  }

  public Boolean isCachesObjects() {
    return myCachesObjects;
  }

  public void setCachesObjects(Boolean _cachesObjects) {
    myCachesObjects = _cachesObjects;
  }

  public String getRestrictingQualifier() {
    return myRestrictingQualifier;
  }

  public void setRestrictingQualifier(String _restrictingQualifier) {
    myRestrictingQualifier = _restrictingQualifier;
  }

  public List getAttributes() {
    return myAttributes;
  }

  public List getRelationships() {
    return myRelationships;
  }

  public List getFetchSpecs() {
    return myFetchSpecs;
  }

  public void _checkForDuplicateAttributeName(EOAttribute _attribute, String _newName) {
    EOAttribute attribute = getAttributeNamed(_newName);
    if (attribute != null && attribute != _attribute) {
      throw new IllegalArgumentException("There is already an attribute named '" + _newName + "' in " + this + ".");
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

  public void _checkForDuplicateFetchSpecName(EOFetchSpecification _fetchSpec, String _newName) {
    EOFetchSpecification fetchSpec = getFetchSpecNamed(_newName);
    if (fetchSpec != null && fetchSpec != _fetchSpec) {
      throw new IllegalArgumentException("There is already a fetch specification named '" + _newName + "' in " + this + ".");
    }
  }

  public void addFetchSpecification(EOFetchSpecification _fetchSpecification) {
    _checkForDuplicateFetchSpecName(_fetchSpecification, _fetchSpecification.getName());
    myFetchSpecs.add(_fetchSpecification);
  }

  public void removeFetchSpecification(EOFetchSpecification _fetchSpecification) {
    myFetchSpecs.remove(_fetchSpecification);
  }

  public void addAttribute(EOAttribute _attribute) {
    _checkForDuplicateAttributeName(_attribute, _attribute.getName());
    myAttributes.add(_attribute);
  }

  public void removeAttribute(EOAttribute _attribute) {
    myAttributes.remove(_attribute);
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
      if (attribute.getName().equals(_name)) {
        matchingAttribute = attribute;
      }
    }
    return matchingAttribute;
  }

  public void _checkForDuplicateRelationshipName(EORelationship _relationship, String _newName) {
    EORelationship relationship = getRelationshipNamed(_newName);
    if (relationship != null && relationship != _relationship) {
      throw new IllegalArgumentException("There is already an relationship named '" + _newName + "' in " + this + ".");
    }
  }

  public void addRelationship(EORelationship _relationship) {
    _checkForDuplicateRelationshipName(_relationship, _relationship.getName());
    myRelationships.add(_relationship);
  }

  public void removeRelationship(EORelationship _relationship) {
    myRelationships.remove(_relationship);
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

  public void setUserInfo(Map _userInfo) {
    myUserInfo = _userInfo;
  }

  public Map getUserInfo() {
    return myUserInfo;
  }

  public void loadFromFile(File _entityFile, File _fetchSpecFile) throws IOException {
    if (_entityFile.exists()) {
      EOModelMap entityMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(_entityFile));
      loadFromMap(entityMap);
    }
    if (_fetchSpecFile.exists()) {
      EOModelMap fspecMap = new EOModelMap((Map) PropertyListSerialization.propertyListFromFile(_fetchSpecFile));
      loadFetchSpecsFromMap(fspecMap);
    }
  }

  public void loadFromMap(EOModelMap _entityMap) {
    myEntityMap = _entityMap;
    myName = _entityMap.getString("name", true);
    myExternalName = _entityMap.getString("externalName", true);
    myClassName = _entityMap.getString("className", true);
    myParent = _entityMap.getString("parent", true);
    myCachesObjects = _entityMap.getBoolean("cachesObjects");
    myAbstractEntity = _entityMap.getBoolean("isAbstractEntity");
    myReadOnly = _entityMap.getBoolean("readOnly");
    myRestrictingQualifier = _entityMap.getString("restrictingQualifier", true);
    myExternalQuery = _entityMap.getString("externalQuery", true);
    myMaxNumberOfInstancesToBatchFetch = _entityMap.getInteger("maxNumberOfInstancesToBatchFetch");
    myUserInfo = _entityMap.getMap("userInfo", true);

    //Map fetchSpecifications = _entityMap.getMap("fetchSpecificationDictionary");
    // TODO: Fetch Specs

    List attributeList = _entityMap.getList("attributes");
    if (attributeList != null) {
      Iterator attributeIter = attributeList.iterator();
      while (attributeIter.hasNext()) {
        EOModelMap attributeMap = new EOModelMap((Map) attributeIter.next());
        EOAttribute attribute = new EOAttribute(this);
        attribute.loadFromMap(attributeMap);
        addAttribute(attribute);
      }
    }

    List relationshipList = _entityMap.getList("relationships");
    if (relationshipList != null) {
      Iterator relationshipIter = relationshipList.iterator();
      while (relationshipIter.hasNext()) {
        EOModelMap relationshipMap = new EOModelMap((Map) relationshipIter.next());
        EORelationship relationship = new EORelationship(this);
        relationship.loadFromMap(relationshipMap);
        addRelationship(relationship);
      }
    }

    List attributesUsedForLocking = _entityMap.getList("attributesUsedForLocking");
    if (attributesUsedForLocking != null) {
      Iterator attributesUsedForLockingIter = attributesUsedForLocking.iterator();
      while (attributesUsedForLockingIter.hasNext()) {
        String attributeName = (String) attributesUsedForLockingIter.next();
        EOAttribute attribute = getAttributeNamed(attributeName);
        if (attribute != null) {
          attribute.setUsedForLocking(Boolean.TRUE);
        }
      }
    }

    List classProperties = _entityMap.getList("classProperties");
    if (classProperties != null) {
      Iterator classPropertiesIter = classProperties.iterator();
      while (classPropertiesIter.hasNext()) {
        String attributeName = (String) classPropertiesIter.next();
        IEOAttribute attribute = _getAttributeNamed(attributeName);
        if (attribute != null) {
          attribute.setClassProperty(Boolean.TRUE);
        }
      }
    }

    List primaryKeyAttributes = _entityMap.getList("primaryKeyAttributes");
    if (primaryKeyAttributes != null) {
      Iterator primaryKeyAttributesIter = primaryKeyAttributes.iterator();
      while (primaryKeyAttributesIter.hasNext()) {
        String attributeName = (String) primaryKeyAttributesIter.next();
        EOAttribute attribute = getAttributeNamed(attributeName);
        if (attribute != null) {
          attribute.setPrimaryKey(Boolean.TRUE);
        }
      }
    }

    Map internalInfo = _entityMap.getMap("internalInfo");
    if (internalInfo != null) {
      List clientClassPropertyNames = _entityMap.getList("_clientClassPropertyNames");
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

  public void loadFetchSpecsFromMap(EOModelMap _map) {
    myFetchSpecsMap = _map;
    Iterator fetchSpecIter = _map.entrySet().iterator();
    while (fetchSpecIter.hasNext()) {
      Map.Entry fetchSpecEntry = (Map.Entry) fetchSpecIter.next();
      String fetchSpecName = (String) fetchSpecEntry.getKey();
      EOModelMap fetchSpecMap = new EOModelMap((Map) fetchSpecEntry.getValue());
      EOFetchSpecification fetchSpec = new EOFetchSpecification(this, fetchSpecName);
      fetchSpec.loadFromMap(_map);
      addFetchSpecification(fetchSpec);
    }
  }

  public EOModelMap toEntityMap() {
    EOModelMap entityMap = myEntityMap.cloneModelMap();
    entityMap.setString("name", myName, true);
    entityMap.setString("externalName", myExternalName, true);
    entityMap.setString("className", myClassName, true);
    entityMap.setString("parent", myParent, true);
    entityMap.setBoolean("cachesObjects", myCachesObjects);
    entityMap.setBoolean("isAbstractEntity", myAbstractEntity);
    entityMap.setBoolean("readOnly", myReadOnly);
    entityMap.setString("restrictingQualifier", myRestrictingQualifier, true);
    entityMap.setString("externalQuery", myExternalQuery, true);
    entityMap.setInteger("maxNumberOfInstancesToBatchFetch", myMaxNumberOfInstancesToBatchFetch);

    //Map fetchSpecifications = _entityMap.getMap("fetchSpecificationDictionary");
    // TODO: Fetch Specs

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
    entityMap.setList("attributes", attributes);

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
    entityMap.setList("relationships", relationships);
    entityMap.setList("attributesUsedForLocking", attributesUsedForLocking);
    entityMap.setList("classProperties", classProperties);
    entityMap.setList("primaryKeyAttributes", primaryKeyAttributes);

    Map internalInfoMap = entityMap.getMap("internalInfo");
    if (internalInfoMap == null) {
      internalInfoMap = new HashMap();
      entityMap.put("internalInfo", internalInfoMap);
    }
    internalInfoMap.put("_clientClassPropertyNames", clientClassProperties);

    entityMap.put("userInfo", myUserInfo);

    return entityMap;
  }

  public EOModelMap toFetchSpecsMap() {
    EOModelMap fetchSpecsMap = myFetchSpecsMap.cloneModelMap();
    fetchSpecsMap.clear();
    Iterator fetchSpecIter = myFetchSpecs.iterator();
    while (fetchSpecIter.hasNext()) {
      EOFetchSpecification fetchSpec = (EOFetchSpecification) fetchSpecIter.next();
      EOModelMap fetchSpecMap = fetchSpec.toMap();
      fetchSpecsMap.setMap(fetchSpec.getName(), fetchSpecMap);
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

  public void verify(List _failures) {
    if (myParent != null && getParent() == null) {
      _failures.add(new MissingEntityFailure(myParent));
    }

    // TODO

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
    return "[EOEntity: name = " + myName + "; attributes = " + myAttributes + "; relationships = " + myRelationships + "; fetchSpecs = " + myFetchSpecs + "]";
  }
}

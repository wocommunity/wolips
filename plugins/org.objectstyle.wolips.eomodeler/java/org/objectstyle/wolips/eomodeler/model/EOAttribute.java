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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.kvc.IKey;
import org.objectstyle.wolips.eomodeler.kvc.ResolvedKey;
import org.objectstyle.wolips.eomodeler.utils.BooleanUtils;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.StringUtils;

public class EOAttribute extends UserInfoableEOModelObject implements IEOAttribute, ISortableEOModelObject {
  public static final String PRIMARY_KEY = "primaryKey";
  public static final String CLASS_PROPERTY = "classProperty";
  public static final String USED_FOR_LOCKING = "usedForLocking";
  public static final String ALLOWS_NULL = "allowsNull";
  public static final String PROTOTYPE = "prototype";
  public static final String NAME = "name";
  public static final String COLUMN_NAME = "columnName";
  public static final String ADAPTOR_VALUE_CONVERSION_METHOD_NAME = "adaptorValueConversionMethodName";
  public static final String EXTERNAL_TYPE = "externalType";
  public static final String FACTORY_METHOD_ARGUMENT_TYPE = "factoryMethodArgumentType";
  public static final String PRECISION = "precision";
  public static final String SCALE = "scale";
  public static final String VALUE_CLASS_NAME = "valueClassName";
  public static final String VALUE_FACTORY_METHOD_NAME = "valueFactoryMethodName";
  public static final String VALUE_TYPE = "valueType";
  public static final String DEFINITION = "definition";
  public static final String WIDTH = "width";
  public static final String READ_FORMAT = "readFormat";
  public static final String WRITE_FORMAT = "writeFormat";
  public static final String CLIENT_CLASS_PROPERTY = "clientClassProperty";
  public static final String INDEXED = "indexed";
  public static final String READ_ONLY = "readOnly";
  public static final String DATA_TYPE = "dataType";

  private static final String[] PROTOTYPED_PROPERTIES = { EOAttribute.COLUMN_NAME, EOAttribute.ALLOWS_NULL, EOAttribute.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, EOAttribute.EXTERNAL_TYPE, EOAttribute.FACTORY_METHOD_ARGUMENT_TYPE, EOAttribute.PRECISION, EOAttribute.SCALE, EOAttribute.VALUE_CLASS_NAME, EOAttribute.VALUE_FACTORY_METHOD_NAME, EOAttribute.VALUE_TYPE, EOAttribute.DEFINITION, EOAttribute.WIDTH, EOAttribute.READ_FORMAT, EOAttribute.WRITE_FORMAT, EOAttribute.INDEXED, EOAttribute.READ_ONLY };

  private static Map myCachedPropertyKeys;

  static {
    myCachedPropertyKeys = new HashMap();
  }

  protected static synchronized IKey getPropertyKey(String _property) {
    IKey key = (IKey) myCachedPropertyKeys.get(_property);
    if (key == null) {
      key = new ResolvedKey(EOAttribute.class, _property);
      myCachedPropertyKeys.put(_property, key);
    }
    return key;
  }

  private EOEntity myEntity;
  private EOAttribute myPrototype;
  private String myName;
  private String myColumnName;
  private String myExternalType;
  private String myValueType;
  private String myValueClassName;
  private String myValueFactoryMethodName;
  private EOFactoryMethodArgumentType myFactoryMethodArgumentType;
  private String myAdaptorValueConversionMethodName;
  private Integer myScale;
  private Integer myPrecision;
  private Integer myWidth;
  private Boolean myAllowsNull;
  private Boolean myClassProperty;
  private Boolean myPrimaryKey;
  private Boolean myUsedForLocking;
  private Boolean myClientClassProperty;
  private Boolean myIndexed;
  private Boolean myReadOnly;
  private String myDefinition;
  private String myReadFormat;
  private String myWriteFormat;
  private EOModelMap myAttributeMap;
  private EODataType myDataType;

  public EOAttribute() {
    myAttributeMap = new EOModelMap();
  }

  public EOAttribute(String _name) {
    this();
    myName = _name;
  }

  public EOAttribute(String _name, String _definition) {
    this(_name);
    myDefinition = _definition;
  }

  public void pasted() {
    // DO NOTHING
  }

  public EOAttribute cloneAttribute() {
    EOAttribute attribute = new EOAttribute(myName);
    attribute.myPrototype = myPrototype;
    attribute.myColumnName = myColumnName;
    attribute.myExternalType = myExternalType;
    attribute.myValueType = myValueType;
    attribute.myValueClassName = myValueClassName;
    attribute.myValueFactoryMethodName = myValueFactoryMethodName;
    attribute.myFactoryMethodArgumentType = myFactoryMethodArgumentType;
    attribute.myAdaptorValueConversionMethodName = myAdaptorValueConversionMethodName;
    attribute.myScale = myScale;
    attribute.myPrecision = myPrecision;
    attribute.myWidth = myWidth;
    attribute.myAllowsNull = myAllowsNull;
    attribute.myClassProperty = myClassProperty;
    attribute.myPrimaryKey = myPrimaryKey;
    attribute.myUsedForLocking = myUsedForLocking;
    attribute.myClientClassProperty = myClientClassProperty;
    attribute.myIndexed = myIndexed;
    attribute.myReadOnly = myReadOnly;
    attribute.myDefinition = myDefinition;
    attribute.myReadFormat = myReadFormat;
    attribute.myWriteFormat = myWriteFormat;
    return attribute;
  }

  protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
    if (myEntity != null) {
      myEntity._attributeChanged(this, _propertyName, _oldValue, _newValue);
    }
  }

  public int hashCode() {
    return ((myEntity == null) ? 1 : myEntity.hashCode()) * ((myName == null) ? super.hashCode() : myName.hashCode());
  }

  public boolean equals(Object _obj) {
    boolean equals = false;
    if (_obj instanceof EOAttribute) {
      EOAttribute attribute = (EOAttribute) _obj;
      equals = (attribute == this) || (ComparisonUtils.equals(attribute.myEntity, myEntity) && ComparisonUtils.equals(attribute.myName, myName));
    }
    return equals;
  }

  public boolean isPrototyped(String _property) {
    boolean prototyped = false;
    if (myPrototype != null) {
      IKey key = EOAttribute.getPropertyKey(_property);
      Object value = key.getValue(this);
      if (value != null) {
        Object prototypeValue = key.getValue(myPrototype);
        prototyped = value.equals(prototypeValue);
      }
    }
    return prototyped;
  }

  public boolean isPrototyped() {
    boolean prototyped = false;
    EOAttribute prototype = getPrototype();
    if (prototype != null) {
      prototyped = true;
    }
    return prototyped;
  }

  public boolean isFlattened() {
    return getDefinition() != null;
  }

  public boolean isInherited() {
    boolean inherited = false;
    if (myEntity != null) {
      EOEntity parent = myEntity.getParent();
      if (parent != null) {
        EOAttribute attribute = parent.getAttributeNamed(myName);
        inherited = (attribute != null);
      }
    }
    return inherited;
  }

  public EOAttribute getPrototype() {
    return myPrototype;
  }

  public void setPrototype(EOAttribute _prototype) {
    setPrototype(_prototype, true);
  }

  public void setPrototype(EOAttribute _prototype, boolean _updateFromPrototype) {
    EOAttribute oldPrototype = getPrototype();
    boolean prototypeNameChanged = true;
    if (_prototype == null && oldPrototype == null) {
      prototypeNameChanged = false;
    }
    else if (ComparisonUtils.equals(_prototype, oldPrototype)) {
      prototypeNameChanged = false;
    }

    EODataType oldDataType = getDataType();
    Map oldValues = new HashMap();
    for (int propertyNum = 0; propertyNum < PROTOTYPED_PROPERTIES.length; propertyNum++) {
      String propertyName = PROTOTYPED_PROPERTIES[propertyNum];
      Object oldValue = EOAttribute.getPropertyKey(propertyName).getValue(this);
      oldValues.put(propertyName, oldValue);
    }
    myPrototype = _prototype;
    firePropertyChange(EOAttribute.PROTOTYPE, oldPrototype, _prototype);
    if (prototypeNameChanged && _updateFromPrototype) {
      for (int propertyNum = 0; propertyNum < PROTOTYPED_PROPERTIES.length; propertyNum++) {
        String propertyName = PROTOTYPED_PROPERTIES[propertyNum];
        IKey propertyKey = EOAttribute.getPropertyKey(propertyName);
        Object newValue = propertyKey.getValue(this);
        Object oldValue = oldValues.get(propertyName);
        propertyKey.setValue(this, newValue);
        firePropertyChange(propertyName, oldValue, newValue);
      }
      updateDataType(oldDataType);
    }
  }

  protected Object _prototypeValueIfNull(String _property, Object _value) {
    Object value = _value;
    if ((value == null || (value instanceof String && ((String) _value).length() == 0)) && myPrototype != null) {
      value = EOAttribute.getPropertyKey(_property).getValue(myPrototype);
    }
    return value;
  }

  protected Object _nullIfPrototyped(String _property, Object _value) {
    Object value = _value;
    if (value != null && myPrototype != null && value.equals(EOAttribute.getPropertyKey(_property).getValue(myPrototype))) {
      value = null;
    }
    return value;
  }

  public void setName(String _name) throws DuplicateNameException {
    setName(_name, true);
  }

  public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
    String oldName = getName();
    String name = (String) _prototypeValueIfNull(EOAttribute.NAME, _name);
    if (name == null) {
      throw new NullPointerException(Messages.getString("EOAttribute.noBlankAttributeNames"));
    }
    if (myEntity != null) {
      myEntity._checkForDuplicateAttributeName(this, name, null);
    }
    myName = (String) _nullIfPrototyped(EOAttribute.NAME, name);
    if (_fireEvents) {
      firePropertyChange(EOAttribute.NAME, oldName, getName());
    }
  }

  public String getName() {
    return (String) _prototypeValueIfNull(EOAttribute.NAME, myName);
  }

  public Boolean getReadOnly() {
    return isReadOnly();
  }

  public Boolean isReadOnly() {
    return (Boolean) _prototypeValueIfNull(EOAttribute.READ_ONLY, myReadOnly);
  }

  public void setReadOnly(Boolean _readOnly) {
    setReadOnly(_readOnly, true);
  }

  public void setReadOnly(Boolean _readOnly, boolean _fireEvents) {
    Boolean oldReadOnly = getAllowsNull();
    myReadOnly = (Boolean) _nullIfPrototyped(EOAttribute.READ_ONLY, _readOnly);
    if (_fireEvents) {
      firePropertyChange(EOAttribute.READ_ONLY, oldReadOnly, getReadOnly());
    }
  }

  public Boolean getIndexed() {
    return isIndexed();
  }

  public Boolean isIndexed() {
    return (Boolean) _prototypeValueIfNull(EOAttribute.INDEXED, myIndexed);
  }

  public void setIndexed(Boolean _indexed) {
    setIndexed(_indexed, true);
  }

  public void setIndexed(Boolean _indexed, boolean _fireEvents) {
    Boolean oldIndexed = getIndexed();
    myIndexed = (Boolean) _nullIfPrototyped(EOAttribute.INDEXED, _indexed);
    if (_fireEvents) {
      firePropertyChange(EOAttribute.INDEXED, oldIndexed, getIndexed());
    }
  }

  public Boolean getAllowsNull() {
    return isAllowsNull();
  }

  public Boolean isAllowsNull() {
    return (Boolean) _prototypeValueIfNull(EOAttribute.ALLOWS_NULL, myAllowsNull);
  }

  public void setAllowsNull(Boolean _allowsNull) {
    setAllowsNull(_allowsNull, true);
  }

  public void setAllowsNull(Boolean _allowsNull, boolean _fireEvents) {
    Boolean oldAllowsNull = getAllowsNull();
    Boolean newAllowsNull = _allowsNull;
    if (_fireEvents && BooleanUtils.isTrue(getPrimaryKey())) {
      newAllowsNull = Boolean.FALSE;
    }
    myAllowsNull = (Boolean) _nullIfPrototyped(EOAttribute.ALLOWS_NULL, newAllowsNull);
    if (_fireEvents) {
      firePropertyChange(EOAttribute.ALLOWS_NULL, oldAllowsNull, getAllowsNull());
    }
  }

  public Boolean getClassProperty() {
    return isClassProperty();
  }

  public Boolean isClassProperty() {
    return myClassProperty;//(Boolean) _prototypeValueIfNull(EOAttribute.CLASS_PROPERTY, myClassProperty);
  }

  public void setClassProperty(Boolean _classProperty) {
    setClassProperty(_classProperty, true);
  }

  public void setClassProperty(Boolean _classProperty, boolean _fireEvents) {
    Boolean oldClassProperty = getClassProperty();
    //myClassProperty = (Boolean) _nullIfPrototyped(EOAttribute.CLASS_PROPERTY, _classProperty);
    myClassProperty = _classProperty;
    if (_fireEvents) {
      firePropertyChange(EOAttribute.CLASS_PROPERTY, oldClassProperty, getClassProperty());
    }
  }

  public String getColumnName() {
    return (String) _prototypeValueIfNull(EOAttribute.COLUMN_NAME, myColumnName);
  }

  public void setColumnName(String _columnName) {
    String oldColumnName = getColumnName();
    myColumnName = (String) _nullIfPrototyped(EOAttribute.COLUMN_NAME, _columnName);
    //myColumnName = _columnName;
    firePropertyChange(EOAttribute.COLUMN_NAME, oldColumnName, getColumnName());
  }

  public void _setEntity(EOEntity _entity) {
    myEntity = _entity;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public Boolean getPrimaryKey() {
    return isPrimaryKey();
  }

  public Boolean isPrimaryKey() {
    //return (Boolean) _prototypeValueIfNull(EOAttribute.PRIMARY_KEY, myPrimaryKey);
    return myPrimaryKey;
  }

  public void setPrimaryKey(Boolean _primaryKey) {
    setPrimaryKey(_primaryKey, true);
  }

  public void setPrimaryKey(Boolean _primaryKey, boolean _fireEvents) {
    Boolean oldPrimaryKey = getPrimaryKey();
    //myPrimaryKey = (Boolean) _nullIfPrototyped(EOAttribute.PRIMARY_KEY, _primaryKey);
    myPrimaryKey = _primaryKey;
    if (_fireEvents && BooleanUtils.isTrue(_primaryKey)) {
      setAllowsNull(Boolean.FALSE, _fireEvents);
    }
    if (_fireEvents) {
      firePropertyChange(EOAttribute.PRIMARY_KEY, oldPrimaryKey, getPrimaryKey());
    }
  }

  public Boolean getUsedForLocking() {
    return isUsedForLocking();
  }

  public Boolean isUsedForLocking() {
    //return (Boolean) _prototypeValueIfNull(EOAttribute.USED_FOR_LOCKING, myUsedForLocking);
    return myUsedForLocking;
  }

  public void setUsedForLocking(Boolean _usedForLocking) {
    setUsedForLocking(_usedForLocking, true);
  }

  public void setUsedForLocking(Boolean _usedForLocking, boolean _fireEvents) {
    Boolean oldUsedForLocking = getUsedForLocking();
    //myUsedForLocking = (Boolean) _nullIfPrototyped(EOAttribute.USED_FOR_LOCKING, _usedForLocking);
    myUsedForLocking = _usedForLocking;
    if (_fireEvents) {
      firePropertyChange(EOAttribute.USED_FOR_LOCKING, oldUsedForLocking, getUsedForLocking());
    }
  }

  public String getAdaptorValueConversionMethodName() {
    return (String) _prototypeValueIfNull(EOAttribute.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, myAdaptorValueConversionMethodName);
  }

  public void setAdaptorValueConversionMethodName(String _adaptorValueConversionMethodName) {
    String oldAdaptorValueConversionMethodName = getAdaptorValueConversionMethodName();
    myAdaptorValueConversionMethodName = (String) _nullIfPrototyped(EOAttribute.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, _adaptorValueConversionMethodName);
    firePropertyChange(EOAttribute.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, oldAdaptorValueConversionMethodName, getAdaptorValueConversionMethodName());
  }

  public String getExternalType() {
    return (String) _prototypeValueIfNull(EOAttribute.EXTERNAL_TYPE, myExternalType);
  }

  public void setExternalType(String _externalType) {
    String oldExternalType = getExternalType();
    myExternalType = (String) _nullIfPrototyped(EOAttribute.EXTERNAL_TYPE, _externalType);
    firePropertyChange(EOAttribute.EXTERNAL_TYPE, oldExternalType, getExternalType());
  }

  public EOFactoryMethodArgumentType getFactoryMethodArgumentType() {
    return (EOFactoryMethodArgumentType) _prototypeValueIfNull(EOAttribute.FACTORY_METHOD_ARGUMENT_TYPE, myFactoryMethodArgumentType);
  }

  public void setFactoryMethodArgumentType(EOFactoryMethodArgumentType _factoryMethodArgumentType) {
    EOFactoryMethodArgumentType oldFactoryMethodArgumentType = getFactoryMethodArgumentType();
    myFactoryMethodArgumentType = (EOFactoryMethodArgumentType) _nullIfPrototyped(EOAttribute.FACTORY_METHOD_ARGUMENT_TYPE, _factoryMethodArgumentType);
    firePropertyChange(EOAttribute.FACTORY_METHOD_ARGUMENT_TYPE, oldFactoryMethodArgumentType, getFactoryMethodArgumentType());
  }

  public Integer getPrecision() {
    return (Integer) _prototypeValueIfNull(EOAttribute.PRECISION, myPrecision);
  }

  public void setPrecision(Integer _precision) {
    Integer oldPrecision = getPrecision();
    myPrecision = (Integer) _nullIfPrototyped(EOAttribute.PRECISION, _precision);
    firePropertyChange(EOAttribute.PRECISION, oldPrecision, getPrecision());
  }

  public Integer getScale() {
    return (Integer) _prototypeValueIfNull(EOAttribute.SCALE, myScale);
  }

  public void setScale(Integer _scale) {
    Integer oldScale = getScale();
    myScale = (Integer) _nullIfPrototyped(EOAttribute.SCALE, _scale);
    firePropertyChange(EOAttribute.SCALE, oldScale, getScale());
  }

  public synchronized EODataType getDataType() {
    EODataType dataType = myDataType;
    if (dataType == null) {
      if (getValueFactoryMethodName() != null || getAdaptorValueConversionMethodName() != null) {
        dataType = EODataType.CUSTOM;
      }
      else {
        dataType = EODataType.getDataTypeByValueClassAndType(getValueClassName(), getValueType());
      }
      myDataType = dataType;
    }
    return dataType;
  }

  public void setDataType(EODataType _dataType) {
    EODataType oldDataType = getDataType();
    EODataType dataType = _dataType;
    if (dataType == null) {
      dataType = EODataType.CUSTOM;
    }
    setValueClassName(dataType.getValueClass(), false);
    setValueType(dataType.getFirstValueType(), false);
    myDataType = dataType;
    updateDataType(oldDataType);
  }

  protected void updateDataType(EODataType _oldDataType) {
    EODataType dataType = getDataType();
    firePropertyChange(EOAttribute.DATA_TYPE, _oldDataType, dataType);
  }

  public String getValueClassName() {
    return (String) _prototypeValueIfNull(EOAttribute.VALUE_CLASS_NAME, myValueClassName);
  }

  public void setValueClassName(String _valueClassName) {
    setValueClassName(_valueClassName, true);
  }

  public synchronized void setValueClassName(String _valueClassName, boolean _updateDataType) {
    EODataType oldDataType = getDataType();
    String oldValueClassName = getValueClassName();
    myValueClassName = (String) _nullIfPrototyped(EOAttribute.VALUE_CLASS_NAME, _valueClassName);
    myDataType = null;
    firePropertyChange(EOAttribute.VALUE_CLASS_NAME, oldValueClassName, getValueClassName());
    if (_updateDataType) {
      updateDataType(oldDataType);
    }
  }

  public String getValueFactoryMethodName() {
    return (String) _prototypeValueIfNull(EOAttribute.VALUE_FACTORY_METHOD_NAME, myValueFactoryMethodName);
  }

  public void setValueFactoryMethodName(String _valueFactoryMethodName) {
    String oldValueFactoryMethodName = getValueFactoryMethodName();
    myValueFactoryMethodName = (String) _nullIfPrototyped(EOAttribute.VALUE_FACTORY_METHOD_NAME, _valueFactoryMethodName);
    firePropertyChange(EOAttribute.VALUE_FACTORY_METHOD_NAME, oldValueFactoryMethodName, getValueFactoryMethodName());
  }

  public String getValueType() {
    return (String) _prototypeValueIfNull(EOAttribute.VALUE_TYPE, myValueType);
  }

  public void setValueType(String _valueType) {
    setValueType(_valueType, false);
  }

  public synchronized void setValueType(String _valueType, boolean _updateDataType) {
    EODataType oldDataType = getDataType();
    String oldValueType = getValueType();
    myValueType = (String) _nullIfPrototyped(EOAttribute.VALUE_TYPE, _valueType);
    myDataType = null;
    firePropertyChange(EOAttribute.VALUE_TYPE, oldValueType, getValueType());
    if (_updateDataType) {
      updateDataType(oldDataType);
    }
  }

  public Integer getWidth() {
    return (Integer) _prototypeValueIfNull(EOAttribute.WIDTH, myWidth);
  }

  public void setWidth(Integer _width) {
    Integer oldWidth = getWidth();
    myWidth = (Integer) _nullIfPrototyped(EOAttribute.WIDTH, _width);
    firePropertyChange(EOAttribute.WIDTH, oldWidth, getWidth());
  }

  public String getDefinition() {
    return (String) _prototypeValueIfNull(EOAttribute.DEFINITION, myDefinition);
  }

  public void setDefinition(String _definition) {
    String oldDefinition = getDefinition();
    myDefinition = (String) _nullIfPrototyped(EOAttribute.DEFINITION, _definition);
    firePropertyChange(EOAttribute.DEFINITION, oldDefinition, getDefinition());
  }

  public String getReadFormat() {
    return (String) _prototypeValueIfNull(EOAttribute.READ_FORMAT, myReadFormat);
  }

  public void setReadFormat(String _readFormat) {
    String oldReadFormat = getReadFormat();
    myReadFormat = (String) _nullIfPrototyped(EOAttribute.READ_FORMAT, _readFormat);
    firePropertyChange(EOAttribute.READ_FORMAT, oldReadFormat, getReadFormat());
  }

  public String getWriteFormat() {
    return (String) _prototypeValueIfNull(EOAttribute.WRITE_FORMAT, myWriteFormat);
  }

  public void setWriteFormat(String _writeFormat) {
    String oldWriteFormat = getWriteFormat();
    myWriteFormat = (String) _nullIfPrototyped(EOAttribute.WRITE_FORMAT, _writeFormat);
    firePropertyChange(EOAttribute.WRITE_FORMAT, oldWriteFormat, getWriteFormat());
  }

  public void setClientClassProperty(Boolean _clientClassProperty) {
    setClientClassProperty(_clientClassProperty, false);
  }

  public void setClientClassProperty(Boolean _clientClassProperty, boolean _fireEvents) {
    Boolean oldClientClassProperty = getClientClassProperty();
    //myClientClassProperty = (Boolean) _nullIfPrototyped(EOAttribute.CLIENT_CLASS_PROPERTY, _clientClassProperty);
    myClientClassProperty = _clientClassProperty;
    if (_fireEvents) {
      firePropertyChange(EOAttribute.CLIENT_CLASS_PROPERTY, oldClientClassProperty, getClientClassProperty());
    }
  }

  public Boolean getClientClassProperty() {
    return isClientClassProperty();
  }

  public Boolean isClientClassProperty() {
    //return (Boolean) _prototypeValueIfNull(EOAttribute.CLIENT_CLASS_PROPERTY, myClientClassProperty);
    return myClientClassProperty;
  }

  public Set getReferenceFailures() {
    Set referenceFailures = new HashSet();
    Iterator referencingRelationshipsIter = getReferencingRelationships(true).iterator();
    while (referencingRelationshipsIter.hasNext()) {
      EORelationship referencingRelationship = (EORelationship) referencingRelationshipsIter.next();
      referenceFailures.add(new EOAttributeRelationshipReferenceFailure(this, referencingRelationship));
    }
    return referenceFailures;
  }

  public List getReferencingRelationships(boolean _includeInheritedAttributes) {
    List referencingRelationships = new LinkedList();
    if (myEntity != null) {
      Iterator modelsIter = getEntity().getModel().getModelGroup().getModels().iterator();
      while (modelsIter.hasNext()) {
        EOModel model = (EOModel) modelsIter.next();
        Iterator entitiesIter = model.getEntities().iterator();
        while (entitiesIter.hasNext()) {
          EOEntity entity = (EOEntity) entitiesIter.next();
          Iterator relationshipsIter = entity.getRelationships().iterator();
          while (relationshipsIter.hasNext()) {
            EORelationship relationship = (EORelationship) relationshipsIter.next();
            if (relationship.isRelatedTo(this)) {
              referencingRelationships.add(relationship);
            }
          }
        }
      }

      if (_includeInheritedAttributes && myEntity != null) {
        Iterator childrenEntitiesIter = myEntity.getChildrenEntities().iterator();
        while (childrenEntitiesIter.hasNext()) {
          EOEntity childEntity = (EOEntity) childrenEntitiesIter.next();
          EOAttribute childAttribute = childEntity.getAttributeNamed(myName);
          referencingRelationships.addAll(childAttribute.getReferencingRelationships(_includeInheritedAttributes));
        }
      }
    }
    return referencingRelationships;
  }

  public void loadFromMap(EOModelMap _attributeMap, Set _failures) {
    myAttributeMap = _attributeMap;
    myName = _attributeMap.getString("name", true);
    myColumnName = _attributeMap.getString("columnName", true);
    myExternalType = _attributeMap.getString("externalType", true);
    myScale = _attributeMap.getInteger("scale");
    myPrecision = _attributeMap.getInteger("precision");
    myWidth = _attributeMap.getInteger("width");
    myValueType = _attributeMap.getString("valueType", true);
    myValueClassName = _attributeMap.getString("valueClassName", true);
    myValueFactoryMethodName = _attributeMap.getString("valueFactoryMethodName", true);
    myFactoryMethodArgumentType = EOFactoryMethodArgumentType.getFactoryMethodArgumentTypeByID(_attributeMap.getString("factoryMethodArgumentType", true));
    myAdaptorValueConversionMethodName = _attributeMap.getString("adaptorValueConversionMethodName", true);
    myAllowsNull = _attributeMap.getBoolean("allowsNull");
    myReadOnly = _attributeMap.getBoolean("isReadOnly");
    myIndexed = _attributeMap.getBoolean("isIndexed");
    myDefinition = _attributeMap.getString("definition", true);
    myReadFormat = _attributeMap.getString("readFormat", true);
    myWriteFormat = _attributeMap.getString("writeFormat", true);
    setUserInfo(_attributeMap.getMap("userInfo", true), false);
  }

  public EOModelMap toMap() {
    EOModelMap attributeMap = myAttributeMap.cloneModelMap();
    attributeMap.setString("name", myName, true);
    if (myPrototype != null) {
      attributeMap.setString("prototypeName", myPrototype.getName(), true);
    }
    attributeMap.setString("columnName", (myColumnName == null) ? "" : myColumnName, false);
    attributeMap.setString("externalType", myExternalType, true);
    attributeMap.setInteger("scale", myScale);
    attributeMap.setInteger("precision", myPrecision);
    attributeMap.setInteger("width", myWidth);
    attributeMap.setString("valueType", myValueType, true);
    attributeMap.setString("valueClassName", myValueClassName, true);
    attributeMap.setString("valueFactoryMethodName", myValueFactoryMethodName, true);
    if (myFactoryMethodArgumentType != null) {
      attributeMap.setString("factoryMethodArgumentType", myFactoryMethodArgumentType.getID(), true);
    }
    attributeMap.setString("adaptorValueConversionMethodName", myAdaptorValueConversionMethodName, true);
    attributeMap.setBoolean("allowsNull", myAllowsNull, EOModelMap.YN);
    attributeMap.setBoolean("isReadOnly", myReadOnly, EOModelMap.YN);
    attributeMap.setBoolean("isIndexed", myIndexed, EOModelMap.YN);
    attributeMap.setString("definition", myDefinition, true);
    attributeMap.setString("readFormat", myReadFormat, true);
    attributeMap.setString("writeFormat", myWriteFormat, true);
    return attributeMap;
  }

  public void resolve(Set _failures) {
    String prototypeName = myAttributeMap.getString("prototypeName", true);
    if (prototypeName != null && myEntity != null) {
      myPrototype = myEntity.getModel().getModelGroup().getPrototypeAttributeNamed(prototypeName);
      if (myPrototype == null) {
        _failures.add(new MissingPrototypeFailure(prototypeName, this));
      }
    }
  }

  public void verify(Set _failures) {
    String name = getName();
    if (name == null || name.trim().length() == 0) {
      _failures.add(new EOModelVerificationFailure(myEntity.getModel().getName() + "/" + myEntity.getName() + "/" + myName + " has an empty name."));
    }
    else {
      if (name.indexOf(' ') != -1) {
        _failures.add(new EOModelVerificationFailure(myEntity.getModel().getName() + "/" + myEntity.getName() + "/" + myName + "'s name has a space in it."));
      }
      if (!StringUtils.isLowercaseFirstLetter(name)) {
        _failures.add(new EOModelVerificationFailure("Attribute names should not be capitalized, but " + myEntity.getModel().getName() + "/" + myEntity.getName() + "/" + myName + " is."));
      }
    }
    if (!myEntity.isPrototype()) {
      if (!isFlattened()) {
        String columnName = getColumnName();
        if (columnName == null || columnName.trim().length() == 0) {
          _failures.add(new EOModelVerificationFailure(myEntity.getModel().getName() + "/" + myEntity.getName() + "/" + myName + " does not have a column name set."));
        }
        else if (columnName.indexOf(' ') != -1) {
          _failures.add(new EOModelVerificationFailure(myEntity.getModel().getName() + "/" + myEntity.getName() + "/" + myName + "'s column name '" + columnName + "' has a space in it."));
        }
      }
    }
  }

  public String toString() {
    return "[EOAttribute: " + myName + "]";
  }
}

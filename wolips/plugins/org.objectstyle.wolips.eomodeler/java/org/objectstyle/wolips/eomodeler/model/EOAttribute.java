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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EOAttribute implements IEOAttribute {
  public static final String PRIMARY_KEY = "Primary Key";
  public static final String CLASS_PROPERTY = "Class Property";
  public static final String LOCKING = "Locking";
  public static final String ALLOW_NULL = "Allow Null";
  public static final String PROTOTYPE = "Prototype";
  public static final String NAME = "Name";
  public static final String COLUMN = "Column";

  private EOEntity myEntity;
  private String myName;
  private String myColumnName;
  private String myPrototypeName;
  private String myExternalType;
  private String myValueType;
  private String myValueClassName;
  private String myValueFactoryMethodName;
  private String myFactoryMethodArgumentType;
  private String myAdaptorValueConversionMethodName;
  private Integer myScale;
  private Integer myPrecision;
  private Integer myWidth;
  private Boolean myAllowsNull;
  private Boolean myClassProperty;
  private Boolean myPrimaryKey;
  private Boolean myUsedForLocking;
  private Boolean myClientClassProperty;
  private String myDefinition;
  private String myReadFormat;
  private String myWriteFormat;
  private Map myUserInfo;
  private EOModelMap myAttributeMap;

  public EOAttribute(EOEntity _entity) {
    myEntity = _entity;
    myAttributeMap = new EOModelMap();
  }

  public int hashCode() {
    return myEntity.hashCode() * myName.hashCode();
  }

  public boolean equals(Object _obj) {
    return (_obj instanceof EOAttribute && ((EOAttribute) _obj).myEntity.equals(myEntity) && ((EOAttribute) _obj).myName.equals(myName));
  }

  public boolean isPrototyped() {
    boolean prototyped = false;
    EOAttribute prototype = getPrototype();
    if (prototype != null) {
      prototyped = true;
    }
    return prototyped;
  }

  public boolean isInherited() {
    boolean inherited = false;
    EOEntity parent = myEntity.getParent();
    if (parent != null) {
      EOAttribute attribute = parent.getAttributeNamed(myName);
      inherited = (attribute != null);
    }
    return inherited;
  }

  public EOAttribute getPrototype() {
    return myEntity.getModel().getModelGroup().getPrototypeAttributeNamed(myPrototypeName);
  }

  public void setPrototype(EOAttribute _prototype, boolean _updateFromPrototype) {
    if (_prototype == null) {
      setPrototypeName(null, _updateFromPrototype);
    }
    else {
      setPrototypeName(_prototype.getName(), _updateFromPrototype);
    }
  }

  public String getPrototypeName() {
    return myPrototypeName;
  }

  public void setPrototypeName(String _prototypeName, boolean _updateFromPrototype) {
    boolean prototypeNameChanged = true;
    if (_prototypeName == null && myPrototypeName == null) {
      prototypeNameChanged = false;
    }
    else if ((_prototypeName != null && _prototypeName.equals(myPrototypeName)) || (myPrototypeName != null && myPrototypeName.equals(_prototypeName))) {
      prototypeNameChanged = false;
    }
    myPrototypeName = _prototypeName;
    if (prototypeNameChanged && _updateFromPrototype) {
      EOAttribute prototype = getPrototype();
      _updateFromPrototype(prototype);
    }
  }

  protected void _updateFromPrototype(EOAttribute _prototype) {
    if (_prototype != null) {
      setColumnName((String) _nullIfPrototyped(getColumnName(), _prototype.getColumnName()));
      setExternalType((String) _nullIfPrototyped(getExternalType(), _prototype.getExternalType()));
      setScale((Integer) _nullIfPrototyped(getScale(), _prototype.getScale()));
      setPrecision((Integer) _nullIfPrototyped(getPrecision(), _prototype.getPrecision()));
      setWidth((Integer) _nullIfPrototyped(getWidth(), _prototype.getWidth()));
      setValueType((String) _nullIfPrototyped(getValueType(), _prototype.getValueType()));
      setValueClassName((String) _nullIfPrototyped(getValueClassName(), _prototype.getValueClassName()));
      setValueFactoryMethodName((String) _nullIfPrototyped(getValueFactoryMethodName(), _prototype.getValueFactoryMethodName()));
      setFactoryMethodArgumentType((String) _nullIfPrototyped(getFactoryMethodArgumentType(), _prototype.getFactoryMethodArgumentType()));
      setAdaptorValueConversionMethodName((String) _nullIfPrototyped(getAdaptorValueConversionMethodName(), _prototype.getAdaptorValueConversionMethodName()));
      setAllowsNull((Boolean) _nullIfPrototyped(isAllowsNull(), _prototype.isAllowsNull()));
      setClassProperty((Boolean) _nullIfPrototyped(isClassProperty(), _prototype.isClassProperty()));
      setClientClassProperty((Boolean) _nullIfPrototyped(isClientClassProperty(), _prototype.isClientClassProperty()));
      setPrimaryKey((Boolean) _nullIfPrototyped(isPrimaryKey(), _prototype.isPrimaryKey()));
      setUsedForLocking((Boolean) _nullIfPrototyped(isUsedForLocking(), _prototype.isUsedForLocking()));
      setDefinition((String) _nullIfPrototyped(getDefinition(), _prototype.getDefinition()));
      setReadFormat((String) _nullIfPrototyped(getReadFormat(), _prototype.getReadFormat()));
      setWriteFormat((String) _nullIfPrototyped(getWriteFormat(), _prototype.getWriteFormat()));
    }
  }

  protected Object _nullIfPrototyped(Object _currentValue, Object _prototypeValue) {
    Object value = _currentValue;
    if (_isSet(_prototypeValue)) {
      value = null;
    }
    return value;
  }

  protected boolean _isSet(Object _value) {
    return (_value != null && (!(_value instanceof String) || ((String) _value).trim().length() > 0));
  }

  public void setName(String _name) throws DuplicateAttributeNameException {
    myEntity._checkForDuplicateAttributeName(this, _name);
    myName = _name;
  }

  public String getName() {
    return myName;
  }

  public Boolean isAllowsNull() {
    return myAllowsNull;
  }

  public void setAllowsNull(Boolean _allowsNull) {
    if (myPrimaryKey != null && myPrimaryKey.booleanValue()) {
      myAllowsNull = Boolean.FALSE;
    }
    else {
      myAllowsNull = _allowsNull;
    }
  }

  public Boolean isClassProperty() {
    return myClassProperty;
  }

  public void setClassProperty(Boolean _classProperty) {
    myClassProperty = _classProperty;
  }

  public String getColumnName() {
    return myColumnName;
  }

  public void setColumnName(String _columnName) {
    myColumnName = _columnName;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
  }

  public Boolean isPrimaryKey() {
    return myPrimaryKey;
  }

  public void setPrimaryKey(Boolean _primaryKey) {
    myPrimaryKey = _primaryKey;
    if (_primaryKey != null && _primaryKey.booleanValue()) {
      setAllowsNull(Boolean.FALSE);
    }
  }

  public Boolean isUsedForLocking() {
    return myUsedForLocking;
  }

  public void setUsedForLocking(Boolean _usedForLocking) {
    myUsedForLocking = _usedForLocking;
  }

  public String getAdaptorValueConversionMethodName() {
    return myAdaptorValueConversionMethodName;
  }

  public void setAdaptorValueConversionMethodName(String _adaptorValueConversionMethodName) {
    myAdaptorValueConversionMethodName = _adaptorValueConversionMethodName;
  }

  public String getExternalType() {
    return myExternalType;
  }

  public void setExternalType(String _externalType) {
    myExternalType = _externalType;
  }

  public String getFactoryMethodArgumentType() {
    return myFactoryMethodArgumentType;
  }

  public void setFactoryMethodArgumentType(String _factoryMethodArgumentType) {
    myFactoryMethodArgumentType = _factoryMethodArgumentType;
  }

  public Integer getPrecision() {
    return myPrecision;
  }

  public void setPrecision(Integer _precision) {
    myPrecision = _precision;
  }

  public Integer getScale() {
    return myScale;
  }

  public void setScale(Integer _scale) {
    myScale = _scale;
  }

  public Map getUserInfo() {
    return myUserInfo;
  }

  public void setUserInfo(Map _userInfo) {
    myUserInfo = _userInfo;
  }

  public String getValueClassName() {
    return myValueClassName;
  }

  public void setValueClassName(String _valueClassName) {
    myValueClassName = _valueClassName;
  }

  public String getValueFactoryMethodName() {
    return myValueFactoryMethodName;
  }

  public void setValueFactoryMethodName(String _valueFactoryMethodName) {
    myValueFactoryMethodName = _valueFactoryMethodName;
  }

  public String getValueType() {
    return myValueType;
  }

  public void setValueType(String _valueType) {
    myValueType = _valueType;
  }

  public Integer getWidth() {
    return myWidth;
  }

  public void setWidth(Integer _width) {
    myWidth = _width;
  }

  public String getDefinition() {
    return myDefinition;
  }

  public void setDefinition(String _definition) {
    myDefinition = _definition;
  }

  public String getReadFormat() {
    return myReadFormat;
  }

  public void setReadFormat(String _readFormat) {
    myReadFormat = _readFormat;
  }

  public String getWriteFormat() {
    return myWriteFormat;
  }

  public void setWriteFormat(String _writeFormat) {
    myWriteFormat = _writeFormat;
  }

  public void setClientClassProperty(Boolean _clientClassProperty) {
    myClientClassProperty = _clientClassProperty;
  }

  public Boolean isClientClassProperty() {
    return myClientClassProperty;
  }

  public List getReferencingRelationships() {
    List referencingRelationships = new LinkedList();
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
    return referencingRelationships;
  }

  public void loadFromMap(EOModelMap _attributeMap) {
    myAttributeMap = _attributeMap;
    myName = _attributeMap.getString("name", true);
    myColumnName = _attributeMap.getString("columnName", true);
    myPrototypeName = _attributeMap.getString("prototypeName", true);
    myExternalType = _attributeMap.getString("externalType", true);
    myScale = _attributeMap.getInteger("scale");
    myPrecision = _attributeMap.getInteger("precision");
    myWidth = _attributeMap.getInteger("width");
    myValueType = _attributeMap.getString("valueType", true);
    myValueClassName = _attributeMap.getString("valueClassName", true);
    myValueFactoryMethodName = _attributeMap.getString("valueFactoryMethodName", true);
    myFactoryMethodArgumentType = _attributeMap.getString("factoryMethodArgumentType", true);
    myAdaptorValueConversionMethodName = _attributeMap.getString("adaptorValueConversionMethodName", true);
    myAllowsNull = _attributeMap.getBoolean("allowsNull");
    myDefinition = _attributeMap.getString("definition", true);
    myReadFormat = _attributeMap.getString("readFormat", true);
    myWriteFormat = _attributeMap.getString("writeFormat", true);
    myUserInfo = _attributeMap.getMap("userInfo", true);
  }

  public EOModelMap toMap() {
    EOModelMap attributeMap = myAttributeMap.cloneModelMap();
    attributeMap.setString("name", myName, true);
    attributeMap.setString("columnName", myColumnName, true);
    attributeMap.setString("prototypeName", myPrototypeName, true);
    attributeMap.setString("externalType", myExternalType, true);
    attributeMap.setInteger("scale", myScale);
    attributeMap.setInteger("precision", myPrecision);
    attributeMap.setInteger("width", myWidth);
    attributeMap.setString("valueType", myValueType, true);
    attributeMap.setString("valueClassName", myValueClassName, true);
    attributeMap.setString("valueFactoryMethodName", myValueFactoryMethodName, true);
    attributeMap.setString("factoryMethodArgumentType", myFactoryMethodArgumentType, true);
    attributeMap.setString("adaptorValueConversionMethodName", myAdaptorValueConversionMethodName, true);
    attributeMap.setBoolean("allowsNull", myAllowsNull);
    attributeMap.setString("definition", myDefinition, true);
    attributeMap.setString("readFormat", myReadFormat, true);
    attributeMap.setString("writeFormat", myWriteFormat, true);
    attributeMap.setMap("userInfo", myUserInfo);
    return attributeMap;
  }

  public void verify(List _failures) {
    // TODO
    if (myPrototypeName != null && getPrototype() == null) {
      _failures.add(new MissingPrototypeFailure(myPrototypeName, this));
    }
  }

  public String toString() {
    return "[EOAttribute: " + myName + "]";
  }
}

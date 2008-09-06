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
package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Set;

import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;

public abstract class AbstractEOArgument<T extends EOModelObject> extends UserInfoableEOModelObject<T> implements ISortableEOModelObject {
	public static final String ALLOWS_NULL = "allowsNull";

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

	public static final String DATA_TYPE = "dataType";

	public static final String SERVER_TIME_ZONE = "serverTimeZone";

	private String myName;

	private String myColumnName;

	private String myExternalType;

	private String myValueType;

	private String myValueClassName;

	private boolean _usesClassNameProperty;

	private String myValueFactoryMethodName;

	private EOFactoryMethodArgumentType myFactoryMethodArgumentType;

	private String myAdaptorValueConversionMethodName;

	private Integer myScale;

	private Integer myPrecision;

	private Integer myWidth;

	private Boolean myAllowsNull;

	private String myDefinition;

	private String myServerTimeZone;

	private EOModelMap myArgumentMap;

	private EODataType myDataType;

	public AbstractEOArgument() {
		myArgumentMap = new EOModelMap();
	}

	public AbstractEOArgument(String _name) {
		this();
		myName = _name;
	}

	public AbstractEOArgument(String _name, String _definition) {
		this(_name);
		myDefinition = _definition;
	}

	public void pasted() {
		// DO NOTHING
	}

	protected abstract AbstractEOArgument _createArgument(String _name);

	protected AbstractEOArgument _cloneArgument() {
		AbstractEOArgument argument = _createArgument(myName);
		_cloneIntoArgument(argument, false);
		_cloneUserInfoInto(argument);
		return argument;
	}

	public void _cloneIntoArgument(AbstractEOArgument argument, boolean updatingFlattenedAttribute) {
		if (updatingFlattenedAttribute) {
			argument.myColumnName = "";
		} else {
			argument.myColumnName = myColumnName;
		}
		argument.myExternalType = myExternalType;
		argument.myValueType = myValueType;
		argument._usesClassNameProperty = _usesClassNameProperty;
		argument.myValueClassName = myValueClassName;
		argument.myValueFactoryMethodName = myValueFactoryMethodName;
		argument.myFactoryMethodArgumentType = myFactoryMethodArgumentType;
		argument.myAdaptorValueConversionMethodName = myAdaptorValueConversionMethodName;
		argument.myScale = myScale;
		argument.myPrecision = myPrecision;
		argument.myServerTimeZone = myServerTimeZone;
		argument.myWidth = myWidth;
		argument.myAllowsNull = myAllowsNull;
		if (!updatingFlattenedAttribute) {
			argument.myDefinition = myDefinition;
		}
	}

	public int hashCode() {
		return (myName == null) ? super.hashCode() : myName.hashCode();
	}

	public boolean equals(Object _obj) {
		boolean equals = false;
		if (_obj instanceof AbstractEOArgument) {
			AbstractEOArgument argument = (AbstractEOArgument) _obj;
			equals = (argument == this) || (ComparisonUtils.equals(argument.myName, myName));
		}
		return equals;
	}

	public boolean isFlattened() {
		return StringUtils.isKeyPath(_getDefinition());
	}

	public void setName(String _name) throws DuplicateNameException {
		setName(_name, true);
	}

	@SuppressWarnings("unused")
	public void setName(String _name, boolean _fireEvents) throws DuplicateNameException {
		String oldName = getName();
		myName = _name;
		if (_fireEvents) {
			firePropertyChange(AbstractEOArgument.NAME, oldName, getName());
		}
	}

	public String getName() {
		return myName;
	}

	public Boolean getAllowsNull() {
		return isAllowsNull();
	}

	public Boolean isAllowsNull() {
		return myAllowsNull;
	}

	public void setAllowsNull(Boolean _allowsNull) {
		_setAllowsNull(_allowsNull, true);
	}

	public void _setAllowsNull(Boolean _allowsNull, boolean _fireEvents) {
		setAllowsNull(_allowsNull, _fireEvents);
	}

	public void setAllowsNull(Boolean _allowsNull, boolean _fireEvents) {
		Boolean oldAllowsNull = getAllowsNull();
		myAllowsNull = _allowsNull;
		if (_fireEvents) {
			firePropertyChange(AbstractEOArgument.ALLOWS_NULL, oldAllowsNull, getAllowsNull());
		}
	}

	public String getColumnName() {
		return myColumnName;
	}

	public void setColumnName(String _columnName) {
		String oldColumnName = getColumnName();
		myColumnName = _columnName;
		firePropertyChange(AbstractEOArgument.COLUMN_NAME, oldColumnName, getColumnName());
	}

	public String getAdaptorValueConversionMethodName() {
		return myAdaptorValueConversionMethodName;
	}

	public void setAdaptorValueConversionMethodName(String _adaptorValueConversionMethodName) {
		String oldAdaptorValueConversionMethodName = getAdaptorValueConversionMethodName();
		myAdaptorValueConversionMethodName = _adaptorValueConversionMethodName;
		firePropertyChange(AbstractEOArgument.ADAPTOR_VALUE_CONVERSION_METHOD_NAME, oldAdaptorValueConversionMethodName, getAdaptorValueConversionMethodName());
	}

	public String getExternalType() {
		return myExternalType;
	}

	public void setExternalType(String _externalType) {
		String oldExternalType = getExternalType();
		myExternalType = _externalType;
		firePropertyChange(AbstractEOArgument.EXTERNAL_TYPE, oldExternalType, getExternalType());
	}

	public EOFactoryMethodArgumentType getFactoryMethodArgumentType() {
		return myFactoryMethodArgumentType;
	}

	public void setFactoryMethodArgumentType(EOFactoryMethodArgumentType _factoryMethodArgumentType) {
		EOFactoryMethodArgumentType oldFactoryMethodArgumentType = getFactoryMethodArgumentType();
		myFactoryMethodArgumentType = _factoryMethodArgumentType;
		firePropertyChange(AbstractEOArgument.FACTORY_METHOD_ARGUMENT_TYPE, oldFactoryMethodArgumentType, getFactoryMethodArgumentType());
	}

	public Integer getPrecision() {
		return myPrecision;
	}

	public void setPrecision(Integer _precision) {
		Integer oldPrecision = getPrecision();
		myPrecision = _precision;
		firePropertyChange(AbstractEOArgument.PRECISION, oldPrecision, getPrecision());
	}

	public Integer getScale() {
		return myScale;
	}

	public void setScale(Integer _scale) {
		Integer oldScale = getScale();
		myScale = _scale;
		firePropertyChange(AbstractEOArgument.SCALE, oldScale, getScale());
	}

	public String getServerTimeZone() {
		return myServerTimeZone;
	}

	public void setServerTimeZone(String _serverTimeZone) {
		String oldServerTimeZone = getServerTimeZone();
		myServerTimeZone = _serverTimeZone;
		firePropertyChange(AbstractEOArgument.SERVER_TIME_ZONE, oldServerTimeZone, getServerTimeZone());
	}

	public synchronized EODataType getDataType() {
		EODataType dataType = myDataType;
		if (dataType == null) {
			if (getValueFactoryMethodName() != null || getAdaptorValueConversionMethodName() != null) {
				dataType = EODataType.CUSTOM;
			} else {
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
		setFactoryMethodArgumentType(null);
		setAdaptorValueConversionMethodName(null);
		// MS: I think clearing external type may be a little aggressive
		// setExternalType(null);
		setPrecision(null);
		setScale(null);
		setServerTimeZone(null);
		setValueFactoryMethodName(null);
		setWidth(null);
		setValueClassName(dataType.getValueClass(), false);
		setValueType(dataType.getFirstValueType(), false);
		myDataType = dataType;
		updateDataType(oldDataType);
	}

	protected void updateDataType(EODataType _oldDataType) {
		EODataType dataType = getDataType();
		firePropertyChange(AbstractEOArgument.DATA_TYPE, _oldDataType, dataType);
	}

	public String getJavaClassName() {
		return getJavaClassName(true);
	}

	protected String _convertJavaClassNameToValueClassName(String javaClassName) {
		String className = javaClassName;
		if (className == null) {
			className = null;
		} else if (className.equals("java.lang.String")) {
			className = "NSString";
		} else if (className.equals("java.lang.Number")) {
			className = "NSNumber";
		} else if (className.equals("java.math.BigDecimal")) {
			className = "NSDecimalNumber";
		} else if (className.equals("com.webobjects.foundation.NSTimestamp")) {
			className = "NSCalendarDate";
		} else if (className.equals("com.webobjects.foundation.NSData")) {
			className = "NSData";
		} else if (className.equals("")) {
			className = null;
		} else {
			//String[] strs = className.split("\\.");
			//className = strs[strs.length - 1];
			className = javaClassName;
		}
		return className;
	}

	public String getJavaClassName(boolean shorten) {
		String className = getValueClassName();
		if (shorten && className != null && className.startsWith("java.lang.")) {
			className = className.substring("java.lang.".length());
		}
		if ("java.lang.Number".equals(className) || "Number".equals(className) || "NSNumber".equals(className)) {
			String valueType = getValueType();
			if (valueType == null || valueType.length() == 0) {
				className = "java.lang.Integer";
			} else if ("B".equals(valueType)) {
				className = "java.lang.BigDecimal";
			} else if ("b".equals(valueType)) {
				className = "java.lang.Byte";
			} else if ("d".equals(valueType)) {
				className = "java.lang.Double";
			} else if ("f".equals(valueType)) {
				className = "java.lang.Float";
			} else if ("i".equals(valueType)) {
				className = "java.lang.Integer";
			} else if ("l".equals(valueType)) {
				className = "java.lang.Long";
			} else if ("s".equals(valueType)) {
				className = "java.lang.Short";
			} else if ("c".equals(valueType)) {
				className = "java.lang.Boolean";
			}
		} else if ("NSString".equals(className)) {
			className = "java.lang.String";
		} else if ("NSCalendarDate".equals(className)) {
			className = "NSTimestamp";
		} else if ("NSDecimalNumber".equals(className)) {
			String valueType = getValueType();
			if (valueType == null || valueType.length() == 0) {
				className = "java.lang.Integer";
			} else {
				className = "java.lang.BigDecimal";
			}
		}
		if (shorten && className != null && className.startsWith("java.lang.")) {
			className = className.substring("java.lang.".length());
		}
		return className;
	}

	public String getValueClassName() {
		return myValueClassName;
	}

	public void setValueClassName(String _valueClassName) {
		setValueClassName(_valueClassName, true);
	}

	public synchronized void setValueClassName(String _valueClassName, boolean _updateDataType) {
		EODataType oldDataType = getDataType();
		String oldValueClassName = getValueClassName();
		myValueClassName = _valueClassName;
		myDataType = null;
		firePropertyChange(AbstractEOArgument.VALUE_CLASS_NAME, oldValueClassName, getValueClassName());
		if (_updateDataType) {
			updateDataType(oldDataType);
		}
	}

	public String getValueFactoryMethodName() {
		return myValueFactoryMethodName;
	}

	public void setValueFactoryMethodName(String _valueFactoryMethodName) {
		String oldValueFactoryMethodName = getValueFactoryMethodName();
		myValueFactoryMethodName = _valueFactoryMethodName;
		firePropertyChange(AbstractEOArgument.VALUE_FACTORY_METHOD_NAME, oldValueFactoryMethodName, getValueFactoryMethodName());
	}

	public String getValueType() {
		return myValueType;
	}

	public void setValueType(String _valueType) {
		setValueType(_valueType, false);
	}

	public synchronized void setValueType(String _valueType, boolean _updateDataType) {
		EODataType oldDataType = getDataType();
		String oldValueType = getValueType();
		myValueType = _valueType;
		myDataType = null;
		firePropertyChange(AbstractEOArgument.VALUE_TYPE, oldValueType, getValueType());
		if (_updateDataType) {
			updateDataType(oldDataType);
		}
	}

	public Integer getWidth() {
		return myWidth;
	}

	public void setWidth(Integer _width) {
		Integer oldWidth = getWidth();
		myWidth = _width;
		firePropertyChange(AbstractEOArgument.WIDTH, oldWidth, getWidth());
	}

	public void setDefinition(String _definition) {
		String oldDefinition = myDefinition;
		myDefinition = _definition;
		updateDefinitionPath();
		firePropertyChange(AbstractEOArgument.DEFINITION, oldDefinition, getDefinition());
	}

	public String getDefinition() {
		return _getDefinition();
	}

	protected String _getDefinition() {
		return myDefinition;
	}

	protected void updateDefinitionPath() {
		// DO NOTHING
	}

	@SuppressWarnings("unused")
	public void loadFromMap(EOModelMap _argumentMap, Set<EOModelVerificationFailure> _failures) {
		myArgumentMap = _argumentMap;
		myName = _argumentMap.getString("name", true);
		if (_argumentMap.containsKey("externalName")) {
			myColumnName = _argumentMap.getString("externalName", true);
		} else {
			myColumnName = _argumentMap.getString("columnName", true);
		}
		myExternalType = _argumentMap.getString("externalType", true);
		myScale = _argumentMap.getInteger("scale");
		myPrecision = _argumentMap.getInteger("precision");
		if (_argumentMap.containsKey("maximumLength")) {
			myWidth = _argumentMap.getInteger("maximumLength");
		} else {
			myWidth = _argumentMap.getInteger("width");
		}
		myValueType = _argumentMap.getString("valueType", true);
		myValueClassName = _argumentMap.getString("valueClassName", true);
		if (myValueClassName == null) {
			myValueClassName = _convertJavaClassNameToValueClassName(_argumentMap.getString("className", true));
			_usesClassNameProperty = true;
		}
		myValueFactoryMethodName = _argumentMap.getString("valueFactoryMethodName", true);
		myFactoryMethodArgumentType = EOFactoryMethodArgumentType.getFactoryMethodArgumentTypeByID(_argumentMap.getString("factoryMethodArgumentType", true));
		myAdaptorValueConversionMethodName = _argumentMap.getString("adaptorValueConversionMethodName", true);
		myServerTimeZone = _argumentMap.getString("serverTimeZone", true);
		myAllowsNull = _argumentMap.getBoolean("allowsNull");
		myDefinition = _argumentMap.getString("definition", true);
		loadUserInfo(_argumentMap);
	}

	public EOModelMap toMap() {
		EOModelMap argumentMap = myArgumentMap.cloneModelMap();
		argumentMap.setString("name", getName(), true);
		// If columnName is prototyped, EOModeler leaves out the columnName. If,
		// however, columnName is MISSING, then it needs to write a "".
		if (isFlattened()) {
			argumentMap.remove("columnName");
		} else if (myColumnName == null && getColumnName() == null) {
			argumentMap.setString("columnName", "", false);
		} else {
			argumentMap.setString("columnName", myColumnName, false);
		}
		argumentMap.remove("externalName");
		argumentMap.setString("externalType", myExternalType, true);
		argumentMap.setInteger("scale", myScale);
		argumentMap.setInteger("precision", myPrecision);
		argumentMap.setInteger("width", myWidth);
		argumentMap.setString("serverTimeZone", myServerTimeZone, true);
		argumentMap.remove("maximumLength");
		argumentMap.setString("valueType", myValueType, true);
		if (_usesClassNameProperty) {
			argumentMap.setString("className", getJavaClassName(false), true);
		} else {
			argumentMap.setString("valueClassName", myValueClassName, true);
		}
		argumentMap.setString("valueFactoryMethodName", myValueFactoryMethodName, true);
		if (myFactoryMethodArgumentType != null) {
			argumentMap.setString("factoryMethodArgumentType", myFactoryMethodArgumentType.getID(), true);
		} else {
			argumentMap.remove("factoryMethodArgumentType");
		}
		argumentMap.setString("adaptorValueConversionMethodName", myAdaptorValueConversionMethodName, true);

		if (isPrototyped()) {
			argumentMap.setBoolean("allowsNull", myAllowsNull, EOModelMap.YN);
		} else {
			argumentMap.setBoolean("allowsNull", myAllowsNull, EOModelMap.YNOptionalDefaultNo);
		}

		argumentMap.setString("definition", myDefinition, true);
		writeUserInfo(argumentMap);
		return argumentMap;
	}

	protected boolean isPrototyped() {
		return false;
	}

	public EOModelMap getArgumentMap() {
		return myArgumentMap;
	}

	public String toString() {
		return "[EOArgument: " + myName + "]";
	}
}

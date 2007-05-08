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

import org.objectstyle.wolips.eomodeler.core.Messages;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;

public class EODataType {
	public static final EODataType STRING = new EODataType("NSString", "String", new String[] { null, "" }, Messages.getString("EODataType.string"));
	public static final EODataType STRING_SET = new EODataType("NSString", "String", "S", Messages.getString("EODataType.stringSetString"));
	public static final EODataType STRING_CHAR = new EODataType("NSString", "String", "C", Messages.getString("EODataType.stringChar"));
	public static final EODataType STRING_UTF = new EODataType("NSString", "String", "E", Messages.getString("EODataType.stringUTF"));
	public static final EODataType STRING_RTRIM = new EODataType("NSString", "String", "c", Messages.getString("EODataType.stringRTRIM"));
	public static final EODataType BYTE = new EODataType("NSNumber", "Byte", "b", Messages.getString("EODataType.byte"));
	public static final EODataType SHORT = new EODataType("NSNumber", "Short", "s", Messages.getString("EODataType.short"));
	public static final EODataType INTEGER = new EODataType("NSNumber", "Integer", new String[] { "i", null, "" }, Messages.getString("EODataType.integer")); //$NON-NLS-4$
	public static final EODataType LONG = new EODataType("NSNumber", "Long", "l", Messages.getString("EODataType.long"));
	public static final EODataType FLOAT = new EODataType("NSNumber", "Float", "f", Messages.getString("EODataType.float"));
	public static final EODataType DOUBLE = new EODataType("NSNumber", "Double", "d", Messages.getString("EODataType.double"));
	public static final EODataType BOOLEAN = new EODataType("NSNumber", "boolean", "c", Messages.getString("EODataType.boolean"));
	public static final EODataType BIGDECIMAL = new EODataType("NSNumber", "BigDecimal", "B", Messages.getString("EODataType.bigDecimal"));
	public static final EODataType DECIMAL_NUMBER = new EODataType("NSDecimalNumber", "BigDecimal", (String) null, Messages.getString("EODataType.decimalNumber"));
	public static final EODataType DATE_OBJ = new EODataType("NSCalendarDate", "NSTimestamp", new String[] { null, "" }, Messages.getString("EODataType.dateObj"));
	public static final EODataType DATE = new EODataType("NSCalendarDate", "NSTimestamp", "D", Messages.getString("EODataType.date"));
	public static final EODataType TIME = new EODataType("NSCalendarDate", "NSTimestamp", "t", Messages.getString("EODataType.time"));
	public static final EODataType TIMESTAMP = new EODataType("NSCalendarDate", "NSTimestamp", "T", Messages.getString("EODataType.timestamp"));
	public static final EODataType DATE_MSSQL = new EODataType("NSCalendarDate", "NSTimestamp", "M", Messages.getString("EODataType.dateMSSQL"));
	public static final EODataType DATA = new EODataType("NSData", "NSData", (String) null, Messages.getString("EODataType.data"));
	public static final EODataType CUSTOM = new EODataType(null, "Custom", (String) null, Messages.getString("EODataType.custom"));
	public static final EODataType[] DATA_TYPES = new EODataType[] { EODataType.STRING, EODataType.STRING_SET, EODataType.STRING_CHAR, EODataType.STRING_UTF, EODataType.STRING_RTRIM, EODataType.BYTE, EODataType.SHORT, EODataType.INTEGER, EODataType.LONG, EODataType.FLOAT, EODataType.DOUBLE, EODataType.BIGDECIMAL, EODataType.DECIMAL_NUMBER, EODataType.BOOLEAN, EODataType.DATE_OBJ, EODataType.DATE, EODataType.TIME, EODataType.TIMESTAMP, EODataType.DATE_MSSQL, EODataType.DATA, EODataType.CUSTOM };

	private String myValueClass;

	private String myJavaValueClass;
	
	private String[] myValueTypes;

	private String myName;

	public EODataType(String _valueClass, String _javaValueClass, String _valueType, String _name) {
		this(_valueClass, _javaValueClass, new String[] { _valueType }, _name);
	}

	public EODataType(String _valueClass, String _javaValueClass, String[] _valueTypes, String _name) {
		myValueClass = _valueClass;
		myJavaValueClass = _javaValueClass;
		myValueTypes = _valueTypes;
		myName = _name;
	}

	public String getJavaValueClass() {
		return myJavaValueClass;
	}
	
	public String getValueClass() {
		return myValueClass;
	}

	public String getFirstValueType() {
		return myValueTypes[0];
	}

	public String[] getValueTypes() {
		return myValueTypes;
	}

	public String getName() {
		return myName;
	}

	public String toString() {
		return "[EODataType: name = " + myName + "]";
	}

	public static EODataType getDataTypeByValueClassAndType(String _valueClass, String _valueType) {
		EODataType matchingDataType = null;
		for (int dataTypeNum = 0; matchingDataType == null && dataTypeNum < EODataType.DATA_TYPES.length; dataTypeNum++) {
			EODataType dataType = EODataType.DATA_TYPES[dataTypeNum];
			if (ComparisonUtils.equals(dataType.myValueClass, _valueClass)) {
				for (int valueTypeNum = 0; matchingDataType == null && valueTypeNum < dataType.myValueTypes.length; valueTypeNum++) {
					if (ComparisonUtils.equals(dataType.myValueTypes[valueTypeNum], _valueType)) {
						matchingDataType = dataType;
					}
				}
			}
		}
		if (matchingDataType == null) {
			matchingDataType = EODataType.CUSTOM;
		}
		return matchingDataType;
	}

	public static EODataType getDataTypeByValueClass(String _valueClass) {
		EODataType matchingDataType = null;
		for (int dataTypeNum = 0; matchingDataType == null && dataTypeNum < EODataType.DATA_TYPES.length; dataTypeNum++) {
			EODataType dataType = EODataType.DATA_TYPES[dataTypeNum];
			if (ComparisonUtils.equals(dataType.myValueClass, _valueClass)) {
				matchingDataType = dataType;
			}
		}
		if (matchingDataType == null) {
			matchingDataType = EODataType.CUSTOM;
		}
		return matchingDataType;
	}
}

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
import java.util.Map;

import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public class EODataType {
  public static final EODataType STRING = new EODataType("NSString", new String[] { null, "", "c", "S" }, Messages.getString("EODataType.string")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
  public static final EODataType DECIMAL_NUMBER = new EODataType("NSDecimalNumber", (String) null, Messages.getString("EODataType.decimalNumber")); //$NON-NLS-1$ //$NON-NLS-2$
  public static final EODataType BIGDECIMAL_NUMBER = new EODataType("NSNumber", "B", Messages.getString("EODataType.bigDecimalNumber")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  public static final EODataType INTEGER = new EODataType("NSNumber", new String[] { "i", null, "" }, Messages.getString("EODataType.integer")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
  public static final EODataType DOUBLE = new EODataType("NSNumber", "d", Messages.getString("EODataType.double")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  public static final EODataType DATE = new EODataType("NSCalendarDate", (String) null, Messages.getString("EODataType.date")); //$NON-NLS-1$ //$NON-NLS-2$
  public static final EODataType DATA = new EODataType("NSData", (String) null, Messages.getString("EODataType.data")); //$NON-NLS-1$ //$NON-NLS-2$
  public static final EODataType CUSTOM = new EODataType(null, (String) null, Messages.getString("EODataType.custom")); //$NON-NLS-1$
  public static final EODataType[] DATA_TYPES = new EODataType[] { EODataType.STRING, EODataType.DECIMAL_NUMBER, EODataType.BIGDECIMAL_NUMBER, EODataType.INTEGER, EODataType.DOUBLE, EODataType.DATE, EODataType.DATA, EODataType.CUSTOM };

  private String myValueClass;
  private String[] myValueTypes;
  private String myName;

  public EODataType(String _valueClass, String _valueType, String _name) {
    this(_valueClass, new String[] { _valueType }, _name);
  }

  public EODataType(String _valueClass, String[] _valueTypes, String _name) {
    myValueClass = _valueClass;
    myValueTypes = _valueTypes;
    myName = _name;
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
    return "[EODataType: name = " + myName + "]"; //$NON-NLS-1$ //$NON-NLS-2$
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

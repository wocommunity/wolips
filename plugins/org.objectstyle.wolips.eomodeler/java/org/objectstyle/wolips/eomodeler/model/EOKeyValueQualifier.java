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

import java.util.Map;
import java.util.Set;

public class EOKeyValueQualifier extends EOModelObject implements IEOQualifier {
  public static final String KEY = "key"; //$NON-NLS-1$
  public static final String SELECTOR_NAME = "selectorName"; //$NON-NLS-1$
  public static final String VALUE = "value"; //$NON-NLS-1$

  private String myKey;
  private Object myValue;
  private String mySelectorName;

  public String getKey() {
    return myKey;
  }

  public void setKey(String _key) {
    String oldKey = myKey;
    myKey = _key;
    firePropertyChange(EOKeyValueQualifier.KEY, oldKey, myKey);
  }

  public String getSelectorName() {
    return mySelectorName;
  }

  public void setSelectorName(String _selectorName) {
    String oldSelectorName = mySelectorName;
    mySelectorName = _selectorName;
    firePropertyChange(EOKeyValueQualifier.SELECTOR_NAME, oldSelectorName, mySelectorName);
  }

  public Object getValue() {
    return myValue;
  }

  public void setValue(Object _value) {
    Object oldValue = myValue;
    myValue = _value;
    firePropertyChange(EOKeyValueQualifier.VALUE, oldValue, myValue);
  }

  public void loadFromMap(EOModelMap _map) throws EOModelException {
    myKey = _map.getString("key", true); //$NON-NLS-1$
    mySelectorName = _map.getString("selectorName", true); //$NON-NLS-1$
    Object value = _map.get("value"); //$NON-NLS-1$
    if (value instanceof Map) {
      Map valueMap = (Map) value;
      String clazz = (String) valueMap.get("class"); //$NON-NLS-1$
      if ("EONull".equals(clazz)) { //$NON-NLS-1$
        myValue = null;
      }
      else if ("EOQualifierVariable".equals(clazz)) { //$NON-NLS-1$
        myValue = new EOQualifierVariable((String) valueMap.get("_key")); //$NON-NLS-1$
      }
      else {
        throw new EOModelException("Unknown class " + clazz);
      }
    }
    else {
      myValue = value;
    }
  }

  public EOModelMap toMap() {
    EOModelMap qualifierMap = new EOModelMap();
    qualifierMap.setString("class", "EOKeyValueQualifier", true); //$NON-NLS-1$ //$NON-NLS-2$
    qualifierMap.setString("key", myKey, true); //$NON-NLS-1$
    qualifierMap.setString("selectorName", mySelectorName, true); //$NON-NLS-1$
    if (myValue == null) {
      EOModelMap nullMap = new EOModelMap();
      nullMap.setString("class", "EONull", true); //$NON-NLS-1$ //$NON-NLS-2$
      qualifierMap.setMap("value", nullMap, true); //$NON-NLS-1$
    }
    else if (myValue instanceof EOQualifierVariable) {
      EOQualifierVariable var = (EOQualifierVariable) myValue;
      EOModelMap variableMap = new EOModelMap();
      variableMap.setString("class", "EOQualifierVariable", true); //$NON-NLS-1$ //$NON-NLS-2$
      variableMap.setString("_key", var.getKey(), false); //$NON-NLS-1$
      qualifierMap.setMap("value", variableMap, true); //$NON-NLS-1$
    }
    else {
      qualifierMap.put("value", myValue); //$NON-NLS-1$
    }
    return qualifierMap;
  }

  public void verify(Set _failures) {
    // TODO
  }
}

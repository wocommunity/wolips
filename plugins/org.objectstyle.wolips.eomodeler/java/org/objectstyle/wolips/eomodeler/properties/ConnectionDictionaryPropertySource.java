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
package org.objectstyle.wolips.eomodeler.properties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class ConnectionDictionaryPropertySource implements IPropertySource2 {
  private EOModel myModel;
  private IPropertyDescriptor[] myDescriptors;
  private Map myConnectionDictionary;
  private Map myOriginalConnectionDictionary;

  public ConnectionDictionaryPropertySource(EOModel _model) {
    myModel = _model;
    myConnectionDictionary = new HashMap(_model.getConnectionDictionary());
    myOriginalConnectionDictionary = new HashMap(_model.getConnectionDictionary());
  }

  public Object getEditableValue() {
    return this;
  }

  public EOModel getModel() {
    return myModel;
  }

  public IPropertyDescriptor[] getPropertyDescriptors() {
    if (myDescriptors == null) {
      List descriptorsList = new LinkedList();
      descriptorsList.add(new TextPropertyDescriptor("username", "User Name"));
      descriptorsList.add(new TextPropertyDescriptor("password", "Password"));
      descriptorsList.add(new TextPropertyDescriptor("URL", "URL"));
      myDescriptors = (IPropertyDescriptor[]) descriptorsList.toArray(new IPropertyDescriptor[descriptorsList.size()]);
    }
    return myDescriptors;
  }

  public boolean isPropertyResettable(Object _id) {
    return true;
  }

  public Map getConnectionDictionary() {
    return myConnectionDictionary;
  }
  
  public boolean isPropertySet(Object _id) {
    Map connectionDictionary = getConnectionDictionary();
    return connectionDictionary != null && connectionDictionary.containsKey(_id);
  }

  public Object getPropertyValue(Object _id) {
    Object value = null;
    Map connectionDictionary = getConnectionDictionary();
    if (connectionDictionary != null) {
      value = getConnectionDictionary().get(_id);
    }
    return value;
  }

  public void resetPropertyValue(Object _id) {
    Map connectionDictionary = getConnectionDictionary();
    if (connectionDictionary != null) {
      if (myOriginalConnectionDictionary == null || !myOriginalConnectionDictionary.containsKey(_id)) {
        connectionDictionary.remove(_id);
      }
      else {
        connectionDictionary.put(_id, myOriginalConnectionDictionary.get(_id));
      }
    }
  }

  public void setPropertyValue(Object _id, Object _value) {
    System.out.println("ConnectionDictionaryPropertySource.setPropertyValue: " + _id + "=" + _value);
    Map connectionDictionary = getConnectionDictionary();
    if (connectionDictionary == null) {
      connectionDictionary = new HashMap();
      myConnectionDictionary = connectionDictionary;
    }
    connectionDictionary.put(_id, _value);
  }
}

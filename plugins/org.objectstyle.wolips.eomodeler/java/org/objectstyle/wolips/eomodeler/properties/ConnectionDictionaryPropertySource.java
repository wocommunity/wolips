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
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class ConnectionDictionaryPropertySource implements IPropertySource {
  private IPropertyDescriptor[] myDescriptors;
  private Map myConnectionDictionary;

  public ConnectionDictionaryPropertySource(EOModel _model) {
    myConnectionDictionary = _model.getConnectionDictionary();
    if (myConnectionDictionary == null) {
      myConnectionDictionary = new HashMap();
      _model.setConnectionDictionary(myConnectionDictionary);
    }
  }

  public Object getEditableValue() {
    return this;
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

  public Map getConnectionDictionary() {
    return myConnectionDictionary;
  }

  public Object getPropertyValue(Object _id) {
    Object value = null;
    Map connectionDictionary = getConnectionDictionary();
    if (connectionDictionary != null) {
      value = getConnectionDictionary().get(_id);
    }
    return value;
  }

  public void setPropertyValue(Object _id, Object _value) {
    Map connectionDictionary = getConnectionDictionary();
    if (connectionDictionary == null) {
      connectionDictionary = new HashMap();
      myConnectionDictionary = connectionDictionary;
    }
    connectionDictionary.put(_id, _value);
  }

  public boolean isPropertyResettable(Object _id) {
    return false;
  }

  public boolean isPropertySet(Object _id) {
    return true;
  }

  public void resetPropertyValue(Object _id) {
    // DO NOTHING
  }
}

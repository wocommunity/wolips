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
package org.objectstyle.wolips.eomodeler.utils;

import java.beans.Expression;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Statement;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.objectstyle.wolips.eomodeler.model.EOModelObject;

public class ComboViewerBinding implements ISelectionChangedListener, PropertyChangeListener {
  private ComboViewer myViewer;
  private EOModelObject myObj;
  private String myPropertyName;
  private EOModelObject myListObj;
  private String myListPropertyName;

  public ComboViewerBinding(ComboViewer _viewer, EOModelObject _obj, String _propertyName, EOModelObject _listObj, String _listPropertyName) {
    myViewer = _viewer;
    myObj = _obj;
    myPropertyName = _propertyName;
    myListObj = _listObj;
    myListPropertyName = _listPropertyName;

    try {
      Object existingValue = new Expression(myObj, MiscUtils.toGetMethod(myPropertyName, false), null).getValue();
      myViewer.setSelection(new StructuredSelection(existingValue));
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    myViewer.addSelectionChangedListener(this);
    myObj.addPropertyChangeListener(myPropertyName, this);
    if (myListObj != null) {
      myListObj.addPropertyChangeListener(myListPropertyName, this);
    }
  }

  public void dispose() {
    myViewer.removeSelectionChangedListener(this);
    myObj.removePropertyChangeListener(myPropertyName, this);
    if (myListObj != null) {
      myListObj.removePropertyChangeListener(myListPropertyName, this);
    }
  }

  public void propertyChange(PropertyChangeEvent _event) {
    Object source = _event.getSource();
    String propertyName = _event.getPropertyName();
    if (source == myObj && myPropertyName.equals(propertyName)) {
      Object newValue = _event.getNewValue();
      myViewer.setSelection(new StructuredSelection(newValue));
    }
    else if (myListObj != null && source == myListObj && myListPropertyName.equals(propertyName)) {
      myViewer.setInput(myListObj);
    }
  }

  public void selectionChanged(SelectionChangedEvent _event) {
    try {
      Object newValue = ((IStructuredSelection) _event.getSelection()).getFirstElement();
      Object existingValue = new Expression(myObj, MiscUtils.toGetMethod(myPropertyName, false), null).getValue();
      if (existingValue != newValue || (newValue != null && !newValue.equals(existingValue))) {
        new Statement(myObj, MiscUtils.toSetMethod(myPropertyName), new Object[] { newValue }).execute();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}

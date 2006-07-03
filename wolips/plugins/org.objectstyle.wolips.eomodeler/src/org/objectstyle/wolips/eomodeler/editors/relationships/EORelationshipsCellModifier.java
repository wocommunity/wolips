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
package org.objectstyle.wolips.eomodeler.editors.relationships;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.eomodeler.model.DuplicateRelationshipNameException;
import org.objectstyle.wolips.eomodeler.model.EORelationship;

public class EORelationshipsCellModifier implements ICellModifier {
  private TableViewer myRelationshipsTableViewer;

  public EORelationshipsCellModifier(TableViewer _relationshipsTableViewer) {
    myRelationshipsTableViewer = _relationshipsTableViewer;
  }

  public boolean canModify(Object _element, String _property) {
    boolean canModify = (_property == EORelationshipsConstants.CLASS_PROPERTY || _property == EORelationshipsConstants.NAME);
    return canModify;
  }

  public Object getValue(Object _element, String _property) {
    EORelationship relationship = (EORelationship) _element;
    Object value = null;
    if (_property == EORelationshipsConstants.CLASS_PROPERTY) {
      value = relationship.isClassProperty();
      if (value == null) {
        value = Boolean.FALSE;
      }
    }
    else if (_property == EORelationshipsConstants.NAME) {
      value = relationship.getName();
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return value;
  }

  public void modify(Object _element, String _property, Object _value) {
    try {
      TableItem tableItem = (TableItem) _element;
      EORelationship relationship = (EORelationship) tableItem.getData();
      if (_property == EORelationshipsConstants.CLASS_PROPERTY) {
        relationship.setClassProperty((Boolean) _value);
      }
      else if (_property == EORelationshipsConstants.NAME) {
        relationship.setName((String) _value);
      }
      else {
        throw new IllegalArgumentException("Unknown property '" + _property + "'");
      }
      myRelationshipsTableViewer.refresh(relationship);
    }
    catch (DuplicateRelationshipNameException e) {
      MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e.getMessage());
    }
  }
}
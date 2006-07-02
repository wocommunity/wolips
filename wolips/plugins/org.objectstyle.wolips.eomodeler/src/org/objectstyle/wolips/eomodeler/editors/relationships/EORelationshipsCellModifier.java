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
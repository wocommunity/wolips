package org.objectstyle.wolips.eomodeler.editors.eoentity;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.eomodeler.editors.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.editors.TableUtils;
import org.objectstyle.wolips.eomodeler.model.DuplicateAttributeNameException;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOAttributesCellModifier implements ICellModifier {
  private static final String NO_PROTOYPE_VALUE = "No Prototype";
  private TableViewer myAttributesTableViewer;
  private CellEditor[] myCellEditors;
  private List myPrototypeNames;

  public EOAttributesCellModifier(TableViewer _attributesTableViewer, CellEditor[] _cellEditors) {
    myAttributesTableViewer = _attributesTableViewer;
    myCellEditors = _cellEditors;
  }

  public boolean canModify(Object _element, String _property) {
    if (_property == EOAttributesConstants.PROTOTYPE) {
      EOEntity entity = (EOEntity) myAttributesTableViewer.getInput();
      myPrototypeNames = entity.getModel().getModelGroup().getPrototypeAttributeNames();
      myPrototypeNames.add(0, EOAttributesCellModifier.NO_PROTOYPE_VALUE);
      String[] prototypeNames = (String[]) myPrototypeNames.toArray(new String[myPrototypeNames.size()]);
      KeyComboBoxCellEditor cellEditor = (KeyComboBoxCellEditor) myCellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, _property)];
      cellEditor.setItems(prototypeNames);
    }
    return true;
  }

  public Object getValue(Object _element, String _property) {
    EOAttribute attribute = (EOAttribute) _element;
    Object value = null;
    if (_property == EOAttributesConstants.PRIMARY_KEY) {
      value = attribute.isPrimaryKey();
      if (value == null) {
        value = Boolean.FALSE;
      }
    }
    else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
      value = attribute.isClassProperty();
      if (value == null) {
        value = Boolean.FALSE;
      }
    }
    else if (_property == EOAttributesConstants.LOCKING) {
      value = attribute.isUsedForLocking();
      if (value == null) {
        value = Boolean.FALSE;
      }
    }
    else if (_property == EOAttributesConstants.ALLOW_NULL) {
      value = attribute.isAllowsNull();
      if (value == null) {
        value = Boolean.FALSE;
      }
    }
    else if (_property == EOAttributesConstants.NAME) {
      value = attribute.getName();
    }
    else if (_property == EOAttributesConstants.COLUMN) {
      value = attribute.getColumnName();
    }
    else if (_property == EOAttributesConstants.PROTOTYPE) {
      String prototypeName = attribute.getPrototypeName();
      if (prototypeName == null) {
        prototypeName = EOAttributesCellModifier.NO_PROTOYPE_VALUE;
      }
      value = Integer.valueOf(myPrototypeNames.indexOf(prototypeName));
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return value;
  }

  public void modify(Object _element, String _property, Object _value) {
    try {
      TableItem tableItem = (TableItem) _element;
      EOAttribute attribute = (EOAttribute) tableItem.getData();
      if (_property == EOAttributesConstants.PRIMARY_KEY) {
        attribute.setPrimaryKey((Boolean) _value);
      }
      else if (_property == EOAttributesConstants.CLASS_PROPERTY) {
        attribute.setClassProperty((Boolean) _value);
      }
      else if (_property == EOAttributesConstants.LOCKING) {
        attribute.setUsedForLocking((Boolean) _value);
      }
      else if (_property == EOAttributesConstants.ALLOW_NULL) {
        attribute.setAllowsNull((Boolean) _value);
      }
      else if (_property == EOAttributesConstants.NAME) {
        attribute.setName((String) _value);
      }
      else if (_property == EOAttributesConstants.COLUMN) {
        attribute.setColumnName((String) _value);
      }
      else if (_property == EOAttributesConstants.PROTOTYPE) {
        Integer prototypeIndex = (Integer) _value;
        int prototypeIndexInt = prototypeIndex.intValue();
        String prototypeName = (prototypeIndexInt == -1) ? null : (String) myPrototypeNames.get(prototypeIndexInt);
        if (EOAttributesCellModifier.NO_PROTOYPE_VALUE.equals(prototypeName)) {
          attribute.setPrototypeName(null);
        }
        else {
          attribute.setPrototypeName(prototypeName);
        }
      }
      else {
        throw new IllegalArgumentException("Unknown property '" + _property + "'");
      }
      myAttributesTableViewer.refresh(attribute);
    }
    catch (DuplicateAttributeNameException e) {
      MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e.getMessage());
    }
  }
}
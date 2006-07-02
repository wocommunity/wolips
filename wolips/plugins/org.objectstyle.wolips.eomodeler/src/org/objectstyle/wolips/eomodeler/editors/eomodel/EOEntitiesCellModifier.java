package org.objectstyle.wolips.eomodeler.editors.eomodel;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.eomodeler.editors.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.editors.TableUtils;
import org.objectstyle.wolips.eomodeler.model.DuplicateEntityNameException;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class EOEntitiesCellModifier implements ICellModifier {
  private static final String NO_PARENT_VALUE = "No Parent";
  private TableViewer myModelTableViewer;
  private CellEditor[] myCellEditors;
  private List myEntityNames;

  public EOEntitiesCellModifier(TableViewer _modelTableViewer, CellEditor[] _cellEditors) {
    myModelTableViewer = _modelTableViewer;
    myCellEditors = _cellEditors;
  }

  public boolean canModify(Object _element, String _property) {
    if (_property == EOEntitiesConstants.PARENT) {
      EOModel model = (EOModel) myModelTableViewer.getInput();
      myEntityNames = model.getModelGroup().getEntityNames();
      myEntityNames.add(0, EOEntitiesCellModifier.NO_PARENT_VALUE);
      String[] entityNames = (String[]) myEntityNames.toArray(new String[myEntityNames.size()]);
      KeyComboBoxCellEditor cellEditor = (KeyComboBoxCellEditor) myCellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, _property)];
      cellEditor.setItems(entityNames);
    }
    return true;
  }

  public Object getValue(Object _element, String _property) {
    EOEntity entity = (EOEntity) _element;
    Object value = null;
    if (_property == EOEntitiesConstants.NAME) {
      value = entity.getName();
    }
    else if (_property == EOEntitiesConstants.TABLE) {
      value = entity.getExternalName();
    }
    else if (_property == EOEntitiesConstants.CLASS_NAME) {
      value = entity.getClassName();
    }
    else if (_property == EOEntitiesConstants.PARENT) {
      String parentName = entity.getParentName();
      if (parentName == null) {
        parentName = EOEntitiesCellModifier.NO_PARENT_VALUE;
      }
      value = Integer.valueOf(myEntityNames.indexOf(parentName));
    }
    else {
      throw new IllegalArgumentException("Unknown property '" + _property + "'");
    }
    return value;
  }

  public void modify(Object _element, String _property, Object _value) {
    try {
      TableItem tableItem = (TableItem) _element;
      EOEntity entity = (EOEntity) tableItem.getData();
      if (_property == EOEntitiesConstants.NAME) {
        entity.setName((String) _value);
      }
      else if (_property == EOEntitiesConstants.TABLE) {
        entity.setExternalName((String) _value);
      }
      else if (_property == EOEntitiesConstants.CLASS_NAME) {
        entity.setClassName((String) _value);
      }
      else if (_property == EOEntitiesConstants.PARENT) {
        Integer entityIndex = (Integer) _value;
        int entityIndexInt = entityIndex.intValue();
        String entityName = (entityIndexInt == -1) ? null : (String) myEntityNames.get(entityIndexInt);
        if (EOEntitiesCellModifier.NO_PARENT_VALUE.equals(entityName)) {
          entity.setParentName(null);
        }
        else {
          entity.setParentName(entityName);
        }
      }
      else {
        throw new IllegalArgumentException("Unknown property '" + _property + "'");
      }
      myModelTableViewer.refresh(entity);
    }
    catch (DuplicateEntityNameException e) {
      MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e.getMessage());
    }
  }
}

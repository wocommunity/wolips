package org.objectstyle.wolips.eomodeler.editors.eoentity;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditorInput;
import org.objectstyle.wolips.eomodeler.editors.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.editors.TableUtils;
import org.objectstyle.wolips.eomodeler.editors.TriStateCellEditor;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOAttributesTableEditor extends EditorPart {
  private TableViewer myAttributesTableViewer;
  private EOModelEditorInput myEditorInput;
  private EOEntity myEntity;

  public EOAttributesTableEditor() {
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
    updateEditorInput();
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public void doSave(IProgressMonitor _monitor) {
    System.out.println("EOModelEntitiesTableEditor.doSave: save");
  }

  public void doSaveAs() {
    System.out.println("EOModelEntitiesTableEditor.doSaveAs: saveAs");
  }

  public void init(IEditorSite _site, IEditorInput _input) {
    setSite(_site);
    myEditorInput = (EOModelEditorInput) _input;
    setEntity(null);
    updateEditorInput();
  }

  protected void updateCellEditors(CellEditor[] _cellEditors) {
    Table attributesTable = myAttributesTableViewer.getTable();
    if (myEntity != null && myEntity.isPrototype()) {
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.PRIMARY_KEY)] = new TriStateCellEditor(attributesTable);
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.CLASS_PROPERTY)] = new TriStateCellEditor(attributesTable);
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.LOCKING)] = new TriStateCellEditor(attributesTable);
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.ALLOW_NULL)] = new TriStateCellEditor(attributesTable);
    }
    else {
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.PRIMARY_KEY)] = new CheckboxCellEditor(attributesTable);
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.CLASS_PROPERTY)] = new CheckboxCellEditor(attributesTable);
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.LOCKING)] = new CheckboxCellEditor(attributesTable);
      _cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.ALLOW_NULL)] = new CheckboxCellEditor(attributesTable);
    }

  }

  protected void updateEditorInput() {
    if (myAttributesTableViewer != null) {
      myAttributesTableViewer.setInput(myEntity);
      TableUtils.packTableColumns(myAttributesTableViewer);
      updateCellEditors(myAttributesTableViewer.getCellEditors());
    }
  }

  protected void setPartName(String _partName) {
    super.setPartName(_partName);
  }

  public boolean isDirty() {
    return false;
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void createPartControl(Composite _parent) {
    myAttributesTableViewer = new TableViewer(_parent, SWT.FULL_SELECTION);
    myAttributesTableViewer.setContentProvider(new EOAttributesContentProvider());
    myAttributesTableViewer.setLabelProvider(new EOAttributesLabelProvider(EOAttributesConstants.COLUMNS));
    myAttributesTableViewer.setSorter(new EOAttributesViewerSorter(EOAttributesConstants.COLUMNS));
    myAttributesTableViewer.setColumnProperties(EOAttributesConstants.COLUMNS);

    Table attributesTable = myAttributesTableViewer.getTable();
    attributesTable.setLayoutData(new GridData(GridData.FILL_BOTH));
    attributesTable.setHeaderVisible(true);
    attributesTable.setLinesVisible(true);

    TableUtils.createTableColumns(myAttributesTableViewer, EOAttributesConstants.COLUMNS);

    CellEditor[] cellEditors = new CellEditor[EOAttributesConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.PROTOTYPE)] = new KeyComboBoxCellEditor(attributesTable, new String[0], SWT.READ_ONLY);
    cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.NAME)] = new TextCellEditor(attributesTable);
    cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.COLUMN)] = new TextCellEditor(attributesTable);
    updateCellEditors(cellEditors);
    myAttributesTableViewer.setCellModifier(new EOAttributesCellModifier(myAttributesTableViewer, cellEditors));
    myAttributesTableViewer.setCellEditors(cellEditors);

    updateEditorInput();
  }

  public void setFocus() {
  }
}

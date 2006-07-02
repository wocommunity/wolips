package org.objectstyle.wolips.eomodeler.editors.eomodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class EOEntitiesTableEditor extends EditorPart {
  private TableViewer myEntitiesTableViewer;
  private EOModelEditorInput myEditorInput;

  public EOEntitiesTableEditor() {
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
    updateEditorInput();
  }

  protected void updateEditorInput() {
    if (myEntitiesTableViewer != null) {
      EOModel model = myEditorInput.getModel();
      myEntitiesTableViewer.setInput(model);
      TableUtils.packTableColumns(myEntitiesTableViewer);
    }
  }

  public boolean isDirty() {
    return false;
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void createPartControl(Composite _parent) {
    myEntitiesTableViewer = new TableViewer(_parent, SWT.FULL_SELECTION);
    myEntitiesTableViewer.setContentProvider(new EOEntitiesContentProvider());
    myEntitiesTableViewer.setLabelProvider(new EOEntitiesLabelProvider(EOEntitiesConstants.COLUMNS));
    myEntitiesTableViewer.setSorter(new EOEntitiesViewerSorter(EOEntitiesConstants.COLUMNS));
    myEntitiesTableViewer.setColumnProperties(EOEntitiesConstants.COLUMNS);

    Table entitiesTable = myEntitiesTableViewer.getTable();
    entitiesTable.setLayoutData(new GridData(GridData.FILL_BOTH));
    entitiesTable.setHeaderVisible(true);
    entitiesTable.setLinesVisible(true);

    TableUtils.createTableColumns(myEntitiesTableViewer, EOEntitiesConstants.COLUMNS);

    CellEditor[] cellEditors = new CellEditor[EOEntitiesConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.NAME)] = new TextCellEditor(entitiesTable);
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.TABLE)] = new TextCellEditor(entitiesTable);
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.CLASS_NAME)] = new TextCellEditor(entitiesTable);
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.PARENT)] = new KeyComboBoxCellEditor(entitiesTable, new String[0], SWT.READ_ONLY);
    myEntitiesTableViewer.setCellModifier(new EOEntitiesCellModifier(myEntitiesTableViewer, cellEditors));
    myEntitiesTableViewer.setCellEditors(cellEditors);

    updateEditorInput();
  }

  public void addSelectionListener(ISelectionChangedListener _listener) {
    myEntitiesTableViewer.addSelectionChangedListener(_listener);
  }

  public void setFocus() {
  }
}

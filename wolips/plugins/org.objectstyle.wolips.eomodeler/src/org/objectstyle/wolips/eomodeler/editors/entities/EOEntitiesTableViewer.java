package org.objectstyle.wolips.eomodeler.editors.entities;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.objectstyle.wolips.eomodeler.editors.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.editors.TableUtils;
import org.objectstyle.wolips.eomodeler.editors.relationships.EORelationshipsConstants;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class EOEntitiesTableViewer extends Composite {
  private TableViewer myEntitiesTableViewer;
  private EOModel myModel;

  public EOEntitiesTableViewer(Composite _parent, int _style) {
    super(_parent, _style);
    setLayout(new GridLayout(1, true));
    myEntitiesTableViewer = new TableViewer(this, SWT.FULL_SELECTION);
    myEntitiesTableViewer.setContentProvider(new EOEntitiesContentProvider());
    myEntitiesTableViewer.setLabelProvider(new EOEntitiesLabelProvider(EOEntitiesConstants.COLUMNS));
    myEntitiesTableViewer.setSorter(new EOEntitiesViewerSorter(EOEntitiesConstants.COLUMNS));
    myEntitiesTableViewer.setColumnProperties(EOEntitiesConstants.COLUMNS);

    Table entitiesTable = myEntitiesTableViewer.getTable();
    entitiesTable.setLayoutData(new GridData(GridData.FILL_BOTH));
    entitiesTable.setHeaderVisible(true);
    entitiesTable.setLinesVisible(true);

    TableUtils.createTableColumns(myEntitiesTableViewer, EOEntitiesConstants.COLUMNS);

    ((EOEntitiesViewerSorter) myEntitiesTableViewer.getSorter()).sort(myEntitiesTableViewer, EORelationshipsConstants.NAME);

    CellEditor[] cellEditors = new CellEditor[EOEntitiesConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.NAME)] = new TextCellEditor(entitiesTable);
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.TABLE)] = new TextCellEditor(entitiesTable);
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.CLASS_NAME)] = new TextCellEditor(entitiesTable);
    cellEditors[TableUtils.getColumnNumber(EOEntitiesConstants.COLUMNS, EOEntitiesConstants.PARENT)] = new KeyComboBoxCellEditor(entitiesTable, new String[0], SWT.READ_ONLY);
    myEntitiesTableViewer.setCellModifier(new EOEntitiesCellModifier(myEntitiesTableViewer, cellEditors));
    myEntitiesTableViewer.setCellEditors(cellEditors);
  }

  public void setModel(EOModel _model) {
    myModel = _model;
    myEntitiesTableViewer.setInput(myModel);
    TableUtils.packTableColumns(myEntitiesTableViewer);
  }

  public EOModel getModel() {
    return myModel;
  }

  public TableViewer getTableViewer() {
    return myEntitiesTableViewer;
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myEntitiesTableViewer.addSelectionChangedListener(_listener);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myEntitiesTableViewer.removeSelectionChangedListener(_listener);
  }
}

package org.objectstyle.wolips.eomodeler.editors.relationships;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.editors.TableUtils;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EORelationshipsTableViewer extends Composite {
  private TableViewer myRelationshipsTableViewer;
  private EOEntity myEntity;

  public EORelationshipsTableViewer(Composite _parent, int _style) {
    super(_parent, _style);
    setLayout(new GridLayout(1, true));
    myRelationshipsTableViewer = new TableViewer(this, SWT.FULL_SELECTION);
    myRelationshipsTableViewer.setContentProvider(new EORelationshipsContentProvider());
    myRelationshipsTableViewer.setLabelProvider(new EORelationshipsLabelProvider(EORelationshipsConstants.COLUMNS));
    myRelationshipsTableViewer.setSorter(new EORelationshipsViewerSorter(EORelationshipsConstants.COLUMNS));
    myRelationshipsTableViewer.setColumnProperties(EORelationshipsConstants.COLUMNS);

    Table relationshipsTable = myRelationshipsTableViewer.getTable();
    relationshipsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
    relationshipsTable.setHeaderVisible(true);
    relationshipsTable.setLinesVisible(true);

    TableUtils.createTableColumns(myRelationshipsTableViewer, EORelationshipsConstants.COLUMNS);

    TableColumn toManyColumn = relationshipsTable.getColumn(TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationshipsConstants.TO_MANY));
    toManyColumn.setText("");

    TableColumn classPropertyColumn = relationshipsTable.getColumn(TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationshipsConstants.CLASS_PROPERTY));
    classPropertyColumn.setText("");
    classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));

    ((EORelationshipsViewerSorter) myRelationshipsTableViewer.getSorter()).sort(myRelationshipsTableViewer, EORelationshipsConstants.NAME);

    CellEditor[] cellEditors = new CellEditor[EORelationshipsConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationshipsConstants.TO_MANY)] = new CheckboxCellEditor();
    cellEditors[TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationshipsConstants.CLASS_PROPERTY)] = new CheckboxCellEditor();
    cellEditors[TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationshipsConstants.NAME)] = new TextCellEditor(relationshipsTable);
    myRelationshipsTableViewer.setCellModifier(new EORelationshipsCellModifier(myRelationshipsTableViewer));
    myRelationshipsTableViewer.setCellEditors(cellEditors);
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
    myRelationshipsTableViewer.setInput(myEntity);
    TableUtils.packTableColumns(myRelationshipsTableViewer);
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public TableViewer getTableViewer() {
    return myRelationshipsTableViewer;
  }

  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myRelationshipsTableViewer.addSelectionChangedListener(_listener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myRelationshipsTableViewer.removeSelectionChangedListener(_listener);
  }
}

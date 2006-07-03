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
package org.objectstyle.wolips.eomodeler.editors.attributes;

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
import org.objectstyle.wolips.eomodeler.editors.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.editors.TableUtils;
import org.objectstyle.wolips.eomodeler.editors.TriStateCellEditor;
import org.objectstyle.wolips.eomodeler.editors.relationships.EORelationshipsConstants;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOAttributesTableViewer extends Composite {
  private TableViewer myAttributesTableViewer;
  private EOEntity myEntity;

  public EOAttributesTableViewer(Composite _parent, int _style) {
    super(_parent, _style);
    setLayout(new GridLayout(1, true));
    myAttributesTableViewer = new TableViewer(this, SWT.FULL_SELECTION);
    myAttributesTableViewer.setContentProvider(new EOAttributesContentProvider());
    myAttributesTableViewer.setLabelProvider(new EOAttributesLabelProvider(myAttributesTableViewer, EOAttributesConstants.COLUMNS));
    myAttributesTableViewer.setSorter(new EOAttributesViewerSorter(EOAttributesConstants.COLUMNS));
    myAttributesTableViewer.setColumnProperties(EOAttributesConstants.COLUMNS);

    Table attributesTable = myAttributesTableViewer.getTable();
    attributesTable.setLayoutData(new GridData(GridData.FILL_BOTH));
    attributesTable.setHeaderVisible(true);
    attributesTable.setLinesVisible(true);

    TableUtils.createTableColumns(myAttributesTableViewer, EOAttributesConstants.COLUMNS);

    TableColumn primaryKeyColumn = attributesTable.getColumn(TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.PRIMARY_KEY));
    primaryKeyColumn.setText("");
    //primaryKeyColumn.setAlignment(SWT.CENTER);
    primaryKeyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.PRIMARY_KEY_ICON));

    TableColumn lockingColumn = attributesTable.getColumn(TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.LOCKING));
    lockingColumn.setText("");
    //lockingColumn.setAlignment(SWT.CENTER);
    lockingColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.LOCKING_ICON));

    TableColumn classPropertyColumn = attributesTable.getColumn(TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.CLASS_PROPERTY));
    classPropertyColumn.setText("");
    //classPropertyColumn.setAlignment(SWT.CENTER);
    classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));

    TableColumn allowNullColumn = attributesTable.getColumn(TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.ALLOW_NULL));
    allowNullColumn.setText("0");
    //allowNullColumn.setAlignment(SWT.CENTER);
    //classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(EOAttributesConstants.CLASS_PROPERTY));

    ((EOAttributesViewerSorter) myAttributesTableViewer.getSorter()).sort(myAttributesTableViewer, EORelationshipsConstants.NAME);

    CellEditor[] cellEditors = new CellEditor[EOAttributesConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.PROTOTYPE)] = new KeyComboBoxCellEditor(attributesTable, new String[0], SWT.READ_ONLY);
    cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.NAME)] = new TextCellEditor(attributesTable);
    cellEditors[TableUtils.getColumnNumber(EOAttributesConstants.COLUMNS, EOAttributesConstants.COLUMN)] = new TextCellEditor(attributesTable);
    updateCellEditors(cellEditors);
    myAttributesTableViewer.setCellModifier(new EOAttributesCellModifier(myAttributesTableViewer, cellEditors));
    myAttributesTableViewer.setCellEditors(cellEditors);
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
    myAttributesTableViewer.setInput(myEntity);
    updateCellEditors(myAttributesTableViewer.getCellEditors());
    TableUtils.packTableColumns(myAttributesTableViewer);
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public TableViewer getTableViewer() {
    return myAttributesTableViewer;
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

  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myAttributesTableViewer.addSelectionChangedListener(_listener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myAttributesTableViewer.removeSelectionChangedListener(_listener);
  }
}

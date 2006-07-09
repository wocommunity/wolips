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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EORelationshipsTableViewer extends Composite implements ISelectionProvider {
  private TableViewer myRelationshipsTableViewer;
  private EOEntity myEntity;
  private TableRefreshPropertyListener myTableRefresher;
  private TableRowRefreshPropertyListener myTableRowRefresher;

  public EORelationshipsTableViewer(Composite _parent, int _style) {
    super(_parent, _style);
    setLayout(new GridLayout(1, true));
    myRelationshipsTableViewer = new TableViewer(this, SWT.FULL_SELECTION);
    myRelationshipsTableViewer.setContentProvider(new EORelationshipsContentProvider());
    myRelationshipsTableViewer.setLabelProvider(new EORelationshipsLabelProvider(myRelationshipsTableViewer, EORelationshipsConstants.COLUMNS));
    myRelationshipsTableViewer.setSorter(new EORelationshipsViewerSorter(EORelationshipsConstants.COLUMNS));
    myRelationshipsTableViewer.setColumnProperties(EORelationshipsConstants.COLUMNS);
    myTableRefresher = new TableRefreshPropertyListener(myRelationshipsTableViewer, EOEntity.RELATIONSHIPS);
    myTableRowRefresher = new TableRowRefreshPropertyListener(myRelationshipsTableViewer, EOEntity.RELATIONSHIP);

    Table relationshipsTable = myRelationshipsTableViewer.getTable();
    relationshipsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
    relationshipsTable.setHeaderVisible(true);
    relationshipsTable.setLinesVisible(true);

    TableUtils.createTableColumns(myRelationshipsTableViewer, EORelationshipsConstants.COLUMNS);

    TableColumn toManyColumn = relationshipsTable.getColumn(TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationship.TO_MANY));
    toManyColumn.setText(""); //$NON-NLS-1$

    TableColumn classPropertyColumn = relationshipsTable.getColumn(TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationship.CLASS_PROPERTY));
    classPropertyColumn.setText(""); //$NON-NLS-1$
    classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));

    ((EORelationshipsViewerSorter) myRelationshipsTableViewer.getSorter()).sort(myRelationshipsTableViewer, EORelationship.NAME);

    CellEditor[] cellEditors = new CellEditor[EORelationshipsConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationship.TO_MANY)] = new CheckboxCellEditor();
    cellEditors[TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationship.CLASS_PROPERTY)] = new CheckboxCellEditor();
    cellEditors[TableUtils.getColumnNumber(EORelationshipsConstants.COLUMNS, EORelationship.NAME)] = new TextCellEditor(relationshipsTable);
    myRelationshipsTableViewer.setCellModifier(new EORelationshipsCellModifier(myRelationshipsTableViewer));
    myRelationshipsTableViewer.setCellEditors(cellEditors);
  }

  public void setEntity(EOEntity _entity) {
    if (myEntity != null) {
      myEntity.removePropertyChangeListener(myTableRefresher);
      myEntity.removePropertyChangeListener(myTableRowRefresher);
    }
    myEntity = _entity;
    myRelationshipsTableViewer.setInput(myEntity);
    TableUtils.packTableColumns(myRelationshipsTableViewer);
    if (myEntity != null) {
      myEntity.addPropertyChangeListener(myTableRefresher);
      myEntity.addPropertyChangeListener(myTableRowRefresher);
    }
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

  public ISelection getSelection() {
    return myRelationshipsTableViewer.getSelection();
  }

  public void setSelection(ISelection _selection) {
    myRelationshipsTableViewer.setSelection(_selection);
  }
}

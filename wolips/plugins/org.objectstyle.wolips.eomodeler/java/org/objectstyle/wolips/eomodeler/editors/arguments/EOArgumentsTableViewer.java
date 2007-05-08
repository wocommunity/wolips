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
package org.objectstyle.wolips.eomodeler.editors.arguments;

import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOArgumentDirection;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.utils.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowDoubleClickHandler;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOArgumentsTableViewer extends Composite implements ISelectionProvider {
	private TableViewer myArgumentsTableViewer;

	private EOStoredProcedure myStoredProcedure;

	private ArgumentsChangeRefresher myArgumentsChangedRefresher;

	private TableRowRefreshPropertyListener myTableRowRefresher;

	public EOArgumentsTableViewer(Composite _parent, int _style) {
		super(_parent, _style);

		setLayout(new GridLayout(1, true));
		myArgumentsTableViewer = TableUtils.createTableViewer(this, SWT.MULTI | SWT.FULL_SELECTION, "EOArgument", EOArgumentsConstants.COLUMNS, new EOArgumentsContentProvider(), new EOArgumentsLabelProvider(EOArgumentsConstants.COLUMNS), new TablePropertyViewerSorter(EOArgumentsConstants.COLUMNS));
		new DoubleClickNewAttributeHandler(myArgumentsTableViewer).attach();
		myArgumentsChangedRefresher = new ArgumentsChangeRefresher(myArgumentsTableViewer);
		myTableRowRefresher = new TableRowRefreshPropertyListener(myArgumentsTableViewer);
		Table argumentsTable = myArgumentsTableViewer.getTable();
		argumentsTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		// TableColumn allowNullColumn =
		// attributesTable.getColumn(TableUtils.getColumnNumber(EOArgumentsConstants.COLUMNS,
		// EOAttribute.ALLOWS_NULL));
		// allowNullColumn.setText("0");
		// allowNullColumn.setAlignment(SWT.CENTER);
		//
		TableUtils.sort(myArgumentsTableViewer, AbstractEOArgument.NAME);

		CellEditor[] cellEditors = new CellEditor[EOArgumentsConstants.COLUMNS.length];
		cellEditors[TableUtils.getColumnNumber(EOArgumentsConstants.COLUMNS, AbstractEOArgument.NAME)] = new TextCellEditor(argumentsTable);
		cellEditors[TableUtils.getColumnNumber(EOArgumentsConstants.COLUMNS, AbstractEOArgument.COLUMN_NAME)] = new TextCellEditor(argumentsTable);
		String[] argumentDirectionNames = new String[EOArgumentDirection.ARGUMENT_DIRECTIONS.length];
		for (int argumentDirectionNum = 0; argumentDirectionNum < argumentDirectionNames.length; argumentDirectionNum++) {
			argumentDirectionNames[argumentDirectionNum] = EOArgumentDirection.ARGUMENT_DIRECTIONS[argumentDirectionNum].getName();
		}
		cellEditors[TableUtils.getColumnNumber(EOArgumentsConstants.COLUMNS, EOArgument.DIRECTION)] = new KeyComboBoxCellEditor(argumentsTable, argumentDirectionNames);
		myArgumentsTableViewer.setCellModifier(new EOArgumentsCellModifier(myArgumentsTableViewer));
		myArgumentsTableViewer.setCellEditors(cellEditors);
	}

	public void setStoredProcedure(EOStoredProcedure _storedProcedure) {
		if (myStoredProcedure != null) {
			myStoredProcedure.removePropertyChangeListener(EOStoredProcedure.ARGUMENTS, myArgumentsChangedRefresher);
			myStoredProcedure.removePropertyChangeListener(EOStoredProcedure.ARGUMENT, myTableRowRefresher);
		}
		myStoredProcedure = _storedProcedure;
		if (myStoredProcedure != null) {
			myArgumentsTableViewer.setInput(myStoredProcedure);
			TableUtils.packTableColumns(myArgumentsTableViewer);
			myStoredProcedure.addPropertyChangeListener(EOStoredProcedure.ARGUMENTS, myArgumentsChangedRefresher);
			myStoredProcedure.addPropertyChangeListener(EOStoredProcedure.ARGUMENT, myTableRowRefresher);
		}
	}

	public EOStoredProcedure getStoredProcedure() {
		return myStoredProcedure;
	}

	public TableViewer getTableViewer() {
		return myArgumentsTableViewer;
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		myArgumentsTableViewer.addSelectionChangedListener(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		myArgumentsTableViewer.removeSelectionChangedListener(_listener);
	}

	public ISelection getSelection() {
		return myArgumentsTableViewer.getSelection();
	}

	public void setSelection(ISelection _selection) {
		myArgumentsTableViewer.setSelection(_selection);
	}

	protected class DoubleClickNewAttributeHandler extends TableRowDoubleClickHandler {
		public DoubleClickNewAttributeHandler(TableViewer _viewer) {
			super(_viewer);
		}

		protected void emptyDoubleSelectionOccurred() {
			try {
				EOArgumentsTableViewer.this.getStoredProcedure().addBlankArgument(Messages.getString("EOArgument.newName"));
			} catch (Throwable e) {
				ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
			}
		}

		protected void doubleSelectionOccurred(ISelection _selection) {
			// DO NOTHING
		}
	}

	protected class ArgumentsChangeRefresher extends TableRefreshPropertyListener {
		public ArgumentsChangeRefresher(TableViewer _tableViewer) {
			super(_tableViewer);
		}

		public void propertyChange(PropertyChangeEvent _event) {
			super.propertyChange(_event);
			Set oldValues = (Set) _event.getOldValue();
			Set newValues = (Set) _event.getNewValue();
			if (newValues != null && oldValues != null) {
				if (newValues.size() > oldValues.size()) {
					List newList = new LinkedList(newValues);
					newList.removeAll(oldValues);
					EOArgumentsTableViewer.this.setSelection(new StructuredSelection(newList));
				}
				TableUtils.packTableColumns(EOArgumentsTableViewer.this.getTableViewer());
			}
		}
	}
}

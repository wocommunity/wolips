package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.baseforuiplugins.utils.KeyComboBoxCellEditor;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;

public class StayEditingCellEditorListener implements ICellEditorListener, SelectionListener, TraverseListener {
	private TableViewer _tableViewer;

	private int _column;

	public StayEditingCellEditorListener(TableViewer tableViewer, String tableName, String propertyName) {
		this(tableViewer, TableUtils.getColumnNumberForTablePropertyNamed(tableName, propertyName));
	}
	
	public StayEditingCellEditorListener(TableViewer tableViewer, int column) {
		_tableViewer = tableViewer;
		_column = column;
		if (_column != -1) {
			CellEditor cellEditor = _tableViewer.getCellEditors()[_column];
			if (cellEditor instanceof WOTextCellEditor) {
				((WOTextCellEditor) cellEditor).getText().addSelectionListener(this);
				((WOTextCellEditor) cellEditor).getText().addTraverseListener(this);
			} else if (cellEditor instanceof KeyComboBoxCellEditor) {
				((KeyComboBoxCellEditor) cellEditor).getComboBox().addSelectionListener(this);
				((KeyComboBoxCellEditor) cellEditor).getComboBox().addTraverseListener(this);
			}
		}
	}

	public TableViewer getTableViewer() {
		return _tableViewer;
	}

	public int getColumn() {
		return _column;
	}

	public void keyTraversed(TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
			int selectionIndex = _tableViewer.getTable().getSelectionIndex();
			if (selectionIndex != -1) {
				final Object thisObject = _tableViewer.getElementAt(selectionIndex);
				if (thisObject != null) {
					Display.getCurrent().asyncExec(new Runnable() {
						public void run() {
							getTableViewer().editElement(thisObject, getColumn() + 1);
						}
					});
				}
			}
		} else if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
			int selectionIndex = _tableViewer.getTable().getSelectionIndex();
			if (selectionIndex != -1) {
				final Object thisObject = _tableViewer.getElementAt(selectionIndex);
				if (thisObject != null) {
					Display.getCurrent().asyncExec(new Runnable() {
						public void run() {
							getTableViewer().editElement(thisObject, getColumn() - 1);
						}
					});
				}
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		int selectionIndex = _tableViewer.getTable().getSelectionIndex();
		if (selectionIndex != -1) {
			final Object nextObject = _tableViewer.getElementAt(selectionIndex + 1);
			if (nextObject != null) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						getTableViewer().editElement(nextObject, getColumn());
					}
				});
			}
		}
	}

	public void widgetSelected(SelectionEvent e) {
		// DO NOTHING
	}

	public void applyEditorValue() {
		// DO NOTHING
	}

	public void cancelEditor() {
		// DO NOTHING
	}

	public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		// DO NOTHING
	}
}

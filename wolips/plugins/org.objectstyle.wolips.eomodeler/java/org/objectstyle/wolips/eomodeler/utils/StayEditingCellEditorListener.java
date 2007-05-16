package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;

public class StayEditingCellEditorListener implements ICellEditorListener, SelectionListener {
	private TableViewer _tableViewer;

	private int _column;

	public StayEditingCellEditorListener(TableViewer tableViewer, int column) {
		_tableViewer = tableViewer;
		CellEditor cellEditor = _tableViewer.getCellEditors()[column];
		if (cellEditor instanceof EMTextCellEditor) {
			((EMTextCellEditor) cellEditor).getText().addSelectionListener(this);
		} else if (cellEditor instanceof KeyComboBoxCellEditor) {
			((KeyComboBoxCellEditor) cellEditor).getComboBox().addSelectionListener(this);
		}
		_column = column;
	}

	public TableViewer getTableViewer() {
		return _tableViewer;
	}

	public int getColumn() {
		return _column;
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

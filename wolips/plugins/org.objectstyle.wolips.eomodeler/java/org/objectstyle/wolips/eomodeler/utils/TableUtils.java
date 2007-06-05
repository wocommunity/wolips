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
package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;

public class TableUtils {
	public static TableViewer createTableViewer(Composite parent, String messagePrefix, String tableName, IStructuredContentProvider contentProvider, ITableLabelProvider labelProvider, ViewerSorter sorter) {
		return TableUtils.createTableViewer(parent, SWT.FULL_SELECTION, messagePrefix, tableName, contentProvider, labelProvider, sorter);
	}

	public static EMTableViewer createTableViewer(Composite parent, int style, String messagePrefix, String tableName, IStructuredContentProvider contentProvider, ITableLabelProvider labelProvider, ViewerSorter sorter) {
		return TableUtils.createTableViewer(parent, style, messagePrefix, TableUtils.getColumnsForTableNamed(tableName), contentProvider, labelProvider, sorter);
	}

	public static TableViewer createTableViewer(Composite parent, String messagePrefix, String[] columns, IStructuredContentProvider contentProvider, ITableLabelProvider labelProvider, ViewerSorter sorter) {
		return TableUtils.createTableViewer(parent, SWT.FULL_SELECTION, messagePrefix, columns, contentProvider, labelProvider, sorter);
	}

	public static EMTableViewer createTableViewer(Composite parent, int style, String messagePrefix, String[] columns, IStructuredContentProvider contentProvider, ITableLabelProvider labelProvider, ViewerSorter sorter) {
		EMTableViewer tableViewer = new EMTableViewer(parent, style);
		tableViewer.setColumnProperties(columns);
		if (contentProvider != null) {
			tableViewer.setContentProvider(contentProvider);
		}
		if (labelProvider != null) {
			tableViewer.setLabelProvider(labelProvider);
		}
		if (sorter != null) {
			tableViewer.setSorter(sorter);
		}
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableUtils.createTableColumns(tableViewer, messagePrefix, columns, (sorter instanceof TablePropertyViewerSorter));
		return tableViewer;
	}

	public static void packTableColumns(TableViewer viewer) {
		Table table = viewer.getTable();
		int columnCount = table.getColumnCount();
		for (int columnNum = 0; columnNum < columnCount; columnNum++) {
			TableColumn column = table.getColumn(columnNum);
			int originalWidth = column.getWidth();
			column.pack();
			int newWidth = column.getWidth();
			if (newWidth < originalWidth) {
				column.setWidth(originalWidth);
			}
		}
	}

	public static CellEditor getCellEditor(TableViewer tableViewer, String tableName, String propertyName) {
		CellEditor cellEditor = null;
		int columnNumber = TableUtils.getColumnNumberForTablePropertyNamed(tableName, propertyName);
		if (columnNumber != -1) {
			cellEditor = tableViewer.getCellEditors()[columnNumber];
		}
		return cellEditor;
	}
	
	public static void setCellEditor(String tableName, String propertyName, CellEditor cellEditor, CellEditor[] cellEditors) {
		int columnNumber = TableUtils.getColumnNumberForTablePropertyNamed(tableName, propertyName);
		if (columnNumber == -1) {
			cellEditor.dispose();
		}
		else {
			cellEditors[columnNumber] = cellEditor;
		}
	}
	
	public static void createTableColumns(TableViewer viewer, String messagePrefix, String[] properties, boolean addSortHandler) {
		for (int columnNum = 0; columnNum < properties.length; columnNum++) {
			TableUtils.createTableColumn(viewer, messagePrefix, properties[columnNum], addSortHandler);
		}
	}

	public static TableColumn createTableColumn(TableViewer viewer, String messagePrefix, String propertyName, boolean addSortHandler) {
		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setMoveable(true);
		String text;
		if (messagePrefix == null) {
			text = propertyName;
		} else {
			text = Messages.getString(messagePrefix + "." + propertyName);
		}
		column.setText(text);
		if (addSortHandler) {
			column.addSelectionListener(new TableSortHandler(viewer, propertyName));
		}
		return column;
	}

	public static String getPreferenceNameForTableNamed(String tableName) {
		return "EntityModeler." + tableName + ".columns";
	}
	
	public static void setColumnsForTableNamed(String tableName, String[] properties, boolean defaults) {
		StringBuffer columnsBuf = new StringBuffer();
		for (String property : properties) {
			columnsBuf.append(property);
			columnsBuf.append(",");
		}
		if (columnsBuf.length() > 0) {
			columnsBuf.setLength(columnsBuf.length() - 1);
		}
		String columnsStr = columnsBuf.toString();
		if (defaults) {
			Activator.getDefault().getPreferenceStore().setDefault(TableUtils.getPreferenceNameForTableNamed(tableName), columnsStr);
		} else {
			Activator.getDefault().getPreferenceStore().setValue(TableUtils.getPreferenceNameForTableNamed(tableName), columnsStr);
		}
	}

	public static String[] getColumnsForTableNamed(String tableName) {
		String columnsStr = Activator.getDefault().getPreferenceStore().getString(TableUtils.getPreferenceNameForTableNamed(tableName));
		String[] columns = columnsStr.split(",");
		return columns;
	}

	public static int getColumnNumberForTablePropertyNamed(String tableName, String property) {
		return TableUtils._getColumnNumber(TableUtils.getColumnsForTableNamed(tableName), property);
	}

	public static int _getColumnNumber(String[] properties, String property) {
		int matchingColumnIndex = -1;
		for (int columnNum = 0; columnNum < properties.length; columnNum++) {
			if (properties[columnNum].equals(property)) {
				matchingColumnIndex = columnNum;
			}
		}
		return matchingColumnIndex;
	}

	public static void sort(TableViewer tableViewer, String propertyName) {
		TablePropertyViewerSorter sorter = (TablePropertyViewerSorter) tableViewer.getSorter();
		if (sorter != null) {
			sorter.sort(tableViewer, propertyName);
		}
	}
	
	public static TableColumn getColumn(TableViewer tableViewer, String tableName, String propertyName) {
		Table table = tableViewer.getTable();
		int columnNumber = TableUtils.getColumnNumberForTablePropertyNamed(tableName, propertyName);
		TableColumn column = null;
		if (columnNumber != -1) {
			column = table.getColumn(columnNumber);
		}
		return column;
	}
}

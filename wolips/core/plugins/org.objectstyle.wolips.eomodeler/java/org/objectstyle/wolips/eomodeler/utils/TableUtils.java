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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.eomodeler.Messages;

public class TableUtils {
	public static TableViewer createTableViewer(Composite _parent, String _messagePrefix, String[] _columns, IStructuredContentProvider _contentProvider, ITableLabelProvider _labelProvider, ViewerSorter _sorter) {
		return TableUtils.createTableViewer(_parent, SWT.FULL_SELECTION, _messagePrefix, _columns, _contentProvider, _labelProvider, _sorter);
	}

	public static EMTableViewer createTableViewer(Composite _parent, int _style, String _messagePrefix, String[] _columns, IStructuredContentProvider _contentProvider, ITableLabelProvider _labelProvider, ViewerSorter _sorter) {
		EMTableViewer tableViewer = new EMTableViewer(_parent, _style);
		tableViewer.setColumnProperties(_columns);
		if (_contentProvider != null) {
			tableViewer.setContentProvider(_contentProvider);
		}
		if (_labelProvider != null) {
			tableViewer.setLabelProvider(_labelProvider);
		}
		if (_sorter != null) {
			tableViewer.setSorter(_sorter);
		}
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableUtils.createTableColumns(tableViewer, _messagePrefix, _columns, (_sorter instanceof TablePropertyViewerSorter));
		return tableViewer;
	}

	public static void packTableColumns(TableViewer _viewer) {
		Table table = _viewer.getTable();
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

	public static void createTableColumns(TableViewer _viewer, String _messagePrefix, String[] _properties, boolean _addSortHandler) {
		for (int columnNum = 0; columnNum < _properties.length; columnNum++) {
			TableUtils.createTableColumn(_viewer, _messagePrefix, _properties[columnNum], _addSortHandler);
		}
	}

	public static TableColumn createTableColumn(TableViewer _viewer, String _messagePrefix, String _propertyName, boolean _addSortHandler) {
		TableColumn column = new TableColumn(_viewer.getTable(), SWT.LEFT);
		column.setMoveable(true);
		String text;
		if (_messagePrefix == null) {
			text = _propertyName;
		} else {
			text = Messages.getString(_messagePrefix + "." + _propertyName);
		}
		column.setText(text);
		if (_addSortHandler) {
			column.addSelectionListener(new TableSortHandler(_viewer, _propertyName));
		}
		return column;
	}

	public static int getColumnNumber(String[] _properties, String _property) {
		int matchingColumnIndex = -1;
		for (int columnNum = 0; columnNum < _properties.length; columnNum++) {
			if (_properties[columnNum].equals(_property)) {
				matchingColumnIndex = columnNum;
			}
		}
		return matchingColumnIndex;
	}

	public static void sort(TableViewer _tableViewer, String _propertyName) {
		TablePropertyViewerSorter sorter = (TablePropertyViewerSorter) _tableViewer.getSorter();
		if (sorter != null) {
			sorter.sort(_tableViewer, _propertyName);
		}
	}
}

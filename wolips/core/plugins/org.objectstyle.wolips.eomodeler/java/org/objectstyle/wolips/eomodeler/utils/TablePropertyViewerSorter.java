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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.eomodeler.core.kvc.CachingKeyPath;
import org.objectstyle.wolips.eomodeler.core.kvc.KeyPath;

public class TablePropertyViewerSorter extends ViewerSorter {
	private String[] myColumnProperties;

	private int mySortedColumn;

	private int myDirection;

	private Map myKeys;

	public TablePropertyViewerSorter(String tableName) {
		this(TableUtils.getColumnsForTableNamed(tableName));
	}
	
	public TablePropertyViewerSorter(String[] columnProperties) {
		myColumnProperties = columnProperties;
		myKeys = new HashMap();
		for (int keyNum = 0; keyNum < columnProperties.length; keyNum++) {
			KeyPath keyPath = new CachingKeyPath(columnProperties[keyNum]);
			myKeys.put(columnProperties[keyNum], keyPath);
		}
	}

	public void sort(TableViewer _viewer, String _property) {
		int matchingColumn = TableUtils._getColumnNumber(myColumnProperties, _property);
		if (matchingColumn != -1) {
			sort(_viewer, matchingColumn);
		}
	}

	public void sort(TableViewer _viewer, int _column) {
		Table table = _viewer.getTable();
		TableColumn sortColumn = table.getSortColumn();
		TableColumn selectedColumn = table.getColumn(_column);
		int direction = table.getSortDirection();
		if (sortColumn == selectedColumn) {
			direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
		} else {
			table.setSortColumn(selectedColumn);
			direction = SWT.UP;
		}
		table.setSortDirection(direction);
		mySortedColumn = _column;
		myDirection = direction;
		_viewer.refresh();
	}

	public int compare(Viewer _viewer, Object _o1, Object _o2) {
		String property = myColumnProperties[mySortedColumn];
		Object o1 = getComparisonValue(_o1, property);
		Object o2 = getComparisonValue(_o2, property);
		int comparison = 0;
		if (o1 == null && o2 == null) {
			comparison = 0;
		} else if (o1 == null) {
			comparison = -1;
		} else if (o2 == null) {
			comparison = 1;
		} else if (o1 instanceof Boolean && o2 instanceof Boolean) {
			boolean left = ((Boolean) _o1).booleanValue();
			boolean right = ((Boolean) _o2).booleanValue();
			if (left == right) {
				comparison = 0;
			} else if (left == true) {
				comparison = 1;
			} else {
				comparison = -1;
			}
		} else if (o1 instanceof Integer && o2 instanceof Integer) {
			comparison = ((Integer) o1).compareTo((Integer) o2);
		} else if (o1 instanceof String && o2 instanceof String) {
			comparison = collator.compare(o1, o2);
		}

		if (myDirection == SWT.DOWN) {
			comparison = -comparison;
		}

		return comparison;
	}

	public Object getComparisonValue(Object _obj, String _property) {
		Object value = ((KeyPath) myKeys.get(_property)).getValue(_obj);
		return value;
	}
}

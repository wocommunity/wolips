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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.core.kvc.CachingKeyPath;
import org.objectstyle.wolips.eomodeler.core.kvc.Key;
import org.objectstyle.wolips.eomodeler.core.kvc.KeyPath;

public class TablePropertyCellModifier implements ICellModifier, ISelectionChangedListener {
	private TableViewer myTableViewer;

	private ISelection mySelection;

	private Map myKeys;

	public TablePropertyCellModifier(TableViewer _tableViewer) {
		myTableViewer = _tableViewer;
		myTableViewer.addSelectionChangedListener(this);
		myKeys = new HashMap();
	}

	public TableViewer getTableViewer() {
		return myTableViewer;
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		mySelection = _event.getSelection();
	}

	public boolean canModify(Object _element, String _property) {
		boolean canModify = false;
		if (mySelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) mySelection;
			if (selection.size() == 1 && _element == selection.getFirstElement()) {
				try {
					canModify = _canModify(_element, _property);
				} catch (Throwable t) {
					t.printStackTrace();
					canModify = false;
				}
			}
		}
		return canModify;
	}

	protected boolean _canModify(Object _element, String _property) throws Throwable {
		return true;
	}

	public Object getValue(Object _element, String _property) {
		Object value = new Key(_property).getValue(_element);
		return value;
	}

	public void modify(Object _element, String _property, Object _value) {
		try {
			TableItem tableItem = (TableItem) _element;
			Object obj = tableItem.getData();
			if (!_modify(obj, _property, _value)) {
				KeyPath keyPath = (KeyPath) myKeys.get(_property);
				if (keyPath == null) {
					keyPath = new CachingKeyPath(_property);
					myKeys.put(_property, keyPath);
				}
				keyPath.setValue(obj, _value);
			}
			myTableViewer.refresh(obj);
		} catch (Throwable t) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
		}
	}

	protected boolean _modify(Object _element, String _property, Object _value) throws Throwable {
		return false;
	}
}
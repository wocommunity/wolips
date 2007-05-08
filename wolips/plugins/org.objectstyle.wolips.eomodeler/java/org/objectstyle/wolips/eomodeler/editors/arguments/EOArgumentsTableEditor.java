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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;

public class EOArgumentsTableEditor extends EditorPart implements ISelectionProvider {
	private EOArgumentsTableViewer myArgumentsTableViewer;

	private EOStoredProcedure myStoredProcedure;

	private ListenerList myListenerList;

	public EOArgumentsTableEditor() {
		myListenerList = new ListenerList();
	}

	public void setStoredProcedure(EOStoredProcedure _storedProcedure) {
		myStoredProcedure = _storedProcedure;
		updateArgumentsTableViewer();
	}

	public EOModel getModel() {
		return (myStoredProcedure == null) ? null : myStoredProcedure.getModel();
	}

	public EOStoredProcedure getStoredProcedure() {
		return myStoredProcedure;
	}

	public void doSave(IProgressMonitor _monitor) {
		// DO NOTHING
	}

	public void doSaveAs() {
		// DO NOTHING
	}

	public void init(IEditorSite _site, IEditorInput _input) {
		setSite(_site);
		setInput(_input);
		setStoredProcedure(null);
	}

	public boolean isDirty() {
		return myStoredProcedure != null && myStoredProcedure.getModel().isDirty();
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void createPartControl(Composite _parent) {
		myArgumentsTableViewer = new EOArgumentsTableViewer(_parent, SWT.NONE);
		myArgumentsTableViewer.addSelectionChangedListener(new ArgumentSelectionChangedListener());
		myArgumentsTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		updateArgumentsTableViewer();
	}

	public void setFocus() {
		// DO NOTHING
	}

	protected void updateArgumentsTableViewer() {
		if (myArgumentsTableViewer != null) {
			myArgumentsTableViewer.setStoredProcedure(myStoredProcedure);
		}
	}

	public ISelection getSelection() {
		return myArgumentsTableViewer.getSelection();
	}

	public void setSelection(ISelection _selection) {
		myArgumentsTableViewer.setSelection(_selection);
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		myListenerList.add(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		myListenerList.remove(_listener);
	}

	public void fireSelectionChanged(ISelection _selection) {
		Object[] listeners = myListenerList.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((ISelectionChangedListener) listeners[i]).selectionChanged(new SelectionChangedEvent(this, _selection));
		}
	}

	protected class ArgumentSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			if (!_event.getSelection().isEmpty()) {
				fireSelectionChanged(_event.getSelection());
			}
		}
	}
}

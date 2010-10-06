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
package org.objectstyle.wolips.eomodeler.editors.entity;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.IEntityEditor;
import org.objectstyle.wolips.eomodeler.editors.attributes.EOAttributesTableViewer;
import org.objectstyle.wolips.eomodeler.editors.relationships.EORelationshipsTableViewer;

public class EOEntityEditor extends EditorPart implements IEntityEditor, ISelectionProvider {
	private EOAttributesTableViewer myAttributesTableViewer;

	private EORelationshipsTableViewer myRelationshipsTableViewer;

	private EOEntity myEntity;

	private ListenerList myListenerList;

	public EOEntityEditor() {
		myListenerList = new ListenerList();
	}

	public EOModel getModel() {
		return (myEntity == null) ? null : myEntity.getModel();
	}

	public void setEntity(EOEntity _entity) {
		myEntity = _entity;
		updateTableViewers();
	}

	public EOEntity getEntity() {
		return myEntity;
	}

	public void doSave(IProgressMonitor _monitor) {
		// DO NOTHING
	}

	public void doSaveAs() {
		// DO NOTHING
	}

	public EOAttributesTableViewer getAttributesTableViewer() {
		return myAttributesTableViewer;
	}

	public EORelationshipsTableViewer getRelationshipsTableViewer() {
		return myRelationshipsTableViewer;
	}

	public void init(IEditorSite _site, IEditorInput _input) {
		setSite(_site);
		setInput(_input);
		setEntity(null);
	}

	public boolean isDirty() {
		return myEntity != null && myEntity.getModel().isDirty();
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void createPartControl(Composite _parent) {
		SashForm sashForm = new SashForm(_parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		myAttributesTableViewer = new EOAttributesTableViewer(sashForm, SWT.NONE);
		myAttributesTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		myAttributesTableViewer.addSelectionChangedListener(new AttributeSelectionChangedListener());

		myRelationshipsTableViewer = new EORelationshipsTableViewer(sashForm, SWT.NONE);
		myRelationshipsTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		myRelationshipsTableViewer.addSelectionChangedListener(new RelationshipSelectionChangedListener());

		sashForm.setWeights(new int[] { 2, 1 });
		updateTableViewers();
	}

	public void setFocus() {
		// DO NOTHING
	}
	
	@Override
	public void dispose() {
		if (myRelationshipsTableViewer != null) {
			myRelationshipsTableViewer.dispose();
		}
		if (myAttributesTableViewer != null) {
			myAttributesTableViewer.dispose();
		}
		super.dispose();
	}

	protected void updateTableViewers() {
		if (myRelationshipsTableViewer != null) {
			myRelationshipsTableViewer.setEntity(myEntity);
		}
		if (myAttributesTableViewer != null) {
			myAttributesTableViewer.setEntity(myEntity);
		}
	}

	public void fireSelectionChanged(ISelection _selection) {
		Object[] listeners = myListenerList.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((ISelectionChangedListener) listeners[i]).selectionChanged(new SelectionChangedEvent(this, _selection));
		}
	}

	public void setSelection(ISelection _selection) {
		myAttributesTableViewer.setSelection(_selection);
		myRelationshipsTableViewer.setSelection(_selection);
	}

	public ISelection getSelection() {
		ISelection selection = myAttributesTableViewer.getSelection();
		if (selection.isEmpty()) {
			selection = myRelationshipsTableViewer.getSelection();
		}
		return selection;
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		myListenerList.add(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		myListenerList.remove(_listener);
	}

	protected class AttributeSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			if (!_event.getSelection().isEmpty()) {
				getRelationshipsTableViewer().setSelection(null);
				fireSelectionChanged(_event.getSelection());
			}
		}
	}

	protected class RelationshipSelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			if (!_event.getSelection().isEmpty()) {
				getAttributesTableViewer().setSelection(null);
				fireSelectionChanged(_event.getSelection());
			}
		}
	}
}

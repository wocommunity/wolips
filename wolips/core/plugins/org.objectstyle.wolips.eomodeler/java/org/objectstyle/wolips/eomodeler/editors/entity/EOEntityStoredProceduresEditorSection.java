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

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.editors.storedProcedures.EOStoredProceduresLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.storedProcedures.EOStoredProceduresListContentProvider;
import org.objectstyle.wolips.eomodeler.utils.ComboViewerBinding;

public class EOEntityStoredProceduresEditorSection extends AbstractPropertySection {
	private EOEntity myEntity;

	private ComboViewer myInsertComboViewer;

	private ComboViewer myDeleteComboViewer;

	private ComboViewer myFetchAllComboViewer;

	private ComboViewer myFetchWithPrimaryKeyComboViewer;

	private ComboViewer myNextPrimaryKeyComboViewer;

	private ComboViewerBinding myInsertBinding;

	private ComboViewerBinding myDeleteBinding;

	private ComboViewerBinding myFetchAllBinding;

	private ComboViewerBinding myFetchWithPrimaryKeyBinding;

	private ComboViewerBinding myNextPrimaryKeyBinding;

	public EOEntityStoredProceduresEditorSection() {
		// DO NOTHING
	}

	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(_parent);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = getWidgetFactory().createPlainComposite(form, SWT.NONE);
		FormData topFormData = new FormData();
		topFormData.top = new FormAttachment(0, 5);
		topFormData.left = new FormAttachment(0, 5);
		topFormData.right = new FormAttachment(100, -5);
		topForm.setLayoutData(topFormData);

		GridLayout topFormLayout = new GridLayout();
		topFormLayout.numColumns = 2;
		topForm.setLayout(topFormLayout);

		myInsertComboViewer = createStoredProcedureComboViewer(topForm, EOEntity.INSERT_PROCEDURE);
		myDeleteComboViewer = createStoredProcedureComboViewer(topForm, EOEntity.DELETE_PROCEDURE);
		myFetchAllComboViewer = createStoredProcedureComboViewer(topForm, EOEntity.FETCH_ALL_PROCEDURE);
		myFetchWithPrimaryKeyComboViewer = createStoredProcedureComboViewer(topForm, EOEntity.FETCH_WITH_PRIMARY_KEY_PROCEDURE);
		myNextPrimaryKeyComboViewer = createStoredProcedureComboViewer(topForm, EOEntity.NEXT_PRIMARY_KEY_PROCEDURE);
	}

	protected ComboViewer createStoredProcedureComboViewer(Composite _parent, String _name) {
		getWidgetFactory().createCLabel(_parent, Messages.getString("EOEntity." + _name), SWT.NONE);
		Combo insertCombo = new Combo(_parent, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
		ComboViewer comboViewer = new ComboViewer(insertCombo);
		comboViewer.setLabelProvider(new EOStoredProceduresLabelProvider());
		comboViewer.setContentProvider(new EOStoredProceduresListContentProvider(true));
		GridData comboLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		insertCombo.setLayoutData(comboLayoutData);
		return comboViewer;
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		EOEntity entity = (EOEntity) selectedObject;
		if (!ComparisonUtils.equals(entity, myEntity)) {
			disposeBindings();

			myEntity = entity;
			if (myEntity != null) {
				myInsertComboViewer.setInput(myEntity);
				myDeleteComboViewer.setInput(myEntity);
				myFetchAllComboViewer.setInput(myEntity);
				myFetchWithPrimaryKeyComboViewer.setInput(myEntity);
				myNextPrimaryKeyComboViewer.setInput(myEntity);
				myInsertBinding = new ComboViewerBinding(myInsertComboViewer, myEntity, EOEntity.INSERT_PROCEDURE, myEntity.getModel(), EOModel.STORED_PROCEDURES, EOStoredProceduresListContentProvider.BLANK_STORED_PROCEDURE);
				myDeleteBinding = new ComboViewerBinding(myDeleteComboViewer, myEntity, EOEntity.DELETE_PROCEDURE, myEntity.getModel(), EOModel.STORED_PROCEDURES, EOStoredProceduresListContentProvider.BLANK_STORED_PROCEDURE);
				myFetchAllBinding = new ComboViewerBinding(myFetchAllComboViewer, myEntity, EOEntity.FETCH_ALL_PROCEDURE, myEntity.getModel(), EOModel.STORED_PROCEDURES, EOStoredProceduresListContentProvider.BLANK_STORED_PROCEDURE);
				myFetchWithPrimaryKeyBinding = new ComboViewerBinding(myFetchWithPrimaryKeyComboViewer, myEntity, EOEntity.FETCH_WITH_PRIMARY_KEY_PROCEDURE, myEntity.getModel(), EOModel.STORED_PROCEDURES, EOStoredProceduresListContentProvider.BLANK_STORED_PROCEDURE);
				myNextPrimaryKeyBinding = new ComboViewerBinding(myNextPrimaryKeyComboViewer, myEntity, EOEntity.NEXT_PRIMARY_KEY_PROCEDURE, myEntity.getModel(), EOModel.STORED_PROCEDURES, EOStoredProceduresListContentProvider.BLANK_STORED_PROCEDURE);
			}
		}
	}

	protected void disposeBindings() {
		if (myInsertBinding != null) {
			myInsertBinding.dispose();
		}
		if (myDeleteBinding != null) {
			myDeleteBinding.dispose();
		}
		if (myFetchAllBinding != null) {
			myFetchAllBinding.dispose();
		}
		if (myFetchWithPrimaryKeyBinding != null) {
			myFetchWithPrimaryKeyBinding.dispose();
		}
		if (myNextPrimaryKeyBinding != null) {
			myNextPrimaryKeyBinding.dispose();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}
}

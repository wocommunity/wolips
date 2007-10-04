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
package org.objectstyle.wolips.eomodeler.editors.fetchspec;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.IEOAttribute;
import org.objectstyle.wolips.eomodeler.outline.EOEntityTreeViewUpdater;
import org.objectstyle.wolips.eomodeler.outline.EOModelOutlineContentProvider;

public class EOFetchSpecQualifierEditorSection extends AbstractPropertySection implements ISelectionChangedListener {
	private EOFetchSpecification _fetchSpecification;

	private Text _nameText;

	private Text _qualifierText;

	private TreeViewer _modelTreeViewer;

	private EOEntityTreeViewUpdater _entityTreeViewUpdater;

	private DataBindingContext _bindingContext;

	public EOFetchSpecQualifierEditorSection() {
		// DO NOTHING
	}

	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(parent);
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

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.NAME), SWT.NONE);
		_nameText = new Text(topForm, SWT.BORDER);
		GridData nameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_nameText.setLayoutData(nameLayoutData);

		_modelTreeViewer = new TreeViewer(topForm);
		GridData modelTreeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		modelTreeLayoutData.horizontalSpan = 2;
		modelTreeLayoutData.heightHint = 100;
		_modelTreeViewer.getTree().setLayoutData(modelTreeLayoutData);
		_entityTreeViewUpdater = new EOEntityTreeViewUpdater(_modelTreeViewer, new EOModelOutlineContentProvider(true, true, true, false, false, false, false, true));
		_modelTreeViewer.addSelectionChangedListener(this);

		_qualifierText = getWidgetFactory().createText(topForm, "", SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		_qualifierText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		GridData qualifierLayoutData = new GridData(GridData.FILL_BOTH);
		qualifierLayoutData.horizontalSpan = 2;
		qualifierLayoutData.heightHint = 150;
		_qualifierText.setLayoutData(qualifierLayoutData);
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_fetchSpecification = (EOFetchSpecification) selectedObject;
		if (_fetchSpecification != null) {
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(SWTObservables.observeText(_nameText, SWT.Modify), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.NAME), null, null);
			_bindingContext.bindValue(SWTObservables.observeText(_qualifierText, SWT.Modify), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.QUALIFIER_STRING), null, null);
			_entityTreeViewUpdater.setEntity(_fetchSpecification.getEntity());
		}
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		String keyPath;
		Object selectedObject = selection.getFirstElement();
		if (selectedObject instanceof IEOAttribute) {
			keyPath = ((IEOAttribute) selectedObject).getName();
		} else if (selectedObject instanceof AbstractEOAttributePath) {
			keyPath = ((AbstractEOAttributePath) selectedObject).toKeyPath();
		} else {
			keyPath = null;
		}
		if (keyPath != null) {
			String qualifierString = _qualifierText.getText();
			if (qualifierString != null) {
				int caretPosition = _qualifierText.getCaretPosition();
				int startPosition = caretPosition;
				for (startPosition = caretPosition - 1; startPosition > 0; startPosition--) {
					char ch = qualifierString.charAt(startPosition);
					if (!Character.isLetterOrDigit(ch) && ch != '.') {
						startPosition++;
						break;
					}
				}
				int endPosition;
				for (endPosition = caretPosition; endPosition < qualifierString.length(); endPosition++) {
					char ch = qualifierString.charAt(endPosition);
					if (!Character.isLetterOrDigit(ch) && ch != '.') {
						break;
					}
				}
				_qualifierText.setSelection(startPosition, endPosition);
				if (startPosition > 0 && qualifierString.charAt(startPosition - 1) != ' ' && qualifierString.charAt(startPosition - 1) != '(') {
					keyPath = " " + keyPath;
				}
			}
			_qualifierText.insert(keyPath);
			_qualifierText.setFocus();
		}
	}
}

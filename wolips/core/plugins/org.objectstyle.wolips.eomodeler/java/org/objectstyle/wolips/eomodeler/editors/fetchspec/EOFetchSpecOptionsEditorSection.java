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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.utils.BooleanUpdateValueStrategy;

public class EOFetchSpecOptionsEditorSection extends AbstractPropertySection {
	private EOFetchSpecification _fetchSpecification;

	private Text _fetchLimitText;

	private Button _promptsAfterFetchLimitButton;

	private Button _deepButton;

	private Button _usesDistinctButton;

	private Button _lockObjectsButton;

	private Button _refreshesRefetchedObjectsButton;

	private Button _requiresAllQualifierBindingVariablesButton;

	private DataBindingContext _bindingContext;

	public EOFetchSpecOptionsEditorSection() {
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

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.FETCH_LIMIT), SWT.NONE);
		_fetchLimitText = new Text(topForm, SWT.BORDER);
		GridData fetchLimitLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_fetchLimitText.setLayoutData(fetchLimitLayoutData);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.PROMPTS_AFTER_FETCH_LIMIT), SWT.NONE);
		_promptsAfterFetchLimitButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.DEEP), SWT.NONE);
		_deepButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.USES_DISTINCT), SWT.NONE);
		_usesDistinctButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.LOCKS_OBJECTS), SWT.NONE);
		_lockObjectsButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.REFRESHES_REFETCHED_OBJECTS), SWT.NONE);
		_refreshesRefetchedObjectsButton = new Button(topForm, SWT.CHECK);

		getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.REQUIRES_ALL_QUALIFIER_BINDING_VARIABLES), SWT.NONE);
		_requiresAllQualifierBindingVariablesButton = new Button(topForm, SWT.CHECK);

	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_fetchSpecification = (EOFetchSpecification) selectedObject;
		if (_fetchSpecification != null) {
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(SWTObservables.observeText(_fetchLimitText, SWT.Modify), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.FETCH_LIMIT), null, null);
			// new BindSpec(null, null, new RegexStringValidator("^[0-9]*$",
			// "^[0-9]+$", "Please enter a number"), null));
			_bindingContext.bindValue(SWTObservables.observeSelection(_promptsAfterFetchLimitButton), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.PROMPTS_AFTER_FETCH_LIMIT), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_deepButton), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.DEEP), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_usesDistinctButton), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.USES_DISTINCT), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_lockObjectsButton), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.LOCKS_OBJECTS), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_refreshesRefetchedObjectsButton), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.REFRESHES_REFETCHED_OBJECTS), null, new BooleanUpdateValueStrategy());
			_bindingContext.bindValue(SWTObservables.observeSelection(_requiresAllQualifierBindingVariablesButton), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.REQUIRES_ALL_QUALIFIER_BINDING_VARIABLES), null, new BooleanUpdateValueStrategy());
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
}

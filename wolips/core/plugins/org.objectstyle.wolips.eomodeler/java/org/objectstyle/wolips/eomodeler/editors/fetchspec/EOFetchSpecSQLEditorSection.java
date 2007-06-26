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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.editors.storedProcedures.EOStoredProceduresConstants;
import org.objectstyle.wolips.eomodeler.editors.storedProcedures.EOStoredProceduresContentProvider;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOFetchSpecSQLEditorSection extends AbstractPropertySection implements ISelectionChangedListener, SelectionListener {
	private EOFetchSpecification _fetchSpecification;

	private Text _rawSQLText;

	private TableViewer _storedProcedureTableViewer;

	private TableRefreshPropertyListener _storedProcedureChangedRefresher;

	private StoredProcedureChangedHandler _storedProcedureChangedHandler;

	private Button _useQualifierButton;

	private Button _useRawSQLButton;

	private Button _useStoredProcedureButton;

	private DataBindingContext _bindingContext;

	public EOFetchSpecSQLEditorSection() {
		_storedProcedureChangedHandler = new StoredProcedureChangedHandler();
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
		topForm.setLayout(topFormLayout);

		_useQualifierButton = new Button(topForm, SWT.RADIO);
		_useQualifierButton.setText(Messages.getString("EOFetchSpecSQLEditorSection.useQualifier")); //$NON-NLS-1$
		_useRawSQLButton = new Button(topForm, SWT.RADIO);
		_useRawSQLButton.setText(Messages.getString("EOFetchSpecSQLEditorSection.useRawSQL")); //$NON-NLS-1$

		_rawSQLText = new Text(topForm, SWT.BORDER);
		GridData nameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		_rawSQLText.setLayoutData(nameLayoutData);

		_useStoredProcedureButton = new Button(topForm, SWT.RADIO);
		_useStoredProcedureButton.setText(Messages.getString("EOFetchSpecSQLEditorSection.useStoredProcedure")); //$NON-NLS-1$

		_storedProcedureTableViewer = TableUtils.createTableViewer(topForm, "EOStoredProcedure", EOStoredProceduresConstants.COLUMNS, new EOStoredProceduresContentProvider(), new TablePropertyLabelProvider(EOStoredProceduresConstants.COLUMNS), new TablePropertyViewerSorter(EOStoredProceduresConstants.COLUMNS));
		GridData rawRowKeyPathsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		rawRowKeyPathsTableLayoutData.heightHint = 100;
		_storedProcedureTableViewer.getTable().setLayoutData(rawRowKeyPathsTableLayoutData);
		_storedProcedureTableViewer.addSelectionChangedListener(this);
		_storedProcedureChangedRefresher = new TableRefreshPropertyListener(_storedProcedureTableViewer);
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
		_fetchSpecification = (EOFetchSpecification) selectedObject;
		if (_fetchSpecification != null) {
			addBindings();
			_storedProcedureTableViewer.setInput(_fetchSpecification);
			TableUtils.packTableColumns(_storedProcedureTableViewer);
			updateButtonsEnabled();
		}
	}

	protected void addBindings() {
		if (_fetchSpecification != null) {
			_bindingContext = new DataBindingContext();
			_bindingContext.bindValue(SWTObservables.observeText(_rawSQLText, SWT.Modify), BeansObservables.observeValue(_fetchSpecification, EOFetchSpecification.CUSTOM_QUERY_EXPRESSION), null, null);
			_fetchSpecification.getEntity().getModel().addPropertyChangeListener(EOModel.STORED_PROCEDURES, _storedProcedureChangedRefresher);
			_fetchSpecification.getEntity().getModel().addPropertyChangeListener(EOModel.STORED_PROCEDURE, _storedProcedureChangedRefresher);
			_fetchSpecification.addPropertyChangeListener(EOFetchSpecification.STORED_PROCEDURE, _storedProcedureChangedHandler);
			_fetchSpecification.addPropertyChangeListener(EOFetchSpecification.CUSTOM_QUERY_EXPRESSION, _storedProcedureChangedHandler);
		}
	}

	protected void disposeBindings() {
		if (_bindingContext != null) {
			_bindingContext.dispose();
		}
		if (_fetchSpecification != null) {
			_fetchSpecification.getEntity().getModel().removePropertyChangeListener(EOModel.STORED_PROCEDURES, _storedProcedureChangedRefresher);
			_fetchSpecification.getEntity().getModel().removePropertyChangeListener(EOModel.STORED_PROCEDURE, _storedProcedureChangedRefresher);
			_fetchSpecification.removePropertyChangeListener(EOFetchSpecification.STORED_PROCEDURE, _storedProcedureChangedHandler);
			_fetchSpecification.removePropertyChangeListener(EOFetchSpecification.CUSTOM_QUERY_EXPRESSION, _storedProcedureChangedHandler);
		}
	}

	protected void removeButtonListeners() {
		_useQualifierButton.removeSelectionListener(this);
		_useRawSQLButton.removeSelectionListener(this);
		_useStoredProcedureButton.removeSelectionListener(this);
	}

	protected void addButtonListeners() {
		_useQualifierButton.addSelectionListener(this);
		_useRawSQLButton.addSelectionListener(this);
		_useStoredProcedureButton.addSelectionListener(this);
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}

	public void widgetSelected(SelectionEvent event) {
		Button source = (Button) event.getSource();
		if (source.getSelection()) {
			disposeBindings();
			if (source == _useQualifierButton) {
				_fetchSpecification.useQualifier();
			} else if (source == _useRawSQLButton) {
				_fetchSpecification.useCustomQueryExpression();
			} else if (source == _useStoredProcedureButton) {
				Iterator storedProceduresIter = _fetchSpecification.getEntity().getModel().getStoredProcedures().iterator();
				if (storedProceduresIter.hasNext()) {
					EOStoredProcedure storedProcedure = (EOStoredProcedure) storedProceduresIter.next();
					_fetchSpecification.setStoredProcedure(storedProcedure);
				}
			}
			updateButtonsEnabled();
			addBindings();
		}
	}

	public void updateButtonsEnabled() {
		removeButtonListeners();
		_useQualifierButton.setSelection(_fetchSpecification.isUsingQualifier());
		_useRawSQLButton.setSelection(_fetchSpecification.isUsingCustomQuery());
		_useStoredProcedureButton.setSelection(_fetchSpecification.isUsingStoredProcedure());
		_rawSQLText.setEnabled(_fetchSpecification.isUsingCustomQuery());
		if (!_fetchSpecification.isUsingStoredProcedure() && !_storedProcedureTableViewer.getSelection().isEmpty()) {
			_storedProcedureTableViewer.setSelection(new StructuredSelection());
			_storedProcedureTableViewer.getTable().setEnabled(false);
		} else if (_fetchSpecification.isUsingStoredProcedure()) {
			_storedProcedureTableViewer.getTable().setEnabled(true);
			_storedProcedureTableViewer.setSelection(new StructuredSelection(_fetchSpecification.getStoredProcedure()));
		}
		addButtonListeners();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		EOStoredProcedure storedProcedure = (EOStoredProcedure) selection.getFirstElement();
		_fetchSpecification.setStoredProcedure(storedProcedure);
	}

	protected class StoredProcedureChangedHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			EOFetchSpecSQLEditorSection.this.updateButtonsEnabled();
		}
	}
}

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

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
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
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOFetchSpecSQLEditorSection extends AbstractPropertySection implements ISelectionChangedListener, SelectionListener {
	private EOFetchSpecification myFetchSpecification;

	private Text myRawSQLText;

	private TableViewer myStoredProcedureTableViewer;

	private TableRefreshPropertyListener myStoredProcedureChangedRefresher;

	private StoredProcedureChangedHandler myStoredProcedureChangedHandler;

	private Button myUseQualifierButton;

	private Button myUseRawSQLButton;

	private Button myUseStoredProcedureButton;

	private DataBindingContext myBindingContext;

	public EOFetchSpecSQLEditorSection() {
		myStoredProcedureChangedHandler = new StoredProcedureChangedHandler();
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
		topForm.setLayout(topFormLayout);

		myUseQualifierButton = new Button(topForm, SWT.RADIO);
		myUseQualifierButton.setText(Messages.getString("EOFetchSpecSQLEditorSection.useQualifier")); //$NON-NLS-1$
		myUseRawSQLButton = new Button(topForm, SWT.RADIO);
		myUseRawSQLButton.setText(Messages.getString("EOFetchSpecSQLEditorSection.useRawSQL")); //$NON-NLS-1$

		myRawSQLText = new Text(topForm, SWT.BORDER);
		GridData nameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		myRawSQLText.setLayoutData(nameLayoutData);

		myUseStoredProcedureButton = new Button(topForm, SWT.RADIO);
		myUseStoredProcedureButton.setText(Messages.getString("EOFetchSpecSQLEditorSection.useStoredProcedure")); //$NON-NLS-1$

		myStoredProcedureTableViewer = TableUtils.createTableViewer(topForm, "EOStoredProcedure", EOStoredProceduresConstants.COLUMNS, new EOStoredProceduresContentProvider(), new TablePropertyLabelProvider(EOStoredProceduresConstants.COLUMNS), new TablePropertyViewerSorter(EOStoredProceduresConstants.COLUMNS));
		GridData rawRowKeyPathsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		rawRowKeyPathsTableLayoutData.heightHint = 100;
		myStoredProcedureTableViewer.getTable().setLayoutData(rawRowKeyPathsTableLayoutData);
		myStoredProcedureTableViewer.addSelectionChangedListener(this);
		myStoredProcedureChangedRefresher = new TableRefreshPropertyListener(myStoredProcedureTableViewer);
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		myFetchSpecification = (EOFetchSpecification) selectedObject;
		if (myFetchSpecification != null) {
			addBindings();
			myStoredProcedureTableViewer.setInput(myFetchSpecification);
			TableUtils.packTableColumns(myStoredProcedureTableViewer);
			updateButtonsEnabled();
		}
	}

	protected void addBindings() {
		if (myFetchSpecification != null) {
			myBindingContext = BindingFactory.createContext();
			myBindingContext.bind(myRawSQLText, new Property(myFetchSpecification, EOFetchSpecification.CUSTOM_QUERY_EXPRESSION), null);
			myFetchSpecification.getEntity().getModel().addPropertyChangeListener(EOModel.STORED_PROCEDURES, myStoredProcedureChangedRefresher);
			myFetchSpecification.getEntity().getModel().addPropertyChangeListener(EOModel.STORED_PROCEDURE, myStoredProcedureChangedRefresher);
			myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.STORED_PROCEDURE, myStoredProcedureChangedHandler);
			myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.CUSTOM_QUERY_EXPRESSION, myStoredProcedureChangedHandler);
		}
	}

	protected void disposeBindings() {
		if (myBindingContext != null) {
			myBindingContext.dispose();
		}
		if (myFetchSpecification != null) {
			myFetchSpecification.getEntity().getModel().removePropertyChangeListener(EOModel.STORED_PROCEDURES, myStoredProcedureChangedRefresher);
			myFetchSpecification.getEntity().getModel().removePropertyChangeListener(EOModel.STORED_PROCEDURE, myStoredProcedureChangedRefresher);
			myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.STORED_PROCEDURE, myStoredProcedureChangedHandler);
			myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.CUSTOM_QUERY_EXPRESSION, myStoredProcedureChangedHandler);
		}
	}

	protected void removeButtonListeners() {
		myUseQualifierButton.removeSelectionListener(this);
		myUseRawSQLButton.removeSelectionListener(this);
		myUseStoredProcedureButton.removeSelectionListener(this);
	}

	protected void addButtonListeners() {
		myUseQualifierButton.addSelectionListener(this);
		myUseRawSQLButton.addSelectionListener(this);
		myUseStoredProcedureButton.addSelectionListener(this);
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void widgetDefaultSelected(SelectionEvent _e) {
		widgetSelected(_e);
	}

	public void widgetSelected(SelectionEvent _e) {
		Button source = (Button) _e.getSource();
		if (source.getSelection()) {
			disposeBindings();
			if (source == myUseQualifierButton) {
				myFetchSpecification.useQualifier();
			} else if (source == myUseRawSQLButton) {
				myFetchSpecification.useCustomQueryExpression();
			} else if (source == myUseStoredProcedureButton) {
				Iterator storedProceduresIter = myFetchSpecification.getEntity().getModel().getStoredProcedures().iterator();
				if (storedProceduresIter.hasNext()) {
					EOStoredProcedure storedProcedure = (EOStoredProcedure) storedProceduresIter.next();
					myFetchSpecification.setStoredProcedure(storedProcedure);
				}
			}
			updateButtonsEnabled();
			addBindings();
		}
	}

	public void updateButtonsEnabled() {
		removeButtonListeners();
		myUseQualifierButton.setSelection(myFetchSpecification.isUsingQualifier());
		myUseRawSQLButton.setSelection(myFetchSpecification.isUsingCustomQuery());
		myUseStoredProcedureButton.setSelection(myFetchSpecification.isUsingStoredProcedure());
		myRawSQLText.setEnabled(myFetchSpecification.isUsingCustomQuery());
		if (!myFetchSpecification.isUsingStoredProcedure() && !myStoredProcedureTableViewer.getSelection().isEmpty()) {
			myStoredProcedureTableViewer.setSelection(new StructuredSelection());
			myStoredProcedureTableViewer.getTable().setEnabled(false);
		} else if (myFetchSpecification.isUsingStoredProcedure()) {
			myStoredProcedureTableViewer.getTable().setEnabled(true);
			myStoredProcedureTableViewer.setSelection(new StructuredSelection(myFetchSpecification.getStoredProcedure()));
		}
		addButtonListeners();
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
		EOStoredProcedure storedProcedure = (EOStoredProcedure) selection.getFirstElement();
		myFetchSpecification.setStoredProcedure(storedProcedure);
	}

	protected class StoredProcedureChangedHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _evt) {
			EOFetchSpecSQLEditorSection.this.updateButtonsEnabled();
		}
	}
}

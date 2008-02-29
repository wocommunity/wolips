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

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.outline.EOEntityTreeViewUpdater;
import org.objectstyle.wolips.eomodeler.outline.EOModelOutlineContentProvider;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOFetchSpecRawFetchEditorSection extends AbstractPropertySection implements ISelectionChangedListener, SelectionListener {
	private EOFetchSpecification myFetchSpecification;

	private TreeViewer myModelTreeViewer;

	private TableViewer myRawRowKeyPathsTableViewer;

	private AddRemoveButtonGroup myAddRemoveButtonGroup;

	private EOEntityTreeViewUpdater myEntityTreeViewUpdater;

	private TableRefreshPropertyListener myRawRowKeyPathsChangedRefresher;

	private Button myFetchEnterpriseObjectsButton;

	private Button myFetchAllAttributesAsRawRowsButton;

	private Button myFetchSpecificAttributesAsRawRowsButton;

	public EOFetchSpecRawFetchEditorSection() {
		// DO NOTHING
	}
	
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);
		Composite form = getWidgetFactory().createFlatFormComposite(_parent);
		((FormLayout)form.getLayout()).marginWidth = 10;
		((FormLayout)form.getLayout()).marginHeight = 10;

		Composite topForm = getWidgetFactory().createPlainComposite(form, SWT.NONE);
		FormData topFormData = new FormData();
		topFormData.top = new FormAttachment(0);
		topFormData.left = new FormAttachment(0);
		topFormData.right = new FormAttachment(100);
		topFormData.bottom = new FormAttachment(100);
		topForm.setLayoutData(topFormData);

		GridLayout topFormLayout = new GridLayout();
		topFormLayout.marginWidth = 0;
		topFormLayout.marginHeight = 0;
		topForm.setLayout(topFormLayout);

		Composite fetchStyleComposite = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
		GridLayout fetchStyleLayout = new GridLayout();
		fetchStyleComposite.setLayout(fetchStyleLayout);
		myFetchEnterpriseObjectsButton = new Button(fetchStyleComposite, SWT.RADIO);
		myFetchEnterpriseObjectsButton.setText(Messages.getString("EOFetchSpecRawFetchEditorSection.fetchEnterpriseObjects")); //$NON-NLS-1$
		myFetchAllAttributesAsRawRowsButton = new Button(fetchStyleComposite, SWT.RADIO);
		myFetchAllAttributesAsRawRowsButton.setText(Messages.getString("EOFetchSpecRawFetchEditorSection.fetchAllAttributes")); //$NON-NLS-1$
		myFetchSpecificAttributesAsRawRowsButton = new Button(fetchStyleComposite, SWT.RADIO);
		myFetchSpecificAttributesAsRawRowsButton.setText(Messages.getString("EOFetchSpecRawFetchEditorSection.fetchSpecificAttributes")); //$NON-NLS-1$

		myModelTreeViewer = new TreeViewer(topForm);
		GridData modelTreeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		modelTreeLayoutData.heightHint = 100;
		myModelTreeViewer.getTree().setLayoutData(modelTreeLayoutData);
		myEntityTreeViewUpdater = new EOEntityTreeViewUpdater(myModelTreeViewer, new EOModelOutlineContentProvider(true, true, true, false, false, false, false, true));
		myModelTreeViewer.addSelectionChangedListener(this);

		myRawRowKeyPathsTableViewer = TableUtils.createTableViewer(topForm, "EOFetchSpecification", EORawRowKeyPathsConstants.COLUMNS, new RawRowKeyPathsContentProvider(), new RawRowKeyPathsLabelProvider(EORawRowKeyPathsConstants.COLUMNS), new RawRowKeyPathsViewerSorter(EORawRowKeyPathsConstants.COLUMNS));
		GridData rawRowKeyPathsTableLayoutData = new GridData(GridData.FILL_BOTH);
		rawRowKeyPathsTableLayoutData.heightHint = 100;
		myRawRowKeyPathsTableViewer.getTable().setLayoutData(rawRowKeyPathsTableLayoutData);
		myRawRowKeyPathsTableViewer.addSelectionChangedListener(this);
		myRawRowKeyPathsChangedRefresher = new TableRefreshPropertyListener("RawRowKeyPathsChanged", myRawRowKeyPathsTableViewer);

		myAddRemoveButtonGroup = new AddRemoveButtonGroup(topForm, new AddRawRowKeyPathHandler(), new RemoveRawRowKeyPathHandler());
		myAddRemoveButtonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		myFetchSpecification = (EOFetchSpecification) selectedObject;
		if (myFetchSpecification != null) {
			myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.RAW_ROW_KEY_PATHS, myRawRowKeyPathsChangedRefresher);
			myEntityTreeViewUpdater.setEntity(myFetchSpecification.getEntity());
			myRawRowKeyPathsTableViewer.setInput(myFetchSpecification);
			TableUtils.packTableColumns(myRawRowKeyPathsTableViewer);
			updateButtonsEnabled();
		}
	}

	protected void disposeBindings() {
		if (myFetchSpecification != null) {
			myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.RAW_ROW_KEY_PATHS, myRawRowKeyPathsChangedRefresher);
		}
	}

	protected void removeButtonListeners() {
		myFetchEnterpriseObjectsButton.removeSelectionListener(this);
		myFetchAllAttributesAsRawRowsButton.removeSelectionListener(this);
		myFetchSpecificAttributesAsRawRowsButton.removeSelectionListener(this);
	}

	protected void addButtonListeners() {
		myFetchEnterpriseObjectsButton.addSelectionListener(this);
		myFetchAllAttributesAsRawRowsButton.addSelectionListener(this);
		myFetchSpecificAttributesAsRawRowsButton.addSelectionListener(this);
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void widgetDefaultSelected(SelectionEvent _e) {
		widgetSelected(_e);
	}

	public void widgetSelected(SelectionEvent _e) {
		Object source = _e.getSource();
		if (source == myFetchEnterpriseObjectsButton) {
			myFetchSpecification.fetchEnterpriseObjects();
		} else if (source == myFetchAllAttributesAsRawRowsButton) {
			myFetchSpecification.fetchAllAttributesAsRawRows();
		} else if (source == myFetchSpecificAttributesAsRawRowsButton) {
			myFetchSpecification.fetchSpecificAttributesAsRawRows();
		}
		updateButtonsEnabled();
	}

	public void addRawRowKeyPath() {
		IStructuredSelection selection = (IStructuredSelection) myModelTreeViewer.getSelection();
		Object selectedObject = selection.getFirstElement();
		String path;
		if (selectedObject instanceof EOAttributePath) {
			path = ((EOAttributePath) selectedObject).toKeyPath();
		} else if (selectedObject instanceof EOAttribute) {
			path = ((EOAttribute) selectedObject).getName();
		} else {
			path = null;
		}
		if (path != null) {
			myFetchSpecification.addRawRowKeyPath(path, true);
			TableUtils.packTableColumns(myRawRowKeyPathsTableViewer);
			updateButtonsEnabled();
		}
	}

	public void removePrefetchKeyPath() {
		IStructuredSelection selection = (IStructuredSelection) myRawRowKeyPathsTableViewer.getSelection();
		Iterator selectedObjectsIter = selection.toList().iterator();
		while (selectedObjectsIter.hasNext()) {
			String rawRowKeyPath = (String) selectedObjectsIter.next();
			myFetchSpecification.removeRawRowKeyPath(rawRowKeyPath, true);
		}
		updateButtonsEnabled();
	}

	public void updateButtonsEnabled() {
		removeButtonListeners();
		if (myFetchSpecification.isFetchEnterpriseObjects()) {
			myFetchEnterpriseObjectsButton.setSelection(true);
			myFetchAllAttributesAsRawRowsButton.setSelection(false);
			myFetchSpecificAttributesAsRawRowsButton.setSelection(false);
		} else if (myFetchSpecification.isFetchAllAttributesAsRawRows()) {
			myFetchEnterpriseObjectsButton.setSelection(false);
			myFetchAllAttributesAsRawRowsButton.setSelection(true);
			myFetchSpecificAttributesAsRawRowsButton.setSelection(false);
		} else {
			myFetchEnterpriseObjectsButton.setSelection(false);
			myFetchAllAttributesAsRawRowsButton.setSelection(false);
			myFetchSpecificAttributesAsRawRowsButton.setSelection(true);
		}
		addButtonListeners();
		boolean enabled = myFetchSpecification.isFetchSpecificAttributesAsRawRows();
		Object selectedObject = ((IStructuredSelection) myModelTreeViewer.getSelection()).getFirstElement();
		boolean addEnabled = (selectedObject instanceof EOAttributePath || selectedObject instanceof EOAttribute);
		myModelTreeViewer.getTree().setEnabled(enabled);
		myAddRemoveButtonGroup.setAddEnabled(enabled && addEnabled);
		myAddRemoveButtonGroup.setRemoveEnabled(enabled && !myRawRowKeyPathsTableViewer.getSelection().isEmpty());
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		updateButtonsEnabled();
	}

	protected class AddRawRowKeyPathHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOFetchSpecRawFetchEditorSection.this.addRawRowKeyPath();
		}
	}

	protected class RemoveRawRowKeyPathHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOFetchSpecRawFetchEditorSection.this.removePrefetchKeyPath();
		}
	}
}

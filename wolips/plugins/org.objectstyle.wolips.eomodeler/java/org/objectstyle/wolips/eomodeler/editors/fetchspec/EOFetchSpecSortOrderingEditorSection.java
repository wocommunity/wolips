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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.outline.EOEntityTreeViewUpdater;
import org.objectstyle.wolips.eomodeler.outline.EOModelOutlineContentProvider;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyCellModifier;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOFetchSpecSortOrderingEditorSection extends AbstractPropertySection implements ISelectionChangedListener {
	private EOFetchSpecification myFetchSpecification;

	private TreeViewer myModelTreeViewer;

	private TableViewer mySortOrderingsTableViewer;

	private AddRemoveButtonGroup myAddRemoveButtonGroup;

	private EOEntityTreeViewUpdater myEntityTreeViewUpdater;

	private TableRefreshPropertyListener mySortOrderingsChangedRefresher;

	private TableRowRefreshPropertyListener myTableRowRefresher;

	public EOFetchSpecSortOrderingEditorSection() {
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
		topForm.setLayout(topFormLayout);

		myModelTreeViewer = new TreeViewer(topForm);
		GridData modelTreeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		modelTreeLayoutData.heightHint = 100;
		myModelTreeViewer.getTree().setLayoutData(modelTreeLayoutData);
		myEntityTreeViewUpdater = new EOEntityTreeViewUpdater(myModelTreeViewer, new EOModelOutlineContentProvider(true, true, true, false, false, false, false));
		myModelTreeViewer.addSelectionChangedListener(this);

		mySortOrderingsTableViewer = TableUtils.createTableViewer(topForm, "EOFetchSpecification", EOSortOrderingsConstants.COLUMNS, new EOSortOrderingsContentProvider(), new EOSortOrderingsLabelProvider(EOSortOrderingsConstants.COLUMNS), null);

		TableColumn ascendingColumn = mySortOrderingsTableViewer.getTable().getColumn(TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.ASCENDING));
		ascendingColumn.setText("");
		ascendingColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.ASCENDING_ICON));

		TableColumn caseInsensitiveColumn = mySortOrderingsTableViewer.getTable().getColumn(TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.CASE_INSENSITIVE));
		caseInsensitiveColumn.setText("i/s");

		GridData sortOrderingsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		sortOrderingsTableLayoutData.heightHint = 100;
		mySortOrderingsTableViewer.getTable().setLayoutData(sortOrderingsTableLayoutData);
		mySortOrderingsTableViewer.addSelectionChangedListener(this);
		mySortOrderingsChangedRefresher = new TableRefreshPropertyListener(mySortOrderingsTableViewer);
		myTableRowRefresher = new TableRowRefreshPropertyListener(mySortOrderingsTableViewer);

		CellEditor[] cellEditors = new CellEditor[EOSortOrderingsConstants.COLUMNS.length];
		cellEditors[TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.KEY)] = new TextCellEditor(mySortOrderingsTableViewer.getTable());
		cellEditors[TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.ASCENDING)] = new CheckboxCellEditor(mySortOrderingsTableViewer.getTable());
		cellEditors[TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.CASE_INSENSITIVE)] = new CheckboxCellEditor(mySortOrderingsTableViewer.getTable());
		mySortOrderingsTableViewer.setCellEditors(cellEditors);
		mySortOrderingsTableViewer.setCellModifier(new TablePropertyCellModifier(mySortOrderingsTableViewer));

		myAddRemoveButtonGroup = new AddRemoveButtonGroup(topForm, new AddSortOrderingHandler(), new RemoveSortOrderingHandler());
		myAddRemoveButtonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		myFetchSpecification = (EOFetchSpecification) selectedObject;
		if (myFetchSpecification != null) {
			myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.SORT_ORDERINGS, mySortOrderingsChangedRefresher);
			myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.SORT_ORDERING, myTableRowRefresher);
			myEntityTreeViewUpdater.setEntity(myFetchSpecification.getEntity());
			mySortOrderingsTableViewer.setInput(myFetchSpecification);
			TableUtils.packTableColumns(mySortOrderingsTableViewer);
			updateButtonsEnabled();
		}
	}

	protected void disposeBindings() {
		if (myFetchSpecification != null) {
			myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.SORT_ORDERINGS, mySortOrderingsChangedRefresher);
			myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.SORT_ORDERING, myTableRowRefresher);
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void addSortOrdering() {
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
			EOSortOrdering sortOrdering = new EOSortOrdering();
			sortOrdering.setKey(path);
			sortOrdering.setSelectorName(EOSortOrdering.ASCENDING);
			myFetchSpecification.addSortOrdering(sortOrdering, true);
			TableUtils.packTableColumns(mySortOrderingsTableViewer);
		}
	}

	public void removeSortOrdering() {
		IStructuredSelection selection = (IStructuredSelection) mySortOrderingsTableViewer.getSelection();
		Iterator selectedObjectsIter = selection.toList().iterator();
		while (selectedObjectsIter.hasNext()) {
			EOSortOrdering sortOrdering = (EOSortOrdering) selectedObjectsIter.next();
			myFetchSpecification.removeSortOrdering(sortOrdering, true);
		}
	}

	public void updateButtonsEnabled() {
		Object selectedObject = ((IStructuredSelection) myModelTreeViewer.getSelection()).getFirstElement();
		boolean addEnabled = (selectedObject instanceof EOAttributePath || selectedObject instanceof EOAttribute);
		myAddRemoveButtonGroup.setAddEnabled(addEnabled);
		myAddRemoveButtonGroup.setRemoveEnabled(!mySortOrderingsTableViewer.getSelection().isEmpty());
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		updateButtonsEnabled();
	}

	protected class AddSortOrderingHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOFetchSpecSortOrderingEditorSection.this.addSortOrdering();
		}
	}

	protected class RemoveSortOrderingHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOFetchSpecSortOrderingEditorSection.this.removeSortOrdering();
		}
	}
}

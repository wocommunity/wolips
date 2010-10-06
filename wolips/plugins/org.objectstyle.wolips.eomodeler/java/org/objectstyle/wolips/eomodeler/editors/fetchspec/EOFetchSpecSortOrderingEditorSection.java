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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.outline.EOEntityTreeViewUpdater;
import org.objectstyle.wolips.eomodeler.outline.EOModelOutlineContentProvider;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.FormUtils;
import org.objectstyle.wolips.eomodeler.utils.StayEditingCellEditorListener;
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
	
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	public void createControls(Composite _parent, TabbedPropertySheetPage _tabbedPropertySheetPage) {
		super.createControls(_parent, _tabbedPropertySheetPage);

		Composite form = getWidgetFactory().createPlainComposite(_parent, SWT.NONE);
		FormLayout formLayout = new FormLayout();
		form.setLayout(formLayout);

		Composite topForm = FormUtils.createForm(getWidgetFactory(), form, 1);

		myModelTreeViewer = new TreeViewer(topForm);
		GridData modelTreeLayoutData = new GridData(GridData.FILL_BOTH);
		//modelTreeLayoutData.heightHint = 100;
		myModelTreeViewer.getTree().setLayoutData(modelTreeLayoutData);
		myEntityTreeViewUpdater = new EOEntityTreeViewUpdater(myModelTreeViewer, new EOModelOutlineContentProvider(true, true, true, false, false, false, false, true));
		myModelTreeViewer.addSelectionChangedListener(this);

		mySortOrderingsTableViewer = TableUtils.createTableViewer(topForm, "EOFetchSpecification", EOSortOrdering.class.getName(), new EOSortOrderingsContentProvider(), new EOSortOrderingsLabelProvider(EOSortOrdering.class.getName()), null);

		TableColumn ascendingColumn = TableUtils.getColumn(mySortOrderingsTableViewer, EOSortOrdering.class.getName(), EOSortOrdering.ASCENDING);
		if (ascendingColumn != null) {
			ascendingColumn.setText("");
			ascendingColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.ASCENDING_ICON));
		}

		TableColumn caseInsensitiveColumn = TableUtils.getColumn(mySortOrderingsTableViewer, EOSortOrdering.class.getName(), EOSortOrdering.CASE_INSENSITIVE);
		if (caseInsensitiveColumn != null) {
			caseInsensitiveColumn.setText("i/s");
		}

		GridData sortOrderingsTableLayoutData = new GridData(GridData.FILL_BOTH);
		//sortOrderingsTableLayoutData.heightHint = 100;
		mySortOrderingsTableViewer.addSelectionChangedListener(this);
		mySortOrderingsChangedRefresher = new TableRefreshPropertyListener("SortOrderingsChanged", mySortOrderingsTableViewer);
		myTableRowRefresher = new TableRowRefreshPropertyListener(mySortOrderingsTableViewer);

		CellEditor[] cellEditors = new CellEditor[TableUtils.getColumnsForTableNamed(EOSortOrdering.class.getName()).length];
		TableUtils.setCellEditor(EOSortOrdering.class.getName(), EOSortOrdering.KEY, new WOTextCellEditor(mySortOrderingsTableViewer.getTable()), cellEditors);
		TableUtils.setCellEditor(EOSortOrdering.class.getName(), EOSortOrdering.ASCENDING, new CheckboxCellEditor(mySortOrderingsTableViewer.getTable()), cellEditors);
		TableUtils.setCellEditor(EOSortOrdering.class.getName(), EOSortOrdering.CASE_INSENSITIVE, new CheckboxCellEditor(mySortOrderingsTableViewer.getTable()), cellEditors);
		mySortOrderingsTableViewer.setCellEditors(cellEditors);
		mySortOrderingsTableViewer.setCellModifier(new TablePropertyCellModifier(mySortOrderingsTableViewer));
		mySortOrderingsTableViewer.getTable().setLayoutData(sortOrderingsTableLayoutData);

		new StayEditingCellEditorListener(mySortOrderingsTableViewer, EOSortOrdering.class.getName(), EOSortOrdering.KEY);

		myAddRemoveButtonGroup = new AddRemoveButtonGroup(topForm, new AddSortOrderingHandler(), new RemoveSortOrderingHandler());
		myAddRemoveButtonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (ComparisonUtils.equals(selection, getSelection())) {
			return;
		}
		
		super.setInput(part, selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
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

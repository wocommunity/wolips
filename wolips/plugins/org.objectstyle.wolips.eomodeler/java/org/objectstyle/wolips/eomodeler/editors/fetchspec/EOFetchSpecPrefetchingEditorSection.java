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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.outline.EOEntityTreeViewUpdater;
import org.objectstyle.wolips.eomodeler.outline.EOModelOutlineContentProvider;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOFetchSpecPrefetchingEditorSection extends AbstractPropertySection implements ISelectionChangedListener {
	private EOFetchSpecification myFetchSpecification;

	private TreeViewer myModelTreeViewer;

	private TableViewer myPrefetchKeyPathsTableViewer;

	private AddRemoveButtonGroup myAddRemoveButtonGroup;

	private EOEntityTreeViewUpdater myEntityTreeViewUpdater;

	private TableRefreshPropertyListener myPrefetchKeyPathsChangedRefresher;

	public EOFetchSpecPrefetchingEditorSection() {
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
		myEntityTreeViewUpdater = new EOEntityTreeViewUpdater(myModelTreeViewer, new EOModelOutlineContentProvider(true, false, true, false, false, false, false));
		myModelTreeViewer.addSelectionChangedListener(this);

		myPrefetchKeyPathsTableViewer = TableUtils.createTableViewer(topForm, "EOFetchSpecification", EOPrefetchingKeyPathsConstants.COLUMNS, new PrefetchingKeyPathsContentProvider(), new PrefetchingKeyPathsLabelProvider(EOPrefetchingKeyPathsConstants.COLUMNS), new PrefetchingKeyPathsViewerSorter(EOPrefetchingKeyPathsConstants.COLUMNS));
		GridData prefetchKeyPathsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		prefetchKeyPathsTableLayoutData.heightHint = 100;
		myPrefetchKeyPathsTableViewer.getTable().setLayoutData(prefetchKeyPathsTableLayoutData);
		myPrefetchKeyPathsTableViewer.addSelectionChangedListener(this);
		myPrefetchKeyPathsChangedRefresher = new TableRefreshPropertyListener(myPrefetchKeyPathsTableViewer);

		myAddRemoveButtonGroup = new AddRemoveButtonGroup(topForm, new AddPrefetchKeyPathHandler(), new RemovePrefetchKeyPathHandler());
		myAddRemoveButtonGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setInput(IWorkbenchPart _part, ISelection _selection) {
		super.setInput(_part, _selection);
		disposeBindings();

		Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
		myFetchSpecification = (EOFetchSpecification) selectedObject;
		if (myFetchSpecification != null) {
			myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.PREFETCHING_RELATIONSHIP_KEY_PATHS, myPrefetchKeyPathsChangedRefresher);
			myEntityTreeViewUpdater.setEntity(myFetchSpecification.getEntity());
			myPrefetchKeyPathsTableViewer.setInput(myFetchSpecification);
			TableUtils.packTableColumns(myPrefetchKeyPathsTableViewer);
			updateButtonsEnabled();
		}
	}

	protected void disposeBindings() {
		if (myFetchSpecification != null) {
			myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.PREFETCHING_RELATIONSHIP_KEY_PATHS, myPrefetchKeyPathsChangedRefresher);
		}
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	public void addPrefetchKeyPath() {
		IStructuredSelection selection = (IStructuredSelection) myModelTreeViewer.getSelection();
		Object selectedObject = selection.getFirstElement();
		String path;
		if (selectedObject instanceof EORelationshipPath) {
			path = ((EORelationshipPath) selectedObject).toKeyPath();
		} else if (selectedObject instanceof EORelationship) {
			path = ((EORelationship) selectedObject).getName();
		} else {
			path = null;
		}
		if (path != null) {
			myFetchSpecification.addPrefetchingRelationshipKeyPath(path, true);
			TableUtils.packTableColumns(myPrefetchKeyPathsTableViewer);
		}
	}

	public void removePrefetchKeyPath() {
		IStructuredSelection selection = (IStructuredSelection) myPrefetchKeyPathsTableViewer.getSelection();
		Iterator selectedObjectsIter = selection.toList().iterator();
		while (selectedObjectsIter.hasNext()) {
			String prefetchKeyPath = (String) selectedObjectsIter.next();
			myFetchSpecification.removePrefetchingRelationshipKeyPath(prefetchKeyPath, true);
		}
	}

	public void updateButtonsEnabled() {
		myAddRemoveButtonGroup.setAddEnabled(!myModelTreeViewer.getSelection().isEmpty());
		myAddRemoveButtonGroup.setRemoveEnabled(!myPrefetchKeyPathsTableViewer.getSelection().isEmpty());
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		updateButtonsEnabled();
	}

	protected class AddPrefetchKeyPathHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOFetchSpecPrefetchingEditorSection.this.addPrefetchKeyPath();
		}
	}

	protected class RemovePrefetchKeyPathHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			EOFetchSpecPrefetchingEditorSection.this.removePrefetchKeyPath();
		}
	}
}

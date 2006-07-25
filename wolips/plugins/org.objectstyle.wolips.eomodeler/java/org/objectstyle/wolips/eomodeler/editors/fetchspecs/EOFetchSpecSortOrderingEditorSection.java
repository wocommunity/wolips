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
package org.objectstyle.wolips.eomodeler.editors.fetchspecs;

import java.util.Iterator;

import org.eclipse.jface.internal.databinding.provisional.DataBindingContext;
import org.eclipse.jface.internal.databinding.provisional.description.Property;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOSortOrdering;
import org.objectstyle.wolips.eomodeler.model.IEOAttribute;
import org.objectstyle.wolips.eomodeler.model.IEOAttributePath;
import org.objectstyle.wolips.eomodeler.outline.EOEntityTreeViewUpdater;
import org.objectstyle.wolips.eomodeler.utils.BindingFactory;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyCellModifier;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyLabelProvider;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EOFetchSpecSortOrderingEditorSection extends AbstractPropertySection implements ISelectionChangedListener {
  private EOFetchSpecification myFetchSpecification;

  private Text myNameText;
  private TreeViewer myModelTreeViewer;
  private TableViewer mySortOrderingsTableViewer;
  private Button myAddButton;
  private Button myRemoveButton;
  private EOEntityTreeViewUpdater myEntityTreeViewUpdater;

  private DataBindingContext myBindingContext;
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
    topFormLayout.numColumns = 2;
    topForm.setLayout(topFormLayout);

    getWidgetFactory().createCLabel(topForm, Messages.getString("EOFetchSpecification." + EOFetchSpecification.NAME), SWT.NONE);
    myNameText = new Text(topForm, SWT.BORDER);
    GridData nameLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    myNameText.setLayoutData(nameLayoutData);

    myModelTreeViewer = new TreeViewer(topForm);
    GridData modelTreeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    modelTreeLayoutData.horizontalSpan = 2;
    modelTreeLayoutData.heightHint = 100;
    myModelTreeViewer.getTree().setLayoutData(modelTreeLayoutData);
    myEntityTreeViewUpdater = new EOEntityTreeViewUpdater(myModelTreeViewer);
    myModelTreeViewer.addSelectionChangedListener(this);

    mySortOrderingsTableViewer = new TableViewer(topForm, SWT.FULL_SELECTION);
    mySortOrderingsTableViewer.setColumnProperties(EOSortOrderingsConstants.COLUMNS);
    mySortOrderingsTableViewer.setContentProvider(new EOSortOrderingsContentProvider());
    mySortOrderingsTableViewer.setLabelProvider(new EOSortOrderingsLabelProvider(EOSortOrderingsConstants.COLUMNS));
    Table sortOrderingsTable = mySortOrderingsTableViewer.getTable();
    sortOrderingsTable.setHeaderVisible(true);
    sortOrderingsTable.setLinesVisible(true);

    TableUtils.createTableColumns(mySortOrderingsTableViewer, "EOFetchSpecification", EOSortOrderingsConstants.COLUMNS);

    TableColumn ascendingColumn = sortOrderingsTable.getColumn(TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.ASCENDING));
    ascendingColumn.setText("");
    ascendingColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.ASCENDING_ICON));

    TableColumn caseInsensitiveColumn = sortOrderingsTable.getColumn(TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.CASE_INSENSITIVE));
    caseInsensitiveColumn.setText("i/s");

    GridData sortOrderingsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    sortOrderingsTableLayoutData.horizontalSpan = 2;
    sortOrderingsTableLayoutData.heightHint = 100;
    sortOrderingsTable.setLayoutData(sortOrderingsTableLayoutData);
    mySortOrderingsTableViewer.addSelectionChangedListener(this);
    mySortOrderingsChangedRefresher = new TableRefreshPropertyListener(mySortOrderingsTableViewer, EOFetchSpecification.SORT_ORDERINGS);
    myTableRowRefresher = new TableRowRefreshPropertyListener(mySortOrderingsTableViewer, EOFetchSpecification.SORT_ORDERING);

    CellEditor[] cellEditors = new CellEditor[EOSortOrderingsConstants.COLUMNS.length];
    cellEditors[TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.KEY)] = new TextCellEditor(sortOrderingsTable);
    cellEditors[TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.ASCENDING)] = new CheckboxCellEditor(sortOrderingsTable);
    cellEditors[TableUtils.getColumnNumber(EOSortOrderingsConstants.COLUMNS, EOSortOrdering.CASE_INSENSITIVE)] = new CheckboxCellEditor(sortOrderingsTable);
    mySortOrderingsTableViewer.setCellEditors(cellEditors);
    mySortOrderingsTableViewer.setCellModifier(new TablePropertyCellModifier(mySortOrderingsTableViewer));

    Composite buttonGroup = getWidgetFactory().createPlainComposite(topForm, SWT.NONE);
    GridData buttonLayoutData = new GridData(GridData.FILL_HORIZONTAL);
    buttonLayoutData.horizontalSpan = 2;
    buttonGroup.setLayoutData(buttonLayoutData);
    FormLayout layout = new FormLayout();
    buttonGroup.setLayout(layout);

    myAddButton = new Button(buttonGroup, SWT.PUSH);
    myAddButton.setText(Messages.getString("button.add"));
    FormData addButtonData = new FormData();
    addButtonData.right = new FormAttachment(100, 0);
    myAddButton.setLayoutData(addButtonData);
    myAddButton.addSelectionListener(new AddSortOrderingHandler());

    myRemoveButton = new Button(buttonGroup, SWT.PUSH);
    myRemoveButton.setText(Messages.getString("button.remove"));
    FormData remoteButtonData = new FormData();
    remoteButtonData.right = new FormAttachment(myAddButton, 0);
    myRemoveButton.setLayoutData(remoteButtonData);
    myRemoveButton.addSelectionListener(new RemoveSortOrderingHandler());
  }

  public void setInput(IWorkbenchPart _part, ISelection _selection) {
    super.setInput(_part, _selection);
    disposeBindings();

    Object selectedObject = ((IStructuredSelection) _selection).getFirstElement();
    myFetchSpecification = (EOFetchSpecification) selectedObject;

    myBindingContext = BindingFactory.createContext();
    myBindingContext.bind(myNameText, new Property(myFetchSpecification, EOFetchSpecification.NAME), null);
    if (myFetchSpecification != null) {
      myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.SORT_ORDERINGS, mySortOrderingsChangedRefresher);
      myFetchSpecification.addPropertyChangeListener(EOFetchSpecification.SORT_ORDERING, myTableRowRefresher);
      myEntityTreeViewUpdater.setEntity(myFetchSpecification.getEntity());
    }
    mySortOrderingsTableViewer.setInput(myFetchSpecification);
    TableUtils.packTableColumns(mySortOrderingsTableViewer);
  }

  protected void disposeBindings() {
    if (myFetchSpecification != null) {
      myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.SORT_ORDERINGS, mySortOrderingsChangedRefresher);
      myFetchSpecification.removePropertyChangeListener(EOFetchSpecification.SORT_ORDERING, myTableRowRefresher);
    }
    if (myBindingContext != null) {
      myBindingContext.dispose();
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
    if (selectedObject instanceof IEOAttributePath) {
      path = ((IEOAttributePath) selectedObject).toKeyPath();
    }
    else if (selectedObject instanceof IEOAttribute) {
      path = ((IEOAttribute) selectedObject).getName();
    }
    else {
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
    myAddButton.setEnabled(!myModelTreeViewer.getSelection().isEmpty());
    myRemoveButton.setEnabled(!mySortOrderingsTableViewer.getSelection().isEmpty());
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

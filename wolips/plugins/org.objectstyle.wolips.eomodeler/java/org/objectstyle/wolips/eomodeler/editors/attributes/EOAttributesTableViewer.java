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
package org.objectstyle.wolips.eomodeler.editors.attributes;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.actions.NewAttributeAction;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.utils.EMTableViewer;
import org.objectstyle.wolips.eomodeler.utils.EMTextCellEditor;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.utils.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.utils.StayEditingCellEditorListener;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowDoubleClickHandler;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;
import org.objectstyle.wolips.eomodeler.utils.TriStateCellEditor;

public class EOAttributesTableViewer extends Composite implements ISelectionProvider {
	private EMTableViewer myAttributesTableViewer;

	private EOEntity myEntity;

	private TableRefreshPropertyListener myAttributesChangedRefresher;

	private TableRefreshPropertyListener myParentChangedRefresher;

	private TableRowRefreshPropertyListener myTableRowRefresher;

	public EOAttributesTableViewer(Composite _parent, int _style) {
		super(_parent, _style);

		setLayout(new GridLayout(1, true));
		myAttributesTableViewer = TableUtils.createTableViewer(this, SWT.MULTI | SWT.FULL_SELECTION, "EOAttribute", EOAttribute.class.getName(), new EOAttributesContentProvider(), null, new EOAttributesViewerSorter(EOAttribute.class.getName()));
		myAttributesTableViewer.setLabelProvider(new EOAttributesLabelProvider(myAttributesTableViewer, EOAttribute.class.getName()));
		new DoubleClickNewAttributeHandler(myAttributesTableViewer).attach();
		myAttributesChangedRefresher = new TableRefreshPropertyListener(myAttributesTableViewer);
		myParentChangedRefresher = new TableRefreshPropertyListener(myAttributesTableViewer);
		myTableRowRefresher = new TableRowRefreshPropertyListener(myAttributesTableViewer);
		Table attributesTable = myAttributesTableViewer.getTable();
		attributesTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn primaryKeyColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), EOAttribute.PRIMARY_KEY);
		if (primaryKeyColumn != null) {
			primaryKeyColumn.setText("");
			// primaryKeyColumn.setAlignment(SWT.CENTER);
			primaryKeyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.PRIMARY_KEY_ICON));
		}

		TableColumn lockingColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), EOAttribute.USED_FOR_LOCKING);
		if (lockingColumn != null) {
			lockingColumn.setText("");
			// lockingColumn.setAlignment(SWT.CENTER);
			lockingColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.LOCKING_ICON));
		}

		TableColumn classPropertyColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), EOAttribute.CLASS_PROPERTY);
		if (classPropertyColumn != null) {
			classPropertyColumn.setText("");
			// classPropertyColumn.setAlignment(SWT.CENTER);
			classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));
		}

		TableColumn allowNullColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), AbstractEOArgument.ALLOWS_NULL);
		if (allowNullColumn != null) {
			allowNullColumn.setText("0");
			// allowNullColumn.setAlignment(SWT.CENTER);
			// classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(EOAttribute.CLASS_PROPERTY));
		}

		TableUtils.sort(myAttributesTableViewer, AbstractEOArgument.NAME);

		CellEditor[] cellEditors = new CellEditor[TableUtils.getColumnsForTableNamed(EOAttribute.class.getName()).length];
		TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.PROTOTYPE, new KeyComboBoxCellEditor(attributesTable, new String[0], SWT.READ_ONLY), cellEditors);
		TableUtils.setCellEditor(EOAttribute.class.getName(), AbstractEOArgument.NAME, new EMTextCellEditor(attributesTable), cellEditors);
		TableUtils.setCellEditor(EOAttribute.class.getName(), AbstractEOArgument.COLUMN_NAME, new EMTextCellEditor(attributesTable), cellEditors);
		updateCellEditors(cellEditors);
		myAttributesTableViewer.setCellModifier(new EOAttributesCellModifier(myAttributesTableViewer, cellEditors));
		myAttributesTableViewer.setCellEditors(cellEditors);
		
		new StayEditingCellEditorListener(myAttributesTableViewer, EOAttribute.class.getName(), EOAttribute.PROTOTYPE);
		new StayEditingCellEditorListener(myAttributesTableViewer, EOAttribute.class.getName(), AbstractEOArgument.NAME);
		new StayEditingCellEditorListener(myAttributesTableViewer, EOAttribute.class.getName(), AbstractEOArgument.COLUMN_NAME);
	}

	public void setEntity(EOEntity _entity) {
		if (myEntity != null) {
			myAttributesChangedRefresher.stop();
			myEntity.removePropertyChangeListener(EOEntity.PARENT, myParentChangedRefresher);
			myEntity.removePropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesChangedRefresher);
			myEntity.removePropertyChangeListener(EOEntity.ATTRIBUTE, myTableRowRefresher);
		}
		myEntity = _entity;
		if (myEntity != null) {
			myAttributesTableViewer.setInput(myEntity);
			updateCellEditors(myAttributesTableViewer.getCellEditors());
			TableUtils.packTableColumns(myAttributesTableViewer);
			TableColumn prototypeColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), EOAttribute.PROTOTYPE);
			if (prototypeColumn != null) {
				prototypeColumn.setWidth(Math.max(prototypeColumn.getWidth(), 100));
			}
			TableColumn nameColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), AbstractEOArgument.NAME);
			if (nameColumn != null) {
				nameColumn.setWidth(Math.max(nameColumn.getWidth(), 100));
			}
			TableColumn allowsNullColumn = TableUtils.getColumn(myAttributesTableViewer, EOAttribute.class.getName(), AbstractEOArgument.ALLOWS_NULL);
			if (allowsNullColumn != null) {
				allowsNullColumn.setWidth(Math.max(allowsNullColumn.getWidth(), 30));
			}
			myEntity.addPropertyChangeListener(EOEntity.PARENT, myParentChangedRefresher);
			myAttributesChangedRefresher.start();
			myEntity.addPropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesChangedRefresher);
			myEntity.addPropertyChangeListener(EOEntity.ATTRIBUTE, myTableRowRefresher);
		}
	}

	public EOEntity getEntity() {
		return myEntity;
	}

	public EMTableViewer getTableViewer() {
		return myAttributesTableViewer;
	}

	protected void updateCellEditors(CellEditor[] _cellEditors) {
		Table attributesTable = myAttributesTableViewer.getTable();
		if (myEntity != null && myEntity.isPrototype()) {
			TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.PRIMARY_KEY, new TriStateCellEditor(attributesTable), _cellEditors);
			TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.CLASS_PROPERTY, new TriStateCellEditor(attributesTable), _cellEditors);
			TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.USED_FOR_LOCKING, new TriStateCellEditor(attributesTable), _cellEditors);
			TableUtils.setCellEditor(EOAttribute.class.getName(), AbstractEOArgument.ALLOWS_NULL, new TriStateCellEditor(attributesTable), _cellEditors);
		} else {
			TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.PRIMARY_KEY, new CheckboxCellEditor(attributesTable), _cellEditors);
			TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.CLASS_PROPERTY, new CheckboxCellEditor(attributesTable), _cellEditors);
			TableUtils.setCellEditor(EOAttribute.class.getName(), EOAttribute.USED_FOR_LOCKING, new CheckboxCellEditor(attributesTable), _cellEditors);
			TableUtils.setCellEditor(EOAttribute.class.getName(), AbstractEOArgument.ALLOWS_NULL, new CheckboxCellEditor(attributesTable), _cellEditors);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		myAttributesTableViewer.addSelectionChangedListener(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		myAttributesTableViewer.removeSelectionChangedListener(_listener);
	}

	public ISelection getSelection() {
		return myAttributesTableViewer.getSelection();
	}

	public void setSelection(ISelection _selection) {
		myAttributesTableViewer.setSelection(_selection);
	}

	@Override
	public void dispose() {
		myAttributesChangedRefresher.stop();
		super.dispose();
	}

	protected class DoubleClickNewAttributeHandler extends TableRowDoubleClickHandler {
		public DoubleClickNewAttributeHandler(TableViewer _viewer) {
			super(_viewer);
		}

		protected void emptyDoubleSelectionOccurred() {
			try {
				NewAttributeAction.createAttribute(EOAttributesTableViewer.this.getEntity());
			} catch (Throwable e) {
				ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
			}
		}

		protected void doubleSelectionOccurred(ISelection _selection) {
			// DO NOTHING
		}
	}

	// protected class AttributesChangeRefresher extends
	// TableRefreshPropertyListener implements Runnable {
	// public AttributesChangeRefresher(TableViewer _tableViewer) {
	// super(_tableViewer);
	// }
	//		
	// public void propertyChange(PropertyChangeEvent _event) {
	// super.propertyChange(_event);
	// Set<EOAttribute> oldValues = (Set<EOAttribute>) _event.getOldValue();
	// Set<EOAttribute> newValues = (Set<EOAttribute>) _event.getNewValue();
	// if (newValues != null && oldValues != null) {
	// if (newValues.size() > oldValues.size()) {
	// List<EOAttribute> newList = new LinkedList<EOAttribute>(newValues);
	// newList.removeAll(oldValues);
	// synchronized (_addedAttributes) {
	// System.out.println("AttributesChangeRefresher.propertyChange: changed");
	// _addedAttributes.addAll(newList);
	// }
	// _throttle.ping();
	// }
	// TableUtils.packTableColumns(EOAttributesTableViewer.this.getTableViewer());
	// }
	// }
	// }
}

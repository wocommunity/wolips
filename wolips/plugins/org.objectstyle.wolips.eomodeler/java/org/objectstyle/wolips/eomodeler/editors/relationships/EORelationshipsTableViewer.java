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
package org.objectstyle.wolips.eomodeler.editors.relationships;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.TableRowDoubleClickHandler;
import org.objectstyle.wolips.baseforuiplugins.utils.WOTextCellEditor;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.utils.StayEditingCellEditorListener;
import org.objectstyle.wolips.eomodeler.utils.TableRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableRowRefreshPropertyListener;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class EORelationshipsTableViewer extends Composite implements ISelectionProvider {
	private TableViewer myRelationshipsTableViewer;

	private EOEntity myEntity;

	private TableRefreshPropertyListener myRelationshipsChangedRefresher;

	private TableRefreshPropertyListener myParentChangedRefresher;

	private TableRowRefreshPropertyListener myTableRowRefresher;

	private List<ISelectionChangedListener> mySelectionListeners;

	public EORelationshipsTableViewer(Composite _parent, int _style) {
		super(_parent, _style);
		setLayout(new FillLayout());
		mySelectionListeners = new LinkedList<ISelectionChangedListener>();
		myRelationshipsTableViewer = TableUtils.createTableViewer(this, SWT.MULTI | SWT.FULL_SELECTION, "EORelationship", EORelationship.class.getName(), new EORelationshipsContentProvider(), null, new EORelationshipsViewerSorter(EORelationship.class.getName()));
		myRelationshipsTableViewer.setLabelProvider(new EORelationshipsLabelProvider(myRelationshipsTableViewer, EORelationship.class.getName()));
		new DoubleClickNewRelationshipHandler(myRelationshipsTableViewer).attach();
		myRelationshipsChangedRefresher = new TableRefreshPropertyListener("RelationshipsChanged", myRelationshipsTableViewer);
		myParentChangedRefresher = new TableRefreshPropertyListener("EntityParentChanged", myRelationshipsTableViewer);
		myTableRowRefresher = new TableRowRefreshPropertyListener(myRelationshipsTableViewer);

		Table relationshipsTable = myRelationshipsTableViewer.getTable();
		relationshipsTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn toManyColumn = TableUtils.getColumn(myRelationshipsTableViewer, EORelationship.class.getName(), EORelationship.TO_MANY);
		if (toManyColumn != null) {
			toManyColumn.setText("");
		}

		TableColumn classPropertyColumn = TableUtils.getColumn(myRelationshipsTableViewer, EORelationship.class.getName(), EORelationship.CLASS_PROPERTY);
		if (classPropertyColumn != null) {
			classPropertyColumn.setText("");
			classPropertyColumn.setImage(Activator.getDefault().getImageRegistry().get(Activator.CLASS_PROPERTY_ICON));
		}

		TableUtils.sort(myRelationshipsTableViewer, EORelationship.NAME);

		CellEditor[] cellEditors = new CellEditor[TableUtils.getColumnsForTableNamed(EORelationship.class.getName()).length];
		TableUtils.setCellEditor(EORelationship.class.getName(), EORelationship.TO_MANY, new CheckboxCellEditor(), cellEditors);
		TableUtils.setCellEditor(EORelationship.class.getName(), EORelationship.CLASS_PROPERTY, new CheckboxCellEditor(), cellEditors);
		TableUtils.setCellEditor(EORelationship.class.getName(), EORelationship.NAME, new WOTextCellEditor(relationshipsTable), cellEditors);
		myRelationshipsTableViewer.setCellModifier(new EORelationshipsCellModifier(myRelationshipsTableViewer));
		myRelationshipsTableViewer.setCellEditors(cellEditors);

		new StayEditingCellEditorListener(myRelationshipsTableViewer, EORelationship.class.getName(), EORelationship.NAME);
	}

	public void setEntity(EOEntity _entity) {
		if (myEntity != null) {
			myRelationshipsChangedRefresher.stop();
			myEntity.removePropertyChangeListener(EOEntity.PARENT, myParentChangedRefresher);
			myEntity.removePropertyChangeListener(EOEntity.RELATIONSHIPS, myRelationshipsChangedRefresher);
			myEntity.removePropertyChangeListener(EOEntity.RELATIONSHIP, myTableRowRefresher);
		}
		myEntity = _entity;
		if (myEntity != null) {
			myRelationshipsTableViewer.setInput(myEntity);
			TableUtils.packTableColumns(myRelationshipsTableViewer);
			TableColumn nameColumn = TableUtils.getColumn(myRelationshipsTableViewer, EORelationship.class.getName(), EORelationship.NAME);
			if (nameColumn != null) {
				nameColumn.setWidth(Math.max(nameColumn.getWidth(), 100));
			}
			myEntity.addPropertyChangeListener(EOEntity.PARENT, myParentChangedRefresher);
			myRelationshipsChangedRefresher.start();
			myEntity.addPropertyChangeListener(EOEntity.RELATIONSHIPS, myRelationshipsChangedRefresher);
			myEntity.addPropertyChangeListener(EOEntity.RELATIONSHIP, myTableRowRefresher);
		}
	}

	@Override
	public void dispose() {
		myRelationshipsChangedRefresher.stop();
		super.dispose();
	}

	public EOEntity getEntity() {
		return myEntity;
	}

	public TableViewer getTableViewer() {
		return myRelationshipsTableViewer;
	}

	public void addSelectionChangedListener(ISelectionChangedListener _listener) {
		myRelationshipsTableViewer.addSelectionChangedListener(_listener);
		mySelectionListeners.add(_listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
		myRelationshipsTableViewer.removeSelectionChangedListener(_listener);
		mySelectionListeners.remove(_listener);
	}

	public ISelection getSelection() {
		return myRelationshipsTableViewer.getSelection();
	}

	public void setSelection(ISelection _selection) {
		myRelationshipsTableViewer.setSelection(_selection);
	}

	protected List getSelectionListeners() {
		return mySelectionListeners;
	}

	protected TableViewer getRelationshipsTableViewer() {
		return myRelationshipsTableViewer;
	}

	protected class DoubleClickNewRelationshipHandler extends TableRowDoubleClickHandler {
		public DoubleClickNewRelationshipHandler(TableViewer _viewer) {
			super(_viewer);
		}

		protected void emptyDoubleSelectionOccurred() {
			try {
				EORelationshipsTableViewer.this.getEntity().addBlankRelationship(Messages.getString("EORelationship.newName"));
			} catch (Throwable e) {
				ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), e);
			}
		}

		protected void doubleSelectionOccurred(ISelection _selection) {
			EORelationship relationship = (EORelationship) ((IStructuredSelection) _selection).getFirstElement();
			EOModelObject jumpToModelObject = null;
			EORelationship inverseRelationship = relationship.getInverseRelationship();
			if (inverseRelationship != null) {
				jumpToModelObject = inverseRelationship;
			} else {
				EOEntity destination = relationship.getDestination();
				jumpToModelObject = destination;
			}
			if (jumpToModelObject != null) {
				Iterator selectionListenersIter = EORelationshipsTableViewer.this.getSelectionListeners().iterator();
				while (selectionListenersIter.hasNext()) {
					ISelectionChangedListener selectionChangedListener = (ISelectionChangedListener) selectionListenersIter.next();
					selectionChangedListener.selectionChanged(new SelectionChangedEvent(EORelationshipsTableViewer.this.getRelationshipsTableViewer(), new StructuredSelection(jumpToModelObject)));
				}
			}
		}
	}

	// protected class RelationshipsChangeRefresher extends
	// TableRefreshPropertyListener {
	// public RelationshipsChangeRefresher(TableViewer _tableViewer) {
	// super(_tableViewer);
	// }
	//
	// public void propertyChange(PropertyChangeEvent _event) {
	// super.propertyChange(_event);
	// Set oldValues = (Set) _event.getOldValue();
	// Set newValues = (Set) _event.getNewValue();
	// if (newValues != null && oldValues != null) {
	// if (newValues.size() > oldValues.size()) {
	// List newList = new LinkedList(newValues);
	// newList.removeAll(oldValues);
	// EORelationshipsTableViewer.this.setSelection(new
	// StructuredSelection(newList));
	// }
	// TableUtils.packTableColumns(EORelationshipsTableViewer.this.getTableViewer());
	// }
	// }
	// }
}

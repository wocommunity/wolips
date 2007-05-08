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
package org.objectstyle.wolips.eomodeler.editors.relationship;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.utils.AddRemoveButtonGroup;
import org.objectstyle.wolips.eomodeler.utils.KeyComboBoxCellEditor;
import org.objectstyle.wolips.eomodeler.utils.TablePropertyViewerSorter;
import org.objectstyle.wolips.eomodeler.utils.TableRowDoubleClickHandler;
import org.objectstyle.wolips.eomodeler.utils.TableUtils;

public class JoinsTableEditor extends Composite {
	private EORelationship myRelationship;

	private TableViewer myJoinsTableViewer;

	private AddRemoveButtonGroup myAddRemoveButtonGroup;

	private AttributesListener myAttributesListener;

	private RelationshipListener myRelationshipListener;

	private ButtonUpdateListener myButtonUpdateListener;

	public JoinsTableEditor(Composite _parent, int _style) {
		super(_parent, _style);
		setBackground(_parent.getBackground());

		myRelationshipListener = new RelationshipListener();
		myButtonUpdateListener = new ButtonUpdateListener();

		GridLayout layout = new GridLayout();
		setLayout(layout);

		myJoinsTableViewer = TableUtils.createTableViewer(this, SWT.BORDER | SWT.FLAT | SWT.MULTI | SWT.FULL_SELECTION, "EOJoin", EOJoinsConstants.COLUMNS, new EOJoinsContentProvider(), new EOJoinsLabelProvider(EOJoinsConstants.COLUMNS), new TablePropertyViewerSorter(EOJoinsConstants.COLUMNS));

		CellEditor[] cellEditors = new CellEditor[EOJoinsConstants.COLUMNS.length];
		cellEditors[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.SOURCE_ATTRIBUTE_NAME)] = new KeyComboBoxCellEditor(myJoinsTableViewer.getTable(), new String[0], SWT.READ_ONLY);
		cellEditors[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.DESTINATION_ATTRIBUTE_NAME)] = new KeyComboBoxCellEditor(myJoinsTableViewer.getTable(), new String[0], SWT.READ_ONLY);
		myJoinsTableViewer.setCellModifier(new EOJoinsCellModifier(myJoinsTableViewer));
		myJoinsTableViewer.setCellEditors(cellEditors);

		GridData joinsTableLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		joinsTableLayoutData.heightHint = 100;
		myJoinsTableViewer.getTable().setLayoutData(joinsTableLayoutData);
		myJoinsTableViewer.addSelectionChangedListener(myButtonUpdateListener);
		new DoubleClickNewJoinHandler(myJoinsTableViewer).attach();

		myAddRemoveButtonGroup = new AddRemoveButtonGroup(this, new AddJoinHandler(), new RemoveJoinsHandler());
	}

	public void setRelationship(EORelationship _relationship) {
		if (!ComparisonUtils.equals(_relationship, myRelationship)) {
			disposeBindings();

			myRelationship = _relationship;
			if (myRelationship != null) {
				myRelationship.addPropertyChangeListener(EORelationship.DESTINATION, myRelationshipListener);
				myRelationship.addPropertyChangeListener(EORelationship.JOINS, myRelationshipListener);

				myJoinsTableViewer.setInput(myRelationship);
				TableUtils.sort(myJoinsTableViewer, EOJoin.SOURCE_ATTRIBUTE);

				boolean enabled = !myRelationship.isFlattened();
				myJoinsTableViewer.getTable().setEnabled(enabled);
				myAddRemoveButtonGroup.setAddEnabled(enabled);
				myAddRemoveButtonGroup.setRemoveEnabled(enabled);

				updateJoins();
				updateButtons();
			}
		}
	}

	protected void updateButtons() {
		boolean buttonsEnabled = isEnabled() && myRelationship.getDestination() != null;
		boolean removeEnabled = buttonsEnabled && !myJoinsTableViewer.getSelection().isEmpty() && !myRelationship.isFlattened();
		boolean addEnabled = buttonsEnabled;
		myAddRemoveButtonGroup.setRemoveEnabled(removeEnabled);
		myAddRemoveButtonGroup.setAddEnabled(addEnabled);
	}

	protected void updateJoins() {
		if (myJoinsTableViewer != null) {
			myJoinsTableViewer.setInput(myRelationship);
			KeyComboBoxCellEditor sourceCellEditor = (KeyComboBoxCellEditor) myJoinsTableViewer.getCellEditors()[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.SOURCE_ATTRIBUTE_NAME)];
			EOEntity source = myRelationship.getEntity();
			if (source != null) {
				myJoinsTableViewer.getTable().getColumn(TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.SOURCE_ATTRIBUTE_NAME)).setText(source.getName());
				sourceCellEditor.setItems(source.getAttributeNames());
			}
			KeyComboBoxCellEditor destinationCellEditor = (KeyComboBoxCellEditor) myJoinsTableViewer.getCellEditors()[TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.DESTINATION_ATTRIBUTE_NAME)];
			EOEntity destination = myRelationship.getDestination();
			if (destination != null) {
				myJoinsTableViewer.getTable().getColumn(TableUtils.getColumnNumber(EOJoinsConstants.COLUMNS, EOJoin.DESTINATION_ATTRIBUTE_NAME)).setText(destination.getName());
				destinationCellEditor.setItems(destination.getAttributeNames());
			}
			TableUtils.packTableColumns(myJoinsTableViewer);
		}
	}

	protected void addSelectedJoin() {
		EOJoin newJoin = new EOJoin();
		myRelationship.addJoin(newJoin);
		myJoinsTableViewer.setSelection(new StructuredSelection(newJoin));
	}

	protected void removeSelectedJoins() {
		Object[] selectedJoins = ((IStructuredSelection) myJoinsTableViewer.getSelection()).toArray();
		if (selectedJoins.length > 0) {
			boolean confirmed = MessageDialog.openConfirm(getShell(), Messages.getString("EORelationshipBasicEditorSection.removeJoinsTitle"), Messages.getString("EORelationshipBasicEditorSection.removeJoinsMessage"));
			if (confirmed) {
				for (int joinNum = 0; joinNum < selectedJoins.length; joinNum++) {
					EOJoin join = (EOJoin) selectedJoins[joinNum];
					myRelationship.removeJoin(join);
				}
			}
		}
	}

	public void disposeBindings() {
		if (myRelationship != null) {
			myRelationship.removePropertyChangeListener(EORelationship.DESTINATION, myRelationshipListener);
			myRelationship.removePropertyChangeListener(EORelationship.JOINS, myRelationshipListener);
			EOEntity destination = myRelationship.getDestination();
			if (destination != null) {
				destination.removePropertyChangeListener(EOEntity.ATTRIBUTE, myAttributesListener);
				destination.removePropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesListener);
			}
		}
	}

	public void setEnabled(boolean _enabled) {
		super.setEnabled(_enabled);
		myJoinsTableViewer.getTable().setEnabled(_enabled);
		updateButtons();
	}

	public void dispose() {
		super.dispose();
		disposeBindings();
	}

	protected void destinationChanged(EOEntity _oldDestination, EOEntity _newDestination) {
		if (_oldDestination != null) {
			_oldDestination.removePropertyChangeListener(EOEntity.ATTRIBUTE, myAttributesListener);
			_oldDestination.removePropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesListener);
		}
		updateJoins();
		updateButtons();
		if (_newDestination != null) {
			_newDestination.addPropertyChangeListener(EOEntity.ATTRIBUTE, myAttributesListener);
			_newDestination.addPropertyChangeListener(EOEntity.ATTRIBUTES, myAttributesListener);
		}
	}

	protected class DoubleClickNewJoinHandler extends TableRowDoubleClickHandler {
		public DoubleClickNewJoinHandler(TableViewer _viewer) {
			super(_viewer);
		}

		protected void emptyDoubleSelectionOccurred() {
			JoinsTableEditor.this.addSelectedJoin();
		}

		protected void doubleSelectionOccurred(ISelection _selection) {
			// DO NOTHING
		}
	}

	protected class AttributesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (propertyName.equals(EOEntity.ATTRIBUTE)) {
				JoinsTableEditor.this.updateJoins();
			} else if (propertyName.equals(EOEntity.ATTRIBUTES)) {
				JoinsTableEditor.this.updateJoins();
			}
		}
	}

	protected class RelationshipListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			String propertyName = _event.getPropertyName();
			if (propertyName.equals(EORelationship.DESTINATION)) {
				EOEntity oldDestination = (EOEntity) _event.getOldValue();
				EOEntity newDestination = (EOEntity) _event.getNewValue();
				JoinsTableEditor.this.destinationChanged(oldDestination, newDestination);
			} else if (propertyName.equals(EORelationship.JOINS)) {
				JoinsTableEditor.this.updateJoins();
			}
		}
	}

	protected class ButtonUpdateListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent _event) {
			JoinsTableEditor.this.updateButtons();
		}
	}

	protected class AddJoinHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			JoinsTableEditor.this.addSelectedJoin();
		}
	}

	protected class RemoveJoinsHandler implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent _e) {
			widgetSelected(_e);
		}

		public void widgetSelected(SelectionEvent _e) {
			JoinsTableEditor.this.removeSelectedJoins();
		}
	}
}

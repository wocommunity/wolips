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
package org.objectstyle.wolips.eomodeler.editors.entity;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.InheritanceType;
import org.objectstyle.wolips.eomodeler.editors.relationship.EOModelLabelProvider;
import org.objectstyle.wolips.eomodeler.editors.relationship.EOModelListContentProvider;

public class SubclassEntityDialog extends Dialog {
	private EOModel _sourceModel;
	
	private EOModel _destinationModel;

	private String _entityName;

	private EOEntity _parentEntity;

	private InheritanceType _inheritanceType;

	private String _restrictingQualifier;

	private Text _entityNameText;

	private ComboViewer _destinationModelViewer;

	private ComboViewer _parentEntityViewer;

	private ComboViewer _inheritanceTypeViewer;

	private Text _restrictingQualifierText;

	public SubclassEntityDialog(Shell shell, EOModel sourceModel, EOEntity parentEntity, EOModel destinationModel) {
		super(shell);
		_sourceModel = sourceModel;
		_parentEntity = parentEntity;
		_destinationModel = destinationModel;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("SubclassEntityDialog.title"));
	}

	public String getEntityName() {
		return _entityName;
	}

	public EOModel getDestinationModel() {
		return _destinationModel;
	}
	
	public EOEntity getParentEntity() {
		return _parentEntity;
	}

	public InheritanceType getInheritanceType() {
		return _inheritanceType;
	}

	public String getRestrictingQualifier() {
		return _restrictingQualifier;
	}

	protected void _updateSubclassFromUI() {
		if (_entityNameText != null) {
			_entityName = _entityNameText.getText();
		}
		if (_destinationModelViewer != null) {
			_destinationModel = (EOModel) ((IStructuredSelection) _destinationModelViewer.getSelection()).getFirstElement();
		}
		if (_parentEntityViewer != null) {
			_parentEntity = (EOEntity) ((IStructuredSelection) _parentEntityViewer.getSelection()).getFirstElement();
		}
		if (_inheritanceTypeViewer != null) {
			_inheritanceType = (InheritanceType) ((IStructuredSelection) _inheritanceTypeViewer.getSelection()).getFirstElement();
		}
		if (_restrictingQualifierText != null) {
			if (_parentEntity != null && (_inheritanceType == InheritanceType.SINGLE_TABLE || _inheritanceType == InheritanceType.VERTICAL)) {
				_restrictingQualifierText.setEnabled(true);
				_restrictingQualifier = _restrictingQualifierText.getText();
			} else {
				_restrictingQualifierText.setEnabled(false);
				_restrictingQualifier = null;
			}
		}
	}

	protected void _setEntityName(String entityName) {
		_entityName = entityName;
	}

	protected void _setParentEntity(EOEntity parentEntity) {
		_parentEntity = parentEntity;
	}

	protected Control createDialogArea(Composite parent) {
		Composite subclassDialogArea = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 15;
		gridLayout.marginLeft = 15;
		gridLayout.marginRight = 15;
		gridLayout.horizontalSpacing = 15;
		subclassDialogArea.setLayout(gridLayout);

		Label parentEntityLabel = new Label(subclassDialogArea, SWT.NONE);
		parentEntityLabel.setText(Messages.getString("SubclassEntityDialog.parentEntityLabel"));
		_parentEntityViewer = new ComboViewer(subclassDialogArea);
		_parentEntityViewer.setContentProvider(new EOEntityListContentProvider(false, false, false));
		_parentEntityViewer.setLabelProvider(new EOEntityLabelProvider());
		//_parentEntityViewer.setSorter(new ViewerSorter());
		_parentEntityViewer.setComparator(new ViewerComparator());
		_parentEntityViewer.setInput(_sourceModel);
		_parentEntityViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_parentEntityViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent _event) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		if (_parentEntity != null) {
			_parentEntityViewer.setSelection(new StructuredSelection(_parentEntity));
		}

		Label inheritanceTypeLabel = new Label(subclassDialogArea, SWT.NONE);
		inheritanceTypeLabel.setText(Messages.getString("SubclassEntityDialog.inheritanceTypeLabel"));
		_inheritanceTypeViewer = new ComboViewer(subclassDialogArea);
		_inheritanceTypeViewer.setLabelProvider(new InheritanceTypeLabelProvider());
		_inheritanceTypeViewer.setContentProvider(new InheritanceTypeContentProvider());
		//_inheritanceTypeViewer.setSorter(new ViewerSorter());
		_inheritanceTypeViewer.setComparator(new ViewerComparator());
		_inheritanceTypeViewer.setInput(InheritanceType.INHERITANCE_TYPES);
		_inheritanceTypeViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_inheritanceTypeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent _event) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});

		_inheritanceTypeViewer.setSelection(new StructuredSelection(InheritanceType.SINGLE_TABLE));
		Label destinationModelLabel = new Label(subclassDialogArea, SWT.NONE);
		destinationModelLabel.setText(Messages.getString("SubclassEntityDialog.destinationModelLabel"));
		_destinationModelViewer = new ComboViewer(subclassDialogArea);
		_destinationModelViewer.setContentProvider(new EOModelListContentProvider());
		_destinationModelViewer.setLabelProvider(new EOModelLabelProvider());
		//_destinationModelViewer.setSorter(new ViewerSorter());
		_destinationModelViewer.setComparator(new ViewerComparator());
		_destinationModelViewer.setInput(_destinationModel);
		_destinationModelViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_destinationModelViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent _event) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		if (_destinationModel != null) {
			_destinationModelViewer.setSelection(new StructuredSelection(_destinationModel));
		}
		
		Label subclassNameLabel = new Label(subclassDialogArea, SWT.NONE);
		subclassNameLabel.setText(Messages.getString("SubclassEntityDialog.entityNameLabel"));
		_entityNameText = new Text(subclassDialogArea, SWT.BORDER);
		_entityNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_entityNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent _e) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		_entityNameText.setText(_destinationModel.findUnusedEntityName(_parentEntity.getName()));

		Label restrictingQualifierLabel = new Label(subclassDialogArea, SWT.NONE);
		restrictingQualifierLabel.setText(Messages.getString("SubclassEntityDialog.restrictingQualifierLabel"));
		_restrictingQualifierText = new Text(subclassDialogArea, SWT.BORDER);
		_restrictingQualifierText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent _e) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		_restrictingQualifierText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_updateSubclassFromUI();

		return subclassDialogArea;
	}
}
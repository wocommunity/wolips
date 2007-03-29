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
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.InheritanceType;

public class SubclassEntityDialog extends Dialog {
	private EOModel myModel;

	private String myEntityName;

	private EOEntity myParentEntity;

	private InheritanceType myInheritanceType;

	private String myRestrictingQualifier;

	private Text myEntityNameText;

	private ComboViewer myParentEntityViewer;

	private ComboViewer myInheritanceTypeViewer;

	private Text myRestrictingQualifierText;

	public SubclassEntityDialog(Shell _shell, EOModel _model, EOEntity _parentEntity) {
		super(_shell);
		myModel = _model;
		myParentEntity = _parentEntity;
	}

	protected void configureShell(Shell _newShell) {
		super.configureShell(_newShell);
		_newShell.setText(Messages.getString("SubclassEntityDialog.title"));
	}

	public String getEntityName() {
		return myEntityName;
	}

	public EOEntity getParentEntity() {
		return myParentEntity;
	}

	public InheritanceType getInheritanceType() {
		return myInheritanceType;
	}

	public String getRestrictingQualifier() {
		return myRestrictingQualifier;
	}

	protected void _updateSubclassFromUI() {
		if (myEntityNameText != null) {
			myEntityName = myEntityNameText.getText();
		}
		if (myParentEntityViewer != null) {
			myParentEntity = (EOEntity) ((IStructuredSelection) myParentEntityViewer.getSelection()).getFirstElement();
		}
		if (myInheritanceTypeViewer != null) {
			myInheritanceType = (InheritanceType) ((IStructuredSelection) myInheritanceTypeViewer.getSelection()).getFirstElement();
		}
		if (myRestrictingQualifierText != null) {
			if (myParentEntity != null && myInheritanceType == InheritanceType.SINGLE_TABLE) {
				myRestrictingQualifierText.setEnabled(true);
				myRestrictingQualifier = myRestrictingQualifierText.getText();
			} else {
				myRestrictingQualifierText.setEnabled(false);
				myRestrictingQualifier = null;
			}
		}
	}

	protected void _setEntityName(String _entityName) {
		myEntityName = _entityName;
	}

	protected void _setParentEntity(EOEntity _parentEntity) {
		myParentEntity = _parentEntity;
	}

	protected Control createDialogArea(Composite _parent) {
		Composite subclassDialogArea = new Composite(_parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 15;
		gridLayout.marginLeft = 15;
		gridLayout.marginRight = 15;
		gridLayout.horizontalSpacing = 15;
		subclassDialogArea.setLayout(gridLayout);

		Label subclassNameLabel = new Label(subclassDialogArea, SWT.NONE);
		subclassNameLabel.setText(Messages.getString("SubclassEntityDialog.entityNameLabel"));
		myEntityNameText = new Text(subclassDialogArea, SWT.BORDER);
		myEntityNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		myEntityNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent _e) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		myEntityNameText.setText(myModel.findUnusedEntityName(myParentEntity.getName()));

		Label parentEntityLabel = new Label(subclassDialogArea, SWT.NONE);
		parentEntityLabel.setText(Messages.getString("SubclassEntityDialog.parentEntityLabel"));
		myParentEntityViewer = new ComboViewer(subclassDialogArea);
		myParentEntityViewer.setContentProvider(new EOEntityListContentProvider(false, false));
		myParentEntityViewer.setLabelProvider(new EOEntityLabelProvider());
		myParentEntityViewer.setSorter(new ViewerSorter());
		myParentEntityViewer.setInput(myModel);
		myParentEntityViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		myParentEntityViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent _event) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		if (myParentEntity != null) {
			myParentEntityViewer.setSelection(new StructuredSelection(myParentEntity));
		}

		Label inheritanceTypeLabel = new Label(subclassDialogArea, SWT.NONE);
		inheritanceTypeLabel.setText(Messages.getString("SubclassEntityDialog.inheritanceTypeLabel"));
		myInheritanceTypeViewer = new ComboViewer(subclassDialogArea);
		myInheritanceTypeViewer.setLabelProvider(new InheritanceTypeLabelProvider());
		myInheritanceTypeViewer.setContentProvider(new InheritanceTypeContentProvider());
		myInheritanceTypeViewer.setSorter(new ViewerSorter());
		myInheritanceTypeViewer.setInput(InheritanceType.INHERITANCE_TYPES);
		myInheritanceTypeViewer.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		myInheritanceTypeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent _event) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		myInheritanceTypeViewer.setSelection(new StructuredSelection(InheritanceType.HORIZONTAL));

		Label restrictingQualifierLabel = new Label(subclassDialogArea, SWT.NONE);
		restrictingQualifierLabel.setText(Messages.getString("SubclassEntityDialog.restrictingQualifierLabel"));
		myRestrictingQualifierText = new Text(subclassDialogArea, SWT.BORDER);
		myRestrictingQualifierText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent _e) {
				SubclassEntityDialog.this._updateSubclassFromUI();
			}
		});
		myRestrictingQualifierText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_updateSubclassFromUI();

		return subclassDialogArea;
	}
}
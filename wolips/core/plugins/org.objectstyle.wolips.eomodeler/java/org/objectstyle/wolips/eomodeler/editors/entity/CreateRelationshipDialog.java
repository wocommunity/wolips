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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.editors.relationship.JoinsTableEditor;

public class CreateRelationshipDialog extends Dialog implements SelectionListener {
	private EOModelGroup _modelGroup;

	private EOEntity _sourceEntity;

	private EOEntity _destinationEntity;

	private EORelationship _relationship;

	private EORelationship _inverseRelationship;

	private Composite _relationshipFields;
	
	private Label _sourceLabel;

	private Label _destinationLabel;

	private JoinsTableEditor _joinsTableEditor;

	private String _originalName;

	private Text _nameText;

	private Button _toManyButton;

	private Button _createButton;

	private Button _createFKButton;

	private Text _fkNameText;

	private String _originalInverseName;

	private Text _inverseNameText;

	private Button _inverseToManyButton;

	private Button _createInverseButton;

	private Button _createInverseFKButton;

	private Text _inverseFKNameText;

	private Button _flattenButton;

	private Label _joinEntityNameLabel;

	private Text _joinEntityNameText;

	private Font _titleFont;

	private boolean _manyToMany;

	private boolean _createFK;

	private boolean _createInverseFK;

	private String _guessedJoinEntityName;

	public CreateRelationshipDialog(Shell shell, EOModelGroup modelGroup, EOEntity sourceEntity, EOEntity destinationEntity) {
		super(shell);
		_modelGroup = modelGroup;
		_sourceEntity = sourceEntity;
		_destinationEntity = destinationEntity;
	}

	public void setSourceEntity(EOEntity sourceEntity) {
		_sourceEntity = sourceEntity;
		_inverseFKNameText.setText("");
		entitiesChanged();
	}

	public EOEntity getSourceEntity() {
		return _sourceEntity;
	}

	public void setDestinationEntity(EOEntity destinationEntity) {
		_destinationEntity = destinationEntity;
		_fkNameText.setText("");
		entitiesChanged();
	}

	public EOEntity getDestinationEntity() {
		return _destinationEntity;
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("CreateRelationshipDialog.title"));
	}
	
	@Override
	protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
		Rectangle bounds = super.getConstrainedShellBounds(preferredSize);
		if (_sourceEntity == null || _destinationEntity == null) {
			bounds.y -= 75;
		}
		return bounds;
	}

	protected Control createDialogArea(Composite parent) {
		Composite relationshipDialogArea = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 15;
		gridLayout.marginLeft = 15;
		gridLayout.marginRight = 15;
		gridLayout.horizontalSpacing = 15;
		relationshipDialogArea.setLayout(gridLayout);

		boolean showEntityPickers = _sourceEntity == null || _destinationEntity == null;
		if (showEntityPickers) {
			Label hintLabel = new Label(relationshipDialogArea, SWT.NONE);
			hintLabel.setText("Select the entities that this relationship will join together.");
			GridData hintLabelData = new GridData(GridData.FILL_HORIZONTAL);
			//destinationEntityPickerData.horizontalSpan = 2;
			hintLabel.setLayoutData(hintLabelData);
			hintLabel.setFont(parent.getFont());

			// Label sourceEntityLabel = new Label(entityPickers, SWT.NONE);
			// sourceEntityLabel.setText("Entity #1");
			EntityPicker sourceEntityPicker = new EntityPicker(relationshipDialogArea, SWT.NONE);
			sourceEntityPicker.setModelGroup(_modelGroup);
			sourceEntityPicker.setEntity(_sourceEntity);
			GridData sourceEntityPickerData = new GridData(GridData.FILL_HORIZONTAL);
			//sourceEntityPickerData.horizontalSpan = 2;
			sourceEntityPickerData.verticalIndent = 15;
			sourceEntityPicker.setLayoutData(sourceEntityPickerData);
			sourceEntityPicker.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					EOEntity entity = (EOEntity) ((IStructuredSelection) event.getSelection()).getFirstElement();
					setSourceEntity(entity);
				}
			});
			
			EntityPicker destinationEntityPicker = new EntityPicker(relationshipDialogArea, SWT.NONE);
			destinationEntityPicker.setModelGroup(_modelGroup);
			destinationEntityPicker.setEntity(_destinationEntity);
			GridData destinationEntityPickerData = new GridData(GridData.FILL_HORIZONTAL);
			//destinationEntityPickerData.horizontalSpan = 2;
			destinationEntityPicker.setLayoutData(destinationEntityPickerData);
			destinationEntityPicker.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					EOEntity entity = (EOEntity) ((IStructuredSelection) event.getSelection()).getFirstElement();
					setDestinationEntity(entity);
				}
			});
		}

		_relationshipFields = new Composite(relationshipDialogArea, SWT.NONE);
		GridLayout relationshipFieldsLayout = new GridLayout(2, false);
		relationshipFieldsLayout.horizontalSpacing = 15;
		_relationshipFields.setLayout(relationshipFieldsLayout);
		_relationshipFields.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		_sourceLabel = new Label(_relationshipFields, SWT.NONE);
		Font originalFont = _sourceLabel.getFont();
		FontData[] fontData = originalFont.getFontData();
		_titleFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
		_sourceLabel.setFont(_titleFont);
		GridData sourceLabelData = new GridData(GridData.FILL_HORIZONTAL);
		sourceLabelData.horizontalSpan = 2;
		if (showEntityPickers) {
			sourceLabelData.verticalIndent = 15;
		}
		_sourceLabel.setLayoutData(sourceLabelData);

		_createButton = new Button(_relationshipFields, SWT.CHECK);
		_createButton.setSelection(true);
		_createButton.setLayoutData(new GridData());
		_createButton.addSelectionListener(this);

		_nameText = new Text(_relationshipFields, SWT.BORDER);
		GridData nameData = new GridData(GridData.FILL_HORIZONTAL);
		nameData.widthHint = 200;
		_nameText.setLayoutData(nameData);

		_toManyButton = new Button(_relationshipFields, SWT.CHECK);
		_toManyButton.addSelectionListener(this);
		_toManyButton.setText(Messages.getString("CreateRelationshipDialog.toManyLabel"));
		GridData toManyData = new GridData(GridData.FILL_HORIZONTAL);
		toManyData.horizontalSpan = 2;
		_toManyButton.setLayoutData(toManyData);

		_createFKButton = new Button(_relationshipFields, SWT.CHECK);
		_createFKButton.setSelection(true);
		_createFKButton.setLayoutData(new GridData());
		_createFKButton.addSelectionListener(this);

		_fkNameText = new Text(_relationshipFields, SWT.BORDER);
		_fkNameText.setEnabled(false);
		GridData fkNameData = new GridData(GridData.FILL_HORIZONTAL);
		fkNameData.widthHint = 200;
		_fkNameText.setLayoutData(fkNameData);

		_destinationLabel = new Label(_relationshipFields, SWT.NONE);
		_destinationLabel.setFont(_titleFont);
		GridData destinationLabelData = new GridData(GridData.FILL_HORIZONTAL);
		destinationLabelData.horizontalSpan = 2;
		destinationLabelData.verticalIndent = 15;
		_destinationLabel.setLayoutData(destinationLabelData);

		_createInverseButton = new Button(_relationshipFields, SWT.CHECK);
		_createInverseButton.setSelection(true);
		_createInverseButton.setLayoutData(new GridData());
		_createInverseButton.addSelectionListener(this);

		_inverseNameText = new Text(_relationshipFields, SWT.BORDER);
		GridData inverseNameData = new GridData(GridData.FILL_HORIZONTAL);
		inverseNameData.widthHint = 200;
		_inverseNameText.setLayoutData(inverseNameData);

		_inverseToManyButton = new Button(_relationshipFields, SWT.CHECK);
		_inverseToManyButton.addSelectionListener(this);
		_inverseToManyButton.setText(Messages.getString("CreateRelationshipDialog.inverseToManyLabel"));
		GridData inverseToManyData = new GridData(GridData.FILL_HORIZONTAL);
		inverseToManyData.horizontalSpan = 2;
		_inverseToManyButton.setLayoutData(inverseToManyData);

		_createInverseFKButton = new Button(_relationshipFields, SWT.CHECK);
		_createInverseFKButton.setSelection(false);
		_createInverseFKButton.setLayoutData(new GridData());
		_createInverseFKButton.addSelectionListener(this);

		_inverseFKNameText = new Text(_relationshipFields, SWT.BORDER);
		_inverseFKNameText.setEnabled(false);
		GridData inverseFKNameData = new GridData(GridData.FILL_HORIZONTAL);
		inverseFKNameData.widthHint = 200;
		_inverseFKNameText.setLayoutData(inverseFKNameData);

		
		
		_joinEntityNameLabel = new Label(_relationshipFields, SWT.NONE);
		_joinEntityNameLabel.setText(Messages.getString("CreateRelationshipDialog.joinEntityNameLabel"));
		GridData joinEntityNameLabelData = new GridData();
		joinEntityNameLabelData.verticalIndent = 15;
		_joinEntityNameLabel.setLayoutData(joinEntityNameLabelData);

		_joinEntityNameText = new Text(_relationshipFields, SWT.BORDER);
		GridData joinEntityNameData = new GridData(GridData.FILL_HORIZONTAL);
		joinEntityNameData.verticalIndent = 15;
		joinEntityNameData.widthHint = 200;
		_joinEntityNameText.setLayoutData(joinEntityNameData);

		_flattenButton = new Button(_relationshipFields, SWT.CHECK);
		_flattenButton.addSelectionListener(this);
		_flattenButton.setText(Messages.getString("CreateRelationshipDialog.flattenLabel"));
		_flattenButton.setSelection(true);
		GridData flattenData = new GridData(GridData.FILL_HORIZONTAL);
		flattenData.horizontalSpan = 2;
		_flattenButton.setLayoutData(flattenData);

		_joinsTableEditor = new JoinsTableEditor(_relationshipFields, SWT.BORDER);
		GridData joinsGridData = new GridData(GridData.FILL_HORIZONTAL);
		joinsGridData.horizontalSpan = 2;
		//joinsGridData.verticalIndent = 15;
		_joinsTableEditor.setLayoutData(joinsGridData);

		entitiesChanged();

		return _relationshipFields;
	}

	protected void entitiesChanged() {
		boolean relationshipFieldsVisible = _sourceEntity != null && _destinationEntity != null;
		_relationshipFields.setVisible(relationshipFieldsVisible);
		if (relationshipFieldsVisible) {
			((GridData) _relationshipFields.getLayoutData()).heightHint = -1;
		}
		else {
			((GridData) _relationshipFields.getLayoutData()).heightHint = 0;
		}
		
		if (_sourceEntity != null) {
			_sourceLabel.setText("From " + _sourceEntity.getName() + " ...");
			_createInverseButton.setText(Messages.getString("CreateRelationshipDialog.inverseNameLabel", new Object[] { _sourceEntity.getName() }));
			_createInverseFKButton.setText(Messages.getString("CreateRelationshipDialog.inverseFKNameLabel", new Object[] { _sourceEntity.getName() }));
		}

		if (_destinationEntity != null) {
			_createButton.setText(Messages.getString("CreateRelationshipDialog.nameLabel", new Object[] { _destinationEntity.getName() }));
			_createFKButton.setText(Messages.getString("CreateRelationshipDialog.fkNameLabel", new Object[] { _destinationEntity.getName() }));
			_destinationLabel.setText("From " + _destinationEntity.getName() + " ...");
			_guessedJoinEntityName = _sourceEntity.getName() + _destinationEntity.getName();
			_flattenedChanged();
		}

		if (_sourceEntity != null && _destinationEntity != null) {
			_relationship = _sourceEntity.createRelationshipTo(_destinationEntity, false);
			_joinsTableEditor.setRelationship(_relationship);
		} else {
			_relationship = null;
			_joinsTableEditor.setRelationship(null);
		}

		toManyChanged();

		// getButton(IDialogConstants.OK_ID).setEnabled(_sourceEntity != null &&
		// _destinationEntity != null);
	}

	protected void okPressed() {
		buttonBar.forceFocus();
		try {
			String name = _nameText.getText();
			String inverseName = _inverseNameText.getText();
			if (_manyToMany) {
				String joinEntityName = _joinEntityNameText.getText();
				boolean flatten = _flattenButton.getSelection();
				_sourceEntity.joinInManyToManyWith(_destinationEntity, name, inverseName, joinEntityName, flatten);
			} else {
				EOJoin newJoin = null;
				if (_createButton.getSelection() && _createFK) {
					String fkName = _fkNameText.getText();
					EOAttribute foreignKey = _sourceEntity.createForeignKeyTo(_destinationEntity, fkName, fkName, false);
					newJoin = new EOJoin();
					newJoin.setSourceAttribute(foreignKey);
					newJoin.setDestinationAttribute(_destinationEntity.getSinglePrimaryKeyAttribute());
				}
				if (_createInverseButton.getSelection() && _createInverseFK) {
					String inverseFKName = _inverseFKNameText.getText();
					EOAttribute foreignKey = _destinationEntity.createForeignKeyTo(_sourceEntity, inverseFKName, inverseFKName, false);
					newJoin = new EOJoin();
					newJoin.setSourceAttribute(_sourceEntity.getSinglePrimaryKeyAttribute());
					newJoin.setDestinationAttribute(foreignKey);
				}

				if (newJoin != null) {
					_relationship.removeAllJoins();
					_relationship.addJoin(newJoin);
				}

				if (_createButton.getSelection()) {
					_relationship.setName(name);
					_relationship.setToMany(Boolean.valueOf(_toManyButton.getSelection()));
					_relationship.setMandatoryIfNecessary();
					_sourceEntity.addRelationship(_relationship);
				}
				if (_createInverseButton.getSelection()) {
					_inverseRelationship = _relationship.createInverseRelationshipNamed(inverseName, _inverseToManyButton.getSelection());
					_inverseRelationship.setMandatoryIfNecessary();
					_inverseRelationship.getEntity().addRelationship(_inverseRelationship);
				}
			}
			super.okPressed();
		} catch (Throwable t) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
		}
	}

	public void toManyChanged() {
		String name = _nameText.getText();
		if ((_sourceEntity != null && _destinationEntity != null) && (_originalName == null || ComparisonUtils.equals(name, _originalName))) {
			String newName = _sourceEntity._findUnusedRelationshipName(_destinationEntity.getName(), _toManyButton.getSelection());
			_nameText.setText(newName);
			_originalName = newName;
		}
		if (!_toManyButton.getSelection()) {
			_inverseToManyButton.setSelection(true);
		}
		String inverseName = _inverseNameText.getText();
		if ((_sourceEntity != null && _destinationEntity != null) && (_originalInverseName == null || ComparisonUtils.equals(inverseName, _originalInverseName))) {
			String newName = _destinationEntity._findUnusedRelationshipName(_sourceEntity.getName(), _inverseToManyButton.getSelection());
			_inverseNameText.setText(newName);
			_originalInverseName = newName;
		}
		if (!_inverseToManyButton.getSelection()) {
			_toManyButton.setSelection(true);
		}
		_checkManyToMany();
	}

	protected void _checkManyToMany() {
		_nameText.setEnabled(_createButton.getSelection());
		_inverseNameText.setEnabled(_createInverseButton.getSelection());
		_manyToMany = (_createButton.getSelection() && _createInverseButton.getSelection() && _toManyButton.getSelection() && _inverseToManyButton.getSelection());
		_toManyButton.setEnabled(_createButton.getSelection());
		_inverseToManyButton.setEnabled(_createInverseButton.getSelection());
		_joinsTableEditor.setEnabled(!_manyToMany);
		_flattenButton.setEnabled(_manyToMany);
    _joinEntityNameText.setEnabled(_manyToMany);

		boolean canCreateFK = _createButton.getSelection() && !_toManyButton.getSelection();
		_createFK = canCreateFK && _createFKButton.getSelection();
		_createFKButton.setEnabled(canCreateFK);
		_fkNameText.setEnabled(_createFK);

		boolean canCreateInverseFK = _createInverseButton.getSelection() && !_inverseToManyButton.getSelection();
		_createInverseFK = canCreateInverseFK && _createInverseFKButton.getSelection();
		_createInverseFKButton.setEnabled(canCreateInverseFK);
		_inverseFKNameText.setEnabled(_createInverseFK);

		boolean joinsTableVisible = !_manyToMany && !_createFK && !_createInverseFK;
		_joinsTableEditor.setVisible(joinsTableVisible);
		if (!joinsTableVisible) {
			((GridData) _joinsTableEditor.getLayoutData()).heightHint = 0;
		} else {
			((GridData) _joinsTableEditor.getLayoutData()).heightHint = -1;
		}

		if (_sourceEntity != null && _destinationEntity != null) {
			String fkName = _fkNameText.getText();
			if (fkName == null || fkName.length() == 0) {
				String newName = _sourceEntity.findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(_destinationEntity.getName()) + "ID");
				_fkNameText.setText(newName);
			}

			String inverseFKName = _inverseFKNameText.getText();
			if (inverseFKName == null || inverseFKName.length() == 0) {
				String newName = _destinationEntity.findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(_sourceEntity.getName()) + "ID");
				_inverseFKNameText.setText(newName);
			}
		}

		_joinEntityNameLabel.setVisible(_manyToMany);
		_joinEntityNameText.setVisible(_manyToMany);
		_flattenButton.setVisible(_manyToMany);
		if (!_manyToMany) {
			((GridData) _joinEntityNameLabel.getLayoutData()).heightHint = 0;
			((GridData) _joinEntityNameText.getLayoutData()).heightHint = 0;
			((GridData) _flattenButton.getLayoutData()).heightHint = 0;
		} else {
			((GridData) _joinEntityNameLabel.getLayoutData()).heightHint = -1;
			((GridData) _joinEntityNameText.getLayoutData()).heightHint = -1;
			((GridData) _flattenButton.getLayoutData()).heightHint = -1;
		}

		if (getShell().isVisible()) {
			getShell().pack();
		}
	}
	
	protected void _flattenedChanged() {
	  if (_flattenButton.getSelection()) {
	    _joinEntityNameText.setText("EOGenericRecord");
	  }
	  else {
	    _joinEntityNameText.setText(_guessedJoinEntityName);
	  }
	}

	public void widgetDefaultSelected(SelectionEvent event) {
		widgetSelected(event);
	}

	public void widgetSelected(SelectionEvent event) {
		Object source = event.getSource();
		if (source == _toManyButton) {
			toManyChanged();
		} else if (source == _inverseToManyButton) {
			toManyChanged();
		} else if (source == _createFKButton) {
			_checkManyToMany();
		} else if (source == _createInverseFKButton) {
			_checkManyToMany();
		} else if (source == _createButton) {
			_checkManyToMany();
		} else if (source == _createInverseButton) {
			_checkManyToMany();
		} else if (source == _flattenButton) {
		  _flattenedChanged();
		}
	}

	public boolean close() {
		boolean results = super.close();
		if (_titleFont != null) {
			_titleFont.dispose();
		}
		return results;
	}
}
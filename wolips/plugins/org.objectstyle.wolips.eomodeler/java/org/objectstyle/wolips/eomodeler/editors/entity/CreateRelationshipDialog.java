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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOJoin;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;
import org.objectstyle.wolips.eomodeler.core.utils.StringUtils;
import org.objectstyle.wolips.eomodeler.editors.relationship.JoinsTableEditor;
import org.objectstyle.wolips.eomodeler.utils.ErrorUtils;

public class CreateRelationshipDialog extends Dialog implements SelectionListener {
	private EOEntity myEntity1;

	private EOEntity myEntity2;

	private EORelationship myRelationship;

	private EORelationship myInverseRelationship;

	private JoinsTableEditor myJoinsTableEditor;

	private String myOriginalName;

	private Text myNameText;

	private Button myToManyButton;

	private Button myCreateButton;

	private Button myCreateFKButton;

	private Text myFKNameText;

	private String myOriginalInverseName;

	private Text myInverseNameText;

	private Button myInverseToManyButton;

	private Button myCreateInverseButton;

	private Button myCreateInverseFKButton;

	private Text myInverseFKNameText;

	private Button myFlattenButton;

	private Text myJoinEntityNameText;

	private Font myTitleFont;

	private boolean myManyToMany;

	private boolean myCreateFK;

	private boolean myCreateInverseFK;

	public CreateRelationshipDialog(Shell _shell, EOEntity _entity1, EOEntity _entity2) {
		super(_shell);
		myEntity1 = _entity1;
		myEntity2 = _entity2;
		myRelationship = _entity1.createRelationshipTo(_entity2, false);
	}

	protected void configureShell(Shell _newShell) {
		super.configureShell(_newShell);
		_newShell.setText(Messages.getString("CreateRelationshipDialog.title"));
	}

	protected Control createDialogArea(Composite _parent) {
		Composite relationshipDialogArea = new Composite(_parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginBottom = 0;
		gridLayout.marginTop = 15;
		gridLayout.marginLeft = 15;
		gridLayout.marginRight = 15;
		gridLayout.horizontalSpacing = 15;
		relationshipDialogArea.setLayout(gridLayout);

		Label sourceLabel = new Label(relationshipDialogArea, SWT.NONE);
		Font originalFont = sourceLabel.getFont();
		FontData[] fontData = originalFont.getFontData();
		myTitleFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
		sourceLabel.setText("From " + myRelationship.getEntity().getName() + " ...");
		sourceLabel.setFont(myTitleFont);
		GridData sourceLabelData = new GridData(GridData.FILL_HORIZONTAL);
		sourceLabelData.horizontalSpan = 2;
		sourceLabel.setLayoutData(sourceLabelData);

		myCreateButton = new Button(relationshipDialogArea, SWT.CHECK);
		myCreateButton.setSelection(true);
		myCreateButton.setLayoutData(new GridData());
		myCreateButton.addSelectionListener(this);
		myCreateButton.setText(Messages.getString("CreateRelationshipDialog.nameLabel", new Object[] { myRelationship.getDestination().getName() }));

		myNameText = new Text(relationshipDialogArea, SWT.BORDER);
		GridData nameData = new GridData(GridData.FILL_HORIZONTAL);
		nameData.widthHint = 200;
		myNameText.setLayoutData(nameData);

		myToManyButton = new Button(relationshipDialogArea, SWT.CHECK);
		myToManyButton.addSelectionListener(this);
		myToManyButton.setText(Messages.getString("CreateRelationshipDialog.toManyLabel"));
		GridData toManyData = new GridData(GridData.FILL_HORIZONTAL);
		toManyData.horizontalSpan = 2;
		myToManyButton.setLayoutData(toManyData);

		myCreateFKButton = new Button(relationshipDialogArea, SWT.CHECK);
		myCreateFKButton.setSelection(false);
		myCreateFKButton.setLayoutData(new GridData());
		myCreateFKButton.addSelectionListener(this);
		myCreateFKButton.setText(Messages.getString("CreateRelationshipDialog.fkNameLabel", new Object[] { myRelationship.getDestination().getName() }));

		myFKNameText = new Text(relationshipDialogArea, SWT.BORDER);
		myFKNameText.setEnabled(false);
		GridData fkNameData = new GridData(GridData.FILL_HORIZONTAL);
		fkNameData.widthHint = 200;
		myFKNameText.setLayoutData(fkNameData);

		Label destinationLabel = new Label(relationshipDialogArea, SWT.NONE);
		destinationLabel.setText("From " + myRelationship.getDestination().getName() + " ...");
		destinationLabel.setFont(myTitleFont);
		GridData destinationLabelData = new GridData(GridData.FILL_HORIZONTAL);
		destinationLabelData.horizontalSpan = 2;
		destinationLabelData.verticalIndent = 15;
		destinationLabel.setLayoutData(destinationLabelData);

		myCreateInverseButton = new Button(relationshipDialogArea, SWT.CHECK);
		myCreateInverseButton.setSelection(true);
		myCreateInverseButton.setLayoutData(new GridData());
		myCreateInverseButton.addSelectionListener(this);
		myCreateInverseButton.setText(Messages.getString("CreateRelationshipDialog.inverseNameLabel", new Object[] { myRelationship.getEntity().getName() }));

		myInverseNameText = new Text(relationshipDialogArea, SWT.BORDER);
		GridData inverseNameData = new GridData(GridData.FILL_HORIZONTAL);
		inverseNameData.widthHint = 200;
		myInverseNameText.setLayoutData(inverseNameData);

		myInverseToManyButton = new Button(relationshipDialogArea, SWT.CHECK);
		myInverseToManyButton.addSelectionListener(this);
		myInverseToManyButton.setText(Messages.getString("CreateRelationshipDialog.inverseToManyLabel"));
		GridData inverseToManyData = new GridData(GridData.FILL_HORIZONTAL);
		inverseToManyData.horizontalSpan = 2;
		myInverseToManyButton.setLayoutData(inverseToManyData);

		myCreateInverseFKButton = new Button(relationshipDialogArea, SWT.CHECK);
		myCreateInverseFKButton.setSelection(false);
		myCreateInverseFKButton.setLayoutData(new GridData());
		myCreateInverseFKButton.addSelectionListener(this);
		myCreateInverseFKButton.setText(Messages.getString("CreateRelationshipDialog.inverseFKNameLabel", new Object[] { myRelationship.getEntity().getName() }));

		myInverseFKNameText = new Text(relationshipDialogArea, SWT.BORDER);
		myInverseFKNameText.setEnabled(false);
		GridData inverseFKNameData = new GridData(GridData.FILL_HORIZONTAL);
		inverseFKNameData.widthHint = 200;
		myInverseFKNameText.setLayoutData(inverseFKNameData);

		Label joinEntityNameLabel = new Label(relationshipDialogArea, SWT.NONE);
		joinEntityNameLabel.setText(Messages.getString("CreateRelationshipDialog.joinEntityNameLabel"));
		GridData joinEntityNameLabelData = new GridData();
		joinEntityNameLabelData.verticalIndent = 15;
		joinEntityNameLabel.setLayoutData(joinEntityNameLabelData);

		myJoinEntityNameText = new Text(relationshipDialogArea, SWT.BORDER);
		GridData joinEntityNameData = new GridData(GridData.FILL_HORIZONTAL);
		joinEntityNameData.verticalIndent = 15;
		joinEntityNameData.widthHint = 200;
		myJoinEntityNameText.setLayoutData(joinEntityNameData);
		myJoinEntityNameText.setText(myRelationship.getEntity().getName() + myRelationship.getDestination().getName());

		myFlattenButton = new Button(relationshipDialogArea, SWT.CHECK);
		myFlattenButton.addSelectionListener(this);
		myFlattenButton.setText(Messages.getString("CreateRelationshipDialog.flattenLabel"));
		myFlattenButton.setSelection(false);
		GridData flattenData = new GridData(GridData.FILL_HORIZONTAL);
		flattenData.horizontalSpan = 2;
		myFlattenButton.setLayoutData(flattenData);

		myJoinsTableEditor = new JoinsTableEditor(relationshipDialogArea, SWT.BORDER);
		GridData joinsGridData = new GridData(GridData.FILL_HORIZONTAL);
		joinsGridData.horizontalSpan = 2;
		joinsGridData.verticalIndent = 15;
		myJoinsTableEditor.setLayoutData(joinsGridData);
		myJoinsTableEditor.setRelationship(myRelationship);

		toManyChanged();

		return relationshipDialogArea;
	}

	protected void okPressed() {
		buttonBar.forceFocus();
		try {
			String name = myNameText.getText();
			String inverseName = myInverseNameText.getText();
			if (myManyToMany) {
				String joinEntityName = myJoinEntityNameText.getText();
				boolean flatten = myFlattenButton.getSelection();
				myEntity1.joinInManyToManyWith(myEntity2, name, inverseName, joinEntityName, flatten);
			} else {
				EOJoin newJoin = null;
				if (myCreateButton.getSelection() && myCreateFK) {
					String fkName = myFKNameText.getText();
					EOAttribute foreignKey = myRelationship.getEntity().createForeignKeyTo(myRelationship.getDestination(), fkName, fkName, false);
					newJoin = new EOJoin();
					newJoin.setSourceAttribute(foreignKey);
					newJoin.setDestinationAttribute(myRelationship.getDestination().getSinglePrimaryKeyAttribute());
				}
				if (myCreateInverseButton.getSelection() && myCreateInverseFK) {
					String inverseFKName = myInverseFKNameText.getText();
					EOAttribute foreignKey = myRelationship.getDestination().createForeignKeyTo(myRelationship.getEntity(), inverseFKName, inverseFKName, false);
					newJoin = new EOJoin();
					newJoin.setSourceAttribute(myRelationship.getEntity().getSinglePrimaryKeyAttribute());
					newJoin.setDestinationAttribute(foreignKey);
				}

				if (newJoin != null) {
					myRelationship.removeAllJoins();
					myRelationship.addJoin(newJoin);
				}

				if (myCreateButton.getSelection()) {
					myRelationship.setName(name);
					myRelationship.setToMany(Boolean.valueOf(myToManyButton.getSelection()));
					myRelationship.setMandatoryIfNecessary();
					myRelationship.getEntity().addRelationship(myRelationship);
				}
				if (myCreateInverseButton.getSelection()) {
					myInverseRelationship = myRelationship.createInverseRelationshipNamed(inverseName, myInverseToManyButton.getSelection());
					myInverseRelationship.setMandatoryIfNecessary();
					myInverseRelationship.getEntity().addRelationship(myInverseRelationship);
				}
			}
			super.okPressed();
		} catch (Throwable t) {
			ErrorUtils.openErrorDialog(Display.getDefault().getActiveShell(), t);
		}
	}

	public void toManyChanged() {
		String name = myNameText.getText();
		if (myOriginalName == null || ComparisonUtils.equals(name, myOriginalName)) {
			String newName = myRelationship.getEntity()._findUnusedRelationshipName(myRelationship.getDestination().getName(), myToManyButton.getSelection());
			myNameText.setText(newName);
			myOriginalName = newName;
		}
		if (!myToManyButton.getSelection()) {
			myInverseToManyButton.setSelection(true);
		}
		String inverseName = myInverseNameText.getText();
		if (myOriginalInverseName == null || ComparisonUtils.equals(inverseName, myOriginalInverseName)) {
			String newName = myRelationship.getDestination()._findUnusedRelationshipName(myRelationship.getEntity().getName(), myInverseToManyButton.getSelection());
			myInverseNameText.setText(newName);
			myOriginalInverseName = newName;
		}
		if (!myInverseToManyButton.getSelection()) {
			myToManyButton.setSelection(true);
		}
		_checkManyToMany();
	}

	protected void _checkManyToMany() {
		myNameText.setEnabled(myCreateButton.getSelection());
		myInverseNameText.setEnabled(myCreateInverseButton.getSelection());
		myManyToMany = (myCreateButton.getSelection() && myCreateInverseButton.getSelection() && myToManyButton.getSelection() && myInverseToManyButton.getSelection());
		myToManyButton.setEnabled(myCreateButton.getSelection());
		myInverseToManyButton.setEnabled(myCreateInverseButton.getSelection());
		myJoinsTableEditor.setEnabled(!myManyToMany);
		myJoinEntityNameText.setEnabled(myManyToMany);
		myFlattenButton.setEnabled(myManyToMany);

		boolean canCreateFK = myCreateButton.getSelection() && !myToManyButton.getSelection();
		myCreateFK = canCreateFK && myCreateFKButton.getSelection();
		myCreateFKButton.setEnabled(canCreateFK);
		myFKNameText.setEnabled(myCreateFK);

		boolean canCreateInverseFK = myCreateInverseButton.getSelection() && !myInverseToManyButton.getSelection();
		myCreateInverseFK = canCreateInverseFK && myCreateInverseFKButton.getSelection();
		myCreateInverseFKButton.setEnabled(canCreateInverseFK);
		myInverseFKNameText.setEnabled(myCreateInverseFK);

		myJoinsTableEditor.setVisible(!myManyToMany && !myCreateFK && !myCreateInverseFK);

		String fkName = myFKNameText.getText();
		if (fkName == null || fkName.length() == 0) {
			String newName = myRelationship.getEntity().findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(myRelationship.getDestination().getName()) + "ID");
			myFKNameText.setText(newName);
		}

		String inverseFKName = myInverseFKNameText.getText();
		if (inverseFKName == null || inverseFKName.length() == 0) {
			String newName = myRelationship.getDestination().findUnusedAttributeName(StringUtils.toLowercaseFirstLetter(myRelationship.getEntity().getName()) + "ID");
			myInverseFKNameText.setText(newName);
		}
	}

	public void widgetDefaultSelected(SelectionEvent _e) {
		widgetSelected(_e);
	}

	public void widgetSelected(SelectionEvent _e) {
		Object source = _e.getSource();
		if (source == myToManyButton) {
			toManyChanged();
		} else if (source == myInverseToManyButton) {
			toManyChanged();
		} else if (source == myCreateFKButton) {
			_checkManyToMany();
		} else if (source == myCreateInverseFKButton) {
			_checkManyToMany();
		} else if (source == myCreateButton) {
			_checkManyToMany();
		} else if (source == myCreateInverseButton) {
			_checkManyToMany();
		}
	}

	public boolean close() {
		boolean results = super.close();
		if (myTitleFont != null) {
			myTitleFont.dispose();
		}
		return results;
	}
}
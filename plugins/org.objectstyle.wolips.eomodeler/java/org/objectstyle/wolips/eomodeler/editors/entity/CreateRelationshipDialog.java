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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.relationship.JoinsTableEditor;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public class CreateRelationshipDialog extends Dialog implements SelectionListener {
  private EORelationship myRelationship;
  private EORelationship myInverseRelationship;
  private JoinsTableEditor myJoinsTableEditor;
  private String myOriginalName;
  private Text myNameText;
  private Button myToManyButton;
  private Button myCreateButton;
  private String myOriginalInverseName;
  private Text myInverseNameText;
  private Button myInverseToManyButton;
  private Button myCreateInverseButton;
  private Font myTitleFont;

  private boolean myManyToMany;
  private String myName;
  private String myInverseName;

  public CreateRelationshipDialog(Shell _shell, EOEntity _entity1, EOEntity _entity2) {
    super(_shell);
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
    sourceLabel.setText(myRelationship.getEntity().getName());
    sourceLabel.setFont(myTitleFont);
    GridData sourceLabelData = new GridData(GridData.FILL_HORIZONTAL);
    sourceLabelData.horizontalSpan = 2;
    sourceLabel.setLayoutData(sourceLabelData);

    myCreateButton = new Button(relationshipDialogArea, SWT.CHECK);
    myCreateButton.setSelection(true);
    myCreateButton.setLayoutData(new GridData());
    myCreateButton.addSelectionListener(this);
    myCreateButton.setText(Messages.getString("CreateRelationshipDialog.nameLabel"));

    myNameText = new Text(relationshipDialogArea, SWT.BORDER);
    GridData nameData = new GridData(GridData.FILL_HORIZONTAL);
    nameData.widthHint = 200;
    myNameText.setLayoutData(nameData);

    myToManyButton = new Button(relationshipDialogArea, SWT.CHECK);
    myToManyButton.addSelectionListener(this);
    myToManyButton.setText(Messages.getString("CreateRelationshipDialog.toManyLabel"));

    Label destinationLabel = new Label(relationshipDialogArea, SWT.NONE);
    destinationLabel.setText(myRelationship.getDestination().getName());
    destinationLabel.setFont(myTitleFont);
    GridData destinationLabelData = new GridData(GridData.FILL_HORIZONTAL);
    destinationLabelData.horizontalSpan = 2;
    destinationLabelData.verticalIndent = 15;
    destinationLabel.setLayoutData(destinationLabelData);

    myCreateInverseButton = new Button(relationshipDialogArea, SWT.CHECK);
    myCreateInverseButton.setSelection(true);
    myCreateInverseButton.setLayoutData(new GridData());
    myCreateInverseButton.addSelectionListener(this);
    myCreateInverseButton.setText(Messages.getString("CreateRelationshipDialog.inverseNameLabel"));

    myInverseNameText = new Text(relationshipDialogArea, SWT.BORDER);
    GridData inverseNameData = new GridData(GridData.FILL_HORIZONTAL);
    inverseNameData.widthHint = 200;
    myInverseNameText.setLayoutData(inverseNameData);

    myInverseToManyButton = new Button(relationshipDialogArea, SWT.CHECK);
    myInverseToManyButton.addSelectionListener(this);
    myInverseToManyButton.setText(Messages.getString("CreateRelationshipDialog.inverseToManyLabel"));

    myJoinsTableEditor = new JoinsTableEditor(relationshipDialogArea, SWT.BORDER);
    GridData joinsGridData = new GridData(GridData.FILL_HORIZONTAL);
    joinsGridData.horizontalSpan = 2;
    joinsGridData.verticalIndent = 15;
    myJoinsTableEditor.setLayoutData(joinsGridData);
    myJoinsTableEditor.setRelationship(myRelationship);

    toManyChanged();
    inverseToManyChanged();

    return relationshipDialogArea;
  }

  public String getName() {
    return myName;
  }

  public String getInverseName() {
    return myInverseName;
  }

  public EORelationship getRelationship() {
    return myRelationship;
  }

  public EORelationship getInverseRelationship() {
    return myInverseRelationship;
  }

  public boolean isManyToMany() {
    return myManyToMany;
  }

  protected void okPressed() {
    try {
      myName = myNameText.getText();
      myInverseName = myInverseNameText.getText();
      if (isManyToMany()) {
        // DO NOTHING
      }
      else if (myCreateInverseButton.getSelection()) {
        myInverseRelationship = myRelationship.createInverseRelationshipNamed(myInverseName, myInverseToManyButton.getSelection());
      }
      if (!myCreateButton.getSelection()) {
        myRelationship = null;
      }
      else {
        myRelationship.setName(myName);
        myRelationship.setToMany(Boolean.valueOf(myToManyButton.getSelection()));
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    super.okPressed();
  }

  public void toManyChanged() {
    String name = myNameText.getText();
    if (myOriginalName == null || ComparisonUtils.equals(name, myOriginalName)) {
      String newName = myRelationship.getEntity()._findUnusedRelationshipName(myRelationship.getDestination().getName(), myToManyButton.getSelection());
      myNameText.setText(newName);
      myOriginalName = newName;
    }
    _checkManyToMany();
  }

  public void inverseToManyChanged() {
    String name = myInverseNameText.getText();
    if (myOriginalInverseName == null || ComparisonUtils.equals(name, myOriginalInverseName)) {
      String newName = myRelationship.getDestination()._findUnusedRelationshipName(myRelationship.getEntity().getName(), myInverseToManyButton.getSelection());
      myInverseNameText.setText(newName);
      myOriginalInverseName = newName;
    }
    _checkManyToMany();
  }

  protected void _checkManyToMany() {
    myManyToMany = (myCreateButton.getSelection() && myCreateInverseButton.getSelection() && myToManyButton.getSelection() && myInverseToManyButton.getSelection());
    myJoinsTableEditor.setEnabled(!myManyToMany);
  }

  public void widgetDefaultSelected(SelectionEvent _e) {
    widgetSelected(_e);
  }

  public void widgetSelected(SelectionEvent _e) {
    Object source = _e.getSource();
    if (source == myToManyButton) {
      toManyChanged();
    }
    else if (source == myInverseToManyButton) {
      inverseToManyChanged();
    }
    else if (source == myCreateButton) {
      myNameText.setEnabled(myCreateButton.getSelection());
      myToManyButton.setEnabled(myCreateButton.getSelection());
    }
    else if (source == myCreateInverseButton) {
      myInverseNameText.setEnabled(myCreateInverseButton.getSelection());
      myInverseToManyButton.setEnabled(myCreateInverseButton.getSelection());
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
/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.wizards;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Base class for Application and Framework creation wizards page
 * 
 * @author mnolte
 *
 */
public abstract class WOProjectCreationPage
	extends WizardNewProjectCreationPage {

	// widgets
	private boolean importPBWOProject;
	private Text importPBWOProjectPathField;
	private Label importPBWOProjectLabel;
	private Button importPBWOProjectBrowseButton;
	private String importPBWOProjectPathFieldValue;
	// constants
	private static final int IMPORT_TEXT_FIELD_WIDTH = 250;
	/**
	 * Constructor for WOProjectCreationPage.
	 * @param pageName
	 */
	public WOProjectCreationPage(String pageName) {
		super(pageName);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		createProjectCreationOptionsGroup(parent);
	}

	public IPath getImportPath() {
		if (!importPBWOProject()) {
			return null;
		} else {
			return new Path(getImportPBWOProjectPathFieldValue());
		}
	}

	private final void createProjectCreationOptionsGroup(Composite parent) {
		Composite composite = (Composite) getControl();
		GridData data = (GridData) composite.getLayoutData();
		// section generation group
		Group optionsGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		optionsGroup.setLayout(layout);
		optionsGroup.setText(
			Messages.getString("WOProjectCreationPage.creationOptions.title"));
		optionsGroup.setLayoutData(
			new GridData(
				GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		final Button importProjectButton =
			new Button(optionsGroup, SWT.CHECK | SWT.RIGHT);
		importProjectButton.setText(Messages.getString("WOProjectCreationPage.creationOptions.importPBWOProject")); //$NON-NLS-1$
		importProjectButton.setSelection(importPBWOProject);
		importProjectButton.setFont(parent.getFont());
		GridData buttonData = new GridData();
		buttonData.horizontalSpan = 3;
		importProjectButton.setLayoutData(buttonData);
		createImportPBWOProjectLocationGroup(optionsGroup, importPBWOProject);

		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importPBWOProject = importProjectButton.getSelection();
				importPBWOProjectBrowseButton.setEnabled(importPBWOProject);
				importPBWOProjectPathField.setEnabled(importPBWOProject);
				importPBWOProjectLabel.setEnabled(importPBWOProject);
				if (!importPBWOProject) {
					importPBWOProjectPathFieldValue =
						importPBWOProjectPathField.getText();
					importPBWOProjectPathField.setText("");
					//setLocationForSelection();
				} else {
					importPBWOProjectPathField.setText(
						importPBWOProjectPathFieldValue);
				}
			}
		};
		importProjectButton.addSelectionListener(listener);
	}
	private void createImportPBWOProjectLocationGroup(
		Composite importGroup,
		boolean enabled) {
		Font font = importGroup.getFont();
		// location label
		importPBWOProjectLabel = new Label(importGroup, SWT.NONE);
		importPBWOProjectLabel.setText(Messages.getString("WOProjectCreationPage.creationOptions.directoryLabel")); //$NON-NLS-1$
		importPBWOProjectLabel.setEnabled(enabled);
		importPBWOProjectLabel.setFont(font);
		// project location entry field
		importPBWOProjectPathField = new Text(importGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = IMPORT_TEXT_FIELD_WIDTH;
		importPBWOProjectPathField.setLayoutData(data);
		importPBWOProjectPathField.setEnabled(enabled);
		importPBWOProjectPathField.setFont(font);
		// browse button
		importPBWOProjectBrowseButton = new Button(importGroup, SWT.PUSH);
		importPBWOProjectBrowseButton.setText(Messages.getString("WOProjectCreationPage.creationOptions.browseLabel")); //$NON-NLS-1$
		importPBWOProjectBrowseButton
			.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleImportBrowseButtonPressed();
			}
		});
		importPBWOProjectBrowseButton.setEnabled(enabled);
		importPBWOProjectBrowseButton.setFont(font);
		setButtonLayoutData(importPBWOProjectBrowseButton);
		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		importPBWOProjectPathField.setText("");
		//importPBWOProjectPathField.addListener(SWT.Modify, locationModifyListener);
	}
	/**
	 * Returns the value of the project import
	 * field with leading and trailing spaces removed.
	 *
	 * @return the project import location directory in the field
	 */
	private String getImportPBWOProjectPathFieldValue() {
		if (importPBWOProjectPathField == null)
			return ""; //$NON-NLS-1$
		else
			return importPBWOProjectPathField.getText().trim();
	}
	/**
	 *	Open an appropriate directory browser
	 */
	private void handleImportBrowseButtonPressed() {
		DirectoryDialog dialog =
			new DirectoryDialog(importPBWOProjectPathField.getShell());
		dialog.setMessage(Messages.getString("WOProjectCreationPage.creationOptions.directoryDialogLabel")); //$NON-NLS-1$
		String dirName = getImportPBWOProjectPathFieldValue();
		if (!dirName.equals("")) { //$NON-NLS-1$
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			importPBWOProjectPathFieldValue = selectedDirectory;
			importPBWOProjectPathField.setText(importPBWOProjectPathFieldValue);
		}
	}
	/**
	 * Returns the importPBWOProject.
	 * @return boolean
	 */
	public boolean importPBWOProject() {
		return importPBWOProject;
	}

	/**
	 * @see org.eclipse.ui.dialogs.WizardNewProjectCreationPage#getProjectHandle()
	 */
	public IProject getProjectHandle() {
		return super.getProjectHandle();
	}

}

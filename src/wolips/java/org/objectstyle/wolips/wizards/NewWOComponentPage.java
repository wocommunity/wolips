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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.internal.misc.ContainerSelectionGroup;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.io.FileFromTemplateCreator;
import org.objectstyle.wolips.wo.WOVariables;

/**
 * @author mnolte
 * @author uli
 */

public class NewWOComponentPage
	extends WizardNewFileCreationPage {
	private IWorkbench workbench;
	private Composite parentComposite;

	// widgets
	private Button bodyCheckbox;

	/**
	 * Creates the page for the readme creation wizard.
	 *
	 * @param workbench  the workbench on which the page should be created
	 * @param selection  the current selection
	 */
	public NewWOComponentPage(
		IWorkbench workbench,
		IStructuredSelection selection) {
		super("createWOComponentPage1", selection);
		this.setTitle(
			NewWOComponentMessages.getString("WOComponentCreationPage.title"));
		this.setDescription(
			NewWOComponentMessages.getString("WOComponentCreationPage.description"));
		this.workbench = workbench;
	}
	/** (non-Javadoc)
	 * Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {
		parentComposite = parent;
		// inherit default container and name specification widgets
		super.createControl(parent);
		Composite composite = (Composite) getControl();

		//WorkbenchHelp.setHelp(composite, IReadmeConstants.CREATION_WIZARD_PAGE_CONTEXT);

		GridData data = (GridData) composite.getLayoutData();
		this.setFileName(
			NewWOComponentMessages.getString(
				"WOComponentCreationPage.newComponent.defaultName"));

		new Label(composite, SWT.NONE); // vertical spacer

		// sample section generation group
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(
			NewWOComponentMessages.getString(
				"WOComponentCreationPage.creationOptions.title"));
		group.setLayoutData(
			new GridData(
				GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		// sample section generation checkboxes
		bodyCheckbox = new Button(group, SWT.CHECK);
		bodyCheckbox.setText(
			NewWOComponentMessages.getString(
				"WOComponentCreationPage.creationOptions.bodyTag"));
		bodyCheckbox.setSelection(true);
		bodyCheckbox.addListener(SWT.Selection, this);

		new Label(composite, SWT.NONE); // vertical spacer

		setPageComplete(validatePage());
		validatePath();
	}
	/**
	 * Creates a new file resource as requested by the user. If everything
	 * is OK then answer true. If not, false will cause the dialog
	 * to stay open and the appropiate error message is shown
	 *
	 * @return whether creation was successful
	 * @see ReadmeCreationWizard#performFinish()
	 */
	public boolean finish() {

		String componentName = getFileName();
		IPath fullPath = getContainerFullPath();
		String projectName = fullPath.segment(0);

		IProject actualProject =
			ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		NewWOComponentCreator componentCreator =
			new NewWOComponentCreator(actualProject);
		try {
			componentCreator.createWOComponentNamed(
				componentName,
				bodyCheckbox.getSelection(),
				null);
		} catch (CoreException e) {
			WOLipsPlugin.handleException(getShell(), e, null);
			//setErrorMessage(e.getMessage());
			//WebObjectsEclipsePlugin.log(e.getStatus());
			return false;
		} catch (FileFromTemplateCreator.FileCreationException e) {
			WOLipsPlugin.handleException(getShell(), e, null);
			//setErrorMessage(e.getMessage());
			//WebObjectsEclipsePlugin.log(e);
			return false;
		}

		return true;
	}

	/** (non-Javadoc)
	 * Method declared on WizardNewFileCreationPage.
	 */
	protected String getNewFileLabel() {
		return NewWOComponentMessages.getString(
			"WOComponentCreationPage.newComponent.label");
	}

	/** (non-Javadoc)
	* Method declared on WizardNewFileCreationPage.
	*/
	public void handleEvent(Event e) {
		super.handleEvent(e);

		Widget source = e.widget;

		if (source instanceof ContainerSelectionGroup
			|| source instanceof org.eclipse.swt.widgets.Tree) {
			validatePath();

		}

	}

	/**
	 * Method validatePath. Checks if container selection is an project.
	 * All new added WebObjects resource folder must be exact in this hierachy.
	 */
	private void validatePath() {
		IPath currentPath = getContainerFullPath();
		String projectName = currentPath.segment(0);

		IProject actualProject =
			ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IFile projectFile =
			actualProject.getFile(
				WOVariables.woProjectFileName());
		if (!actualProject.exists()) {
			// only projects are allowed as selection
			setErrorMessage(
				NewWOComponentMessages.getString(
					"WOComponentCreationPage.errorMessage.containerNoProject"));
			setPageComplete(false);
		}
		if (!projectFile.exists()) {
			// only projects are allowed as selection
			setErrorMessage(
				NewWOComponentMessages.getString(
					"WOComponentCreationPage.errorMessage.containerNoWOProject"));
			setPageComplete(false);
		}
	}

}

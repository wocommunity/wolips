/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002, 2004 The ObjectStyle Group 
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
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.objectstyle.wolips.variables.VariablesPlugin;

/**
 * @author mnolte
 * @author uli The one and only page in the eo model creation wizard
 */
public class EOModelCreationPage extends WizardNewWOResourcePage {
	private HashMap availableAdaptors;

	private IResource resourceToReveal;

	private Button createEOGeneratorFileButton;
	
	private Button noneAdaptorButton;

	// widgets
	// private Button adaptorJDBCCheckbox;
	// private Button adaptorJDBCPatchedCheckbox;
	/**
	 * Creates the page for the eomodel creation wizard.
	 * 
	 * @param workbench
	 *            the workbench on which the page should be created
	 * @param selection
	 *            the current selection
	 */
	public EOModelCreationPage(IStructuredSelection selection) {
		super("createEOModelPage1", selection);
		this.setTitle(Messages.getString("EOModelCreationPage.title"));
		this.setDescription(Messages.getString("EOModelCreationPage.description"));
	}

	/**
	 * (non-Javadoc) Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {
		// inherit default container and name specification widgets
		super.createControl(parent);
		Composite composite = (Composite) getControl();
		// WorkbenchHelp.setHelp(composite,
		// IReadmeConstants.CREATION_WIZARD_PAGE_CONTEXT);
		// GridData data = (GridData) composite.getLayoutData();
		this.setFileName(Messages.getString("EOModelCreationPage.newEOModel.defaultName"));
		new Label(composite, SWT.NONE); // vertical spacer
		// section generation group

		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.getString("EOModelCreationPage.creationOptions.title"));
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		createAvailableAdaptorButtons(group);
		new Label(composite, SWT.NONE); // vertical spacer

		createEOGeneratorFileButton = new Button(composite, SWT.CHECK);
		createEOGeneratorFileButton.setText("Create EOGenerator File?");
		createEOGeneratorFileButton.setSelection(true);

		setPageComplete(validatePage());
	}

	/**
	 * Creates a new eomodel as requested by the user. If everything is OK then
	 * answer true. If not, false will cause the dialog to stay open and the
	 * appropiate error message is shown
	 * 
	 * @return whether creation was successful
	 * @see EOModelCreationWizard#performFinish()
	 */
	public boolean createEOModel() {
		EOModelCreator modelCreator;
		String modelName = getFileName();
		IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
		// determine adaptor
		String adaptorName = "";
		boolean createEOGeneratorFile = createEOGeneratorFileButton.getSelection();
		Button currentButton;
		Iterator buttonIterator = availableAdaptors.keySet().iterator();
		while (buttonIterator.hasNext()) {
			currentButton = (Button) buttonIterator.next();
			if (currentButton.getSelection()) {
				if (currentButton != noneAdaptorButton) {
					adaptorName = (String) availableAdaptors.get(currentButton);
				}
				break;
			}
		}
		// determine parent resource
		switch (getContainerFullPath().segmentCount()) {
		case 0:
			// not possible ( see validatePage() )
			setErrorMessage("unknown error");
			return false;
		case 1:
			modelCreator = new EOModelCreator(actualProject, modelName, adaptorName, createEOGeneratorFile, this);
			break;
		default:
			IFolder subprojectFolder = actualProject.getFolder(getContainerFullPath().removeFirstSegments(1));
			modelCreator = new EOModelCreator(subprojectFolder, modelName, adaptorName, createEOGeneratorFile, this);
			break;
		}
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(modelCreator);
		return createResourceOperation(op);
	}

	/**
	 * Method createAvailableAdaptorButtons. Parses system framework library
	 * directory for all occurences of "Java[Adaptorname]Adaptor.framework". For
	 * each match one button in the given group is created. The button's text is
	 * equal to the Adaptorname part of the Adaptorframework name.
	 * 
	 * @param group
	 */
	private void createAvailableAdaptorButtons(Group group) {
		Composite row = new Composite(group, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		row.setLayout(rowLayout);
		File systemFrameworkDir = new File(VariablesPlugin.getDefault().getSystemRoot().append("Library").append("Frameworks").toOSString());
		AdaptorFilter adaptorFilter = new AdaptorFilter();
		systemFrameworkDir.listFiles(adaptorFilter);
		availableAdaptors = new HashMap(adaptorFilter.getAdaptorNames().size() + 1);
		String buttonText;
		// add none adaptor entry
		noneAdaptorButton = new Button(row, SWT.RADIO);
		buttonText = "None";
		noneAdaptorButton.setText(buttonText);
		noneAdaptorButton.setSelection(true);
		availableAdaptors.put(noneAdaptorButton, "None");
		for (int i = 0; i < adaptorFilter.getAdaptorNames().size(); i++) {
			Button currentAdaptorButton = new Button(row, SWT.RADIO);
			buttonText = (String) adaptorFilter.getAdaptorNames().elementAt(i);
			currentAdaptorButton.setText(buttonText);
			availableAdaptors.put(currentAdaptorButton, buttonText);
		}
	}

	/**
	 * (non-Javadoc) Method declared on WizardNewFileCreationPage.
	 */
	protected String getNewFileLabel() {
		return Messages.getString("EOModelCreationPage.newEOModel.label");
	}

	private class AdaptorFilter implements FilenameFilter {
		private static final String ADAPTOR_PREFIX = "Java";

		private static final String ADAPTOR_POSTFIX = "Adaptor.framework";

		private Vector adaptorNames;

		public AdaptorFilter() {
			super();
			adaptorNames = new Vector();
		}

		public boolean accept(File dir, String name) {
			String adaptorName = null;
			boolean isAdaptor = (name.length() > (ADAPTOR_PREFIX.length() + ADAPTOR_POSTFIX.length())) && name.startsWith(ADAPTOR_PREFIX) && name.endsWith(ADAPTOR_POSTFIX);
			if (isAdaptor) {
				adaptorName = name.substring(ADAPTOR_PREFIX.length(), name.length() - ADAPTOR_POSTFIX.length());
				adaptorNames.add(adaptorName);
			}
			return isAdaptor;
		}

		/**
		 * Returns the adaptorNames.
		 * 
		 * @return Vector
		 */
		public Vector getAdaptorNames() {
			return adaptorNames;
		}
	}

	public IResource getResourceToReveal() {
		return resourceToReveal;
	}

	public void setResourceToReveal(IResource resourceToReveal) {
		this.resourceToReveal = resourceToReveal;
	}
}

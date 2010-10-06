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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.objectstyle.wolips.baseforuiplugins.utils.ErrorUtils;
import org.objectstyle.wolips.core.resources.types.project.ProjectAdapter;
import org.objectstyle.wolips.jdt.ProjectFrameworkAdapter;

/**
 * @author mnolte
 * @author uli The one and only page in the eo model creation wizard
 */
public class EOModelCreationPage extends WizardNewWOResourcePage {
	private IResource _resourceToReveal;

	private Button _createEOGeneratorFileButton;

	private Combo _pluginCombo;

	private Combo _adaptorCombo;

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

	@Override
	protected void createAdvancedControls(Composite parent) {
		// super.createAdvancedControls(parent);
	}

	@Override
	protected IStatus validateLinkedResource() {
		return Status.OK_STATUS;
	}

	/**
	 * (non-Javadoc) Method declared on IDialogPage.
	 */
	public void createControl(Composite parent) {
		// inherit default container and name specification widgets
		super.createControl(parent);
		
		ProjectAdapter projectAdapter = (ProjectAdapter) getProject().getAdapter(ProjectAdapter.class);
		if (projectAdapter != null) {
			IContainer resource = (IContainer)ResourcesPlugin.getWorkspace().getRoot().findMember(getContainerFullPath());
			if (!projectAdapter.isResourceContainer(resource)) {
				setContainerFullPath(projectAdapter.getDefaultResourcesFolder().getFullPath());
			}
		}

		Composite composite = (Composite) getControl();
		// WorkbenchHelp.setHelp(composite,
		// IReadmeConstants.CREATION_WIZARD_PAGE_CONTEXT);
		// GridData data = (GridData) composite.getLayoutData();
		this.setFileName(Messages.getString("EOModelCreationPage.newEOModel.defaultName"));

		Group modelConfigurationGroup = new Group(composite, SWT.NONE);
		modelConfigurationGroup.setLayout(new GridLayout(2, false));
		modelConfigurationGroup.setText("Options");
		modelConfigurationGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		ProjectFrameworkAdapter projectFrameworkAdapter = (ProjectFrameworkAdapter) getProject().getAdapter(ProjectFrameworkAdapter.class);

		Label adaptorLabel = new Label(modelConfigurationGroup, SWT.NONE);
		adaptorLabel.setText("Adaptor:");
		
		_adaptorCombo = new Combo(modelConfigurationGroup, SWT.READ_ONLY);
		_adaptorCombo.add("None");
		int selectedIndex = 0;
		int index = 0;
		for (String pluginName : projectFrameworkAdapter.getAdaptorFrameworks().keySet()) {
			_adaptorCombo.add(pluginName);
			if ("JDBC".equals(pluginName)) {
				selectedIndex = index; 
			}
			index ++;
		}
		_adaptorCombo.select(selectedIndex + 1);

		Label pluginLabel = new Label(modelConfigurationGroup, SWT.NONE);
		pluginLabel.setText("PlugIn:");
		_pluginCombo = new Combo(modelConfigurationGroup, SWT.READ_ONLY);
		_pluginCombo.add("None");
		for (String pluginName : projectFrameworkAdapter.getPluginFrameworks().keySet()) {
			_pluginCombo.add(pluginName);
		}
		_pluginCombo.select(0);

		_createEOGeneratorFileButton = new Button(modelConfigurationGroup, SWT.CHECK);
		_createEOGeneratorFileButton.setText("Use EOGenerator");
		_createEOGeneratorFileButton.setSelection(true);
		GridData eogenData = new GridData();
		eogenData.horizontalSpan = 2;
		_createEOGeneratorFileButton.setLayoutData(eogenData);

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
		boolean createEOGeneratorFile = _createEOGeneratorFileButton.getSelection();
		String adaptorName = _adaptorCombo.getItem(_adaptorCombo.getSelectionIndex());
		String pluginName = _pluginCombo.getItem(_pluginCombo.getSelectionIndex());
		// determine parent resource
		switch (getContainerFullPath().segmentCount()) {
		case 0:
			// not possible ( see validatePage() )
			setErrorMessage("unknown error");
			return false;
		case 1:
			modelCreator = new EOModelCreator(getProject(), modelName, adaptorName, pluginName, createEOGeneratorFile, this);
			break;
		default:
			IFolder subprojectFolder = getProject().getFolder(getContainerFullPath().removeFirstSegments(1));
			modelCreator = new EOModelCreator(subprojectFolder, modelName, adaptorName, pluginName, createEOGeneratorFile, this);
			break;
		}
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(modelCreator);
		return createResourceOperation(op);
	}

	/**
	 * (non-Javadoc) Method declared on WizardNewFileCreationPage.
	 */
	protected String getNewFileLabel() {
		return Messages.getString("EOModelCreationPage.newEOModel.label");
	}

	public IResource getResourceToReveal() {
		return _resourceToReveal;
	}

	public void setResourceToReveal(IResource resourceToReveal) {
		this._resourceToReveal = resourceToReveal;
	}

	public IProject getProject() {
		IPath containerFullPath = getContainerFullPath();
		if (containerFullPath == null) {
			ErrorUtils.openErrorDialog(getShell(), "No Folder Selectd", "You must select a folder to create a new EOModel.");
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(containerFullPath.segment(0));
		return project;
	}
}

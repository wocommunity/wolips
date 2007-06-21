/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonStatusDialogField;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author mnolte
 * @author uli
 */
public class WOComponentCreationPage extends WizardNewWOResourcePage {
	// widgets
	private static final String BODY_CHECKBOX_KEY = "WOComponentCreationWizardSection.bodyCheckbox";

	private static final String WOO_CHECKBOX_KEY = "WOComponentCreationWizardSection.wooCheckbox";

	private static final String API_CHECKBOX_KEY = "WOComponentCreationWizardSection.apiCheckbox";

	private Button bodyCheckbox;

	private Button wooCheckbox;

	private Button apiCheckbox;

	private IResource[] resourcesToReveal;

	StringButtonStatusDialogField myPackageDialogField;

	/**
	 * Creates the page for the wocomponent creation wizard.
	 * 
	 * @param workbench
	 *            the workbench on which the page should be created
	 * @param selection
	 *            the current selection
	 */
	public WOComponentCreationPage(IStructuredSelection selection) {
		super("createWOComponentPage1", selection);
		this.setTitle(Messages.getString("WOComponentCreationPage.title"));
		this.setDescription(Messages.getString("WOComponentCreationPage.description"));
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
		this.setFileName(Messages.getString("WOComponentCreationPage.newComponent.defaultName"));

		new Label(composite, SWT.NONE); // vertical spacer

		Group packageGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		packageGroup.setLayout(layout);
		packageGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		PackageButtonAdapter adapter = new PackageButtonAdapter();
		myPackageDialogField = new StringButtonStatusDialogField(adapter);
		myPackageDialogField.setDialogFieldListener(adapter);
		myPackageDialogField.setLabelText(NewWizardMessages.NewTypeWizardPage_package_label);
		myPackageDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_package_button);
		myPackageDialogField.setStatusWidthHint(NewWizardMessages.NewTypeWizardPage_default);
		myPackageDialogField.doFillIntoGrid(packageGroup, 4);
		Text text = myPackageDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(text);
		// JavaPackageCompletionProcessor packageCompletionProcessor= new
		// JavaPackageCompletionProcessor();
		// ControlContentAssistHelper.createTextContentAssistant(text,
		// packageCompletionProcessor);

		new Label(composite, SWT.NONE); // vertical spacer
		// section generation group
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(Messages.getString("WOComponentCreationPage.creationOptions.title"));
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Composite row = new Composite(group, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		row.setLayout(rowLayout);
		// section generation checkboxes
		bodyCheckbox = new Button(row, SWT.CHECK);
		bodyCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.bodyTag"));
		bodyCheckbox.setSelection(this.getDialogSettings().getBoolean(BODY_CHECKBOX_KEY));
		bodyCheckbox.addListener(SWT.Selection, this);
		wooCheckbox = new Button(row, SWT.CHECK);
		wooCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.wooFile"));
		wooCheckbox.setSelection(this.getDialogSettings().getBoolean(WOO_CHECKBOX_KEY));
		wooCheckbox.addListener(SWT.Selection, this);
		apiCheckbox = new Button(row, SWT.CHECK);
		apiCheckbox.setText(Messages.getString("WOComponentCreationPage.creationOptions.apiFile"));
		apiCheckbox.setSelection(this.getDialogSettings().getBoolean(API_CHECKBOX_KEY));
		apiCheckbox.addListener(SWT.Selection, this);
		new Label(composite, SWT.NONE); // vertical spacer
		setPageComplete(validatePage());
	}

	/**
	 * Creates a new file resource as requested by the user. If everything is OK
	 * then answer true. If not, false will cause the dialog to stay open and
	 * the appropiate error message is shown
	 * 
	 * @return whether creation was successful
	 * @see WOComponentCreationWizard#performFinish()
	 */
	public boolean createComponent() {
		WOComponentCreator componentCreator;
		String componentName = getFileName();
		String packageName = myPackageDialogField.getText();
		IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
		switch (getContainerFullPath().segmentCount()) {
		case 0:
			// not possible ( see validatePage() )
			setErrorMessage("unknown error");
			return false;
		case 1:
			componentCreator = new WOComponentCreator(actualProject, componentName, packageName, bodyCheckbox.getSelection(), apiCheckbox.getSelection(), wooCheckbox.getSelection(), this);
			break;
		default:
			// determine parent resource for component creator by removing
			// first element (workspace) from full path
			IFolder subprojectFolder = actualProject.getFolder(getContainerFullPath().removeFirstSegments(1));
			componentCreator = new WOComponentCreator(subprojectFolder, componentName, packageName, bodyCheckbox.getSelection(), apiCheckbox.getSelection(), wooCheckbox.getSelection(), this);
			break;
		}
		this.getDialogSettings().put(BODY_CHECKBOX_KEY, bodyCheckbox.getSelection());
		this.getDialogSettings().put(WOO_CHECKBOX_KEY, wooCheckbox.getSelection());
		this.getDialogSettings().put(API_CHECKBOX_KEY, apiCheckbox.getSelection());
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(componentCreator);
		return createResourceOperation(op);
	}

	/**
	 * (non-Javadoc) Method declared on WizardNewFileCreationPage.
	 */
	protected String getNewFileLabel() {
		return Messages.getString("WOComponentCreationPage.newComponent.label");
	}

	public IResource[] getResourcesToReveal() {
		return resourcesToReveal;
	}

	public void setResourcesToReveal(IResource[] resources) {
		this.resourcesToReveal = resources;
	}

	IPackageFragment choosePackage() {
		List<IJavaElement> packagesList = new LinkedList<IJavaElement>();
		try {
			IProject actualProject = ResourcesPlugin.getWorkspace().getRoot().getProject(getContainerFullPath().segment(0));
			IJavaProject javaProject = JavaModelManager.getJavaModelManager().getJavaModel().getJavaProject(actualProject);
			IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
			for (int k = 0; k < roots.length; k++) {
				if (roots[k].getKind() == IPackageFragmentRoot.K_SOURCE) {
					IJavaElement[] children = roots[k].getChildren();
					for (int i = 0; i < children.length; i++) {
						packagesList.add(children[i]);
					}
				}
			}
		} catch (JavaModelException e) {
			// JTourBusPlugin.log(e);
			e.printStackTrace();
		}
		IJavaElement[] packages = packagesList.toArray(new IJavaElement[packagesList.size()]);

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description);
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty);
		dialog.setFilter(myPackageDialogField.getText());
		dialog.setElements(packages);
		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}

	protected class PackageButtonAdapter implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField _field) {
			IPackageFragment pack = choosePackage();
			if (pack != null) {
				myPackageDialogField.setText(pack.getElementName());
			}
		}

		public void dialogFieldChanged(DialogField _field) {
			// fPackageStatus= packageChanged();
			// updatePackageStatusLabel();
		}
	}
}

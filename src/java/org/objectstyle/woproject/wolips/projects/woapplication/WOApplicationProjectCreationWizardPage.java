package org.objectstyle.woproject.wolips.projects.woapplication;

import org.objectstyle.woproject.wolips.WOLipsPlugin;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;

public class WOApplicationProjectCreationWizardPage extends WizardPage {

	private IStatus fCurrStatus;
	
	private boolean fPageVisible;
	
	private IConfigurationElement fConfigurationElement;
	
	private String fNameLabel;
	private String fProjectName;
	
	private Text fTextControl;
	
	public WOApplicationProjectCreationWizardPage(int pageNumber, IConfigurationElement elem) {
		super("page" + pageNumber); //$NON-NLS-1$
		fCurrStatus= createStatus(IStatus.OK, ""); //$NON-NLS-1$
		
		fConfigurationElement= elem;
		
		setTitle(getAttribute(elem, "pagetitle")); //$NON-NLS-1$
		setDescription(getAttribute(elem, "pagedescription")); //$NON-NLS-1$
		
		fNameLabel= getAttribute(elem, "label"); //$NON-NLS-1$
		fProjectName= getAttribute(elem, "name");		 //$NON-NLS-1$
		
	}
	
	private String getAttribute(IConfigurationElement elem, String tag) {
		String res= elem.getAttribute(tag);
		if (res == null) {
			return '!' + tag + '!';
		}
		return res;
	}
	
	/*
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout gd= new GridLayout();
		gd.numColumns= 2;
		composite.setLayout(gd);
		
		Label label= new Label(composite, SWT.LEFT);
		label.setText(fNameLabel);
		label.setLayoutData(new GridData());
		
		fTextControl= new Text(composite, SWT.SINGLE | SWT.BORDER);
		fTextControl.setText(fProjectName);
		fTextControl.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!fTextControl.isDisposed()) {
					validateText(fTextControl.getText());
				}
			}
		});
		fTextControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		setControl(composite);
	}

	private void validateText(String text) {
		IWorkspace workspace= ResourcesPlugin.getWorkspace();
		IStatus status= workspace.validateName(text, IResource.PROJECT);
		if (status.isOK()) {
			if (workspace.getRoot().getProject(text).exists()) {
				status= createStatus(IStatus.ERROR, WOApplicationProjectMessages.getString("WOApplicationProjectCreationWizardPage.error.alreadyexists")); //$NON-NLS-1$
			}
		}	
		updateStatus(status);
		
		fProjectName= text;
	}	
	
	
	/*
	 * @see WizardPage#becomesVisible
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		fPageVisible= visible;
		// policy: wizards are not allowed to come up with an error message
		if (visible && fCurrStatus.matches(IStatus.ERROR)) {
			// keep the error state, but remove the message
			fCurrStatus= createStatus(IStatus.ERROR, ""); //$NON-NLS-1$
		} 
		updateStatus(fCurrStatus);
	}	

	/**
	 * Updates the status line and the ok button depending on the status
	 */
	private void updateStatus(IStatus status) {
		fCurrStatus= status;
		setPageComplete(!status.matches(IStatus.ERROR));
		if (fPageVisible) {
			applyToStatusLine(this, status);
		}
	}

	/**
	 * Applies the status to a dialog page
	 */
	private static void applyToStatusLine(DialogPage page, IStatus status) {
		String errorMessage= null;
		String warningMessage= null;
		String statusMessage= status.getMessage();
		if (statusMessage.length() > 0) {
			if (status.matches(IStatus.ERROR)) {
				errorMessage= statusMessage;
			} else if (!status.isOK()) {
				warningMessage= statusMessage;
			}
		}
		page.setErrorMessage(errorMessage);
		page.setMessage(warningMessage);
	}
	
	
	private static IStatus createStatus(int severity, String message) {
		return new Status(severity, WOLipsPlugin.getPluginId(), severity, message, null);
	}
	
	/**
	 * Returns the name entered by the user
	 */
	public String getName() {
		return fProjectName;
	}

	/**
	 * Returns the configuration element of this page.
	 * @return Returns a IConfigurationElement
	 */
	public IConfigurationElement getConfigurationElement() {
		return fConfigurationElement;
	}

}


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
 package org.objectstyle.woproject.wolips.projects.woframework;

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

public class WOFrameworkProjectCreationWizardPage extends WizardPage {

	private IStatus fCurrStatus;
	
	private boolean fPageVisible;
	
	private IConfigurationElement fConfigurationElement;
	
	private String fNameLabel;
	private String fProjectName;
	
	private Text fTextControl;
	
	public WOFrameworkProjectCreationWizardPage(int pageNumber, IConfigurationElement elem) {
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
				status= createStatus(IStatus.ERROR, WOFrameworkProjectMessages.getString("WOFrameworkProjectCreationWizardPage.error.alreadyexists")); //$NON-NLS-1$
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


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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.WOLipsPlugin;

/**
 * @author mnolte
 * @author uli
 * Basic wizard page for all project file manipulating webobjects wizard pages.
 */
public abstract class WizardNewWOResourcePage
	extends WizardNewFileCreationPage {
	/**
	 * Constructor for WizardNewWOResourcePage.
	 * @param pageName
	 * @param selection
	 */
	public WizardNewWOResourcePage(
		String pageName,
		IStructuredSelection selection) {
		super(pageName, WizardNewWOResourcePage.selection(selection));
	}
	
	private static IStructuredSelection selection(IStructuredSelection aSelection) {
		if(aSelection != null) return aSelection;
		IWorkbench workbench = PlatformUI.getWorkbench();
		ISelection selection = workbench.getActiveWorkbenchWindow().getSelectionService().getSelection();
		IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
		if (selection instanceof IStructuredSelection) {
			selectionToPass = (IStructuredSelection) selection;
			} 	
		else {
			// Build the selection from the IFile of the editor
			IWorkbenchPart part = workbench.getActiveWorkbenchWindow().getPartService().getActivePart();
			if (part instanceof IEditorPart) {
				IEditorInput input = ((IEditorPart)part).getEditorInput();
			if (input instanceof IFileEditorInput) {
				selectionToPass = new StructuredSelection(((IFileEditorInput)input).getFile());
				}	
			}
		}
		return selectionToPass;
	}
	
	protected boolean createResourceOperation(IRunnableWithProgress creationOperation) {
		try {
			new ProgressMonitorDialog(getShell()).run(
				false,
				false,
				creationOperation);
			//getContainer().run(false, false, creationOperation);
		} catch (InvocationTargetException e) {
			WOLipsPlugin.handleException(
				getShell(),
				e.getTargetException(),
				null);
			return false;
		} catch (InterruptedException e) {
			// cancelling is disabled
			return false;
		}
		return true;
	}
	/**
	 * Method validatePage. If super is true, checks if container selection is an project or subproject.
	 * 
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
	 */
	protected boolean validatePage() {
		
		if (super.validatePage()) {
			
			if (getContainerFullPath().segmentCount() > 0) {
				IProject actualProject =
					ResourcesPlugin.getWorkspace().getRoot().getProject(
						getContainerFullPath().segment(0));
						
				switch (getContainerFullPath().segmentCount()) {
					case 0 :
						// no project selected
						setErrorMessage(
							Messages.getString(
								"WizardNewWOResourcePage.errorMessage.containerNoProject"));
						return false;
					case 1 :
						
						if (!actualProject
							.getFile(IWOLipsPluginConstants.PROJECT_FILE_NAME)
							.exists()) {
							// no webobjects project selected
							setErrorMessage(
								Messages.getString(
									"WizardNewWOResourcePage.errorMessage.containerNoWOProject"));
							return false;
						}
						break;
					default :
						
						if (!actualProject
							.getFile(IWOLipsPluginConstants.PROJECT_FILE_NAME)
							.exists()) {
							// no webobjects project selected
							setErrorMessage(
								Messages.getString(
									"WizardNewWOResourcePage.errorMessage.containerNoWOProject"));
							return false;
						} else {
							// project is selected and wo project - now check for subproject
							IPath projectFilePath =
								getContainerFullPath().removeFirstSegments(
									1).append(
									IWOLipsPluginConstants.PROJECT_FILE_NAME);
							if (!actualProject
								.getFile(projectFilePath)
								.exists()) {
								// no webobjects subproject selected
								setErrorMessage(
									Messages.getString(
										"WizardNewWOResourcePage.errorMessage.containerNoWOSubproject"));
								return false;
							}
						}
						break;
				}
				// selection validated
				return true;
			} else {
				// no project selected (container path is < 1)
				setErrorMessage(
					Messages.getString(
						"WizardNewWOResourcePage.errorMessage.containerNoWOProject"));
				return false;
			}
		} else {
			// super validation failed
			return false;
		}
	}

}

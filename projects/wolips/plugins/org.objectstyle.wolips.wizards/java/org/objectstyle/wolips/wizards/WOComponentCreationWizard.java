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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.objectstyle.wolips.core.plugin.WOLipsPluginImages;

/**
 * @author mnolte
 * @author uli
 *
  * This class implements the interface required by the desktop
 * for all 'New' wizards.  This wizard creates WOComponent folders and files.
 */
public class WOComponentCreationWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private IWorkbench workbench;
	private WOComponentCreationPage mainPage;

	/** (non-Javadoc)
	 * Method declared on Wizard.
	 */
	public void addPages() {
		mainPage = new WOComponentCreationPage(selection);
		addPage(mainPage);
	}

	/** (non-Javadoc)
	 * Method declared on INewWizard
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle(Messages.getString("WOComponentCreationWizard.title"));
		setDefaultPageImageDescriptor(
			WOLipsPluginImages.WOCOMPONENT_WIZARD_BANNER);
	}
	/** (non-Javadoc)
	 * Method declared on IWizard
	 */
	public boolean performFinish() {
		return mainPage.createComponent();
		/*
		moved to WizardNewWOResourcePage.sledgeHammer()
		boolean returnValue = mainPage.createComponent();
		IWorkbenchWindow a[] =
			WOLipsPlugin.getDefault().getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < a.length; i++) {
			IWorkbenchPage b[] = a[i].getPages();
			for (int j = 0; j < b.length; j++) {
				IViewReference c[] = b[j].getViewReferences();
				for (int k = 0; k < c.length; k++) {
					IViewPart d = c[k].getView(false); //maybe null
					if ((d != null) && (d instanceof PackageExplorerPart))
						 ((PackageExplorerPart) d).getTreeViewer().refresh();
				}
			}
		}
		return returnValue;
		*/
	}
}

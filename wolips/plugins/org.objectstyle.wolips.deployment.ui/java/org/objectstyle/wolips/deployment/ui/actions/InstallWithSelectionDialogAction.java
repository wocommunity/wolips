/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2004 - 2006 The ObjectStyle Group 
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

package org.objectstyle.wolips.deployment.ui.actions;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IWorkspace.ProjectOrder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.objectstyle.wolips.jdt.ProjectFrameworkAdapter;

/**
 * @author ulrich
 */
public class InstallWithSelectionDialogAction extends AbstractInstallAction {
	IProject[] iProjects;

	private IProject[] getIProjects() {
		ArrayList arrayList = new ArrayList();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject iProject = projects[i];
			if (iProject.isAccessible() && iProject.isOpen()) {
				if (iProject.getFile("build.xml").exists()) {
					ProjectFrameworkAdapter project = (ProjectFrameworkAdapter) this.getIProject().getAdapter(ProjectFrameworkAdapter.class);
					if (project != null) {
						if (iProject == this.getIProject()) {
							arrayList.add(iProject);
						} else {
							if (project.isFrameworkReference(iProject)) {
								arrayList.add(iProject);
							}
						}
					}
				}
			}
		}
		return (IProject[]) arrayList.toArray(new IProject[arrayList.size()]);
	}

	public void run(IAction action) {
		IStructuredContentProvider structuredContentProvider = new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof String) {
					ProjectOrder projectOrder = ResourcesPlugin.getWorkspace().computeProjectOrder(ResourcesPlugin.getWorkspace().getRoot().getProjects());
					return projectOrder.projects;
				}
				return null;
			}

			public void dispose() {
				return;
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				return;
			}

		};
		ILabelProvider labelProvider = new ILabelProvider() {

			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				if (element instanceof String)
					return (String) element;
				if (element instanceof IProject)
					return ((IProject) element).getName();
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
				return;
			}

			public void dispose() {
				return;
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				return;
			}

		};
		iProjects = this.getIProjects();
		ListSelectionDialog listSelectionDialog = new ListSelectionDialog(new Shell(), "WOLips", structuredContentProvider, labelProvider, "Select the projects to install.");
		listSelectionDialog.setInitialSelections(iProjects);
		listSelectionDialog.open();
		if (listSelectionDialog.getReturnCode() == Window.CANCEL) {
			return;
		}
		Object[] selectionResult = listSelectionDialog.getResult();
		IProject[] projectsSelected = new IProject[selectionResult.length];
		for (int i = 0; i < selectionResult.length; i++) {
			projectsSelected[i] = (IProject) selectionResult[i];
		}
		ProjectOrder projectOrder = ResourcesPlugin.getWorkspace().computeProjectOrder(projectsSelected);
		this.install(projectOrder.projects);
	}
}
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

package org.objectstyle.wolips.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.woenvironment.env.WOEnvironment;
import org.objectstyle.woenvironment.env.WOVariables;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * @author ulrich
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class WorkbenchUtilities {

	/**
	 * Utility method with conventions
	 */
	public final static void errorDialog(
		Shell shell,
		String title,
		String message,
		IStatus s) {
		WOLipsLog.log(s);
		// if the 'message' resource string and the IStatus' message are the same,
		// don't show both in the dialog
		if (s != null && message.equals(s.getMessage())) {
			message = null;
		}
		ErrorDialog.openError(shell, title, message, s);
	}

	/**
	 * Utility method with conventions
	 */
	public final static void errorDialog(
		Shell shell,
		String title,
		String message,
		Throwable t) {
		WOLipsLog.log(t);
		IStatus status;
		if (t instanceof CoreException) {
			status = ((CoreException) t).getStatus();
			// if the 'message' resource string and the IStatus' message are the same,
			// don't show both in the dialog
			if (status != null && message.equals(status.getMessage())) {
				message = null;
			}
		} else {
			status = new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IStatus.ERROR, "Error within Debug UI: ", t); //$NON-NLS-1$	
		}
		ErrorDialog.openError(shell, title, message, status);
	}

	/**
			 * Method projectISReferencedByProject.
			 * @param child
			 * @param mother
			 * @return boolean
			 */
	private static boolean projectISReferencedByProject(
		IProject child,
		IProject mother) {
		IProject[] projects = null;
		try {
			if(!mother.isOpen() || !mother.isAccessible())
				//return maybe;
				return false;
			projects = mother.getReferencedProjects();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].equals(child))
				return true;
		}
		return false;
	}

	public final static List findResourcesInProjectByNameAndExtensions(
		IProject project,
		String name,
		String[] extensions,
		boolean includesReferencedProjects) {
		if (includesReferencedProjects) {
			IProject[] projects =
				WorkbenchUtilities.getWorkspace().getRoot().getProjects();
			ArrayList referencedProjects = new ArrayList();
			for (int i = 0; i < projects.length; i++) {
				if (WorkbenchUtilities
					.projectISReferencedByProject(projects[i], project)
					|| WorkbenchUtilities.projectISReferencedByProject(
						project,
						projects[i]))
					referencedProjects.add(projects[i]);
			}
			int numReferencedProjects = referencedProjects.size();
			IProject[] searchScope = new IProject[numReferencedProjects + 1];
			for (int i = 0; i < numReferencedProjects; i++) {
				searchScope[i] = (IProject) referencedProjects.get(i);
			}
			searchScope[numReferencedProjects] = project;
			return WorkbenchUtilities
				.findResourcesInResourcesByNameAndExtensions(
				searchScope,
				name,
				extensions);
		}
		IProject[] searchScope = new IProject[1];
		searchScope[0] = project;
		return WorkbenchUtilities.findResourcesInResourcesByNameAndExtensions(
			searchScope,
			name,
			extensions);
	}

	public final static List findResourcesInResourcesByNameAndExtensions(
		IResource[] resources,
		String name,
		String[] extensions) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < resources.length; i++)
			list.addAll(
				WorkbenchUtilities.findResourcesInResourceByNameAndExtensions(
					resources[i],
					name,
					extensions));
		return list;
	}

	public final static List findResourcesInResourceByNameAndExtensions(
		IResource resource,
		String name,
		String[] extensions) {
		ArrayList list = new ArrayList();
		if ((resource != null)) {
			if ((resource instanceof IContainer)
				|| (resource instanceof IProject)) {
				for (int i = 0; i < extensions.length; i++) {
					IResource foundResource =
						((IContainer) resource).findMember(
							name + "." + extensions[i]);
					if ((foundResource != null))
						list.add(foundResource);
				}
				IResource[] members = WorkbenchUtilities.members(resource);
				WorkbenchUtilities
					.findResourcesInResourceByNameAndExtensionsAndAddToArrayList(
					members,
					name,
					extensions,
					list);
			}
		}
		return list;
	}

	private final static void findResourcesInResourceByNameAndExtensionsAndAddToArrayList(
		IResource[] resources,
		String name,
		String[] extensions,
		ArrayList list) {
		for (int i = 0; i < resources.length; i++) {
			IResource resource = resources[i];
			if ((resource != null)
				&& (resource instanceof IContainer)
				&& (!resource.toString().endsWith(".framework"))
				&& (!resource.toString().endsWith(".woa"))) {
				if ((resource != null)) {
					if ((resource instanceof IContainer)
						|| (resource instanceof IProject)) {
						for (int j = 0; j < extensions.length; j++) {
							IResource foundResource =
								((IContainer) resource).findMember(
									name + "." + extensions[j]);
							if ((foundResource != null))
								list.add(foundResource);
						}
						IResource[] members =
							WorkbenchUtilities.members(resource);
						WorkbenchUtilities
							.findResourcesInResourceByNameAndExtensionsAndAddToArrayList(
							members,
							name,
							extensions,
							list);
					}
				}
			}
		}
	}
	/**
	 * Method findFilesInResourceByName.
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 */
	public final static void findFilesInResourceByName(
		ArrayList anArrayList,
		IResource aResource,
		String aFileName) {
		if ((aResource != null)) {
			if ((aResource instanceof IContainer)
				|| (aResource instanceof IProject)) {
				IResource resource =
					((IContainer) aResource).findMember(aFileName);
				if ((resource != null) && (resource instanceof IFile))
					anArrayList.add(resource);
				IResource[] members = WorkbenchUtilities.members(aResource);
				WorkbenchUtilities.findFilesInResourceByName(
					anArrayList,
					members,
					aFileName);
			}
		}
	}
	/**
	 * Method findFilesInResourceByName.
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 */
	private final static void findFilesInResourceByName(
		ArrayList anArrayList,
		IResource[] aResource,
		String aFileName) {
		for (int i = 0; i < aResource.length; i++) {
			IResource memberResource = aResource[i];
			if ((memberResource != null)
				&& (memberResource instanceof IContainer)
				&& (!memberResource.toString().endsWith(".framework"))
				&& (!memberResource.toString().endsWith(".woa")))
				WorkbenchUtilities.findFilesInResourceByName(
					anArrayList,
					memberResource,
					aFileName);
		}
	}
	/**
	 * Returns the ActiveEditor.
	 * @return IEditorPart
	 */
	public final static IEditorPart getActiveEditor() {
		IWorkbenchPage page = WorkbenchUtilities.getActivePage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}
	/**
	 * Method getEditorInput.
	 * @return IEditorInput
	 */
	public final static IEditorInput getActiveEditorInput() {
		IEditorPart part = WorkbenchUtilities.getActiveEditor();
		if (part != null) {
			return part.getEditorInput();
		}
		return null;
	}
	/**
	 * @return Returns the active editor java input.
	 */
	public final static IJavaElement getActiveEditorJavaInput() {
		IJavaElement result = WorkbenchUtilities.getActiveJavaElement();
		if (result == null) {
			IResource nonjava =
				(IResource) WorkbenchUtilities
					.getActiveEditorInput()
					.getAdapter(
					IResource.class);
			if (nonjava != null) {
				result = WorkbenchUtilities.getJavaParent(nonjava);
			}
		}
		return result;
	}
	/**
	 * Method getActiveJavaElement.
	 * @return IJavaElement
	 */
	public final static IJavaElement getActiveJavaElement() {
		IEditorInput editorInput = WorkbenchUtilities.getActiveEditorInput();
		if (editorInput != null) {
			return (IJavaElement) editorInput.getAdapter(IJavaElement.class);
		}
		return null;
	}
	/**
	 * @return Returns the active page.
	 */
	public final static IWorkbenchPage getActivePage() {
		return WorkbenchUtilities.getActiveWorkbenchWindow().getActivePage();
	}
	/**
	 * @return Returns the active workbench shell.
	 */
	public final static Shell getActiveWorkbenchShell() {
		return WorkbenchUtilities.getActiveWorkbenchWindow().getShell();
	}
	/**
	 * @return Returns the the active workbench window.
	 */
	public final static IWorkbenchWindow getActiveWorkbenchWindow() {
		return WorkbenchUtilities.getWorkbench().getActiveWorkbenchWindow();
	}
	/**
	 * Method getJavaParent.
	 * @param aResource
	 * @return IJavaElement
	 */
	public final static IJavaElement getJavaParent(IResource aResource) {
		IJavaElement result = null;
		IContainer parent = aResource.getParent();
		while (parent != null) {
			result = (IJavaElement) parent.getAdapter(IJavaElement.class);
			if (result != null) {
				break;
			}
			parent = parent.getParent();
		}
		return result;
	}

	/**
	 * Method getShell.
	 * @return Shell
	 */
	public final static Shell getShell() {
		if (WorkbenchUtilities.getActiveWorkbenchWindow() != null) {
			return WorkbenchUtilities.getActiveWorkbenchWindow().getShell();
		}
		return null;
	}
	/**
	 * @return WOEnvironment
	 */
	public final static WOEnvironment getWOEnvironment() {
		return WOLipsPlugin.getDefault().getWOEnvironment();
	}

	/**
	 * @return IWorkbench
	 */
	public final static IWorkbench getWorkbench() {
		return WOLipsPlugin.getDefault().getWorkbench();
	}
	/**
	 * Returns the workspace instance.
	 */
	public final static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	/**
	 * @return WOVariables
	 */
	public final static WOVariables getWOVariables() {
		return WorkbenchUtilities.getWOEnvironment().getWOVariables();
	}
	/**
	 * Method members.
	 * @param aResource
	 * @return IResource[]
	 */
	private final static IResource[] members(IResource aResource) {
		IResource[] members = null;
		try {
			members = ((IContainer) aResource).members();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
		return members;
	}
	/**
	 * Method open.
	 * @param anArrayList
	 */
	public final static void open(ArrayList anArrayList) {
		for (int i = 0; i < anArrayList.size(); i++) {
			IResource resource = (IResource) anArrayList.get(i);
			if ((resource != null) && (resource.getType() == IResource.FILE))
				WorkbenchUtilities.open((IFile) resource);
		}
	}
	/**
	 * Method open.
	 * @param file The file to open.
	 */
	public final static void open(IFile file) {
		WorkbenchUtilities.open(file, false, null);
	}

	/**
	 * Method open.
	 * @param file The file to open.
	 * @param If forceToOpenIntextEditor is set to true the resource opens in a texteditor.
	 */
	public final static void open(
		IFile file,
		boolean forceToOpenIntextEditor,
		String editor) {
		IWorkbenchWindow workbenchWindow =
			WOLipsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					IEditorDescriptor editorDescriptor =
					WOLipsPlugin
					.getDefault()
					.getWorkbench()
					.getEditorRegistry()
					.getDefaultEditor(
							file.getName());
					if (forceToOpenIntextEditor) {
						workbenchPage.openEditor(new FileEditorInput(file), editor);
						WOLipsPlugin
							.getDefault()
							.getWorkbench()
							.getEditorRegistry()
							.setDefaultEditor(
							file.getName(),
							editorDescriptor.getId());
					} else
						workbenchPage.openEditor(new FileEditorInput(file), editorDescriptor.getId());
				} catch (Exception anException) {
					WOLipsLog.log(anException);
				}
			}
		}
	}
}

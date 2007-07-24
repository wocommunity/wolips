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
package org.objectstyle.wolips.workbenchutilities;

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
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.baseforuiplugins.AbstractBaseUIActivator;

/**
 * The main plugin class to be used in the desktop.
 */
public class WorkbenchUtilitiesPlugin extends AbstractBaseUIActivator {
	private static final String PLUGIN_ID = "org.objectstyle.wolips.workbenchutilities";

	// The shared instance.
	private static WorkbenchUtilitiesPlugin plugin;

	/**
	 * The constructor.
	 */
	public WorkbenchUtilitiesPlugin() {
		super();
		plugin = this;
	}

	/**
	 * @return Returns the shared instance.
	 */
	public static WorkbenchUtilitiesPlugin getDefault() {
		return plugin;
	}

	/**
	 * Utility method with conventions
	 * 
	 * @param shell
	 * @param title
	 * @param message
	 * @param s
	 */
	public final static void errorDialog(Shell shell, String title, String message, IStatus s) {
		WorkbenchUtilitiesPlugin.getDefault().log(s);
    String errorMessage = message;
		// if the 'message' resource string and the IStatus' message are the
		// same,
		// don't show both in the dialog
		if (s != null && errorMessage.equals(s.getMessage())) {
      errorMessage = null;
		}
		ErrorDialog.openError(shell, title, errorMessage, s);
	}

	/**
	 * Utility method with conventions
	 * 
	 * @param shell
	 * @param title
	 * @param message
	 * @param t
	 */
	public final static void errorDialog(Shell shell, String title, String message, Throwable t) {
    String errorMessage = message;
		WorkbenchUtilitiesPlugin.getDefault().log(t);
		IStatus status;
		if (t instanceof CoreException) {
			status = ((CoreException) t).getStatus();
			// if the 'message' resource string and the IStatus' message are the
			// same,
			// don't show both in the dialog
			if (status != null && errorMessage.equals(status.getMessage())) {
        errorMessage = null;
			}
		} else {
			status = new Status(IStatus.ERROR, WorkbenchUtilitiesPlugin.PLUGIN_ID, IStatus.ERROR, "Error within Debug UI: ", t); //$NON-NLS-1$	
		}
		ErrorDialog.openError(shell, title, errorMessage, status);
	}

	/**
	 * Method projectISReferencedByProject.
	 * 
	 * @param child
	 * @param mother
	 * @return boolean
	 */
	private static boolean projectISReferencedByProject(IProject child, IProject mother) {
		IProject[] projects = null;
		try {
			if (!mother.isOpen() || !mother.isAccessible())
				// return maybe;
				return false;
			projects = mother.getReferencedProjects();
		} catch (Exception anException) {
			WorkbenchUtilitiesPlugin.getDefault().log(anException);
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].equals(child))
				return true;
		}
		return false;
	}

	/**
	 * @param project
	 * @param name
	 * @param extensions
	 * @param includesReferencedProjects
	 * @return The list of resources.
	 * @deprecated Use the locate stuff.
	 */
	public final static List<IResource> findResourcesInProjectByNameAndExtensions(IProject project, String name, String[] extensions, boolean includesReferencedProjects) {
		if (includesReferencedProjects) {
			IProject[] projects = WorkbenchUtilitiesPlugin.getWorkspace().getRoot().getProjects();
			List<IProject> referencedProjects = new ArrayList<IProject>();
			for (int i = 0; i < projects.length; i++) {
				if (WorkbenchUtilitiesPlugin.projectISReferencedByProject(projects[i], project) || WorkbenchUtilitiesPlugin.projectISReferencedByProject(project, projects[i]))
					referencedProjects.add(projects[i]);
			}
			int numReferencedProjects = referencedProjects.size();
			IProject[] searchScope = new IProject[numReferencedProjects + 1];
			for (int i = 0; i < numReferencedProjects; i++) {
				searchScope[i] = referencedProjects.get(i);
			}
			searchScope[numReferencedProjects] = project;
			return WorkbenchUtilitiesPlugin.findResourcesInResourcesByNameAndExtensions(searchScope, name, extensions);
		}
		IProject[] searchScope = new IProject[1];
		searchScope[0] = project;
		return WorkbenchUtilitiesPlugin.findResourcesInResourcesByNameAndExtensions(searchScope, name, extensions);
	}

	/**
	 * @param resources
	 * @param name
	 * @param extensions
	 * @return List of IResource
	 * @deprecated Use the locate stuff.
	 */
	public final static List<IResource> findResourcesInResourcesByNameAndExtensions(IResource[] resources, String name, String[] extensions) {
		List<IResource> list = new ArrayList<IResource>();
		for (int i = 0; i < resources.length; i++) {
			list.addAll(WorkbenchUtilitiesPlugin.findResourcesInResourceByNameAndExtensions(resources[i], name, extensions));
		}
		return list;
	}

	/**
	 * @param resource
	 * @param name
	 * @param extensions
	 * @return List of IResource
	 * @deprecated Use the locate stuff.
	 */
	public final static List<IResource> findResourcesInResourceByNameAndExtensions(IResource resource, String name, String[] extensions) {
		List<IResource> list = new ArrayList<IResource>();
		if ((resource != null)) {
			if (((resource instanceof IContainer) || (resource instanceof IProject)) && resource.isAccessible()) {
				for (int i = 0; i < extensions.length; i++) {
					IResource foundResource = ((IContainer) resource).findMember(name + "." + extensions[i]);
					if (foundResource != null && !foundResource.isDerived())
						list.add(foundResource);
				}
				IResource[] members = WorkbenchUtilitiesPlugin.members(resource);
				WorkbenchUtilitiesPlugin.findResourcesInResourceByNameAndExtensionsAndAddToArrayList(members, name, extensions, list);
			}
		}
		return list;
	}

	/**
	 * @param resources
	 * @param name
	 * @param extensions
	 * @param list
	 * @deprecated Use the locate stuff.
	 */
	private final static void findResourcesInResourceByNameAndExtensionsAndAddToArrayList(IResource[] resources, String name, String[] extensions, List<IResource> list) {
		if (resources == null) {
			return;
		}
		for (int i = 0; i < resources.length; i++) {
			IResource resource = resources[i];
			if ((resource != null) && (!resource.isDerived()) && (resource instanceof IContainer) && (!resource.getName().endsWith(".framework")) && (!resource.getName().endsWith(".woa")) && (!(resource.getName().equalsIgnoreCase("build") && resource.getParent().equals(resource.getProject()))) && (!(resource.getName().equalsIgnoreCase("dist") && resource.getParent().equals(resource.getProject()))) && (!(resource.getName().equalsIgnoreCase("target") && resource.getParent().equals(resource.getProject())))) {
				if (((resource instanceof IContainer) || (resource instanceof IProject)) && resource.isAccessible()) {
					for (int j = 0; j < extensions.length; j++) {
						IResource foundResource = ((IContainer) resource).findMember(name + "." + extensions[j]);
						if (foundResource != null && !foundResource.isDerived())
							list.add(foundResource);
					}
					IResource[] members = WorkbenchUtilitiesPlugin.members(resource);
					WorkbenchUtilitiesPlugin.findResourcesInResourceByNameAndExtensionsAndAddToArrayList(members, name, extensions, list);
				}
			}
		}
	}

	/**
	 * Method findFilesInResourceByName.
	 * 
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 * @deprecated Use the locate stuff.
	 */
	public final static void findFilesInResourceByName(List<IResource> anArrayList, IResource aResource, String aFileName) {
		if ((aResource != null)) {
			if (((aResource instanceof IContainer) || (aResource instanceof IProject)) && aResource.isAccessible()) {
				IResource resource = ((IContainer) aResource).findMember(aFileName);
				if ((resource != null) && (resource instanceof IFile) && !resource.isDerived())
					anArrayList.add(resource);
				IResource[] members = WorkbenchUtilitiesPlugin.members(aResource);
				WorkbenchUtilitiesPlugin.findFilesInResourceByName(anArrayList, members, aFileName);
			}
		}
	}

	/**
	 * Method findFilesInResourceByName.
	 * 
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 * @deprecated Use the locate stuff.
	 */
	private final static void findFilesInResourceByName(List<IResource> anArrayList, IResource[] aResource, String aFileName) {
		for (int i = 0; i < aResource.length; i++) {
			IResource memberResource = aResource[i];
			if ((memberResource != null) && (!memberResource.isDerived()) && (memberResource instanceof IContainer) && (!memberResource.toString().endsWith(".framework")) && (!memberResource.toString().endsWith(".woa")) && (!(memberResource.toString().equalsIgnoreCase("build") && memberResource.getParent().equals(memberResource.getProject()))) && (!(memberResource.toString().equalsIgnoreCase("dist") && memberResource.getParent().equals(memberResource.getProject()))) && (!(memberResource.toString().equalsIgnoreCase("target") && memberResource.getParent().equals(memberResource.getProject()))))
				WorkbenchUtilitiesPlugin.findFilesInResourceByName(anArrayList, memberResource, aFileName);
		}
	}

	/**
	 * Returns the ActiveEditor.
	 * 
	 * @return IEditorPart
	 */
	public final static IEditorPart getActiveEditor() {
		IWorkbenchPage page = WorkbenchUtilitiesPlugin.getActivePage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}

	/**
	 * Method getEditorInput.
	 * 
	 * @return IEditorInput
	 */
	public final static IEditorInput getActiveEditorInput() {
		IEditorPart part = WorkbenchUtilitiesPlugin.getActiveEditor();
		if (part != null) {
			return part.getEditorInput();
		}
		return null;
	}

	/**
	 * @return Returns the active page.
	 */
	public final static IWorkbenchPage getActivePage() {
		return WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * @return Returns the active workbench shell.
	 */
	public final static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow win = WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow();
		Shell shell = null;
		if (null != win) {
			shell = win.getShell();
		}
		return shell;
	}

	/**
	 * @return Returns the the active workbench window.
	 */
	public final static IWorkbenchWindow getActiveWorkbenchWindow() {
		return WorkbenchUtilitiesPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * @return Returns the workspace instance.
	 */
	public final static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Method members.
	 * 
	 * @param aResource
	 * @return IResource[]
	 */
	private final static IResource[] members(IResource aResource) {
		IResource[] members = null;
		try {
			members = ((IContainer) aResource).members();
		} catch (Exception anException) {
			WorkbenchUtilitiesPlugin.getDefault().log(anException);
		}
		return members;
	}

	/**
	 * Method open.
	 * 
	 * @param anArrayList
	 */
	public final static void open(List<IResource> anArrayList) {
		for (int i = 0; i < anArrayList.size(); i++) {
			IResource resource = anArrayList.get(i);
			if ((resource != null) && (resource.getType() == IResource.FILE)) {
				WorkbenchUtilitiesPlugin.open((IFile) resource);
			}
		}
	}

	/**
	 * Method open.
	 * 
	 * @param file
	 *            The file to open.
	 */
	public final static void open(IFile file) {
		WorkbenchUtilitiesPlugin.open(file, null);
	}

	/**
	 * Method open.
	 * 
	 * @param file
	 *            The file to open.
	 * @param editor
	 */
	public final static void open(IFile file, String editor) {
		IWorkbenchWindow workbenchWindow = WorkbenchUtilitiesPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					String id = null;
					if (editor == null) {
						IEditorDescriptor editorDescriptor = IDE.getDefaultEditor(file);
						if (editorDescriptor == null) {
							editorDescriptor = IDE.getEditorDescriptor(file);
						}
						if (editorDescriptor != null) {
							id = editorDescriptor.getId();
						}
					} else {
						id = editor;
					}
					workbenchPage.openEditor(new FileEditorInput(file), id);
				} catch (Exception anException) {
					WorkbenchUtilitiesPlugin.getDefault().log(anException);
				}
			}
		}
	}

	/**
	 * Returns the selection of the ActiveWorkbenchWindow
	 * 
	 * @return the configured selection
	 */
	public final static IStructuredSelection getActiveWorkbenchWindowSelection() {
		IWorkbenchWindow window = WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow();
		if (window != null) {
			ISelection selection = window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection) selection;
			}
		}
		return StructuredSelection.EMPTY;
	}
}
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
package org.objectstyle.wolips.baseforuiplugins.utils;

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
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.PropertySheet;
import org.objectstyle.wolips.baseforuiplugins.Activator;

/**
 * The main plugin class to be used in the desktop.
 */
public class WorkbenchUtilities {
	public static boolean shouldSaveSelection(IWorkbenchWindow window) {
		return window != null && !(window.getPartService().getActivePart() instanceof PropertySheet);
	}
	
	public final static ISelection getSelection(IWorkbenchWindow window) {
		ISelection selection;
		if (window.getPartService().getActivePart() instanceof PropertySheet) {
			selection = window.getSelectionService().getSelection("org.eclipse.ui.views.ContentOutline");
			if (selection == null) {
				selection = window.getSelectionService().getSelection();
			}
		}
		else {
			selection = window.getSelectionService().getSelection();
		}
		return selection;
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
		Activator.getDefault().log(s);
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
		Activator.getDefault().log(t);
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
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundleID(), IStatus.ERROR, "Error within Debug UI: ", t); //$NON-NLS-1$	
		}
		ErrorDialog.openError(shell, title, errorMessage, status);
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
				IResource[] members = WorkbenchUtilities.members(aResource);
				WorkbenchUtilities.findFilesInResourceByName(anArrayList, members, aFileName);
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
				WorkbenchUtilities.findFilesInResourceByName(anArrayList, memberResource, aFileName);
		}
	}

	/**
	 * Returns the ActiveEditor.
	 * 
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
	 * 
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
	 * @return Returns the active page.
	 */
	public final static IWorkbenchPage getActivePage() {
		return WorkbenchUtilities.getActiveWorkbenchWindow().getActivePage();
	}

	/**
	 * @return Returns the active workbench shell.
	 */
	public final static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow win = WorkbenchUtilities.getActiveWorkbenchWindow();
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
		return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
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
			Activator.getDefault().log(anException);
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
				WorkbenchUtilities.open((IFile) resource);
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
		WorkbenchUtilities.open(file, null);
	}

	/**
	 * Opens an editor for the given file.
	 * 
	 * @param file
	 *            The file to open.
	 * @param editorID
	 *            the ID of the editor to use (or null for the default)
	 * @return the editor part
	 */
	public final static IEditorPart open(IFile file, String editorID) {
		IEditorPart editorPart = null;
		IWorkbenchWindow workbenchWindow = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow == null) {
			IWorkbenchWindow[] workbenchWindows = Activator.getDefault().getWorkbench().getWorkbenchWindows();
			if (workbenchWindows != null && workbenchWindows.length > 0) {
				workbenchWindow = workbenchWindows[0];
			}
		}
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					String id = null;
					if (editorID == null) {
						IEditorDescriptor editorDescriptor = IDE.getDefaultEditor(file);
						if (editorDescriptor == null) {
							editorDescriptor = IDE.getEditorDescriptor(file);
						}
						if (editorDescriptor != null) {
							id = editorDescriptor.getId();
						}
					} else {
						id = editorID;
					}
					editorPart = workbenchPage.openEditor(new FileEditorInput(file), id);
				} catch (Exception anException) {
					Activator.getDefault().log(anException);
				}
			}
		}
		return editorPart;
	}

	/**
	 * Finds an editor for the given file.
	 * 
	 * @param file
	 *            The file to find
	 * @param editorID
	 *            the ID of the editor to look for (or null for the default)
	 * @return the editor reference (or null if there isn't one)
	 */
	public final static IEditorReference findEditor(IFile file, String editorID) {
		IEditorReference editorReference = null;
		for (IWorkbenchWindow workbenchWindow : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage workbenchPage : workbenchWindow.getPages()) {
				try {
					String id = null;
					if (editorID == null) {
						IEditorDescriptor editorDescriptor = IDE.getDefaultEditor(file);
						if (editorDescriptor == null) {
							editorDescriptor = IDE.getEditorDescriptor(file);
						}
						if (editorDescriptor != null) {
							id = editorDescriptor.getId();
						}
					} else {
						id = editorID;
					}
					IEditorReference[] editorReferences = workbenchPage.findEditors(new FileEditorInput(file), id, IWorkbenchPage.MATCH_ID | IWorkbenchPage.MATCH_INPUT);
					if (editorReferences != null && editorReferences.length > 0) {
						editorReference = editorReferences[0];
						break;
					}
				} catch (Exception anException) {
					Activator.getDefault().log(anException);
				}
			}
		}
		return editorReference;
	}

	/**
	 * Returns the selection of the ActiveWorkbenchWindow
	 * 
	 * @return the configured selection
	 */
	public final static IStructuredSelection getActiveWorkbenchWindowSelection() {
		IWorkbenchWindow window = WorkbenchUtilities.getActiveWorkbenchWindow();
		if (window != null) {
			ISelection selection = window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection) selection;
			}
		}
		return StructuredSelection.EMPTY;
	}
}
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

package org.objectstyle.wolips.core.plugin;

import java.org.objectstyle.wolips.logging.WOLipsLog;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.objectstyle.woproject.env.Environment;
import org.objectstyle.woproject.env.WOEnvironment;
import org.objectstyle.woproject.env.WOVariables;

/**
 * @author uli
 *
 * This class should be used as base for more concrete base classes.
 * Take care that this class can only be used after the logging system is set up.	
 */
public abstract class AWOLips implements IWOLipsPluginConstants {

	private static Log log = LogFactory.getLog(AWOLips.class);
	/**
	 * Utility method with conventions
	 */
	public void errorDialog(
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
	public void errorDialog(
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
			status = new Status(IStatus.ERROR, WOLipsPlugin.getPluginId(), IDebugUIConstants.INTERNAL_ERROR, "Error within Debug UI: ", t); //$NON-NLS-1$	
		}
		ErrorDialog.openError(shell, title, message, status);
	}
	/**
	 * Method findFilesInResourceByName.
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 */
	public void findFilesInResourceByName(
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
				IResource[] members = this.members(aResource);
				this.findFilesInResourceByName(anArrayList, members, aFileName);
			}
		}
	}
	/**
	 * Method findFilesInResourceByName.
	 * @param anArrayList
	 * @param aResource
	 * @param aFileName
	 */
	private void findFilesInResourceByName(
		ArrayList anArrayList,
		IResource[] aResource,
		String aFileName) {
		for (int i = 0; i < aResource.length; i++) {
			IResource memberResource = aResource[i];
			if ((memberResource != null)
				&& (memberResource instanceof IContainer)
				&& (!memberResource.toString().endsWith(".framework"))
				&& (!memberResource.toString().endsWith(".woa")))
				this.findFilesInResourceByName(
					anArrayList,
					memberResource,
					aFileName);
		}
	}
	/**
	 * Returns the ActiveEditor.
	 * @return IEditorPart
	 */
	public IEditorPart getActiveEditor() {
		IWorkbenchPage page = this.getActivePage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}
	/**
	 * Method getEditorInput.
	 * @return IEditorInput
	 */
	public IEditorInput getActiveEditorInput() {
		IEditorPart part = this.getActiveEditor();
		if (part != null) {
			return part.getEditorInput();
		}
		return null;
	}
	/**
	 * @return Returns the active editor java input.
	 */
	public IJavaElement getActiveEditorJavaInput() {
		IJavaElement result = this.getActiveJavaElement();
		if (result == null) {
			IResource nonjava =
				(IResource) this.getActiveEditorInput().getAdapter(
					IResource.class);
			if (nonjava != null) {
				result = this.getJavaParent(nonjava);
			}
		}
		return result;
	}
	/**
	 * Method getActiveJavaElement.
	 * @return IJavaElement
	 */
	public IJavaElement getActiveJavaElement() {
		IEditorInput editorInput = this.getActiveEditorInput();
		if (editorInput != null) {
			return (IJavaElement) editorInput.getAdapter(IJavaElement.class);
		}
		return null;
	}
	/**
	 * @return Returns the active page.
	 */
	public IWorkbenchPage getActivePage() {
		return this.getActiveWorkbenchWindow().getActivePage();
	}
	/**
	 * @return Returns the active workbench shell.
	 */
	public Shell getActiveWorkbenchShell() {
		return this.getActiveWorkbenchWindow().getShell();
	}
	/**
	 * @return Returns the the active workbench window.
	 */
	public IWorkbenchWindow getActiveWorkbenchWindow() {
		return this.getWorkbench().getActiveWorkbenchWindow();
	}
	/**
	 * Method getJavaParent.
	 * @param aResource
	 * @return IJavaElement
	 */
	public IJavaElement getJavaParent(IResource aResource) {
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
	 * @return IPath from NextLocalRoot Classpath variable if it exists.
	 */
	public IPath getNextLocalRootClassPathVariable() {
		return JavaCore.getClasspathVariable(Environment.NEXT_LOCAL_ROOT);
	}
	/**
	 * @return IPath from NextRoot Classpath variable if it exists.
	 */
	public IPath getNextRootClassPathVariable() {
		return JavaCore.getClasspathVariable(Environment.NEXT_ROOT);
	}
	/**
	 * @return IPath from NextSystemRoot Classpath variable if it exists.
	 */
	public IPath getNextSystemRootClassPathVariable() {
		return JavaCore.getClasspathVariable(Environment.NEXT_SYSTEM_ROOT);
	}
	/**
	 * Method getShell.
	 * @return Shell
	 */
	public Shell getShell() {
		if (this.getActiveWorkbenchWindow() != null) {
			return this.getActiveWorkbenchWindow().getShell();
		}
		return null;
	}
	/**
	 * @return WOEnvironment
	 */
	public WOEnvironment getWOEnvironment() {
		return this.getWOLipsPlugin().getWOEnvironment();
	}

	/**
	 * @return WOLipsPlugin
	 */
	public WOLipsPlugin getWOLipsPlugin() {
		return WOLipsPlugin.getDefault();
	}
	/**
	 * @return IWorkbench
	 */
	public IWorkbench getWorkbench() {
		return this.getWOLipsPlugin().getWorkbench();
	}
	/**
	 * Returns the workspace instance.
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	/**
	 * @return WOVariables
	 */
	public WOVariables getWOVariables() {
		return this.getWOEnvironment().getWOVariables();
	}
	/**
	 * Method members.
	 * @param aResource
	 * @return IResource[]
	 */
	private IResource[] members(IResource aResource) {
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
	public void open(ArrayList anArrayList) {
		for (int i = 0; i < anArrayList.size(); i++) {
			IResource resource = (IResource) anArrayList.get(i);
			if ((resource != null) && (resource.getType() == IResource.FILE))
				this.open((IFile) resource);
		}
	}
	/**
	 * Method open.
	 * @param aFile
	 */
	public void open(IFile aFile) {
		IWorkbenchWindow workbenchWindow =
			WOLipsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					workbenchPage.openEditor(aFile);
				} catch (Exception anException) {
					WOLipsLog.log(anException);
				}
			}
		}
	}
}

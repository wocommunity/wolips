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
package org.objectstyle.wolips.eomodeler.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditor;
import org.objectstyle.wolips.eomodeler.preferences.PreferenceConstants;

/**
 * @author ulrich/mschrag
 */
public class OpenEntityModelerAction implements IObjectActionDelegate {
	private IResource _actionResource;

	private IWorkbenchPart _part;

	public void dispose() {
		_actionResource = null;
	}

	public void run(IAction action) {
		OpenEntityModelerAction.openResourceIfPossible(_part, _actionResource);
	}

	/**
	 * Resets the project when the selection is changed.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		Object obj = (((IStructuredSelection) selection).getFirstElement());
		_actionResource = null;
		if (obj != null && obj instanceof IResource) {
			_actionResource = (IResource) obj;
		}
		if (obj != null && obj instanceof IProject) {
			_actionResource = (IProject) obj;
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		_part = targetPart;
	}

	public static boolean openResourceIfPossible(IWorkbenchPart part, IResource actionResource) {
		IWorkbenchWindow existingWindow;
		if (part == null) {
			existingWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		} else {
			existingWindow = part.getSite().getWorkbenchWindow();
		}
		IWorkbench workbench = existingWindow.getWorkbench();

		boolean opened = false;
		if (actionResource != null) {
			IFile editorFile = null;
			if (actionResource.getName().endsWith(".eomodeld") && actionResource instanceof IFolder) {
				IFolder eomodeldFolder = (IFolder) actionResource;
				editorFile = eomodeldFolder.getFile("index.eomodeld");
			} else if (actionResource.getName().equals("index.eomodeld") && actionResource instanceof IFile) {
				editorFile = (IFile) actionResource;
			} else if (actionResource.getName().endsWith(".plist") && actionResource instanceof IFile && actionResource.getParent().getFile(new Path("index.eomodeld")).exists()) {
				editorFile = (IFile) actionResource;
			}
			if (editorFile != null && editorFile.exists()) {
				FileEditorInput input = new FileEditorInput(editorFile);
				
				IEditorPart editorPart = null;
				for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
					for (IWorkbenchPage page : window.getPages()) {
						if (editorPart == null) {
							editorPart = page.findEditor(input);
						}
					}
				}
				
				if (editorPart != null) {
					IWorkbenchPage page = editorPart.getEditorSite().getPage();
					IWorkbenchWindow window = page.getWorkbenchWindow();
					window.setActivePage(page);
					window.getShell().setActive();
					opened = true;
				}
				else if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.OPEN_IN_WINDOW_KEY)) {
					try {
						IWorkbenchWindow window = workbench.openWorkbenchWindow(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, null);
						window.getActivePage().openEditor(new FileEditorInput(editorFile), EOModelEditor.EOMODEL_EDITOR_ID);
						// MS: This is super hacky, but having a console open in the entity modeler perspective is fucking annoying.
						// So we're just going to forcefully close the thing.
						window.getPartService().addPartListener(new IPartListener() {
							public void partOpened(final IWorkbenchPart part) {
								if ("org.eclipse.ui.console.ConsoleView".equals(part.getSite().getId())) {
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											part.getSite().getPage().hideView((IViewPart)part);
										}
									});
								}
							}
							
							public void partDeactivated(IWorkbenchPart part) {
								// IGNORE
							}
							
							public void partClosed(IWorkbenchPart part) {
								// IGNORE
							}
							
							public void partBroughtToTop(IWorkbenchPart part) {
								// IGNORE
							}
							
							public void partActivated(IWorkbenchPart part) {
								// IGNORE
							}
						});
						opened = true;
					} catch (WorkbenchException e) {
						Activator.getDefault().log(e);
					}
				} else {
					try {
						IDE.openEditor(existingWindow.getActivePage(), editorFile, EOModelEditor.EOMODEL_EDITOR_ID);
					} catch (PartInitException e) {
						Activator.getDefault().log(e);
					}
					// WorkbenchUtilitiesPlugin.open(editorFile,
					// EOModelEditor.EOMODEL_EDITOR_ID);
				}
			}
		}
		return opened;
	}
}
/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group
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

package org.objectstyle.wolips.wodclipse.mpe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.objectstyle.wolips.wodclipse.editors.WODEditor;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener {

	ArrayList editors = new ArrayList();
	int webEditorIndex = 0;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0() {
		try {
			CompilationUnitEditor javaEditor = new CompilationUnitEditor();
			int index = addPage(javaEditor, getEditorInput());
			setPageText(index, javaEditor.getTitle());
			editors.add(javaEditor);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor, which shows the html.
	 */
	void createPage1() {

		try {
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			IProject project = file.getProject();
			List resources = WorkbenchUtilitiesPlugin
					.findResourcesInProjectByNameAndExtensions(project, file
							.getName()
							.substring(0, file.getName().length() - 5),
							new String[] { "html" }, false);
			if (resources == null || resources.size() != 1) {
				return;
			}
			IResource wodResource = (IResource) resources.get(0);
			if (wodResource.getType() != IResource.FILE) {
				return;
			}
			TextEditor htmlEditor = new TextEditor();
			FileEditorInput fileEditorInput = new FileEditorInput(
					(IFile) wodResource);
			int index = addPage(htmlEditor, fileEditorInput);
			setPageText(index, htmlEditor.getTitle());
			editors.add(htmlEditor);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 2 of the multi-page editor, which shows the wod.
	 */
	void createPage2() {

		try {
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			IProject project = file.getProject();
			List resources = WorkbenchUtilitiesPlugin
					.findResourcesInProjectByNameAndExtensions(project, file
							.getName()
							.substring(0, file.getName().length() - 5),
							new String[] { "wod" }, false);
			if (resources == null || resources.size() != 1) {
				return;
			}
			IResource wodResource = (IResource) resources.get(0);
			if (wodResource.getType() != IResource.FILE) {
				return;
			}
			WODEditor wodEditor = new WODEditor();
			FileEditorInput fileEditorInput = new FileEditorInput(
					(IFile) wodResource);
			int index = addPage(wodEditor, fileEditorInput);
			setPageText(index, wodEditor.getTitle());
			editors.add(wodEditor);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 3 of the multi-page editor, which shows the api.
	 */
	void createPage3() {

		try {
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			IProject project = file.getProject();
			List resources = WorkbenchUtilitiesPlugin
					.findResourcesInProjectByNameAndExtensions(project, file
							.getName()
							.substring(0, file.getName().length() - 5),
							new String[] { "api" }, false);
			if (resources == null || resources.size() != 1) {
				return;
			}
			IResource wodResource = (IResource) resources.get(0);
			if (wodResource.getType() != IResource.FILE) {
				return;
			}
			TextEditor apiEditor = new TextEditor();
			FileEditorInput fileEditorInput = new FileEditorInput(
					(IFile) wodResource);
			int index = addPage(apiEditor, fileEditorInput);
			setPageText(index, apiEditor.getTitle());
			editors.add(apiEditor);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}
	
	/**
	 * Creates page 4 of the multi-page editor, which shows the preview.
	 */
	void createPage4() {

		try {
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			IProject project = file.getProject();
			List resources = WorkbenchUtilitiesPlugin
					.findResourcesInProjectByNameAndExtensions(project, file
							.getName()
							.substring(0, file.getName().length() - 5),
							new String[] { "html" }, false);
			if (resources == null || resources.size() != 1) {
				return;
			}
			IResource wodResource = (IResource) resources.get(0);
			if (wodResource.getType() != IResource.FILE) {
				return;
			}
			WebBrowserEditor webEditor = new WebBrowserEditor();
			FileEditorInput fileEditorInput = new FileEditorInput(
					(IFile) wodResource);
			int index = addPage(webEditor, fileEditorInput);
			setPageText(index, "Preview");
			editors.add(webEditor);
			webEditorIndex = index;
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		createPage3();
		createPage4();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		int pageCount = this.getPageCount();
		for (int i = 0; i < pageCount; i++) {
			//Save all except preview
			if(!(webEditorIndex > 0 && webEditorIndex == i)) {
				getEditor(i).doSave(monitor);
			}
		}
	}

	/**
	 * Do nothing.
	 */
	public void doSaveAs() {
		return;
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		
		IFile file = ((FileEditorInput) editorInput).getFile();
		IProject project = file.getProject();
		String fileName = file
		.getName()
		.substring(0, file.getName().length() - 5);
		List resources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "wo" }, false);
		if (resources == null || resources.size() != 1) {
			WorkbenchUtilitiesPlugin.open(file, false, JavaUI.ID_CU_EDITOR);
			return;
		}
		super.init(site, editorInput);
		this.setPartName(fileName);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
	}


	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		/*if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}*/
	}
}

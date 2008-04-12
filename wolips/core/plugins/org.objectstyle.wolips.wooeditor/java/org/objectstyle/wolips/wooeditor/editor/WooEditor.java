/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 The ObjectStyle Group,
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

package org.objectstyle.wolips.wooeditor.editor;

import java.io.ByteArrayInputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.baseforplugins.util.CharSetUtils;
import org.objectstyle.wolips.wodclipse.core.woo.WooModel;
import org.objectstyle.wolips.wooeditor.WooeditorPlugin;

public class WooEditor extends FormEditor {

	private WooModel model;

	private TextEditor myTextEditor;

	private DisplayGroupPage myDisplayGroupPage;
	
	private IResourceChangeListener resourceChangeListener;

	public WooEditor() {
		super();
		myDisplayGroupPage = new DisplayGroupPage(this, "Display Groups");
	}
	
	@Override
	public boolean isDirty() {
	  return (model != null && model.isDirty()) || super.isDirty();
	}

	@Override
  protected FormToolkit createToolkit(final Display display) {
		return new FormToolkit(WooeditorPlugin.getDefault().getFormColors(
				display));
	}

	@Override
  protected void addPages() {
		try {
			addPage(myDisplayGroupPage);
			myTextEditor = new TextEditor();
			
			IEditorInput input = this.getEditorInput();
			IFile file = ((FileEditorInput) input).getFile();
			if (!file.exists()) {
				try {
					FileEditorInput fileInput = (FileEditorInput)input;
					IFileStore fileStore = EFS.getStore(fileInput.getURI());
					input = new NonExistingFileEditorInput(fileStore, input.getName());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int index = addPage(myTextEditor, input);
			setPageText(index, "Source");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    CTabFolder ctf = (CTabFolder)getContainer();
    ctf.setBorderVisible(false);
    ctf.setTabPosition(SWT.TOP);
    if (getPageCount() <= 1) {
      ctf.setTabHeight(0);
    }
	}

	@Override
  public void doSave(final IProgressMonitor monitor) {
		try {
			if (myTextEditor.isDirty()
					&& (getActivePage() == 1 || !model.isDirty())) {
				IFile file = ((FileEditorInput)getEditorInput()).getFile();
				if (!file.exists()) {
					IEditorInput input = this.getEditorInput();
					((FileEditorInput)input).getFile().create(
							new ByteArrayInputStream(model.toString().getBytes()),
							true, null);
					myTextEditor.setInput(input);
				}
				
				// XXX : Should validate model before save
				myTextEditor.doSave(monitor);
				try {
					model.doRevertToSaved();
				} catch (Throwable e) {
				}
				myDisplayGroupPage.refresh();
			} else {
				model.doSave();
				myTextEditor.doRevertToSaved();
			}
			this.editorDirtyStateChanged();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
  public void doSaveAs() {
		throw new UnsupportedOperationException("doSaveAs");
	}

	@Override
  public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
  public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		
		this.getSite().getSelectionProvider().setSelection(new ISelection() {
			public boolean isEmpty() {
				return true;
			}
		});
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		resourceChangeListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				if (event.getDelta() == null || model == null) return;
				IResourceDelta woComponentDelta = event.getDelta().findMember(
						((FileEditorInput)input).getFile().getFullPath()
						.removeLastSegments(1).removeTrailingSeparator());
				if (woComponentDelta == null) 
					return;
				
				if (woComponentDelta.getKind() != IResourceDelta.CHANGED
						|| (woComponentDelta.getFlags() & IResourceDelta.ENCODING) == 0) {
					return;
				}
				
				final IFolder folder = (IFolder) woComponentDelta.getResource();

				try {
					model.setEncoding(folder.getDefaultCharset());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		};
		workspace.addResourceChangeListener(resourceChangeListener);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(resourceChangeListener);
	}
	
	public WooModel getModel() {
		if (model == null) {
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			if (file.exists()) {
				model = new WooModel(file);
				model.parseModel();
			} else {
				model = new WooModel(this.getEditorInput());
				model.parseModel();
				model.setEncoding(getComponentCharset());
			}
		}
		return model;
	}

	public void dropModel() {
		model = null;
	}

	public void activateFirstPage() {
		this.setActivePage(0);
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 0 && myTextEditor.isDirty()) {
			String editorText = myTextEditor.getDocumentProvider().getDocument(
					myTextEditor.getEditorInput()).get();
			try {
				if (editorText != null && !editorText.equals(model.toString())) {
					getModel().loadModelFromStream(
							new ByteArrayInputStream(editorText.getBytes()));
					getModel().parseModel();
					myDisplayGroupPage.refresh();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else if (newPageIndex == 1 && getModel().isDirty()) {
			String modelText = model.toString();
			if (modelText != null) {
				myTextEditor.getDocumentProvider().getDocument(
						myTextEditor.getEditorInput()).set(modelText);
			}
		}
	}

	public void setModel(WooModel model) {
		this.model = model;
	}
	
	private String getComponentCharset() {
		String encoding = WooModel.DEFAULT_ENCODING;
		IEditorInput input = this.getEditorInput();
		if (input == null || !(input instanceof IFileEditorInput)) {
			return encoding;
		}
		IFile file = ((IFileEditorInput)input).getFile();
		try {
			encoding = file.getParent().getDefaultCharset();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		encoding = CharSetUtils.encodingNameFromObjectiveC(encoding);
		return encoding;
	}
}

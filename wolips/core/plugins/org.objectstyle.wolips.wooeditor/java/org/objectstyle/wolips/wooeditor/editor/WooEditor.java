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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.wooeditor.WooeditorPlugin;
import org.objectstyle.wolips.wooeditor.model.WooModel;
import org.objectstyle.wolips.wooeditor.model.WooModelException;
import org.objectstyle.wolips.wooeditor.plisteditor.PlistEditor;

public class WooEditor extends FormEditor {

	private WooModel model;

	private TextEditor myTextEditor;

	private DisplayGroupPage myDisplayGroupPage;

	public WooEditor() {
		super();
		myDisplayGroupPage = new DisplayGroupPage(this, "Display Groups");
	}

	protected FormToolkit createToolkit(final Display display) {
		return new FormToolkit(WooeditorPlugin.getDefault().getFormColors(
				display));
	}

	protected void addPages() {
		try {
			addPage(myDisplayGroupPage);
			myTextEditor = new PlistEditor();
			int index = addPage(myTextEditor, this.getEditorInput());
			setPageText(index, "Source");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doSave(final IProgressMonitor monitor) {
		try {
			if (myTextEditor.isDirty()
					&& (getActivePage() == 1 || !model.isDirty())) {
				myTextEditor.doSave(monitor);
				model.doRevertToSaved();
				myDisplayGroupPage.refresh();
			} else {
				model.doSave();
				myTextEditor.doRevertToSaved();
			}
			this.editorDirtyStateChanged();
		} catch (WooModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doSaveAs() {
		throw new UnsupportedOperationException("doSaveAs");
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.getSite().getSelectionProvider().setSelection(new ISelection() {
			public boolean isEmpty() {
				return true;
			}
		});
	}

	public WooModel getModel() {
		if (model == null) {
			IFile file = ((FileEditorInput) this.getEditorInput()).getFile();
			if (((FileEditorInput) this.getEditorInput()).getFile().exists()) {
				try {
					model = new WooModel(file);
				} catch (WooModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				model = new WooModel(this.getEditorInput());
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
					myDisplayGroupPage.refresh();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else if (newPageIndex == 1 && getModel().isDirty()) {
			String modelText = model.toString();
			if (modelText != null) {
				myTextEditor.getDocumentProvider().getDocument(
						myTextEditor.getEditorInput()).set(model.toString());
			}
		}
	}

	public void setModel(WooModel model) {
		this.model = model;
	}
}

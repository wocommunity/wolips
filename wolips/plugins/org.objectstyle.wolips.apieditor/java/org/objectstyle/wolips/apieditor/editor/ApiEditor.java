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
package org.objectstyle.wolips.apieditor.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.apieditor.ApieditorPlugin;
import org.objectstyle.wolips.bindings.api.ApiModel;
import org.objectstyle.wolips.bindings.api.ApiModelException;

public class ApiEditor extends FormEditor {

	private ApiModel model;

	public ApiEditor() {
		super();
	}

	protected FormToolkit createToolkit(Display display) {
		return new FormToolkit(ApieditorPlugin.getDefault().getFormColors(display));
	}

	protected void addPages() {
		try {
			if (this.getModel() == null) {
				addPage(new CreatePage(this, "Create"));
			} else {
				addPage(new BindingsPage(this, "Bindings"));
				addPage(new ValidationPage(this, "Validation"));
				addPage(new DisplayPage(this, "Display"));
				addPage(new DeletePage(this, "Delete"));
			}
		} catch (PartInitException e) {
			ApieditorPlugin.getDefault().debug(e);
		} catch (ApiModelException e) {
			ApieditorPlugin.getDefault().debug(e);
		}
	}

	public void doSave(IProgressMonitor monitor) {
		try {
			this.getModel().saveChanges();
			editorDirtyStateChanged();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to save .api file.", t);
		}
	}

	public void doSaveAs() {
		throw new UnsupportedOperationException("doSaveAs");
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		this.getSite().getSelectionProvider().setSelection(new ISelection() {

			public boolean isEmpty() {
				return true;
			}

		});
	}

	public ApiModel getModel() throws ApiModelException {
		if (model == null) {
			if (((FileEditorInput) this.getEditorInput()).getFile().exists()) {
				model = new ApiModel(((FileEditorInput) this.getEditorInput()).getFile().getLocation().toFile());
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

}

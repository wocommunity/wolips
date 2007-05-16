/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eogenerator.ui.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;

public class EOGeneratorEditor extends FormEditor {
	private EOGeneratorModel myModel;

	private EOGeneratorFormPage myFormPage;

	private boolean myModelGroupEditor;

	public EOGeneratorEditor() {
		super();
	}

	protected void setInput(IEditorInput _input) {
		super.setInput(_input);
		try {
			FileEditorInput editorInput = (FileEditorInput) _input;
			IFile eogenFile = editorInput.getFile();
			myModel = EOGeneratorModel.createModelFromFile(eogenFile);
			myModelGroupEditor = "eomodelgroup".equals(eogenFile.getFileExtension());
		} catch (Throwable e) {
			throw new RuntimeException("Failed to read EOGen file.", e);
		}
	}

	protected void addPages() {
		try {
			myFormPage = new EOGeneratorFormPage(this, myModel, myModelGroupEditor);
			addPage(myFormPage);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating form pages.", null, e.getStatus());
		}
	}

	public void doSave(IProgressMonitor _monitor) {
		try {
			FileEditorInput editorInput = (FileEditorInput) getEditorInput();
			myModel.writeToFile(editorInput.getFile(), _monitor);
			editorDirtyStateChanged();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to write EOGen file.", e);
		}
	}

	public void doSaveAs() {
		// do nothing
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public boolean isDirty() {
		return myModel.isDirty() || super.isDirty();
	}

}

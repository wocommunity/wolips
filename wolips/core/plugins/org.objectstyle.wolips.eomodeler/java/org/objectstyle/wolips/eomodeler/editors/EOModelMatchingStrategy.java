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
package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;

public class EOModelMatchingStrategy implements IEditorMatchingStrategy {
	public EOModelMatchingStrategy() {
		System.out.println("EOModelMatchingStrategy.EOModelMatchingStrategy: b");
	}
	
	public boolean matches(IEditorReference _editorRef, IEditorInput _input) {
		boolean matches = false;
		String editorId = _editorRef.getId();
		if (editorId == null) {
			matches = false;
		} else if (!editorId.equals(EOModelEditor.EOMODEL_EDITOR_ID)) {
			matches = false;
		} else if (_input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) _input).getFile();
			IContainer container = file.getParent();
			if ("eomodeld".equals(container.getFileExtension())) {
				IEditorPart editorPart = _editorRef.getEditor(true);
				if (editorPart instanceof EOModelEditor) {
					EOModelEditor eomodelEditorPart = (EOModelEditor) editorPart;
					IFileEditorInput existingEditorInput = (IFileEditorInput) eomodelEditorPart.getEditorInput();
					IContainer existingEOModelFolder = existingEditorInput.getFile().getParent();
					IFileEditorInput possibleEditorInput = (IFileEditorInput) _input;
					IFile possibleEditorFile = possibleEditorInput.getFile();
					IContainer possibleEOModelFolder = possibleEditorFile.getParent();
					matches = existingEOModelFolder.equals(possibleEOModelFolder);
					if ("plist".equals(possibleEditorFile.getFileExtension())) {
						String entityName = possibleEditorFile.getName();
						entityName = entityName.substring(0, entityName.indexOf('.'));
						EOModel eoModel = eomodelEditorPart.getModel();
						if (eoModel != null) {
							EOEntity entity = eoModel.getEntityNamed(entityName);
							eomodelEditorPart.setSelectedEntity(entity);
						}
					}
				}
			}
		}
		return matches;
	}
}

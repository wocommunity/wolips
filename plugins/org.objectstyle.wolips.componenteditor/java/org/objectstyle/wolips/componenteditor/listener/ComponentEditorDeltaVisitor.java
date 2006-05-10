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
package org.objectstyle.wolips.componenteditor.listener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.ui.ide.IDE;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.components.input.ComponentEditorInput;

/**
 * @author ulrich
 */
public class ComponentEditorDeltaVisitor implements IResourceDeltaVisitor {

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (resource.getType() == IResource.FOLDER) {
			if ("framework".equals(resource.getFileExtension())
					|| "woa".equals(resource.getFileExtension())
					|| "build".equals(resource.getName())
					|| "dist".equals(resource.getName())) {
				return false;
			}
		}
		if (resource.getType() == IResource.FILE) {
			if(!resource.isAccessible()) {
				return false;
			}
			IFile file = (IFile) resource;
			QualifiedName qualifiedName = new QualifiedName("org.objectstyle.wolips.componenteditor.listener",
					"touched");
			String property = file.getPersistentProperty(qualifiedName);
			if (property != null) {
				return false;
			}

			String extension = file.getFileExtension();
			ComponentEditorInput input = null;
			if (extension == null) {
				return false;
			}
			if (extension.equals("java")) {
				try {
					input = ComponentEditorInput.createWithDotJava(file);
				} catch (CoreException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
			}
			if (extension.equals("html")) {
				try {
					input = ComponentEditorInput.createWithDotHtml(file);
				} catch (CoreException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
			}
			if (extension.equals("wod")) {
				try {
					input = ComponentEditorInput.createWithDotWod(file);
				} catch (CoreException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
			}
			if (extension.equals("api")) {
				try {
					input = ComponentEditorInput.createWithDotApi(file);
				} catch (CoreException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
			}
			if (extension.equals("woo")) {
				try {
					input = ComponentEditorInput.createWithDotWoo(file);
				} catch (CoreException e) {
					ComponenteditorPlugin.getDefault().log(e);
				}
			}
			if (input != null) {
				IDE.setDefaultEditor(file,
						ComponenteditorPlugin.ComponentEditorID);
//				file.setPersistentProperty(qualifiedName, "true");
				return false;
			}
		}
		return true;
	}

}

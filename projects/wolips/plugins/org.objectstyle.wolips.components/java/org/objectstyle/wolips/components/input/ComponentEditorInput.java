/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.components.input;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;
import org.objectstyle.wolips.apieditor.ApieditorPlugin;
import org.objectstyle.wolips.component.ComponentPlugin;
import org.objectstyle.wolips.htmleditor.HtmleditorPlugin;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;

public class ComponentEditorInput extends MultiEditorInput {

	private boolean createdFromDotJava = false;

	private boolean createdFromDotHtml = false;

	private boolean createdFromDotWod = false;

	private boolean createdFromDotApi = false;

	private boolean createdFromDotWoo = false;

	private LocalizedComponentsLocateResult localizedComponentsLocateResult;

	public boolean isCreatedFromDotApi() {
		return createdFromDotApi;
	}

	public boolean isCreatedFromDotHtml() {
		return createdFromDotHtml;
	}

	public boolean isCreatedFromDotJava() {
		return createdFromDotJava;
	}

	public boolean isCreatedFromDotWod() {
		return createdFromDotWod;
	}

	public boolean isCreatedFromDotWoo() {
		return createdFromDotWoo;
	}

	public ComponentEditorInput(String[] editorIDs, IEditorInput[] innerEditors) {
		super(editorIDs, innerEditors);
	}

	private static ComponentEditorInput create(
			LocalizedComponentsLocateResult localizedComponentsLocateResult)
			throws CoreException {
		String ids[] = null;
		IEditorInput allInput[] = null;
		if (localizedComponentsLocateResult.getDotApi() == null) {
			ids = new String[3];
			allInput = new IEditorInput[3];
		} else {
			ids = new String[4];
			allInput = new IEditorInput[4];
		}
		ids[0] = JavaUI.ID_CU_EDITOR;
		allInput[0] = new FileEditorInput(localizedComponentsLocateResult
				.getDotJava());
		ids[1] = HtmleditorPlugin.HTMLEditorID;
		IFolder folder = localizedComponentsLocateResult.getComponents()[0];
		IFile htmlFile = LocalizedComponentsLocateResult.getHtml(folder);
		IFile wodFile = LocalizedComponentsLocateResult.getWod(folder);
		allInput[1] = new FileEditorInput(htmlFile);
		ids[2] = WodclipsePlugin.WodEditorID;
		allInput[2] = new FileEditorInput(wodFile);
		if (localizedComponentsLocateResult.getDotApi() != null) {
			ids[3] = ApieditorPlugin.ApiEditorID;
			allInput[3] = new FileEditorInput(localizedComponentsLocateResult
					.getDotApi());
		}
		ComponentEditorInput input = new ComponentEditorInput(ids, allInput);
		input.localizedComponentsLocateResult = localizedComponentsLocateResult;
		return input;
	}

	/*
	 * may return null
	 */
	private static ComponentEditorInput create(IProject project, String fileName)
			throws CoreException {
		ComponentLocateScope componentLocateScope = new ComponentLocateScope(
				project, fileName);
		LocalizedComponentsLocateResult localizedComponentsLocateResult = new LocalizedComponentsLocateResult();
		Locate locate = new Locate(componentLocateScope,
				localizedComponentsLocateResult);
		try {
			locate.locate();
		} catch (CoreException e) {
			ComponentPlugin.getDefault().log(e);
			return null;
		} catch (LocateException e) {
			ComponentPlugin.getDefault().log(e);
			return null;
		}
		if (localizedComponentsLocateResult.getDotJava() == null
				|| localizedComponentsLocateResult.getComponents() == null
				|| localizedComponentsLocateResult.getComponents().length == 0) {
			return null;
		}
		ComponentEditorInput input = create(localizedComponentsLocateResult);
		return input;
	}

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotJava(IFile file)
			throws CoreException {
		IProject project = file.getProject();
		String fileName = file.getName().substring(0,
				file.getName().length() - 5);
		ComponentEditorInput input = create(project, fileName);
		if (input != null) {
			input.createdFromDotJava = true;
		}
		return input;
	}

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotHtml(IFile file)
			throws CoreException {
		IProject project = file.getProject();
		String fileName = file.getName().substring(0,
				file.getName().length() - 5);
		ComponentEditorInput input = create(project, fileName);
		if (input != null) {
			input.createdFromDotHtml = true;
		}
		return input;
	}

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotWod(IFile file)
			throws CoreException {
		IProject project = file.getProject();
		String fileName = file.getName().substring(0,
				file.getName().length() - 4);
		ComponentEditorInput input = create(project, fileName);
		if (input != null) {
			input.createdFromDotWod = true;
		}
		return input;
	}

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotApi(IFile file)
			throws CoreException {
		IProject project = file.getProject();
		String fileName = file.getName().substring(0,
				file.getName().length() - 4);
		ComponentEditorInput input = create(project, fileName);
		input.createdFromDotApi = true;
		return input;
	}

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotWoo(IFile file)
			throws CoreException {
		IProject project = file.getProject();
		String fileName = file.getName().substring(0,
				file.getName().length() - 4);
		ComponentEditorInput input = create(project, fileName);
		if (input != null) {
			input.createdFromDotWoo = true;
		}
		return input;
	}

	public LocalizedComponentsLocateResult getLocalizedComponentsLocateResult() {
		return localizedComponentsLocateResult;
	}
}

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
package org.objectstyle.wolips.wodclipse.mpe;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.api.ApiEditorInput;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class ComponentEditorInput extends MultiEditorInput {

	private boolean createdFromDotJava = false;

	private boolean createdFromDotHtml = false;

	private boolean createdFromDotWod = false;

	private boolean createdFromDotApi = false;

	private boolean createdFromDotWoo = false;

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

	/*
	 * may return null
	 */
	public static ComponentEditorInput createWithDotJava(IFile file) {
		return createWithDotJava(file, true, false, false, false, false);
	}

	/*
	 * may return null
	 */
	private static ComponentEditorInput createWithDotJava(IFile file,
			boolean createFromDotJava, boolean createFromDotHtml,
			boolean createFromDotWod, boolean createFromDotApi,
			boolean createFromDotWoo) {
		IProject project = file.getProject();
		String ids[] = null;
		IEditorInput allInput[] = null;
		String fileName = file.getName().substring(0,
				file.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "html" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		List wodResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "wod" }, false);

		if (wodResources == null || wodResources.size() != 1) {
			return null;
		}
		List apiResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "api" }, false);

		if (apiResources == null || apiResources.size() != 1) {
			ids = new String[3];
			allInput = new IEditorInput[3];
		} else {
			ids = new String[4];
			allInput = new IEditorInput[4];
		}
		ids[0] = JavaUI.ID_CU_EDITOR;
		allInput[0] = new FileEditorInput(file);
		ids[1] = WodclipsePlugin.HTMLEditorID;
		allInput[1] = new FileEditorInput(((IFile) htmlResources.get(0)));
		ids[2] = WodclipsePlugin.WODEditorID;
		allInput[2] = new FileEditorInput(((IFile) wodResources.get(0)));
		if (apiResources != null && apiResources.size() == 1) {
			ids[3] = WodclipsePlugin.ApiEditorID;
			allInput[3] = new ApiEditorInput(((IFile) apiResources.get(0)));
		}
		ComponentEditorInput input = new ComponentEditorInput(ids, allInput);
		input.createdFromDotJava = createFromDotJava;
		input.createdFromDotHtml = createFromDotHtml;
		input.createdFromDotWod = createFromDotWod;
		input.createdFromDotApi = createFromDotApi;
		input.createdFromDotWoo = createFromDotWoo;

		return input;
	}

	public static ComponentEditorInput createWithDotHtml(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 5);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						javaFileName, new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile) javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						htmlFileName, new String[] { "html" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile) htmlResources.get(0);
		if (htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile, false, true, false, false, false);
		}
		return null;
	}

	public static ComponentEditorInput createWithDotWod(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 4);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						javaFileName, new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile) javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						htmlFileName, new String[] { "wod" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile) htmlResources.get(0);
		if (htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile, false, false, true, false, false);
		}
		return null;
	}

	public static ComponentEditorInput createWithDotApi(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 4);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						javaFileName, new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile) javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						htmlFileName, new String[] { "api" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile) htmlResources.get(0);
		if (htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile, false, false, false, true, false);
		}
		return null;
	}

	public static ComponentEditorInput createWithDotWoo(IFile file) {
		IProject project = file.getProject();
		String javaFileName = file.getName().substring(0,
				file.getName().length() - 4);
		List javaResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						javaFileName, new String[] { "java" }, false);
		if (javaResources == null || javaResources.size() != 1) {
			return null;
		}
		IFile javaFile = (IFile) javaResources.get(0);
		String htmlFileName = javaFile.getName().substring(0,
				javaFile.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project,
						htmlFileName, new String[] { "woo" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			return null;
		}
		IFile htmlFile = (IFile) htmlResources.get(0);
		if (htmlFile.getLocation().equals(file.getLocation())) {
			return createWithDotJava(javaFile, false, false, false, false, true);
		}
		return null;
	}
}

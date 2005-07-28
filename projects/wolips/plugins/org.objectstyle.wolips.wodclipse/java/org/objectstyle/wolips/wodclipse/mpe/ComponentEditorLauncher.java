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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditorInput;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author uli
 */
public class ComponentEditorLauncher implements IEditorLauncher {

	/**
	 * Open the wocomponent editor with the given file resource.
	 * 
	 * @param file
	 *            the file resource
	 */
	public void open(IFile file) {
		IProject project = file.getProject();
		String ids[] = null;
		IEditorInput allInput[] = null;
		String fileName = file.getName().substring(0,
				file.getName().length() - 5);
		List htmlResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "html" }, false);
		if (htmlResources == null || htmlResources.size() != 1) {
			WorkbenchUtilitiesPlugin.open(file, JavaUI.ID_CU_EDITOR);
			return;
		}
		List wodResources = WorkbenchUtilitiesPlugin
				.findResourcesInProjectByNameAndExtensions(project, fileName,
						new String[] { "wod" }, false);

		if (wodResources == null || wodResources.size() != 1) {
			WorkbenchUtilitiesPlugin.open(file, JavaUI.ID_CU_EDITOR);
			return;
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
		if (isEditorInstalled(WodclipsePlugin.HTMLEditorID)) {
			ids[1] = WodclipsePlugin.HTMLEditorID;
		} else {
			ids[1] = "org.eclipse.ui.DefaultTextEditor";
		}
		allInput[1] = new FileEditorInput(((IFile) htmlResources.get(0)));
		ids[2] = WodclipsePlugin.WODEditorID;
		allInput[2] = new FileEditorInput(((IFile) wodResources.get(0)));
		if (apiResources != null && apiResources.size() == 1) {
			ids[3] = "org.eclipse.ui.DefaultTextEditor";
			allInput[3] = new FileEditorInput(((IFile) apiResources.get(0)));
		}
		MultiEditorInput input = new MultiEditorInput(ids, allInput);
		try {
			WorkbenchUtilitiesPlugin.getActiveWorkbenchWindow().getActivePage()
					.openEditor(input, WodclipsePlugin.ComponentEditorID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorLauncher#open(org.eclipse.core.runtime.IPath)
	 */
	public void open(IPath file) {
		IFile input = WorkbenchUtilitiesPlugin.getWorkspace().getRoot()
				.getFileForLocation(file);
		this.open(input);
	}

	/**
	 * @return true if the plugin with the editor is installed
	 */
	private boolean isEditorInstalled(String editorID) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint("org.eclipse.ui.editors");
		IExtension[] extensions = extensionPoint.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configurationElements = extensions[i]
					.getConfigurationElements();
			for (int j = 0; j < configurationElements.length; j++) {
				IConfigurationElement configurationElement = configurationElements[j];
				String id = configurationElement.getAttribute("id");
				if (editorID.equals(id)) {
					return true;
				}
			}
		}
		return false;
	}
}

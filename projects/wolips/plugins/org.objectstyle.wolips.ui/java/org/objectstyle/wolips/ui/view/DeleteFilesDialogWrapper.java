/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 The ObjectStyle Group
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

package org.objectstyle.wolips.ui.view;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.dialogs.FileSystemElement;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;

/**
 * Wrapper of FileSelectionDialog to select jars from given
 * file root object. The FileSelectionDialog displays only jars
 * not already added to classpath.
 * <br>
 * @author uli
 */
public class DeleteFilesDialogWrapper {

	private FileSelectionDialog dialog;
	private IWorkbenchPart part;

	/**
	 * Method DeleteFilesDialogWrapper.
	 * @param part
	 * @param resourcesToDelete
	 * @param project
	 */
	public DeleteFilesDialogWrapper(
		IWorkbenchPart part,
		ArrayList resourcesToDelete,
		IProject project) {
		super();
		this.part = part;
		//MessageDialog.openInformation(this.part.getSite().getShell(), "Error", "Selection is not a folder!");
		FileSystemElement elem = this.element(resourcesToDelete, project);

		dialog =
			new FileSelectionDialog(
				part.getSite().getShell(),
				elem,
				Messages.getString("WOFrameworkDialogWrapper.message"));

		dialog.setTitle(Messages.getString("WOFrameworkDialogWrapper.title"));
	}
	/**
	 * Method element.
	 * @param aList
	 * @param aProject
	 * @return FileSystemElement
	 */
	private FileSystemElement element(ArrayList aList, IProject aProject) {
		FileSystemElement parentElement =
			new FileSystemElement(aProject.getName(), null, false);
		parentElement.setFileSystemObject(aProject);
		for (int i = 0; i < aList.size(); i++) {
			IResource resource = (IResource) aList.get(i);
			FileSystemElement child =
				new FileSystemElement(resource.getName(), parentElement, (resource.getType() == IResource.FOLDER));
			child.setFileSystemObject(resource);
		}
		return parentElement;
	}
	/**
	 * Method executeDialog.
	 */
	public void executeDialog() {
		Object[] result = null;
		dialog.open();

		if (dialog.getReturnCode() != Window.OK)
			return;
		result = dialog.getResult();
		try {
		//FileSystemElement currentFileElement;
		for (int i = 0; i < result.length; i++) {
			//currentFileElement = (FileSystemElement) result[i];
			//IResource resource = (IResource) currentFileElement.getFileSystemObject();
			//resource.delete(true, null);
		}
	} catch (Exception e) {
			WOLipsPlugin.handleException(part.getSite().getShell(), e, null);
		}

	}

}

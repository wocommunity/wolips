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
package org.objectstyle.wolips.core.resources.internal.types.folder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.internal.types.AbstractResourceAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IFolderAdapter;

public abstract class AbstractFolderAdapter extends AbstractResourceAdapter
		implements IFolderAdapter {

	private IFolder folder;

	public AbstractFolderAdapter(IFolder folder) {
		super(folder);
		this.folder = folder;
	}

	public IFolder getUnderlyingFolder() {
		return this.folder;
	}

	/**
	 * create a Folder recursively
	 * 
	 * @param f
	 *            the Folder to be created
	 * @param m
	 *            a ProgressMonitor
	 * @throws CoreException
	 */
	private void createFolder(IFolder f, IProgressMonitor m)
			throws CoreException {
		if (f.exists()) {
			return;
		}
		IContainer parent = f.getParent();
		if (!f.getParent().exists()) {
			if (parent instanceof IFolder) {
				createFolder((IFolder) parent, m);
			}
		}
		f.create(true, true, m);
	}

	public IResource copy(IResource resource) throws CoreException {
		IPath projectRelativePath = resource.getProjectRelativePath();
		IPath thisPath = this.getUnderlyingFolder().getFullPath();
		IPath destination = thisPath.append(projectRelativePath);
		IResource returnValue = null;
		if (resource.getType() == IResource.FILE) {
			returnValue = ResourcesPlugin.getWorkspace().getRoot().getFile(
					destination);
			this.createFolder(((IFolder) returnValue.getParent()), null);
		} else {
			returnValue = ResourcesPlugin.getWorkspace().getRoot().getFolder(
					destination);

			((IFolder) returnValue).create(true, true, null);
			this.createFolder((IFolder) returnValue, null);
		}
		resource.copy(destination, true, null);
		return ResourcesPlugin.getWorkspace().getRoot().findMember(destination);
	}

	public void deleteCopy(IResource resource) throws CoreException {
		IPath projectRelativePath = resource.getProjectRelativePath();
		IPath thisPath = this.getUnderlyingFolder().getFullPath();
		IPath destination = thisPath.append(projectRelativePath);
		IResource deleteResource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(destination);
		if (deleteResource != null && deleteResource.exists())
			deleteResource.delete(true, null);
	}

	public void markAsDerivated(IProgressMonitor monitor) {
		try {
			this.markAsDerivated(this.getUnderlyingFolder(), monitor);
		} catch (CoreException e) {

			CorePlugin.getDefault().log(e);
		}
	}

	public void markAsDerivated(IFolder folderToMarkAsDerivated, IProgressMonitor monitor)
			throws CoreException {
		if(!folderToMarkAsDerivated.exists()) {
			return;
		}
		folderToMarkAsDerivated.setDerived(true);
		IResource[] members = folderToMarkAsDerivated.members();
		for (int i = 0; i < members.length; i++) {
			IResource member = members[i];
			if (member instanceof IFolder) {
				this.markAsDerivated((IFolder) member, monitor);
			} else {
				if(member.exists()) {
					member.setDerived(true);
				}
			}
		}
	}
}
/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
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

package org.objectstyle.wolips.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.refactoring.changes.CopyResourceChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.DeleteFileChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.DeleteFolderChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;

/**
 * Plugs into the refactoring process and renames a WOComponent by moving the
 * .api, creating a new .wo, copying the .html, .wod, .woo over and deleting the
 * old .wo
 * 
 * @author Mike Schrag original version
 * @author ak wolips integration
 */

public class WOComponentRenameParticipant extends RenameParticipant {
	private IType mySourceType;

	public WOComponentRenameParticipant() {
		super();
	}

	protected boolean initialize(Object _element) {
		boolean initialized = false;
		try {
			if (_element instanceof IType) {
				mySourceType = (IType) _element;
				initialized = PluginUtils.isOfType(mySourceType, "com.webobjects.appserver.WOComponent");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			initialized = false;
		}
		return initialized;
	}

	public String getName() {
		return "Rename WOComponent Files";
	}

	public RefactoringStatus checkConditions(IProgressMonitor _pm, CheckConditionsContext _context) throws OperationCanceledException {
		RefactoringStatus refactoringStatus = new RefactoringStatus();
		return refactoringStatus;
	}

	public Change createChange(IProgressMonitor _pm) throws CoreException, OperationCanceledException {
		Change change = null;
		if (mySourceType != null) {
			RenameArguments arguments = getArguments();
			String oldName = mySourceType.getElementName();
			String newName = arguments.getNewName();
			if (!oldName.equals(newName)) {
				//IProject project = mySourceType.getJavaProject().getProject();
				LocalizedComponentsLocateResult existingLocalizedComponentsLocateResult = null;
				try {
					IResource resource = mySourceType.getResource();
					existingLocalizedComponentsLocateResult = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(resource);
					if(resource == null) {
						throw new CoreException(new Status(IStatus.ERROR, RefactoringPlugin.getDefault().getBundleID(), IStatus.ERROR, "Could not locate component resource is null: " + mySourceType, null)); //$NON-NLS-1$
						}
				} catch (LocateException e) {
					throw new CoreException(new Status(IStatus.ERROR, RefactoringPlugin.getDefault().getBundleID(), IStatus.ERROR, "Could not locate component: " + mySourceType, null)); //$NON-NLS-1$
					}
				IFolder oldWoFolder = (IFolder)existingLocalizedComponentsLocateResult.getFirstWodFile().getParent();
				IFile oldApiFile = existingLocalizedComponentsLocateResult.getDotApi();
				if (oldWoFolder != null || oldApiFile != null) {
					CompositeChange compositeChange = new CompositeChange("Rename WOComponent Files");
					if (oldApiFile != null) {
						// compositeChange.add(new
						// RenameResourceChange(oldApiFile, newName + ".api"));
						CompositeChange renameApiFileChange = new CompositeChange("Rename " + oldApiFile.getName() + ".");
						renameApiFileChange.add(new CopyResourceChange(oldApiFile, oldApiFile.getParent(), new FixedNewNameQuery(newName + ".api")));
						renameApiFileChange.add(new DeleteFileChange(oldApiFile, true));
						compositeChange.add(renameApiFileChange);
					}
					if (oldWoFolder != null) {
						IFolder newWoFolder = oldWoFolder.getParent().getFolder(new Path(newName + ".wo"));
						CompositeChange renameWoFolderChange = new CompositeChange("Rename " + oldWoFolder.getName() + ".");

						// compositeChange.add(createRenameChange(woFolder,
						// newName + ".wo"));
						renameWoFolderChange.add(new CreateFolderChange(newWoFolder));
						String[] renameExtensions = { ".html", ".wod", ".woo" };
						for (int i = 0; i < renameExtensions.length; i++) {
							IFile woFile = oldWoFolder.getFile(oldName + renameExtensions[i]);
							if (woFile.exists()) {
								// compositeChange.add(new
								// RenameResourceChange(woFile, newName +
								// renameExtensions[i]));
								CompositeChange renameWoFileChange = new CompositeChange("Rename " + woFile.getName() + ".");
								renameWoFileChange.add(new CopyResourceChange(woFile, newWoFolder, new FixedNewNameQuery(newName + renameExtensions[i])));
								renameWoFileChange.add(new DeleteFileChange(woFile, true));
								renameWoFolderChange.add(renameWoFileChange);
							}
						}
						renameWoFolderChange.add(new DeleteFolderChange(oldWoFolder, true));
						compositeChange.add(renameWoFolderChange);
					}
					change = compositeChange;
				}
			}
		}
		return change;
	}
}

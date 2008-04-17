/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2007 The ObjectStyle Group 
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
package org.objectstyle.wolips.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.changes.RenameCompilationUnitChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.RenameResourceChange;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;

public final class RenameWOComponentChange extends CompositeChange {

	public static IPath renamedResourcePath(IPath path, String newName) {
		return path.removeLastSegments(1).append(newName);
	}

	private final String _comment;

	private final RefactoringDescriptor _descriptor;

	private final String _newName;

	private final IPath _resourcePath;

	private final boolean _renameClass;
	
	private final LocatePlugin locate = LocatePlugin.getDefault();
	
	private ICompilationUnit _compilationUnit;
	
	private IFile _groovyFile;

	private RenameWOComponentChange(RefactoringDescriptor descriptor, IPath resourcePath, String newName, String comment, boolean renameClass) {
		super("Rename WOComponent");
		_descriptor = descriptor;
		_resourcePath = resourcePath;
		_newName = newName;
		_comment = comment;
		_renameClass = renameClass;
		try {
			createChanges();
		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public RenameWOComponentChange(RefactoringDescriptor descriptor, IResource resource, String newName, String comment, boolean renameClass) {
		this(descriptor, resource.getFullPath(), newName, comment, renameClass);
	}

	public RenameWOComponentChange(RefactoringDescriptor descriptor, IResource resource, String newName, String comment) {
		this(descriptor, resource.getFullPath(), newName, comment, true);
	}

	public ChangeDescriptor getDescriptor() {
		if (_descriptor != null)
			return new RefactoringChangeDescriptor(_descriptor);
		return super.getDescriptor();
	}

	public Object getModifiedElement() {
		return getResource();
	}

	public String getName() {
		return "Rename WOComponent " + getOldName() + " to " + getNewName();
	}

	public String getNewName() {
		return locate.fileNameWithoutExtension(_newName);
	}

	public String getOldName() {
		return locate.fileNameWithoutExtension(_resourcePath.lastSegment());
	}

	private IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(_resourcePath);
	}

	public RefactoringStatus isValid(final IProgressMonitor pm) throws CoreException {
		IResource resource = getResource();
		if (resource == null || !resource.exists()) {
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.RenameResourceChange_does_not_exist, _resourcePath.toString()));
		}
		return new RefactoringStatus();
	}

	
	public Change perform(final IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask(RefactoringCoreMessages.RenameResourceChange_rename_resource, 1);
			IPath newPath = renamedResourcePath(_resourcePath, _newName);
			String oldName = _resourcePath.lastSegment();
			super.perform(pm);
			return new RenameWOComponentChange(null, newPath, oldName, _comment, _renameClass);
		} finally {
			pm.done();
		}
	}
	
	private void createChanges() throws CoreException, OperationCanceledException {
		IFile oldApiFile = null;
		IFolder[] oldWoFolders = new IFolder[0];
	
		try {
			LocalizedComponentsLocateResult result = 
				LocatePlugin.getDefault().getLocalizedComponentsLocateResult(getResource());
			if (result != null) {
				oldWoFolders = result.getComponents();
				oldApiFile = result.getDotApi();
			}
		} catch (LocateException e) {
			throw new CoreException(new Status(IStatus.ERROR, RefactoringPlugin.getDefault().getBundleID(), IStatus.ERROR, "Could not locate component: " + _resourcePath.lastSegment(), null)); //$NON-NLS-1$
		}
		if (oldApiFile != null || oldWoFolders.length > 0) {
			CompositeChange compositeChange = new CompositeChange("Rename WOComponent Files");
			if (oldApiFile != null) {
				compositeChange.add(new RenameResourceChange(_descriptor, oldApiFile, getNewName() + ".api", "Rename " + oldApiFile.getName() + "."));
			}
			for (int i = 0; i < oldWoFolders.length; i++) {
				IFolder oldWoFolder = oldWoFolders[i];
				if (oldWoFolder == null) {
					continue;
				}
				CompositeChange renameWoFolderChange = new CompositeChange("Rename " + oldWoFolder.getName());

				String[] renameExtensions = { ".html", ".wod", ".woo", ".xml", ".xhtml" };
				for (int j = 0; j < renameExtensions.length; j++) {
					IFile woFile = oldWoFolder.getFile(getOldName() + renameExtensions[j]);
					if (woFile.exists()) {
						renameWoFolderChange.add(new RenameResourceChange(_descriptor, woFile, getNewName() + renameExtensions[j], "Rename " + woFile.getName()));
					}
				}
				renameWoFolderChange.add(new RenameResourceChange(_descriptor, oldWoFolder, getNewName() + ".wo", "Rename " + oldWoFolder.getName()));
				compositeChange.add(renameWoFolderChange);

			}
			add(compositeChange);
			if (_renameClass) {
				if (getCompilationUnit() != null) {
					add(new RenameCompilationUnitChange(getCompilationUnit(), getNewName() + ".java"));
				}
				//if (getGroovyFile() != null) {
				//TODO Refactoring of groovy file would go here if there was a refactoring for it
				//     We could just rename it, but that would not refactor the contents
				//     A refactoring plugin is in development http://sifsstud4.hsr.ch/trac/GroovyRefactoring/wiki
				//}
			}
		}
	}
	
	
	private ICompilationUnit getCompilationUnit() {
		if (_compilationUnit != null) {
			return _compilationUnit;
		}
		try {
			LocalizedComponentsLocateResult results = locate.getLocalizedComponentsLocateResult(getResource());
			if (results != null) {
				IType javaType = results.getDotJavaType();
				if (javaType != null)
					_compilationUnit = javaType.getCompilationUnit();				
			}
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (LocateException e) {
			e.printStackTrace();
		}
		return _compilationUnit;
	}
	
	private IFile getGroovyFile() {
		if (_groovyFile != null) {
			return _groovyFile;
		}
		try {
			LocalizedComponentsLocateResult results = locate.getLocalizedComponentsLocateResult(getResource());
			_groovyFile = results.getDotGroovy();

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (LocateException e) {
			e.printStackTrace();
		}
		return _groovyFile;
	}
}

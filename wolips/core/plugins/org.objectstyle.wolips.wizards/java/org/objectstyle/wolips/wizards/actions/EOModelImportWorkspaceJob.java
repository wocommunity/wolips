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
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.wizards.actions;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Job for importing sets of EOModels into workspace
 *
 */
public class EOModelImportWorkspaceJob extends WorkspaceJob {
	IProject _project;
	String _dstPath;
	HashMap<String, String> _modelPaths;

	/**
	 * @param name
	 * @param paths
	 * @param project
	 */
	public EOModelImportWorkspaceJob(String name, HashMap<String, String> paths, IProject project) {
		super(name);
		_project = project;
		_modelPaths = paths;
		_dstPath = null;
	}

	/**
	 * @param name
	 * @param paths
	 * @param project
	 * @param dstPath
	 */
	public EOModelImportWorkspaceJob(String name, HashMap<String, String> paths, IProject project, String dstPath) {
		super(name);
		_project = project;
		_modelPaths = paths;
		_dstPath = dstPath;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		try {
			EOModelImportSupport.importEOModelsToProject(_modelPaths, _project, _dstPath);
			_project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (final Throwable t) {
			t.printStackTrace();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(new Shell(), "EOModel import failed", t.getMessage());
				}
			});
		}
		return new Status(IStatus.OK, org.objectstyle.wolips.eogenerator.ui.Activator.PLUGIN_ID, IStatus.OK, "Done", null);
	}

}

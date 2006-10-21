/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2006 The ObjectStyle Group
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

package org.objectstyle.wolips.launching.exceptionhandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.launching.LaunchingPlugin;

public abstract class AbstractConsoleHandler implements IExceptionHandler {

	private IConsole currentConsole;

	public AbstractConsoleHandler() {
		super();
	}

	public int lineAppendedToConsole(String line, IConsole console) {
		this.currentConsole = console;
		int linesToSkip = lineAppended(line);
		this.currentConsole = null;
		return linesToSkip;
	}

	public abstract int lineAppended(String line);

	public IConsole getCurrentConsole() {
		return currentConsole;
	}

	public IJavaProject getJavaProject() {
		IJavaProject javaProject = null;
		try {
			javaProject = JavaRuntime.getJavaProject(this.currentConsole.getProcess().getLaunch().getLaunchConfiguration());
		} catch (CoreException e) {
			LaunchingPlugin.getDefault().log(e);
		}
		return javaProject;
	}

	public void selectAndReveal(IFile file, final String errorMessage, final String errorTitle) {
		LaunchingPlugin.getDefault().selectAndReveal(file);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, errorMessage, null);
				ErrorDialog.openError(null, errorTitle, "", status);
			}
		});
	}

	public void selectAndReveal(IFile file, String string, int targetEditorID, final String errorMessage, final String errorTitle) {
		LaunchingPlugin.getDefault().selectAndReveal(file, string, targetEditorID);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, errorMessage, null);
				ErrorDialog.openError(null, errorTitle, "", status);
			}
		});
	}

	public String fileNameWithoutPackage(String fileName) {
		if (fileName == null) {
			return fileName;
		}
		int index = fileName.lastIndexOf('.');
		if (index < 0) {
			return fileName;
		}
		return fileName.substring(index + 1);
	}
}

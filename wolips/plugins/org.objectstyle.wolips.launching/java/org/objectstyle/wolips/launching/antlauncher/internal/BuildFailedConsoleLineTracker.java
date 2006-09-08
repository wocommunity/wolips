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

package org.objectstyle.wolips.launching.antlauncher.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.launching.LaunchingPlugin;

public class BuildFailedConsoleLineTracker implements IConsoleLineTracker {

	public static final String ATTR_BUILD_FAILED_CONSOLE_LINE_TRACKER_ENABLED = "org.objectstyle.wolips.launching.antlauncher.internal.BuildFailedConsoleLineTrackerEnabled";

	private boolean isDefaultAntLauncherConsole;

	private IConsole currentConsole;

	public BuildFailedConsoleLineTracker() {
		super();
	}

	public void init(IConsole console) {
		this.currentConsole = console;
		isDefaultAntLauncherConsole = false;
		try {
			isDefaultAntLauncherConsole = console.getProcess().getLaunch().getLaunchConfiguration().getAttribute(ATTR_BUILD_FAILED_CONSOLE_LINE_TRACKER_ENABLED, false);
		} catch (CoreException e) {
			LaunchingPlugin.getDefault().log(e);
		}

	}

	public void lineAppended(IRegion line) {
		if (!isDefaultAntLauncherConsole) {
			return;
		}
		int offset = line.getOffset();
		int length = line.getLength();
		String text = null;
		try {
			text = currentConsole.getDocument().get(offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (text != null && text.startsWith("BUILD FAILED")) {
			try {
				final String projectName = currentConsole.getProcess().getLaunch().getLaunchConfiguration().getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "UNKNOWN");
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, "Error while building project: " + projectName + " with the ant builder. Take a look into the console for details.\n\n" + "You mave have to enable the logging of the ant output in the WOLips preferences.", null);
						ErrorDialog.openError(null, "Error", "BUILD FAILED", status);
					}
				});
			} catch (CoreException e) {
				LaunchingPlugin.getDefault().log(e);
			}
		}
	}

	public void dispose() {
		this.currentConsole = null;
	}
}

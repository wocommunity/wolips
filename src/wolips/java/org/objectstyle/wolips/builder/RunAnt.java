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

package org.objectstyle.wolips.builder;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.externaltools.model.IExternalToolConstants;
import org.eclipse.ui.externaltools.model.ToolUtil;
import org.objectstyle.wolips.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.plugin.WOLipsPlugin;
import org.objectstyle.wolips.preferences.Preferences;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RunAnt {

	/**
	 * Method asAnt.
	 * @param buildFile
	 * @param monitor
	 * @throws Exception
	 */
	public static void asAnt(String buildFile, IProgressMonitor monitor)
		throws Exception {
		AntRunner runner = new AntRunner();
		runner.setBuildFileLocation(buildFile);
		//runner.setArguments("-Dmessage=Building -verbose");
		monitor.subTask(
			BuildMessages.getString("Build.SubTask.Name") + " " + buildFile);
		runner.run(new SubProgressMonitor(monitor, 1));
	}

	/**
	 * Method asExternalTool.
	 * @param buildFile
	 * @param kind
	 * @param monitor
	 * @throws Exception
	 */
	public static void asExternalTool(
		IFile buildFile,
		int kind,
		IProgressMonitor monitor)
		throws Exception {
		ILaunchConfiguration config = null;
		try {
			config = RunAnt.createDefaultLaunchConfiguration(buildFile);
		} catch (CoreException e) {
			config = null;
			WOLipsPlugin.handleException(
				Display.getCurrent().getActiveShell(),
				e,
				BuildMessages.getString("Build.Exception"));
			return;
		}
		try {
			RunAnt.launch(config, ILaunchManager.RUN_MODE);
		} finally {
			config = null;
		}
		/*if (kind == IncrementalProjectBuilder.AUTO_BUILD)
			config.setAttribute(
				IExternalToolConstants.VAR_BUILD_TYPE,
				IExternalToolConstants.BUILD_TYPE_AUTO);
		if (kind == IncrementalProjectBuilder.FULL_BUILD)
			config.setAttribute(
				IExternalToolConstants.VAR_BUILD_TYPE,
				IExternalToolConstants.BUILD_TYPE_FULL);
		if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD)
			config.setAttribute(
				IExternalToolConstants.VAR_BUILD_TYPE,
				IExternalToolConstants.BUILD_TYPE_INCREMENTAL);
		config.setAttribute(IExternalToolConstants.ATTR_LOCATION, buildFile);*/
	}

	/**
	 * Launches the given launch configuration in the specified mode with a
	 * progress dialog. Reports any exceptions that occurr in an error dilaog.
	 * 
	 * @param configuration the configuration to launch
	 * @param mode launch mode - run or debug
	 */
	public static void launch(
		final ILaunchConfiguration configuration,
		final String mode) {
		ProgressMonitorDialog dialog =
			new ProgressMonitorDialog(DebugUIPlugin.getShell());
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
				try {
					configuration.launch(mode, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			dialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			Throwable t = e;
			if (targetException instanceof CoreException) {
				t = targetException;
			}
			DebugUIPlugin.errorDialog(
				DebugUIPlugin.getShell(),
				"Error",
				"Exception occurred during launch",
				t);
		} catch (InterruptedException e) {
			// cancelled
		}
	}

	/**
	 * Creates and returns a default launch configuration for the given file.
	 * 
	 * @param file
	 * @return default launch configuration
	 */
	protected static ILaunchConfiguration createDefaultLaunchConfiguration(IFile file)
		throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type =
			manager.getLaunchConfigurationType(
				IExternalToolConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		IPath path = file.getFullPath();
		if (path.segmentCount() > 2) {
			path = path.removeFirstSegments(path.segmentCount() - 2);
		}
		StringBuffer buffer = new StringBuffer();
		String[] segments = path.segments();
		for (int i = 0; i < segments.length; i++) {
			String string = segments[i];
			buffer.append(string);
			buffer.append(" "); //$NON-NLS-1$
		}
		String name = buffer.toString().trim();
		name = manager.generateUniqueLaunchConfigurationNameFrom(name);
		ILaunchConfigurationWorkingCopy workingCopy =
			type.newInstance(null, name);
		StringBuffer buf = new StringBuffer();
		ToolUtil.buildVariableTag(
			IExternalToolConstants.VAR_WORKSPACE_LOC,
			file.getFullPath().toString(),
			buf);
		workingCopy.setAttribute(
			IExternalToolConstants.ATTR_LOCATION,
			buf.toString());
		workingCopy.setAttribute(
			IExternalToolConstants.ATTR_RUN_IN_BACKGROUND,
			true);

		// set default for common settings
		CommonTab tab = new CommonTab();
		tab.setDefaults(workingCopy);
		tab.dispose();
		if (Preferences
			.getBoolean(IWOLipsPluginConstants.PREF_SHOW_BUILD_OUTPUT)) {
			workingCopy.setAttribute(
				IExternalToolConstants.ATTR_CAPTURE_OUTPUT,
				true);
			workingCopy.setAttribute(
				IExternalToolConstants.ATTR_SHOW_CONSOLE,
				true);
		} else {
			workingCopy.setAttribute(
				IExternalToolConstants.ATTR_CAPTURE_OUTPUT,
				false);
			workingCopy.setAttribute(
				IExternalToolConstants.ATTR_SHOW_CONSOLE,
				false);
		}

		return workingCopy;
	}
}

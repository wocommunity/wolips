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

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.externaltools.model.IExternalToolConstants;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.WOLipsPlugin;
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

	public static void asAnt(String buildFile, IProgressMonitor monitor)
		throws Exception {
		AntRunner runner = new AntRunner();
		runner.setBuildFileLocation(buildFile);
		//runner.setArguments("-Dmessage=Building -verbose");
		monitor.subTask(BuildMessages.getString("Build.SubTask.Name") + " " + buildFile);
		runner.run(new SubProgressMonitor(monitor, 1));
	}

	public static void asExternalTool(
		String buildFile,
		String buildDirectory,
		int kind,
		IProgressMonitor monitor)
		throws Exception {
		ILaunchConfigurationType type =
			DebugPlugin
				.getDefault()
				.getLaunchManager()
				.getLaunchConfigurationType(
				IExternalToolConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		ILaunchConfigurationWorkingCopy config = null;
		try {
			config =
				type.newInstance(
					null,
					BuildMessages.getString("Build.ExternalTool.Name"));
		} catch (CoreException e) {
			WOLipsPlugin.handleException(
				Display.getCurrent().getActiveShell(),
				e,
				BuildMessages.getString("Build.Exception"));
			return;
		}
		config.setAttribute(IExternalToolConstants.ATTR_LOCATION, buildFile);
		config.setAttribute(
			IExternalToolConstants.ATTR_ANT_PROPERTY_FILES,
			buildDirectory + "/build.properties");
		/*config.setAttribute(
					IExternalToolConstants.ATTR_WORKING_DIRECTORY, buildDirectory);*/
		config.setAttribute(
			IExternalToolConstants.ATTR_RUN_IN_BACKGROUND,
			false);
		if (Preferences
			.getBoolean(IWOLipsPluginConstants.PREF_SHOW_BUILD_OUTPUT)) {
			config.setAttribute(
				IExternalToolConstants.ATTR_CAPTURE_OUTPUT,
				true);
			config.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);
		} else {
			config.setAttribute(
				IExternalToolConstants.ATTR_CAPTURE_OUTPUT,
				false);
			config.setAttribute(
				IExternalToolConstants.ATTR_SHOW_CONSOLE,
				false);
		}
		monitor.subTask(BuildMessages.getString("Build.SubTask.Name"));
		config.launch(
			ILaunchManager.RUN_MODE,
			new SubProgressMonitor(monitor, 1));
		/*
		if (kind == IncrementalProjectBuilder.AUTO_BUILD)
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

}

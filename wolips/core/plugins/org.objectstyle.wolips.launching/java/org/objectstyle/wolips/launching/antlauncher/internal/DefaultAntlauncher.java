/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2004 - 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.launching.antlauncher.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.objectstyle.wolips.ant.antlaunchers.IAntlauncher;

/**
 * @author ulrich
 */
public class DefaultAntlauncher implements IAntlauncher {
	private static final String MAIN_TYPE_NAME = "org.eclipse.ant.internal.ui.antsupport.InternalAntRunner";

	private static final String REMOTE_ANT_PROCESS_FACTORY_ID = "org.eclipse.ant.ui.remoteAntProcessFactory";

	private static final String ID_ANT_LAUNCH_CONFIGURATION_TYPE = "org.eclipse.ant.AntLaunchConfigurationType"; //$NON-NLS-1$

	private static final String ATTR_ANT_TARGETS = "org.eclipse.ui.externaltools.ATTR_ANT_TARGETS"; //$NON-NLS-1$

	private static final String ATTR_LOCATION = "org.eclipse.ui.externaltools.ATTR_LOCATION";

	/**
	 * Method inExternalVM.
	 * 
	 * @param buildFile
	 * @param monitor
	 * @param captureOutput
	 * @param targets
	 * @throws CoreException
	 */
	public void launchAntInExternalVM(IFile buildFile, IProgressMonitor monitor, boolean captureOutput, String targets) throws CoreException {
		ILaunchConfigurationWorkingCopy workingCopy = null;
		try {
			workingCopy = DefaultAntlauncher.createDefaultLaunchConfiguration(buildFile, captureOutput, targets);
			ILaunch launch = workingCopy.launch(ILaunchManager.RUN_MODE, new SubProgressMonitor(monitor, 1));
			if (!captureOutput) {
				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
				manager.removeLaunch(launch);
			}
		} finally {
			workingCopy = null;
		}
	}

	/**
	 * Creates and returns a default launch configuration for the given file.
	 * 
	 * @param file
	 * @param captureOutput
	 * @param targets
	 * @return default launch configuration
	 * @throws CoreException
	 */
	private static ILaunchConfigurationWorkingCopy createDefaultLaunchConfiguration(IFile file, boolean captureOutput, String targets) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		StringBuffer buffer = new StringBuffer(file.getProject().getName());
		buffer.append(' ');
		buffer.append(file.getName());
		buffer.append(" (WOLips)");
		String name = buffer.toString().trim();
		name = manager.generateUniqueLaunchConfigurationNameFrom(name);
		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);
		workingCopy.setAttribute(ATTR_LOCATION, VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression("workspace_loc", file.getFullPath().toString())); //$NON-NLS-1$
		workingCopy.setAttribute("org.eclipse.jdt.launching.WORKING_DIRECTORY", file.getProject().getLocation().toOSString());
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, "org.eclipse.ant.ui.AntClasspathProvider"); //$NON-NLS-1$
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, MAIN_TYPE_NAME);
		workingCopy.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, REMOTE_ANT_PROCESS_FACTORY_ID);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "-Xms256m -Xmx512m");

		workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", false);
		if (captureOutput) {
			workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_SHOW_CONSOLE", true);
			workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_CAPTURE_OUTPUT", true);
		} else {
			workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_SHOW_CONSOLE", false);
			workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_CAPTURE_OUTPUT", false);
		}
		workingCopy.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
		workingCopy.setAttribute(ATTR_ANT_TARGETS, targets);
		workingCopy.setAttribute(BuildFailedConsoleLineTracker.ATTR_BUILD_FAILED_CONSOLE_LINE_TRACKER_ENABLED, true);
		workingCopy.setAttribute("org.eclipse.jdt.launching.JRE_CONTAINER", "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5");
		workingCopy.setAttribute("org.eclipse.ant.ui.DEFAULT_VM_INSTALL", false);		
		return workingCopy;
	}

}
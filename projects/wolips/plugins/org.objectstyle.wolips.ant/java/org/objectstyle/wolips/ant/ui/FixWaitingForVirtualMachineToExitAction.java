/* ====================================================================
*
* The ObjectStyle Group Software License, Version 1.0
*
* Copyright (c) 2004 The ObjectStyle Group
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

package org.objectstyle.wolips.ant.ui;

import org.eclipse.ant.internal.ui.launchConfigurations.IAntLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.objectstyle.wolips.ant.AntPlugin;

/**
 * @author ulrich
 */
public class FixWaitingForVirtualMachineToExitAction implements IActionDelegate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(IAntLaunchConfigurationConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		if (type != null) {
			ILaunchConfiguration[] configs = null;
			try {
				configs = manager.getLaunchConfigurations(type);
			} catch (CoreException e) {
				AntPlugin.getDefault().getPluginLogger().log(e);
			}
			if (configs != null && configs.length > 0) {
				for (int i = 0; i < configs.length; i++) {
					ILaunchConfiguration launchConfiguration = configs[i];
					String name = launchConfiguration.getName();
					if (name != null && name.indexOf("(WOLips)") != -1) {
						try {
							launchConfiguration.delete();
						} catch (CoreException e) {
							AntPlugin.getDefault().getPluginLogger().log(e);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		//can be ignored
	}

}

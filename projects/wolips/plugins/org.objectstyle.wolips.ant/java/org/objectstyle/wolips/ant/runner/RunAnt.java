/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2004 The ObjectStyle Group and individual authors of the
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
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.ant.runner;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.externaltools.internal.registry.ExternalToolMigration;
/**
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
public final class RunAnt {
	/**
	 * Method asAnt.
	 * 
	 * @param buildFile
	 * @param monitor
	 * @param target
	 * @throws Exception
	 */
	public void asAnt(IFile buildFile, IProgressMonitor monitor, String target)
			throws Exception {
		this.asAnt(buildFile.getLocation().toOSString(), monitor, target);
	}
	/**
	 * Method asAnt.
	 * 
	 * @param buildFile
	 * @param monitor
	 * @param target
	 * @throws Exception
	 */
	public void asAnt(String buildFile, IProgressMonitor monitor, String target)
			throws Exception {
		if (monitor == null)
			monitor = new NullProgressMonitor();
		AntRunner runner = null;
		try {
			runner = new AntRunner();
			runner.setBuildFileLocation(buildFile);
			if (target != null) {
				String[] targets = new String[1];
				targets[1] = target;
				runner.setExecutionTargets(targets);
			}
			runner.setArguments("-quiet");
			//runner.setArguments("-Dmessage=Building -verbose");
			monitor.subTask(BuildMessages.getString("Build.SubTask.Name") + " "
					+ buildFile);
			runner.run(new SubProgressMonitor(monitor, 1));
		} finally {
			runner = null;
		}
	}
	/**
	 * Method inExternalVM.
	 * 
	 * @param buildFile
	 * @param kind
	 * @param monitor
	 * @throws Exception
	 */
	public void inExternalVM(IFile buildFile, IProgressMonitor monitor)
			throws CoreException {
		ILaunchConfiguration config = null;
		try {
			config = RunAnt.createDefaultLaunchConfiguration(buildFile);
		} catch (CoreException e) {
			config = null;
			/*
			 * WOLipsPlugin.handleException(
			 * Display.getCurrent().getActiveShell(), e,
			 * BuildMessages.getString("Build.Exception"));
			 */
			return;
		}
		try {
			config= ExternalToolMigration.migrateRunInBackground(config);
			config.launch(ILaunchManager.RUN_MODE, monitor);
			
		} finally {
			config = null;
		}
	}
	/**
	 * Creates and returns a default launch configuration for the given file.
	 * 
	 * @param file
	 * @return default launch configuration
	 */
	private static ILaunchConfiguration createDefaultLaunchConfiguration(
			IFile buildFile) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration config = manager.getLaunchConfiguration(buildFile.getProject().getFile(buildFile.getProject().getName() + ".build.launch"));
		return config;
	}
}
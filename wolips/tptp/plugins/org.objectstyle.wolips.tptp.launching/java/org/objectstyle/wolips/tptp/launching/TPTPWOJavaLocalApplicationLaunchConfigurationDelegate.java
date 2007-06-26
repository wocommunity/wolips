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
 *  1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 *  4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact andrus@objectstyle.org.
 *  5. Products derived from this software may not be called "ObjectStyle" nor
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
 * Group, please see <http://objectstyle.org/> .
 *  
 */

package org.objectstyle.wolips.tptp.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.hyades.trace.ui.HyadesConstants;
import org.eclipse.hyades.trace.ui.UIPlugin;
import org.eclipse.hyades.trace.ui.internal.launcher.IProfileLaunchConfigurationConstants;
import org.eclipse.hyades.trace.ui.internal.launcher.ProfileLaunchUtil;
import org.eclipse.hyades.trace.ui.internal.launcher.ProfilingSetsManager;
import org.eclipse.hyades.trace.ui.internal.launcher.TraceArguments;
import org.eclipse.hyades.trace.ui.internal.util.PDCoreUtil;
import org.eclipse.hyades.trace.ui.internal.util.TraceMessages;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.tptp.platform.common.ui.trace.internal.CommonUITraceConstants;
import org.objectstyle.wolips.launching.delegates.WOJavaLocalApplicationLaunchConfigurationDelegate;

/**
 * Launches a local VM.
 */
public class TPTPWOJavaLocalApplicationLaunchConfigurationDelegate extends WOJavaLocalApplicationLaunchConfigurationDelegate {

	public final static String TPTPWOJavaLocalApplicationID = "org.objectstyle.wolips.tptp.launching.TPTPWOJavaLocalApplicationLaunchConfigurationDelegate";

	public void launch(ILaunchConfiguration conf, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 4);
			monitor.subTask(TraceMessages.LNCH_MSGV);
			boolean success = ProfileLaunchUtil.performProfilingTypesLaunch(conf);
			monitor.worked(1);

			if (!success) {
				monitor.setCanceled(true);
				return;
			}

			ProfilingSetsManager manager = ProfilingSetsManager.instance();
			IPreferenceStore store = UIPlugin.getDefault().getPreferenceStore();

			TraceArguments args = new TraceArguments(getMainTypeName(conf));
			args.setClassPath(getClasspathString(conf));
			args.setParameters(getProgramArguments(conf));
			args.setVMArguments(getVMArguments(conf));
			args.setEnvironmentVariable(ProfileLaunchUtil.getEnvironmentVariables(conf));
			args.setAutoMonitoring(manager.getAutoMonitoring(conf));

			File workingDir = getWorkingDirectory(conf);
			if (workingDir != null) {
				args.setLocation(workingDir.getAbsolutePath());
			} else {
				args.setLocation(System.getProperty("user.dir"));
			}

			monitor.worked(1);

			String hostName = conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_HOSTNAME, "localhost");
			int port = conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_PORT, store.getInt(HyadesConstants.LOCALHOST_PORT));
			String projectName = conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_DESTINATION_PROJECT, store.getString(CommonUITraceConstants.TRACE_PROJECT_NAME));
			String monitorName = conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_DESTINATION_MONITOR, store.getString(CommonUITraceConstants.TRACE_MONITOR_NAME));
			args.setHostName(hostName);
			args.setPortNumber(port);

			if (conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_PROFILE_TO_FILE, false))
				args.setProfileFile(conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_DESTINATION_FILE, (String) null));

			ArrayList filters = manager.getFilters(conf);
			Vector options = manager.getOptions(conf);
			monitor.worked(1);

			if (!PDCoreUtil.launchTrace(args, filters, options, projectName, monitorName, launch)) {
				monitor.setCanceled(true);
			}
			monitor.worked(1);
		} catch (CoreException e) {
			monitor.setCanceled(true);
			throw e;
		}
	}

	private String getClasspathString(ILaunchConfiguration conf) throws CoreException {
		String classPath = conf.getAttribute(IProfileLaunchConfigurationConstants.ATTR_CLASSPATH, (String) null);

		if (classPath == null) {
			StringBuffer buf = new StringBuffer();
			String[] entries = getClasspath(conf);
			for (int i = 0; i < entries.length - 1; ++i) {
				buf.append(entries[i]);
				buf.append(File.pathSeparatorChar);
			}
			buf.append(entries[entries.length - 1]);
			return buf.toString();
		}
		return classPath;
	}
}

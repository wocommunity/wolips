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

package org.objectstyle.wolips.profiling;

import java.io.IOException;
import java.net.ServerSocket;

import jmechanic.eclipse.profiler.ProfilerPlugin;
import jmechanic.eclipse.profiler.launching.JavaProfilingLaunchConfigDelegate;
import jmechanic.eclipse.profiler.ui.AbstractProfilerView;
import jmechanic.hprof.ProfilerConnection;
import jmechanic.hprof.ProfilerConnectionMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.SocketUtil;
import org.objectstyle.wolips.launching.WOJavaLocalApplicationLaunchConfigurationDelegate;

/**
 * Launches a local VM.
 */
public class ProfiledWOJavaLocalApplicationLaunchConfigurationDelegate
	extends WOJavaLocalApplicationLaunchConfigurationDelegate {

	protected void addVMArguments(
		StringBuffer vmArgs,
		ILaunchConfiguration configuration,
		ILaunch launch,
		String mode)
		throws CoreException {
		super.addVMArguments(vmArgs, configuration, launch, mode);
		// Set things up for profiling only if this is
		// debug mode
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			int hprofPort = getProfilerPortNumber(configuration);
			if (hprofPort != -1) {
				// Calculate the arguments
				addHProfVMArguments(vmArgs, configuration, hprofPort);

				// Fire up the listener thread
				launchHprofListener(launch, hprofPort);
			}
		}
	}
	/**
	 * Add the VM arguments for the HPROF connection.
	 */
	protected void addHProfVMArguments(
		StringBuffer vmArgs,
		ILaunchConfiguration configuration,
		int hprofPort)
		throws CoreException {
		boolean doe =
			configuration.getAttribute(
				JavaProfilingLaunchConfigDelegate.ATTR_PROFILER_HEAP_DOE,
				false);
		String sitesString =
			configuration.getAttribute(
				JavaProfilingLaunchConfigDelegate
					.ATTR_PROFILER_SITES_SAMPLE_START,
				false)
				? ",heap=sites"
				: "";
		int stackDepth =
			configuration.getAttribute(
				JavaProfilingLaunchConfigDelegate.ATTR_PROFILER_STACK_DEPTH,
				4);

		String cutoffString =
			configuration.getAttribute(
				JavaProfilingLaunchConfigDelegate.ATTR_PROFILER_CUTOFF_RATIO,
				"0.0001");
		float cutoffRatio = 0.0001f;
		try {
			cutoffRatio = Float.parseFloat(cutoffString);
		} catch (NumberFormatException e) {
		}

		vmArgs
			.append(" ")
			.append("-Xrunhprof:cpu=samples,format=b")
			.append(sitesString)
			.append(",depth=")
			.append(stackDepth)
			.append(",cutoff=")
			.append(cutoffRatio)
			.append(",doe=")
			.append(doe ? "y" : "n")
			.append(",net=localhost:")
			.append(hprofPort);
	}

	/**
	 * Get the port number for the specified launch configuration
	 * based on user settings.
	 */
	private int getProfilerPortNumber(ILaunchConfiguration configuration)
		throws CoreException {
		int port = -1;

		if (configuration
			.getAttribute(
				JavaProfilingLaunchConfigDelegate.ATTR_PROFILER_PORT_AUTO,
				true)) {
			port = SocketUtil.findUnusedLocalPort("localhost", 1025, 65533); //$NON-NLS-1$
		} else {
			port =
				configuration.getAttribute(
					JavaProfilingLaunchConfigDelegate.ATTR_PROFILER_PORT_VALUE,
					12000);
		}

		return port;
	}

	/**
	 * Launch a new listener for incoming HPROF connections.
	 */
	private void launchHprofListener(ILaunch launch, int hprofPort) {
		final ILaunch theLaunch = launch;
		final int thePort = hprofPort;

		Runnable runnable = new Runnable() {
			public void run() {
				try {
						// Wait for a connection on the specified
		// port
	ServerSocket listeningSocket = new ServerSocket(thePort);

					// Incoming connection arrived... build
					// a profiler connection
					ProfilerConnection connection =
						new ProfilerConnection(
							listeningSocket.accept(),
							theLaunch);

					// Save the connection
					ProfilerConnectionMap.registerProfilerConnection(
						theLaunch,
						connection);

					// Kick the viewers
					AbstractProfilerView.updateProfilerViews();

				} catch (IOException e) {
					ProfilerPlugin.log(IStatus.WARNING, "launchHprofListener - Runnable", e); //$NON-NLS-1$
				}
			}
		};

		// Launch the wait into a new thread
		Thread newThread = new Thread(runnable, "HPROF Connection Listener " + hprofPort); //$NON-NLS-1$
		newThread.setDaemon(true);
		newThread.start();
	}
}

/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group
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

package org.objectstyle.wolips.launching.delegates;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.baseforplugins.util.StringUtilities;
import org.objectstyle.wolips.datasets.adaptable.JavaProject;
import org.objectstyle.wolips.launching.LaunchingMessages;
import org.objectstyle.wolips.launching.LaunchingPlugin;
import org.objectstyle.wolips.launching.classpath.WORuntimeClasspathProvider;
import org.objectstyle.wolips.preferences.ILaunchInfo;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.variables.VariablesPlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

/**
 * @author ulrich Launches a local VM.
 */
public class WOJavaLocalApplicationLaunchConfigurationDelegate extends JavaLaunchDelegate {
	/**
	 * Comment for <code>WOJavaLocalApplicationID</code>
	 */
	public static final String WOJavaLocalApplicationID = "org.objectstyle.wolips.launching.WOLocalJavaApplication";

	/** The launch configuration attribute for stack trace depth */
	public static final String ATTR_WOLIPS_LAUNCH_WOARGUMENTS = "org.objectstyle.wolips.launchinfo";

	/**
	 * Comment for <code>ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS</code>
	 */
	public static final String ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS = "WOJavaLocalApplicationLaunchConfigurationDelegate.NSDebugGroups";

	public static final String ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER = "WOJavaLocalApplicationLaunchConfigurationDelegate.OpenInBrowser";

	public static final String ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT = "WOJavaLocalApplicationLaunchConfigurationDelegate.WebServerConnect";

	/**
	 * @param config
	 */
	public static void initConfiguration(ILaunchConfigurationWorkingCopy config) {
		try {
			IJavaProject javaProject = JavaRuntime.getJavaProject(config);
			if (javaProject != null) {
				config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, "${working_dir_loc_WOLips:" + javaProject.getProject().getName() + "}");
			}
		} catch (CoreException ce) {
			LaunchingPlugin.getDefault().log(ce);
		}
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, WORuntimeClasspathProvider.ID);
		config.setAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER, "false");
	}

	public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
		return super.getClasspath(configuration);
	}

	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		String notFound = "notFound";
		if (configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, notFound).equals(notFound)) {
			ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, WORuntimeClasspathProvider.ID);
			workingCopy.doSave();
			this.informUser("LaunchConfiguration update. The message should occur only once. Please launch your app again.");
			return false;
		}
		if (configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, notFound).equals(WORuntimeClasspathProvider.OLD_ID)) {
			ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, WORuntimeClasspathProvider.ID);
			workingCopy.doSave();
			this.informUser("LaunchConfiguration update. The message should occur only once. Please launch your app again.");
			return false;
		}
		if (configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, notFound).equals(WORuntimeClasspathProvider.VERY_OLD_ID)) {
			ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, WORuntimeClasspathProvider.ID);
			workingCopy.doSave();
			this.informUser("LaunchConfiguration update. The message should occur only once. Please launch your app again.");
			return false;
		}
		return super.preLaunchCheck(configuration, mode, monitor);
	}

	private final void informUser(final String message) {
		class RunnableExceptionHandler implements Runnable {

			public void run() {
				Status status = new Status(IStatus.ERROR, "org.objectstyle.wolips.launching", IStatus.ERROR, "Classpath Provider missing or invalid", null);
				WorkbenchUtilitiesPlugin.errorDialog(Display.getCurrent().getActiveShell(), "WOLips", message, status);
			}
		}
		RunnableExceptionHandler runnable = new RunnableExceptionHandler();
		Display.getDefault().asyncExec(runnable);
	}


	/**
	 * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#verifyWorkingDirectory(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public File verifyWorkingDirectory(ILaunchConfiguration configuration) throws CoreException {
		IProject theProject = this.getJavaProject(configuration).getProject();

		IPath wd = getWorkingDirectoryPath(configuration);
		JavaProject javaProject = (JavaProject) (this.getJavaProject(configuration).getAdapter(JavaProject.class));

		File wdFile = javaProject.getWDFolder(theProject, wd);
		if (null == wdFile) {
			IPath path = VariablesPlugin.getDefault().getProjectVariables(theProject).getExternalBuildRoot();
			if(path != null) {
				path = path.append(theProject.getName() + ".woa");
				wdFile = path.toFile();
				if (!wdFile.exists()) {
					wdFile = null;
				}
			} else {
				wdFile = null;
			}
		}
		if (null == wdFile) {
			wdFile = super.verifyWorkingDirectory(configuration);
		}
		if (((wdFile == null) || (wdFile.toString().indexOf(".woa") < 0))) {
			abort(MessageFormat.format(LaunchingMessages.getString("WOJavaLocalApplicationLaunchConfigurationDelegate.Working_directory_is_not_a_woa__{0}_12"), new Object[] { wdFile.toString() }), null, IJavaLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST); //$NON-NLS-1$
		}

		return wdFile;
	}

	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		IProject theProject = this.getJavaProject(configuration).getProject();

		StringBuffer launchArgument = new StringBuffer(super.getProgramArguments(configuration));
		launchArgument.append(" ");
		JavaProject javaProject = (JavaProject) (this.getJavaProject(configuration).getAdapter(JavaProject.class));

		String mainTypeName = verifyMainTypeName(configuration);

		File workingDir = verifyWorkingDirectory(configuration);
		String launchArguments = configuration.getAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WOARGUMENTS, Preferences.getLaunchGlobal());
		if (launchArguments != null && launchArguments.indexOf("<>-EOAdaptorDebugEnabled=<>") != -1) {
			launchArguments = launchArguments.replaceAll("\\Q<>-EOAdaptorDebugEnabled=<>\\E", "<>-EOAdaptorDebugEnabled<>");
		}
		ILaunchInfo[] launchInfo = Preferences.getLaunchInfoFrom(launchArguments);
		String automatic = "Automatic";
		for (int i = 0; i < launchInfo.length; i++) {
			if (launchInfo[i].isEnabled()) {
				// -WOApplicationClassName
				String parameter = launchInfo[i].getParameter();
				String argument = launchInfo[i].getArgument();
				if (automatic.equals(argument)) {
					if ("-WOApplicationClassName".equals(parameter))
						argument = mainTypeName;
					if ("-DWORoot=".equals(parameter)) {
						IPath systemRoot = VariablesPlugin.getDefault().getProjectVariables(theProject).getSystemRoot();
						if (javaProject.isOnMacOSX() || systemRoot == null) {
							parameter = "";
							argument = "";
						}
						else {
							argument = systemRoot.toOSString();
						}
					}
					if ("-DWORootDirectory=".equals(parameter)) {
						IPath systemRoot = VariablesPlugin.getDefault().getProjectVariables(theProject).getSystemRoot();
						if (javaProject.isOnMacOSX() || systemRoot == null) {
							parameter = "";
							argument = "";
						}
						else {
							argument = systemRoot.toOSString();
						}
					}
					if ("-DWOUserDirectory=".equals(parameter)) {
						argument = workingDir.getAbsolutePath();
					}
					if ("-NSProjectSearchPath".equals(parameter)) {
						argument = javaProject.getGeneratedByWOLips(Preferences.getNSProjectSearchPath());
					}

				}

				launchArgument.append(StringUtilities.toCommandlineParameterFormat(parameter, argument, true));
				launchArgument.append(" ");
			}
		}
		String debugGroups = configuration.getAttribute(WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_DEBUG_GROUPS, "");
		if (debugGroups != null && debugGroups.length() > 0) {
			launchArgument.append(" -DNSDebugGroups=\"(");
			launchArgument.append(debugGroups);
			launchArgument.append(")\"");
		}
		return launchArgument.toString();
	}

	/**
	 * for the profiling plugin
	 * 
	 * @param configuration
	 * @param launch
	 * @return
	 * @throws CoreException
	 */
	public String getVMArguments(ILaunchConfiguration configuration, ILaunch launch) throws CoreException {
		return super.getVMArguments(configuration);
	}

	protected void setDefaultSourceLocator(ILaunch launch, ILaunchConfiguration configuration) throws CoreException {
		// set default source locator if none specified
		if (launch.getSourceLocator() == null || !(launch.getSourceLocator() instanceof JavaSourceLookupDirectorWO)) {
			ISourceLookupDirector sourceLocator = new JavaSourceLookupDirectorWO();
			sourceLocator.setSourcePathComputer(getLaunchManager().getSourcePathComputer("org.eclipse.jdt.launching.sourceLookup.javaSourcePathComputer")); //$NON-NLS-1$
			sourceLocator.initializeDefaults(configuration);
			launch.setSourceLocator(sourceLocator);
		}
	}
}

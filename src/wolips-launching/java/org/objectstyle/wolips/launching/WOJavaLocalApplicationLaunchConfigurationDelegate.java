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

package org.objectstyle.wolips.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.plugin.WOLipsPlugin;
import org.objectstyle.wolips.core.preferences.ILaunchInfo;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.project.WOLipsProject;
import org.objectstyle.wolips.logging.WOLipsLog;
import org.objectstyle.woproject.util.FileStringScanner;

/**
 * Launches a local VM.
 */
public class WOJavaLocalApplicationLaunchConfigurationDelegate
	extends AbstractJavaLaunchConfigurationDelegate 
{
	public static final String WOJavaLocalApplicationID =
		"org.objectstyle.wolips.launching.WOLocalJavaApplication";
	/** The launch configuration attribute for stack trace depth */
	public static final String ATTR_WOLIPS_LAUNCH_WOARGUMENTS =
		"org.objectstyle.wolips.launchinfo";


  public static void initConfiguration (ILaunchConfigurationWorkingCopy config) {
    config.setAttribute(
      IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, 
      WORuntimeClasspathProvider.ID
    );

  }

	/**
	 * @see ILaunchConfigurationDelegate#launch(ILaunchConfiguration, String, ILaunch, IProgressMonitor)
	 */
	public void launch(
		ILaunchConfiguration configuration,
		String mode,
		ILaunch launch,
		IProgressMonitor monitor)
		throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask(LaunchingMessages.getString("WOJavaLocalApplicationLaunchConfigurationDelegate.Launching..._1"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		String mainTypeName = verifyMainTypeName(configuration);

		IVMInstall vm = verifyVMInstall(configuration);

		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null) {
			if (mode == ILaunchManager.DEBUG_MODE) {
				abort(MessageFormat.format(LaunchingMessages.getString("WOJavaLocalApplicationLaunchConfigurationDelegate.JRE_{0}_does_not_support_debug_mode._1"), new String[] { vm.getName()}), null, IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); //$NON-NLS-1$
			} else {
				abort(MessageFormat.format(LaunchingMessages.getString("WOJavaLocalApplicationLaunchConfigurationDelegate.JRE_{0}_does_not_support_run_mode._2"), new String[] { vm.getName()}), null, IJavaLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); //$NON-NLS-1$
			}
		}

		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null) {
			workingDirName = workingDir.getAbsolutePath();
		}
		String launchArguments =
			configuration.getAttribute(
				WOJavaLocalApplicationLaunchConfigurationDelegate
					.ATTR_WOLIPS_LAUNCH_WOARGUMENTS,
				Preferences.getString(
					IWOLipsPluginConstants.PREF_LAUNCH_GLOBAL));
		ILaunchInfo[] launchInfo =
			Preferences.getLaunchInfoFrom(launchArguments);
		StringBuffer launchArgument = new StringBuffer();
		String automatic = "Automatic";
		for (int i = 0; i < launchInfo.length; i++) {
			boolean spaceBetweenParameterAndArgument = true;
			if (launchInfo[i].isEnabled()) {
				//-WOApplicationClassName
				String parameter = launchInfo[i].getParameter();
				String argument = launchInfo[i].getArgument();
				if (automatic.equals(argument)) {
					if ("-WOApplicationClassName".equals(parameter))
						argument =
							this.getWOApplicationClassNameArgument(
								configuration);
					if ("-DWORoot=".equals(parameter)) {
						argument =
							WOLipsPlugin
								.getDefault()
								.getWOEnvironment()
								.getWOVariables()
								.systemRoot();
						spaceBetweenParameterAndArgument = false;
						if (this.isOnMacOSX()) {
							parameter = "";
							argument = "";
						}
					}
					if ("-DWORootDirectory=".equals(parameter)) {
						argument =
							WOLipsPlugin
								.getDefault()
								.getWOEnvironment()
								.getWOVariables()
								.systemRoot();
						spaceBetweenParameterAndArgument = false;
						if (this.isOnMacOSX()) {
							parameter = "";
							argument = "";
						}
					}
					if ("-NSProjectSearchPath".equals(parameter)) {
						argument = this.getGeneratedByWOLips(configuration);
					}

				}

				launchArgument.append(parameter);
				if (spaceBetweenParameterAndArgument)
					launchArgument.append(" ");
				launchArgument.append(argument);
				launchArgument.append(" ");
			}
		}
		// Program & VM args
		String pgmArgs =
			getProgramArguments(configuration)
				+ " "
				+ launchArgument.toString();
		String vmArgs = getVMArguments(configuration);
		StringBuffer vmArgsBuffer = new StringBuffer(vmArgs);

		this.addVMArguments(vmArgsBuffer, configuration, launch, mode);
		ExecutionArguments execArgs =
			new ExecutionArguments(vmArgsBuffer.toString(), pgmArgs);

		// VM-specific attributes
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		// Classpath
		String[] classpath = getClasspath(configuration);

		// Create VM config
		VMRunnerConfiguration runConfig =
			new VMRunnerConfiguration(mainTypeName, classpath);
		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
		runConfig.setVMArguments(execArgs.getVMArgumentsArray());
		runConfig.setWorkingDirectory(workingDirName);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		// Bootpath
		String[] bootpath = getBootpath(configuration);
		runConfig.setBootClassPath(bootpath);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		// stop in main
		prepareStopInMain(configuration);

		// Launch the configuration
		runner.run(runConfig, launch, monitor);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		// set the default source locator if required
		setDefaultSourceLocator(launch, configuration);

		monitor.done();
	}
	/**
	 * @see org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate#verifyWorkingDirectory(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public File verifyWorkingDirectory(ILaunchConfiguration configuration)
		throws CoreException 
  {
    IProject theProject = this.getJavaProject(configuration).getProject();  
    WOLipsProject wolipsProject = new WOLipsProject(theProject);
    WOLipsProject.NaturesAccessor na = wolipsProject.getNaturesAccessor();

    IPath wd = getWorkingDirectoryPath(configuration);
    
    File wdFile = null;
    if (wd == null) {
      IFolder wdFolder;
      if (na.isAnt()) {
        wdFolder = theProject.getFolder("dist/"+theProject.getName()+".woa");
      } else {
        wdFolder = theProject.getFolder("build/"+theProject.getName()+".woa");
      }
      if (wdFolder == null || !wdFolder.exists()) {
        wdFolder = theProject.getFolder(theProject.getName()+".woa");
      }
      if (wdFolder != null && !wdFolder.exists()) {
        wdFolder = null;
      } else {
        wdFile = wdFolder.getLocation().toFile();
      }
    } 

    if (null == wdFile) {
      wdFile = super.verifyWorkingDirectory(configuration);
    }

    if (((wdFile == null) || (wdFile.toString().indexOf(".woa") < 0))) {
			abort(MessageFormat.format(LaunchingMessages.getString("WOJavaLocalApplicationLaunchConfigurationDelegate.Working_directory_is_not_a_woa__{0}_12"), new String[] { wdFile.toString()}), null, IJavaLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST); //$NON-NLS-1$
		}

		return wdFile;
	}

	/**
	 * Method addVMArgument return the vmArgs.
	 * @param vmArgs
	 * @param configuration
	 * @param hprofPort
	 */
	protected StringBuffer addVMArguments(
		StringBuffer vmArgs,
		ILaunchConfiguration configuration,
		ILaunch launch,
		String mode)
		throws CoreException {
		return vmArgs;
	}
	/**
	 * Method getGeneratedByWOLips.
	 * @return String
	 */
	private String getGeneratedByWOLips(ILaunchConfiguration configuration) {
		String returnValue = "";
		IProject[] projects =
			ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isValidProjectPath(projects[i], configuration)) {
				if (isAFramework(projects[i], configuration)) {
					if (returnValue.length() > 0) {
						returnValue = returnValue + ",";
					}
					returnValue =
						returnValue
							+ "\""
							+ projects[i].getLocation().toOSString()
							+ "\"";
				}
				if (isTheLaunchApp(projects[i], configuration)) {
					if (returnValue.length() > 0) {
						returnValue = returnValue + ",";
					}
					returnValue =
						returnValue
							+ "\""
							+ ".."
							+ "\""
							+ ","
							+ "\""
							+ "../.."
							+ "\"";
				}
			}
		}
		returnValue = FileStringScanner.replace(returnValue, "\\", "/");
		returnValue = this.addPreferencesValue(returnValue);
		if ("".equals(returnValue))
			returnValue = "\"" + ".." + "\"";

		returnValue = "(" + returnValue + ")";

		return returnValue;
	}
	/**
	 * Method isValidProjectPath.
	 * @param project
	 * @param configuration
	 * @return boolean
	 */
	private boolean isValidProjectPath(
		IProject project,
		ILaunchConfiguration configuration) {
		try {
			return project.getLocation().toOSString().indexOf("-") == -1;
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return false;
		}
	}
	/**
	 * Method isTheLaunchAppOrFramework.
	 * @param project
	 * @param configuration
	 * @return boolean
	 */
	private boolean isTheLaunchApp(
		IProject project,
		ILaunchConfiguration configuration) {
		IJavaProject buildProject = null;
		try {
			buildProject = this.getJavaProject(configuration);
			if (project.equals(buildProject.getProject()))
				return true;
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
		return false;
	}
	/**
	 * Method isTheLaunchAppOrFramework.
	 * @param project
	 * @param configuration
	 * @return boolean
	 */
	private boolean isAFramework(
		IProject project,
		ILaunchConfiguration configuration) {
		IJavaProject buildProject = null;
		try {
			buildProject = this.getJavaProject(configuration);
			WOLipsProject woLipsProject = new WOLipsProject(project);
			if (woLipsProject.getNaturesAccessor().isFramework()
				&& projectISReferencedByProject(
					project,
					buildProject.getProject()))
				return true;
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return false;
		}
		return false;
	}
	/**
	 * Method projectISReferencedByProject.
	 * @param child
	 * @param mother
	 * @return boolean
	 */
	private boolean projectISReferencedByProject(
		IProject child,
		IProject mother) {
		IProject[] projects = null;
		try {
			projects = mother.getReferencedProjects();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return false;
		}
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].equals(child))
				return true;
		}
		return false;
	}
	/**
	 * Method addPreferencesValue.
	 * @param aString
	 * @return String
	 */
	protected String addPreferencesValue(String aString) {
		if (aString == null)
			return aString;
		String nsProjectSarchPath =
			Preferences.getString(
				IWOLipsPluginConstants.PREF_NS_PROJECT_SEARCH_PATH);
		if (nsProjectSarchPath == null || nsProjectSarchPath.length() == 0)
			return aString;
		if (aString.length() > 0)
			aString = aString + ",";
		return aString + nsProjectSarchPath;
	}

	private boolean isOnMacOSX() {
		return WOLipsPlugin
			.getDefault()
			.getWOEnvironment()
			.getWOVariables()
			.systemRoot()
			.startsWith("/System");
	}

	/**
	 * Method getWOApplicationClassNameArgument.
	 * @return String
	 */
	private String getWOApplicationClassNameArgument(ILaunchConfiguration config) {
		String main = null;
		try {
			main =
				config.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
					"");
		} catch (Exception anException) {
			WOLipsLog.log(anException);
			return "";
		}
		if ("".equals(main))
			return "";
		return main;
	}
}

/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002,2004 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.projectbuild.builder;
import java.util.Map;
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.ant.internal.ui.launchConfigurations.IAntLaunchConfigurationConstants;
import org.eclipse.ant.internal.ui.model.IAntUIConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.projectbuild.ProjectBuildPlugin;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;
/**
 * @author uli
 */
public class WOAntBuilder extends IncrementalProjectBuilder {
	private static final String MAIN_TYPE_NAME = "org.eclipse.ant.internal.ui.antsupport.InternalAntRunner";
	private static final int TOTAL_WORK_UNITS = 1;
	private String informUserString = "WOLips: "
			+ "To avoid frequent crashes don't forget to install the patched org.eclipse.core.ant plugin. "
			+ "It's available as a separate download and from the optional folder in the download.";
	/**
	 * Constructor for WOBuilder.
	 */
	public WOAntBuilder() {
		super();
	}
	private boolean isPatchInstalled() {
		//TODO: No check needed ant runs in a external vm
		String string = AntCorePlugin.getPlugin().getDescriptor()
				.getProviderName();
		return string.endsWith("objectstyle.org");
	}
	/**
	 * Runs the build with the ant runner.
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.
	 *      util. Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (AntRunner.isBuildRunning() || this.getProject() == null) {
			monitor.done();
			return null;
		}
		monitor.beginTask(AntBuildMessages.getString("Build.Monitor.Title"),
				WOAntBuilder.TOTAL_WORK_UNITS);
		if (!Preferences.getPREF_RUN_WOBUILDER_ON_BUILD()
				|| getProject() == null || !getProject().exists()) {
			monitor.done();
			return null;
		}
		String aBuildFile = null;
		try {
			getProject().deleteMarkers(
					ProjectBuildPlugin.MARKER_TASK_GENERIC, false,
					IResource.DEPTH_ONE);
			if (!projectNeedsAnUpdate()
					&& kind != IncrementalProjectBuilder.FULL_BUILD) {
				monitor.done();
				return null;
			}
			aBuildFile = this.buildFile();
			if (checkIfBuildfileExist(aBuildFile)) {
				getProject().getFile(aBuildFile).deleteMarkers(
						ProjectBuildPlugin.MARKER_TASK_GENERIC, false,
						IResource.DEPTH_ONE);
				this.execute(monitor, aBuildFile);
			}
		} catch (Exception e) {
			this.handleException(e);
		}
		aBuildFile = null;
		/*
		 * monitor.beginTask( BuildMessages.getString("Build.Refresh.Title"),
		 * WOAntBuilder.TOTAL_WORK_UNITS);
		 */
		//this.forgetLastBuiltState();
		//getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
		monitor.done();
		return null;
	}
	/**
	 * Method execute.
	 * 
	 * @param monitor
	 * @param aBuildFile
	 * @throws Exception
	 */
	private void execute(IProgressMonitor monitor, String aBuildFile)
			throws Exception {
		if (!this.isPatchInstalled()) {
			IMarker aMarker = null;
			try {
				aMarker = this.getBuildfileMarker();
				aMarker.setAttribute(IMarker.MESSAGE, informUserString);
			} catch (Exception e) {
				ProjectBuildPlugin.getDefault().getPluginLogger().log(e);
			} finally {
				aMarker = null;
			}
		}
		//RunAnt runAnt = new RunAnt();
		//if (projectNeedsClean())
		//TODO:handle clean
		this.launchAntInExternalVM(getProject().getFile(aBuildFile), monitor);
	}
	/**
	 * Method projectNeedsClean.
	 * 
	 * @return boolean
	 */
	private boolean projectNeedsClean() {
		//currently we get an an exception on clean
		//return this.getDelta(this.getProject()) == null;
		return false;
	}
	/**
	 * Method projectNeedsAnUpdate.
	 * 
	 * @return boolean
	 */
	private boolean projectNeedsAnUpdate() {
		BuildResourceValidator resourceValidator = new BuildResourceValidator();
		if (this.getProject() == null
				|| this.getDelta(this.getProject()) == null)
			return false;
		try {
			this.getDelta(this.getProject()).accept(resourceValidator);
		} catch (CoreException e) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(e);
			return false;
		}
		return resourceValidator.isBuildRequired();
	}
	/**
	 * Method handleException.
	 * 
	 * @param anException
	 */
	private void handleException(Exception anException) {
		IMarker aMarker = null;
		try {
			if (anException == null) {
				throw new NullPointerException(
						"WOBuilder.handleException called without an exception.");
			}
			aMarker = this.getBuildfileMarker();
			aMarker.setAttribute(IMarker.MESSAGE, "WOLips: "
					+ anException.getMessage());
		} catch (Exception e) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(e);
		} finally {
			aMarker = null;
		}
	}
	private IMarker getBuildfileMarker() {
		IMarker aMarker = null;
		try {
			aMarker = getProject().getFile(this.buildFile()).createMarker(
					ProjectBuildPlugin.MARKER_TASK_GENERIC);
			aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		} catch (CoreException e) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(e);
		}
		return aMarker;
	}
	/**
	 * Checks if the build file exists.
	 * 
	 * @param aBuildFile
	 * @return boolean
	 */
	private boolean checkIfBuildfileExist(String aBuildFile) {
		try {
			if (getProject().getFile(aBuildFile).exists())
				return true;
		} catch (Exception anException) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(anException);
		}
		IMarker aMarker = null;
		try {
			aMarker = getProject().createMarker(
					ProjectBuildPlugin.MARKER_TASK_GENERIC);
			aMarker.setAttribute(IMarker.MESSAGE, "WOLips: Can not find: "
					+ this.buildFile());
			aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		} catch (Exception anException) {
			ProjectBuildPlugin.getDefault().getPluginLogger().log(anException);
		} finally {
			aMarker = null;
		}
		return false;
	}
	/**
	 * @return String
	 */
	public String buildFile() {
		return "build.xml";
	}
	/**
	 * @return String
	 */
	public String defaultTarget() {
		return null;
	}
	/**
	 * @return String
	 */
	public String cleanTarget() {
		return "clean";
	}
	private final class BuildResourceValidator implements IResourceDeltaVisitor {
		private boolean buildRequired = false;
		private Project project;
		/**
		 * Constructor for ProjectFileResourceValidator.
		 */
		public BuildResourceValidator() {
			super();
		}
		/**
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		public final boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();
			return examineResource(resource, delta.getKind());
		}
		/**
		 * Method examineResource. Examines changed resources for added and/or
		 * removed webobjects project resources and synchronizes project file.
		 * <br>
		 * 
		 * @param resource
		 * @param kindOfChange
		 * @return boolean
		 */
		private final boolean examineResource(IResource resource,
				int kindOfChange) {
			//see bugreport #708385
			if (!resource.isAccessible()
					&& kindOfChange != IResourceDelta.REMOVED)
				return false;
			// reset project file to update
			switch (resource.getType()) {
				case IResource.ROOT :
					// further investigation of resource delta needed
					return true;
				case IResource.PROJECT :
					if (!resource.exists() || !resource.isAccessible()) {
						// project deleted no further investigation needed
						return false;
					}
					project = (Project)resource.getAdapter(Project.class);
				case IResource.FOLDER :
					if (IWOLipsModel.EXT_FRAMEWORK.equals(resource
							.getFileExtension())
							|| IWOLipsModel.EXT_WOA.equals(resource
									.getFileExtension())
							|| "build".equals(resource.getName())
							|| "dist".equals(resource.getName())) {
						// no further examination needed
						return false;
					}
					// further examination of resource delta needed
					return true;
				case IResource.FILE :
					if (needsUpdate(kindOfChange)) {
						if (".project".equals(resource.getName())
								|| "PB.project".equals(resource.getName())
								|| ".classpath".equals(resource.getName())
								|| "Makefile".equals(resource.getName())
								|| resource.getName().startsWith("ant.")) {
						} else
							if(project.matchesResourcesPattern(resource) || project.matchesWOAppResourcesPattern(resource) || project.matchesClassesPattern(resource)) {
								buildRequired = true;
								return false;
							}
					}
			}
			return false;
		}
		/** Method needsUpdate.
		 * @ param kindOfChange
		 * @ return boolean
		 */
		private final boolean needsUpdate(int kindOfChange) {
			return IResourceDelta.ADDED == kindOfChange
					|| IResourceDelta.REMOVED == kindOfChange
					|| IResourceDelta.CHANGED == kindOfChange;
		}
		/**
		 * @return
		 */
		public boolean isBuildRequired() {
			return buildRequired;
		}
	}
	/**
	 * Method inExternalVM.
	 * 
	 * @param buildFile
	 * @param monitor
	 * @throws CoreException
	 */
	private void launchAntInExternalVM(IFile buildFile, IProgressMonitor monitor)
			throws CoreException {
		ILaunchConfiguration config = null;
		try {
			//config = this.createDefaultLaunchConfiguration(buildFile,
			// monitor);
			config = this.createDefaultLaunchConfiguration(buildFile);
		} catch (CoreException e) {
			config = null;
			WorkbenchUtilitiesPlugin.handleException(Display.getCurrent()
					.getActiveShell(), e, AntBuildMessages
					.getString("Build.Exception"));
			return;
		}
		try {
			//config= ExternalToolMigration.migrateRunInBackground(config);
			ILaunch launch = config.launch(ILaunchManager.RUN_MODE, monitor);
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			manager.removeLaunch(launch);	
		} finally {
			config = null;
		}
	}
	/**
	 * Creates and returns a default launch configuration for the given file.
	 * 
	 * @param file
	 * @return default launch configuration
	 * @throws CoreException
	 */
	private ILaunchConfiguration createDefaultLaunchConfiguration(IFile file)
			throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(IAntLaunchConfigurationConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		StringBuffer buffer = new StringBuffer(file.getProject().getName());
		buffer.append(' ');
		buffer.append(file.getName());
		buffer.append(" (WOLips)");
		String name = buffer.toString().trim();
		name = manager.generateUniqueLaunchConfigurationNameFrom(name);
		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null,
				name);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION,
				VariablesPlugin.getDefault().getStringVariableManager()
						.generateVariableExpression("workspace_loc",
								file.getFullPath().toString())); //$NON-NLS-1$
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
				"org.eclipse.ant.ui.AntClasspathProvider"); //$NON-NLS-1$
		IVMInstall defaultInstall= null;
		defaultInstall= JavaRuntime.getDefaultVMInstall();
		//try {
			//defaultInstall = JavaRuntime.computeVMInstall(workingCopy);
		//} catch (CoreException e) {
			//core exception thrown for non-Java project
			//defaultInstall= JavaRuntime.getDefaultVMInstall();
		//}
		if (defaultInstall != null) {
			String vmName = defaultInstall.getName();
			String vmTypeID = defaultInstall.getVMInstallType().getId();					
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, vmName);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, vmTypeID);
		}
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				MAIN_TYPE_NAME);
		workingCopy.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID,
				IAntUIConstants.REMOTE_ANT_PROCESS_FACTORY_ID);
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				"org.eclipse.ant.internal.ui.antsupport.InternalAntRunner");
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				(String) null);
		
		workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_SHOW_CONSOLE", false);
		workingCopy.setAttribute("org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", false);
		workingCopy.setAttribute("org.eclipse.ui.externaltools.ATTR_CAPTURE_OUTPUT", false);
		return workingCopy.doSave();
	}

}
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

package org.objectstyle.wolips.projectbuild.builder;

import java.util.Map;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.project.ant.BuildMessages;
import org.objectstyle.wolips.core.project.ant.RunAnt;
import org.objectstyle.wolips.core.resources.IWOLipsModel;
import org.objectstyle.wolips.projectbuild.WOProjectBuildConstants;

/**
 * @author uli
 */
public class WOAntBuilder
	extends IncrementalProjectBuilder {
	private static final int TOTAL_WORK_UNITS = 1;
	private String informUserString =
		"WOLips: "
			+ "To avoid frequent crashes don't forget to install the patched org.eclipse.core.ant plugin. "
			+ "It's available as a separate download and from the optional folder in the download.";

	/**
	 * Constructor for WOBuilder.
	 */
	public WOAntBuilder() {
		super();
	}

	private boolean isPatchInstalled() {
		String string =
			AntCorePlugin.getPlugin().getDescriptor().getProviderName();
		return string.endsWith("objectstyle.org");
	}

	/**
	 * Runs the build with the ant runner.
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.
	 * util. Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
		throws CoreException {
		if (AntRunner.isBuildRunning() || this.getProject() == null) {
			monitor.done();
			return null;
		}
		monitor.beginTask(
			BuildMessages.getString("Build.Monitor.Title"),
			WOAntBuilder.TOTAL_WORK_UNITS);
		if (!Preferences.getPREF_RUN_WOBUILDER_ON_BUILD()
			|| getProject() == null
			|| !getProject().exists()) {
			monitor.done();
			return null;
		}
		String aBuildFile = null;
		try {
			getProject().deleteMarkers(
				WOProjectBuildConstants.MARKER_TASK_GENERIC,
				false,
				IResource.DEPTH_ONE);
			if (!projectNeedsAnUpdate()
				&& kind != IncrementalProjectBuilder.FULL_BUILD) {
				monitor.done();
				return null;
			}
			aBuildFile = this.buildFile();
			if (checkIfBuildfileExist(aBuildFile)) {
				getProject().getFile(aBuildFile).deleteMarkers(
					WOProjectBuildConstants.MARKER_TASK_GENERIC,
					false,
					IResource.DEPTH_ONE);
				this.execute(monitor, aBuildFile);
			}
		} catch (Exception e) {
			this.handleException(e);
		}
		aBuildFile = null;

		/*monitor.beginTask(
					BuildMessages.getString("Build.Refresh.Title"),
					WOAntBuilder.TOTAL_WORK_UNITS);*/
		//this.forgetLastBuiltState();
		//getProject().refreshLocal(IProject.DEPTH_INFINITE, monitor);
		monitor.done();
		return null;
	}

	/**
	 * Method execute.
	 * @param kind
	 * @param args
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
				WOLipsLog.log(e);
			} finally {
				aMarker = null;
			}
		}

		RunAnt runAnt = new RunAnt();
		if (Preferences.getPREF_RUN_ANT_AS_EXTERNAL_TOOL()) {
			if (projectNeedsClean())
				runAnt.asExternalTool(
					getProject().getFile(aBuildFile),
					this.cleanTarget());
			runAnt.asExternalTool(
				getProject().getFile(aBuildFile),
				this.defaultTarget());

		} else {
			if (projectNeedsClean())
				runAnt.asAnt(
					getProject().getFile(aBuildFile),
					monitor,
					this.cleanTarget());
			runAnt.asAnt(
				getProject().getFile(aBuildFile),
				monitor,
				this.defaultTarget());
		}
		runAnt.waitUntilAntFinished();
	}
	/**
	 * Method projectNeedsClean.
	 * @return boolean
	 */
	private boolean projectNeedsClean() {
		//currently we get an an exception on clean
		//return this.getDelta(this.getProject()) == null;
		return false;
	}
	/**
	 * Method projectNeedsAnUpdate.
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
			WOLipsLog.log(e);
			return false;
		}
		return resourceValidator.isBuildRequired();
	}

	/**
	 * Method handleException.
	 * @param anException
	 */
	private void handleException(Exception anException) {
		IMarker aMarker = null;
		try {
			if (anException == null) {
				throw new NullPointerException("WOBuilder.handleException called without an exception.");
			}
			aMarker = this.getBuildfileMarker();
			aMarker.setAttribute(
				IMarker.MESSAGE,
				"WOLips: " + anException.getMessage());
		} catch (Exception e) {
			WOLipsLog.log(e);
		} finally {
			aMarker = null;
		}
	}

	private IMarker getBuildfileMarker() {
		IMarker aMarker = null;
		try {
			aMarker =
				getProject().getFile(this.buildFile()).createMarker(
					WOProjectBuildConstants.MARKER_TASK_GENERIC);
			aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		} catch (CoreException e) {
			WOLipsLog.log(e);
		}
		return aMarker;
	}
	/**
	 * Checks if the build file exists.
	 * @param aBuildFile
	 * @return boolean
	 */
	private boolean checkIfBuildfileExist(String aBuildFile) {
		try {
			if (getProject().getFile(aBuildFile).exists())
				return true;
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
		IMarker aMarker = null;
		try {
			aMarker =
				getProject().createMarker(
					WOProjectBuildConstants.MARKER_TASK_GENERIC);
			aMarker.setAttribute(
				IMarker.MESSAGE,
				"WOLips: Can not find: " + this.buildFile());
			aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		} catch (Exception anException) {
			WOLipsLog.log(anException);
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

	private final class BuildResourceValidator
		implements IResourceDeltaVisitor {
		private boolean buildRequired = false;
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
		 * Method examineResource. Examines changed resources for added and/or removed webobjects project
		 * resources and synchronizes project file.
		 * <br>
		 * @see #updateProjectFile(int, IResource, QualifiedName, IFile)
		 * @param resource
		 * @param kindOfChange
		 * @return boolean
		 * @throws CoreException
		 */
		private final boolean examineResource(
			IResource resource,
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
				case IResource.FOLDER :
					if (IWOLipsModel.EXT_FRAMEWORK.equals(resource.getFileExtension())
						|| IWOLipsModel.EXT_WOA.equals(resource.getFileExtension())
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
							buildRequired = true;
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
}

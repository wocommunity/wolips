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

import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.plugin.logging.WOLipsLog;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.project.ant.BuildMessages;
import org.objectstyle.wolips.core.project.ant.RunAnt;

/**
 * @author uli
 */
public class WOAntBuilder extends IncrementalProjectBuilder {
	private static final int TOTAL_WORK_UNITS = 1;
	/**
	 * Constructor for WOBuilder.
	 */
	public WOAntBuilder() {
		super();
	}
	private void waitForAnt(IProgressMonitor monitor) {
		monitor.subTask(BuildMessages.getString("Build.Waiting.Name"));
		IProgressMonitor subProgressMonitor =
			new SubProgressMonitor(monitor, 1);
		boolean exception = false;
		while (AntRunner.isBuildRunning() || exception) {
			try {
				this.wait(100);
				Thread.yield();
			} catch (InterruptedException interruptedException) {
				exception = true;
			}
		}
		subProgressMonitor.done();
	}
	/**
	 * Runs the build with the ant runner.
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.
	 * util. Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
		throws CoreException {
		if (AntRunner.isBuildRunning()) {
			monitor.done();
			return null;
		}
		monitor.beginTask(
			BuildMessages.getString("Build.Monitor.Title"),
			WOAntBuilder.TOTAL_WORK_UNITS);
		if (!Preferences
			.getBoolean(IWOLipsPluginConstants.PREF_RUN_WOBUILDER_ON_BUILD)
			|| getProject() == null
			|| !getProject().exists()) {
			monitor.done();
			return null;
		}
		String aBuildFile = null;
		try {
			getProject().deleteMarkers(
				IMarker.TASK,
				false,
				IResource.DEPTH_ONE);
			if (!projectNeedsAnUpdate() && kind != WOAntBuilder.FULL_BUILD) {
				monitor.done();
				return null;
			}
			aBuildFile = this.buildFile();
			if (checkIfBuildfileExist(aBuildFile)) {
				getProject().getFile(aBuildFile).deleteMarkers(
					IMarker.TASK,
					false,
					IProject.DEPTH_ONE);
				this.execute(kind, args, monitor, aBuildFile);
			}
		} catch (Exception e) {
			this.handleException(e);
		}
		aBuildFile = null;
		//this.forgetLastBuiltState();
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
	private void execute(
		int kind,
		Map args,
		IProgressMonitor monitor,
		String aBuildFile)
		throws Exception {
		RunAnt runAnt = new RunAnt();
		if (Preferences
			.getBoolean(IWOLipsPluginConstants.PREF_RUN_ANT_AS_EXTERNAL_TOOL)) {
			if (projectNeedsClean())
				runAnt.asExternalTool(
					getProject().getFile(aBuildFile),
					kind,
					monitor,
					this.cleanTarget());
			runAnt.asExternalTool(
				getProject().getFile(aBuildFile),
				kind,
				monitor,
				this.defaultTarget());

		} else {
			if (projectNeedsClean())
				runAnt.asAnt(
					getProject().getFile(aBuildFile).getLocation().toOSString(),
					monitor,
					this.cleanTarget());
			runAnt.asAnt(
				getProject().getFile(aBuildFile).getLocation().toOSString(),
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
		IResourceDelta aDelta = this.getDelta(this.getProject());
		if (aDelta == null)
			return true;
		IResourceDelta[] children = aDelta.getAffectedChildren();
		for (int i = 0; i < children.length; i++) {
			if (!isIgnoredPath(children[i].getProjectRelativePath()))
				return true;
		}
		return false;
	}
	/**
	 * Method isIgnoredPath.
	 * @param aPath
	 * @return boolean
	 */
	private boolean isIgnoredPath(IPath aPath) {
		if (aPath.isEmpty())
			return false;
		String aString = aPath.toString();
		return (
			aString.indexOf(".woa") > 0
				|| aString.indexOf(".framework") > 0
				|| aString.indexOf("ant.") > 0
				|| aString.indexOf("PB.project") > 0
				|| aString.indexOf("make") > 0);
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
			aMarker =
				getProject().getFile(this.buildFile()).createMarker(
					IMarker.TASK);
			aMarker.setAttribute(
				IMarker.MESSAGE,
				"WOLips: " + anException.getMessage());
			aMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
		} catch (Exception e) {
			WOLipsLog.log(e);
		} finally {
			aMarker = null;
		}
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
			aMarker = getProject().createMarker(IMarker.TASK);
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
}

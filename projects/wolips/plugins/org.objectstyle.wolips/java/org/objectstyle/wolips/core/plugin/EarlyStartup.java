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

package org.objectstyle.wolips.core.plugin;

import java.net.URL;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.listener.JavaElementChangeListener;
import org.objectstyle.wolips.core.listener.ResourceChangeListener;
import org.objectstyle.wolips.core.logging.WOLipsLog;
import org.objectstyle.wolips.core.preferences.Preferences;
import org.objectstyle.wolips.core.project.WOLipsCore;
import org.objectstyle.wolips.core.project.ant.RunAnt;
import org.objectstyle.wolips.core.util.WorkbenchUtilities;

/**
 * @author uli
 *
* Adds listeners for resource and java classpath changes to keep
* webobjects project file synchronized.
*/
public final class EarlyStartup {

	private static final String build_user_home_properties = "woproperties.xml";
	private static final String build_user_home_properties_pde_info =
		"PDE User please copy "
			+ EarlyStartup.build_user_home_properties
			+ " from the woproject/projects/buildscripts to wolips.";

	private WOLipsLog log = new WOLipsLog(EarlyStartup.class.getName(), WOLipsLog.ERROR);

	public EarlyStartup() {
		super();
	}
	/**
		 * Adds listeners for resource and java classpath changes to keep
		 * webobjects project file synchronized.
		 * <br>
		 * @see org.eclipse.ui.IStartup#earlyStartup()
		 */
	public void earlyStartup() {
		try {
			this.setUpPreferencesForPropertiesFile();
			this.writePropertiesFileToUserHome();
			this.setUpPreferencesAfterPropertiesFile();
			this.validateMandatoryAttributes();
			// add resource change listener to update project file on resource changes
			IResourceChangeListener resourceChangeListener =
				new ResourceChangeListener();
			WorkbenchUtilities.getWorkspace().addResourceChangeListener(
				resourceChangeListener,
				IResourceChangeEvent.PRE_AUTO_BUILD);
			// add element change listener to update project file on classpath changes
			IElementChangedListener javaElementChangeListener =
				new JavaElementChangeListener();
			JavaCore.addElementChangedListener(
				javaElementChangeListener,
				ElementChangedEvent.POST_CHANGE);
		} catch (Exception anException) {
			log.fatal(
				EarlyStartup.build_user_home_properties_pde_info,
				anException);
		}
	}
	/**
	 * Method setUpPreferencesForPropertiesFile.
	 */
	private void setUpPreferencesForPropertiesFile() {
		String currentVersion =
			WOLipsPlugin
				.getDefault()
				.getDescriptor()
				.getVersionIdentifier()
				.toString();
		String preferencesVersion =
			Preferences.getPREF_WOLIPS_VERSION_EARLY_STARTUP();
		if (!currentVersion.equals(preferencesVersion))
			Preferences.setPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH(true);
	}
	/**
	 * Method setUpPreferencesAfterPropertiesFile.
	 */
	private void setUpPreferencesAfterPropertiesFile() {
		Preferences.setPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH(false);
		Preferences.setPREF_WOLIPS_VERSION_EARLY_STARTUP(
			WOLipsPlugin
				.getDefault()
				.getDescriptor()
				.getVersionIdentifier()
				.toString());
	}
	/**
	 * Method writePropertiesFileToUserHome.
	 * @throws Exception
	 */
	private void writePropertiesFileToUserHome() throws Exception {
		if (!Preferences.getPREF_REBUILD_WOBUILD_PROPERTIES_ON_NEXT_LAUNCH())
			return;
		URL relativeBuildFile = null;
		URL buildFile = null;
		IProgressMonitor monitor = null;
		relativeBuildFile =
			new URL(
				WOLipsPlugin.baseURL(),
				EarlyStartup.build_user_home_properties);
		buildFile = Platform.asLocalURL(relativeBuildFile);
		monitor = new NullProgressMonitor();
		RunAnt runAnt = new RunAnt();
		try {
			runAnt.asAnt(buildFile.getFile().toString(), monitor, null);
		} catch (Throwable throwable) {
			//this will allways fail for the first time
		} finally {
			relativeBuildFile = null;
			buildFile = null;
			monitor = null;
			runAnt = null;
			relativeBuildFile = null;
			buildFile = null;
			monitor = null;
		}
	}

	/**
	* Method validateMandatoryAttributes.
	*/
	private void validateMandatoryAttributes() {
		if (WOLipsCore
			.getClasspathVariablesAccessor()
			.getNextRootClassPathVariable()
			== null) {
			try {
				WOLipsCore
					.getClasspathVariablesAccessor()
					.setNextRootClassPathVariable(
					new Path(WorkbenchUtilities.getWOVariables().systemRoot()));
			} catch (JavaModelException e) {
				log.fatal(e);
			} catch (Exception e) {
				log.fatal(e);
			}
		}
		if (WOLipsCore
			.getClasspathVariablesAccessor()
			.getNextLocalRootClassPathVariable()
			== null) {
			try {
				WOLipsCore
					.getClasspathVariablesAccessor()
					.setNextLocalRootClassPathVariable(
					new Path(WorkbenchUtilities.getWOVariables().localRoot()));
			} catch (JavaModelException e) {
				log.fatal(e);
			} catch (Exception e) {
				log.fatal(e);
			}
		}
		if (WOLipsCore
			.getClasspathVariablesAccessor()
			.getNextSystemRootClassPathVariable()
			== null) {
			try {
				WOLipsCore
					.getClasspathVariablesAccessor()
					.setNextSystemRootClassPathVariable(
					new Path(WorkbenchUtilities.getWOVariables().systemRoot()));
			} catch (JavaModelException e) {
				log.fatal(e);
			} catch (Exception e) {
				log.fatal(e);
			}
		}
		if (WOLipsCore
			.getClasspathVariablesAccessor()
			.getUserHomeClassPathVariable()
			== null) {
			try {
				WOLipsCore
					.getClasspathVariablesAccessor()
					.setUserHomeClassPathVariable(
					new Path(WorkbenchUtilities.getWOVariables().userHome()));
			} catch (JavaModelException e) {
				log.fatal(e);
			} catch (Exception e) {
				log.fatal(e);
			}
		}
		if (WOLipsCore
			.getClasspathVariablesAccessor()
			.getProjectWonderHomeClassPathVariable()
			== null) {
			try {
				WOLipsCore
					.getClasspathVariablesAccessor()
					.setProjectWonderHomeClassPathVariable(
						new Path(
							WorkbenchUtilities.getWOVariables().userHome()
								+ "/Roots"));
			} catch (JavaModelException e) {
				log.fatal(e);
			} catch (Exception e) {
				log.fatal(e);
			}
		}
	}
}

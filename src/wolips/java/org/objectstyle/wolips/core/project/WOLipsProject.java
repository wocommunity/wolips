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

package org.objectstyle.wolips.core.project;

import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;

/**
 * @author uli
 *
 * Use this class to modify an IProject.
 */
public class WOLipsProject implements IWOLipsPluginConstants {
	private IProject project;
	private WOLipsProjectNatures woLipsProjectNatures;

	/**
	 * @param project
	 */
	public WOLipsProject(IProject project) {
		this.project = project;
	}

	/**
	 * @return IProject
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * @return WOLipsProjectNatures
	 */
	public WOLipsProjectNatures getWOLipsProjectNatures() {
		if (woLipsProjectNatures == null)
			woLipsProjectNatures = new WOLipsProjectNatures(this);
		return woLipsProjectNatures;
	}

	/**
	 * @author uli
	 *
	 * Add and remove WOLips natures.
	 */
	public class WOLipsProjectNatures implements IWOLipsPluginConstants {
		private WOLipsProject woLipsProject;

		/**
		 * @param woLipsProject
		 */
		protected WOLipsProjectNatures(WOLipsProject woLipsProject) {
			this.woLipsProject = woLipsProject;
		}
		/**
		 * @return IProject
		 */
		private IProject getProject() {
			return woLipsProject.getProject();
		}
		/**
		 * @param nature
		 * @return boolean
		 * @throws CoreException
		 */
		private boolean projectHasNature(String nature) throws CoreException {
			return this.getProject().hasNature(nature);
		}
		/**
		 * @return true if at least one of the WOLips natures is installed.
		 * @throws CoreException
		 */
		public boolean hasWOLipsNature() throws CoreException {
			return (this.isApplication() || this.isFramework());
		}
		/**
		 * @return true only if one of the WOLips application natures is installed. False does not mean that this is a framework.
		 * @throws CoreException
		 */
		public boolean isApplication() throws CoreException {
			return (
				this.projectHasNature(ANT_APPLICATION_NATURE_ID)
					|| this.projectHasNature(INCREMENTAL_APPLICATION_NATURE_ID)
					|| this.projectHasNature(WO_APPLICATION_NATURE_OLD));
		}
		/**
		 * @return true only if one of the WOLips framework natures is installed. False does not mean that this is an application.
		 * @throws CoreException
		 */
		public boolean isFramework() throws CoreException {
			return (
				this.projectHasNature(ANT_FRAMEWORK_NATURE_ID)
					|| this.projectHasNature(INCREMENTAL_FRAMEWORK_NATURE_ID)
					|| this.projectHasNature(WO_FRAMEWORK_NATURE_OLD));
		}
		/**
		 * @return true only if one of the WOLips ant natures is installed. False does not mean that this is an incremental nature.
		 * @throws CoreException
		 */
		public boolean isAnt() throws CoreException {
			return (
				this.projectHasNature(ANT_FRAMEWORK_NATURE_ID)
					|| this.projectHasNature(ANT_APPLICATION_NATURE_ID));
		}
		/**
		 * @return true only if one of the WOLips incremental natures is installed. False does not mean that this is an ant nature.
		 * @throws CoreException
		 */
		public boolean isIncremental() throws CoreException {
			return (
				this.projectHasNature(INCREMENTAL_APPLICATION_NATURE_ID)
					|| this.projectHasNature(INCREMENTAL_FRAMEWORK_NATURE_ID));
		}

		/**
		 * @param isFramework
		 * @param useTargetBuilder currently does nothing
		 * Removes all WOLips nature, calls deconfigure on them, adds the ant nature and calls configure on it.
		 * @throws CoreException
		 */
		public void setAntNature(boolean isFramework, boolean useTargetBuilder)
			throws CoreException {
			//TODO : add targetbuilder support
			this.callDeconfigure();
			this.removeWOLipsNatures();
			if (isFramework) {
				this.addNature(ANT_FRAMEWORK_NATURE_ID);
			} else {
				this.addNature(ANT_APPLICATION_NATURE_ID);
			}
			this.callConfigure();
		}
		/**
		 * @param isFramework
		 * @param useTargetBuilder currently does nothing
		 * Removes all WOLips nature, calls deconfigure on them, adds the ant nature and calls configure on it.
		 * @throws CoreException
		 */
		public void setIncrementalNature(
			boolean isFramework,
			boolean useTargetBuilder)
			throws CoreException {
			//TODO : add targetbuilder support
			this.callDeconfigure();
			this.removeWOLipsNatures();
			if (isFramework) {
				this.addNature(INCREMENTAL_FRAMEWORK_NATURE_ID);
			} else {
				this.addNature(INCREMENTAL_APPLICATION_NATURE_ID);
			}
			this.callConfigure();
		}
		/**
		 * @param natureID
		 * @throws CoreException
		 */
		private void addNature(String natureID) throws CoreException {
			String[] projectNatures = this.getProjectNatures();
			Vector projectNaturesVector = new Vector();
			for (int i = 0; i < projectNatures.length; i++) {
				projectNaturesVector.add(projectNatures[i]);
			}
			projectNaturesVector.add(natureID);
			this.getProject().getDescription().setNatureIds((String[])projectNaturesVector.toArray());
		}
		/**
		 * @return IProjectNature[]
		 * @throws CoreException
		 */
		public IProjectNature[] getWOLipsNatures() throws CoreException {
			Vector naturesVector = new Vector();
			for (int i = 0; i < WOLIPS_NATURES.length; i++) {
				if (this.projectHasNature(WOLIPS_NATURES[i])) {
					naturesVector.add(
						this.getProject().getNature(WOLIPS_NATURES[i]));
				}
			}
			if(naturesVector.size() == 0)
			return new IProjectNature[0];
			return (IProjectNature[])naturesVector.toArray(new IProjectNature[naturesVector.size()]);
		}
		/**
		 * @return String[]
		 * @throws CoreException
		 */
		private String[] getProjectNatures() throws CoreException {
			return this.getProject().getDescription().getNatureIds();
		}
		/**
		 * @throws CoreException
		 */
		public void removeWOLipsNatures() throws CoreException {
			String[] projectNatures = this.getProjectNatures();
			Vector naturesVector = new Vector();
			for (int i = 0; i < projectNatures.length; i++) {
				if (!this.isWOLipsNature(projectNatures[i])) {
					naturesVector.add(
						this.getProject().getNature(WOLIPS_NATURES[i]));
				}
			}
			projectNatures = new String[naturesVector.size()];
			projectNatures = (String[]) naturesVector.toArray(projectNatures);
			this.getProject().getDescription().setNatureIds(projectNatures);
		}
		/**
		 * @param natureID
		 * @return boolean
		 */
		private boolean isWOLipsNature(String natureID) {
			for (int i = 0; i < WOLIPS_NATURES.length; i++) {
				if (WOLIPS_NATURES[i].equals(natureID))
					return true;
			}
			return false;
		}
		/**
		 * Calls configure on all WOLips natures.
		 * @throws CoreException
		 */
		public void callConfigure() throws CoreException {
			IProjectNature[] projectNatures = this.getWOLipsNatures();
			for (int i = 0; i < projectNatures.length; i++) {
				projectNatures[i].configure();
			}
		}
		/**
		 * Calls deconfigure on all WOLips natures.
		 * @throws CoreException
		 */
		public void callDeconfigure() throws CoreException {
			IProjectNature[] projectNatures = this.getWOLipsNatures();
			for (int i = 0; i < projectNatures.length; i++) {
				projectNatures[i].deconfigure();
			}
		}

		/**
		 * @param natures
		 * @throws CoreException
		 */
		private void setProjectNatures(String[] natures) throws CoreException {
			this.getProject().getDescription().setNatureIds(natures);

		}
	}
}

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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.org.objectstyle.wolips.logging.WOLipsLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.wolips.core.plugin.WOLipsUtils;

/**
 * @author uli
 *
 * Use this class to modify an IProject.
 */
public class WOLipsProject implements IWOLipsPluginConstants {
	private IProject project;
	private NaturesAccessor naturesAccessor;
	private BuilderAccessor builderAccessor;
	private PBProjectFilesAccessor pbProjectFilesAccessor;

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
	public NaturesAccessor getNaturesAccessor() {
		if (naturesAccessor == null)
		naturesAccessor = new NaturesAccessor(this);
		return naturesAccessor;
	}
	/**
	 * @return BuilderAccessor
	 */
	public BuilderAccessor getBuilderAccessor() {
		if (builderAccessor == null)
			builderAccessor = new BuilderAccessor(this);
		return builderAccessor;
	}
	/**
	 * @return PBProjectFilesHandler
	 */
	public PBProjectFilesAccessor getPBProjectFilesAccessor() {
		if (pbProjectFilesAccessor == null)
			pbProjectFilesAccessor = new PBProjectFilesAccessor(this);
		return pbProjectFilesAccessor;
	}
	/**
	 * Method isWOProjectResource.
	 * @param aResource
	 * @return boolean
	 */
	public static boolean isWOProjectResource(IResource aResource) {
		if (aResource == null)
			return false;
		try {
			switch (aResource.getType()) {
				case IResource.PROJECT :
					return new WOLipsProject((IProject) aResource)
						.getNaturesAccessor()
						.hasWOLipsNature();
				default :
					return aResource.getProject() != null
						&& new WOLipsProject(aResource.getProject())
							.getNaturesAccessor()
							.hasWOLipsNature();
			}
		} catch (CoreException e) {
			return false;
		}
	}
	/**
	 * @author uli
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	private class WOLipsProjectInnerClass {
		private WOLipsProject woLipsProject;
		/**
		 * @param woLipsProject
		 */
		protected WOLipsProjectInnerClass(WOLipsProject woLipsProject) {
			this.woLipsProject = woLipsProject;
		}
		/**
		 * @return IProject
		 */
		protected IProject getProject() {
			return woLipsProject.getProject();
		}

	}
	/**
	 * @author uli
	 *
	 * Add and remove WOLips natures.
	 */
	public class NaturesAccessor
		extends WOLipsProjectInnerClass
		implements IWOLipsPluginConstants {
		/**
		 * @param woLipsProject
		 */
		protected NaturesAccessor(WOLipsProject woLipsProject) {
			super(woLipsProject);
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
				projectHasNature(ANT_APPLICATION_NATURE_ID)
				|| projectHasNature(INCREMENTAL_APPLICATION_NATURE_ID)
				|| projectHasNature(WO_APPLICATION_NATURE_OLD));
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
			//this.callDeconfigure();
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
			List naturesList = new ArrayList(Arrays.asList(projectNatures));
      
      if (!naturesList.contains(natureID)) {
        naturesList.add(natureID);
        IProjectDescription desc = this.getProject().getDescription();

        desc.setNatureIds(
          (String[]) naturesList.toArray(new String[naturesList.size()])
        );
        _setDescription (this.getProject(), desc);
      }
		}
    
    private void _setDescription(
      final IProject f_project,
      final IProjectDescription f_desc) {
      _showProgress(new IRunnableWithProgress() {
        public void run(IProgressMonitor pm) {
          try {
            f_project.setDescription(f_desc, pm);
          } catch (CoreException up) {
            pm.done();
          }
        }
      });
    }

    private void _showProgress(IRunnableWithProgress rwp) {
      IWorkbench workbench = PlatformUI.getWorkbench();
      Shell shell = null;
      if (null != workbench) {
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (null != window) {
          shell = window.getShell();
        }
      }

      ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);

      try {
        pmd.run(true, true, rwp);
      } catch (InvocationTargetException e) {
        // handle exception
        e.printStackTrace();
      } catch (InterruptedException e) {
        // handle cancelation
        e.printStackTrace();
      }
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
			if (naturesVector.size() == 0)
				return new IProjectNature[0];
			return (IProjectNature[]) naturesVector.toArray(
				new IProjectNature[naturesVector.size()]);
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
			List naturesList = new ArrayList(Arrays.asList(projectNatures));
			for (int i = 0; i < projectNatures.length; i++) {
				if (isWOLipsNature(projectNatures[i])) {
					naturesList.remove(projectNatures[i]);
				}
			}
			projectNatures = new String[naturesList.size()];
			projectNatures = (String[]) naturesList.toArray(projectNatures);
      IProjectDescription desc = this.getProject().getDescription();
      desc.setNatureIds(projectNatures);
      _setDescription(this.getProject(), desc);
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
			//This is needed by the project wizard
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
			/*
                        IProjectNature[] projectNatures = this.getWOLipsNatures();
			for (int i = 0; i < projectNatures.length; i++) {
				projectNatures[i].deconfigure();
			}
                        */
		}

		/**
		 * @param natures
		 * @throws CoreException
		 */
		private void setProjectNatures(String[] natures) throws CoreException {
			this.getProject().getDescription().setNatureIds(natures);

		}
	}

	/**
	 * @author uli
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	public class BuilderAccessor extends WOLipsProjectInnerClass {
		public static final int BuilderNotFound = -1;
		/**
		 * @param wolipsProject
		 */
		protected BuilderAccessor(WOLipsProject wolipsProject) {
			super(wolipsProject);
		}
		/**
		 * Method removeJavaBuilder.
		 * @param project
		 */
		public void removeBuilder(String aBuilder) throws CoreException {
			IProjectDescription desc = null;
			ICommand[] coms = null;
			ArrayList comList = null;
			List tmp = null;
			ICommand[] newCom = null;
			try {
				desc = this.getProject().getDescription();
				coms = desc.getBuildSpec();
				comList = new ArrayList();
				tmp = Arrays.asList(coms);
				comList.addAll(tmp);
				boolean foundJBuilder = false;
				for (int i = 0; i < comList.size(); i++) {
					if (((ICommand) comList.get(i))
						.getBuilderName()
						.equals(aBuilder)) {
						comList.remove(i);
						foundJBuilder = true;
					}
				}
				if (foundJBuilder) {
					newCom = new ICommand[comList.size()];
					for (int i = 0; i < comList.size(); i++) {
						newCom[i] = (ICommand) comList.get(i);
					}
					desc.setBuildSpec(newCom);
					this.getProject().setDescription(desc, null);
				}
			} finally {
				desc = null;
				coms = null;
				comList = null;
				tmp = null;
				newCom = null;
			}
		}
		/**
		 * Method installBuilder.
		 * @param aProject
		 * @param aBuilder
		 * @throws CoreException
		 */
		public void installBuilder(String aBuilder) throws CoreException {
			IProjectDescription desc = null;
			ICommand[] coms = null;
			ICommand[] newIc = null;
			ICommand command = null;
			try {
				desc = this.getProject().getDescription();
				coms = desc.getBuildSpec();
				boolean foundJBuilder = false;
				for (int i = 0; i < coms.length; i++) {
					if (coms[i].getBuilderName().equals(aBuilder)) {
						foundJBuilder = true;
					}
				}
				if (!foundJBuilder) {
					newIc = null;
					command = desc.newCommand();
					command.setBuilderName(aBuilder);
					newIc = new ICommand[coms.length + 1];
					System.arraycopy(coms, 0, newIc, 0, coms.length);
					newIc[coms.length] = command;
					desc.setBuildSpec(newIc);
					this.getProject().setDescription(desc, null);
				}
			} finally {
				desc = null;
				coms = null;
				newIc = null;
				command = null;
			}
		}
		/**
		 * Method isBuilderInstalled.
		 * @param aProject
		 * @param anID
		 * @return boolean
		 */
		public boolean isBuilderInstalled(String anID) {
			try {
				ICommand[] nids =
					this.getProject().getDescription().getBuildSpec();
				for (int i = 0; i < nids.length; i++) {
					if (nids[i].getBuilderName().equals(anID))
						return true;
				}
			} catch (Exception anException) {
				WOLipsLog.log(anException);
				return false;
			}
			return false;
		}
		/**
			 * Method positionForBuilder.
			 * @param aProject
			 * @param aBuilder
			 * @return int
			 * @throws CoreException
			 */
		public int positionForBuilder(String aBuilder) throws CoreException {
			IProjectDescription desc = null;
			ICommand[] coms = null;
			try {
				desc = this.getProject().getDescription();
				coms = desc.getBuildSpec();
				for (int i = 0; i < coms.length; i++) {
					if (coms[i].getBuilderName().equals(aBuilder))
						return i;
				}
			} finally {
				desc = null;
				coms = null;
			}
			return BuilderNotFound;
		}
		/**
		 * Method installBuilderAtPosition.
		 * @param aProject
		 * @param aBuilder
		 * @param installPos
		 * @param arguments
		 * @throws CoreException
		 */
		public void installBuilderAtPosition(
			String aBuilder,
			int installPos,
			Map arguments)
			throws CoreException {
			IProjectDescription desc = this.getProject().getDescription();
			ICommand[] coms = desc.getBuildSpec();
			if (arguments == null)
				arguments = new HashMap();
			for (int i = 0; i < coms.length; i++) {
				if (coms[i].getBuilderName().equals(aBuilder)
					&& coms[i].getArguments().equals(arguments))
					return;
			}
			ICommand[] newIc = null;
			ICommand command = desc.newCommand();
			command.setBuilderName(aBuilder);
			command.setArguments(arguments);
			newIc = new ICommand[coms.length + 1];
			if (installPos <= 0) {
				System.arraycopy(coms, 0, newIc, 1, coms.length);
				newIc[0] = command;
			} else if (installPos >= coms.length) {
				System.arraycopy(coms, 0, newIc, 0, coms.length);
				newIc[coms.length] = command;
			} else {
				System.arraycopy(coms, 0, newIc, 0, installPos);
				newIc[installPos] = command;
				System.arraycopy(
					coms,
					installPos,
					newIc,
					installPos + 1,
					coms.length - installPos);
			}
			desc.setBuildSpec(newIc);
			this.getProject().setDescription(desc, null);
		}
	}
	/**
	 * @author uli
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code Template
	 */
	public class PBProjectFilesAccessor extends WOLipsProjectInnerClass {
		private Hashtable pbProjectFileAccessors = new Hashtable();
		/**
		 * @param wolipsProject
		 */
		protected PBProjectFilesAccessor(WOLipsProject wolipsProject) {
			super(wolipsProject);
		}
		/**
		 * @param resource
		 * @return PBProjectFileAccessor
		 */
		public PBProjectFileAccessor getPBProjectFileAccessor(IResource resource) {
			if (resource == null) return null;
			IFolder folder = this.getParentFolderWithPBProject(resource);
			if(folder == null) return null;
			PBProjectFileAccessor pbProjectFileAccessor = (PBProjectFileAccessor)pbProjectFileAccessors.get(folder.getFullPath().toOSString());
			if(pbProjectFileAccessor != null) return pbProjectFileAccessor;
			return new PBProjectFileAccessor(this, folder);
		}
		/**
		 * @param resource
		 * @return IFolder
		 */
		protected IFolder getParentFolderWithPBProject(IResource resource) {
			if(resource.getType() == IResource.FILE)
			return getParentFolderWithPBProject((IFolder)resource.getParent());
			if(resource.getType() == IResource.FOLDER)
			return getParentFolderWithPBProject((IFolder)resource);
			if(resource.getType() == IResource.PROJECT)
			return getParentFolderWithPBProject((IFolder)resource);
			return null;
		}
		/**
		 * Method getParentFolderWithPBProject.
		 * @param aFolder
		 * @return IFolder or one the parents with PB.project if one is found. Null
		 * is returned when Projects PB.project is found
		 */
		protected IFolder getParentFolderWithPBProject(IFolder aFolder) {
			IFolder findFolder = aFolder;
			while ((findFolder
				.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
				== null)
				&& (findFolder.getParent() != null)
				&& (findFolder.getParent().getType() != IProject.PROJECT)) {
				findFolder = (IFolder) findFolder.getParent();
			}
			if (findFolder.getParent() == null)
				return null;
			if (findFolder.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
				!= null)
				return findFolder;
			return null;
		}
		
		public class PBProjectFileAccessor {
			private PBProjectFilesAccessor pbProjectFilesAccessor;
			public PBProjectFileAccessor(PBProjectFilesAccessor pbProjectFilesAccessor, IFolder folder) {
				this.pbProjectFilesAccessor = pbProjectFilesAccessor;
			}
			/**
			 * @return PBProjectFilesAccessor
			 */
			protected PBProjectFilesAccessor getPbProjectFilesAccessor() {
				return pbProjectFilesAccessor;
			}

		}
		/**
		 * 
		 */
		public void cleanAllFileTables() throws IOException {
		ArrayList arrayList = new ArrayList();
		WOLipsUtils woLipsUtils = new WOLipsUtils();
		woLipsUtils.findFilesInResourceByName(arrayList,this.getProject(),IWOLipsPluginConstants.PROJECT_FILE_NAME);
		for(int i = 0; i < arrayList.size(); i++) {
			IResource resource = (IResource)arrayList.get(i);
			PBProjectUpdater pbProjectUpdater = PBProjectUpdater.instance(resource.getParent());
			pbProjectUpdater.cleanTables();
		}
		}
	}

}

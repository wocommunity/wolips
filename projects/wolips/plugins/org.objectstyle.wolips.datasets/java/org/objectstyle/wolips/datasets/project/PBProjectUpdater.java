/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2004 The ObjectStyle Group 
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
package org.objectstyle.wolips.datasets.project;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.objectstyle.woenvironment.pb.PBProject;
import org.objectstyle.woenvironment.util.FileStringScanner;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.adaptable.Project;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;
/**
 * @author uli
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */
public final class PBProjectUpdater {
	//	local framework search for PB.project
	private static final String DefaultLocalFrameworkSearch = "$(NEXT_ROOT)$(LOCAL_LIBRARY_DIR)/Frameworks";
	//do not cache PB.projects see bug #693046
	//private static Hashtable projectUpdater = new Hashtable();
	//public static String PBProject = "PB.projectContainer"; moved to
	// IWOLipsPluginConstants.PROJECT_FILE_NAME (mn)
	private PBProject pbProject;
	private IContainer projectContainer;
	private static final String dirtyPBProject = "<?xml";
	/**
	 * Constructor for PBProjectUpdater.
	 * 
	 * @param aProjectContainer
	 */
	private PBProjectUpdater(IContainer aProjectContainer) {
		super();
		//check if theres a PB.project in the Container. If not go to the
		// parent
		IContainer findContainer = aProjectContainer;
		while ((findContainer.findMember(IWOLipsModel.PROJECT_FILE_NAME) == null)
				&& (findContainer.getParent() != null)) {
			findContainer = findContainer.getParent();
		}
		if (findContainer.getParent() == null)
			this.projectContainer = findContainer.getProject();
		if (findContainer.findMember(IWOLipsModel.PROJECT_FILE_NAME) != null)
			this.projectContainer = findContainer;
		this.removeProjectMarker();
		this.getPBProject(this.projectContainer);
		//projectContainer = aProjectContainer;
	}
	private final void removeProjectMarker() {
		try {
			IFile aFile = this.projectContainer.getFile(new Path(
					IWOLipsModel.PROJECT_FILE_NAME));
			if (aFile.exists())
				aFile
						.deleteMarkers(IMarker.PROBLEM, false,
								IResource.DEPTH_ONE);
		} catch (Exception e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}
	private final void addProjectMarker() {
		try {
			IFile aFile = this.projectContainer.getFile(new Path(
					IWOLipsModel.PROJECT_FILE_NAME));
			if (aFile.exists()) {
				IMarker marker = aFile.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.MESSAGE,
						"Error while updating PB.project");
			}
		} catch (Exception e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}
	private final void handleException(Throwable throwable) {
		this.addProjectMarker();
		WorkbenchUtilitiesPlugin.handleException(null, throwable,
				"An error occured while saving the PB.project in project: "
						+ this.projectContainer.getProject().getName());
	}
	/**
	 * Method instance.
	 * 
	 * @param aProjectContainer
	 * @return PBProjectUpdater
	 */
	public static PBProjectUpdater instance(IContainer aProjectContainer) {
		//		do not cache PB.projects see bug #693046
		/*
		 * PBProjectUpdater returnValue = (PBProjectUpdater)
		 * PBProjectUpdater.projectUpdater.get( aProjectContainer); if
		 * (returnValue == null) { returnValue = new
		 * PBProjectUpdater(aProjectContainer);
		 * PBProjectUpdater.projectUpdater.put(aProjectContainer, returnValue); }
		 * return returnValue;
		 */
		return new PBProjectUpdater(aProjectContainer);
	}
	//	/**
	//	 * Method updatePBProject.
	//	 * @throws CoreException
	//	 */
	//	public void updatePBProject() throws CoreException {
	//		syncPBProjectWithProject();
	//		if (projectContainer != null)
	//			try {
	//				PBProjectNotifications.postPBProjectDidUpgradeNotification(
	//					projectContainer.getName());
	//			} catch (Exception exception) {
	//				WOLipsLog.log(exception);
	//			}
	//	}
	/**
	 * On MacOSX the EOModeler converts the PB.project file to xml.
	 * 
	 * @param aFile
	 */
	private void fixEOModelerMacOSXBug(File aFile) {
		String file = null;
		try {
			if ((aFile != null) && (aFile.exists())) {
				file = FileStringScanner.stringFromFile(aFile);
				if (file.startsWith(PBProjectUpdater.dirtyPBProject)) {
					Project project = (Project)this.projectContainer
							.getProject().getAdapter(Project.class);
					boolean isFramework = project.isFramework();
					this.pbProject = new PBProject(aFile, isFramework);
					String message = this.projectContainer.getProject()
							.getName()
							+ ": The EOModeler has converted your PB.project to an XML file. Please select Update PB.project from the WOLips context menu.";
					DataSetsPlugin.informUser(null, message);
				}
			}
		} catch (Exception anException) {
			this.handleException(anException);
		} finally {
			file = null;
		}
	}
	/**
	 * Method getPBProject.
	 * 
	 * @param aProject
	 */
	private void getPBProject(IContainer aProject) {
		File aFile = aProject.getFile(new Path(IWOLipsModel.PROJECT_FILE_NAME))
				.getLocation().toFile();
		this.pbProject = null;
		fixEOModelerMacOSXBug(aFile);
		try {
			boolean sync = !aFile.exists();
			Project project = (Project)this.projectContainer
			.getProject().getAdapter(Project.class);
			this.pbProject = new PBProject(aFile, project.isFramework());
			if (sync)
				syncPBProjectWithProject();
		} catch (Exception anException) {
			this.handleException(anException);
		} finally {
			aFile = null;
		}
	}
	/**
	 */
	public void cleanTables() {
		this.syncClasses(new ArrayList());
		this.syncWOAppResources(new ArrayList());
		this.syncWOComponents(new ArrayList());
		this._saveChanges();
	}
	private void _saveChanges() {
		this.pbProject.saveChanges();
		_tryRefresh();
	}
	/**
	 * attempt to refresh Eclipse' idea of the resource to avoid "out of synch
	 * warnings" to user
	 */
	private void _tryRefresh() {
		if (null != this.projectContainer) {
			try {
				IResource res = this.projectContainer
						.findMember(IWOLipsModel.PROJECT_FILE_NAME);
				if (null != res)
					res.refreshLocal(IResource.DEPTH_ZERO, null);
			} catch (CoreException up) {
				// no idea how to handle this case, ignore for now (and log, of
				// course
				this.handleException(up);
			}
		}
	}
	/**
	 * Method syncPBProjectWithProject.
	 */
	private void syncPBProjectWithProject() {
		try {
			this.pbProject.update();
			this.syncFilestable();
			this.syncProjectName();
			_saveChanges();
		} catch (Exception ioex) {
			this.handleException(ioex);
		}
	}
	/**
	 * Method syncFilestable.
	 */
	private void syncFilestable() {
		ArrayList aClassesList = new ArrayList();
		ArrayList aWOComponentsList = new ArrayList();
		ArrayList aWOAppResourcesList = new ArrayList();
		IResource[] resources;
		try {
			resources = this.projectContainer.members();
		} catch (Exception anException) {
			this.handleException(anException);
			aClassesList = null;
			aWOComponentsList = null;
			aWOAppResourcesList = null;
			resources = null;
			return;
		}
		int lastResource = resources.length;
		int i = 0;
		while (i < lastResource) {
			IResource aResource = resources[i];
			i++;
			proceedResource(aResource, aClassesList, aWOComponentsList,
					aWOAppResourcesList);
		}
		this.syncClasses(aClassesList);
		this.syncWOComponents(aWOComponentsList);
		this.syncWOAppResources(aWOAppResourcesList);
	}
	/**
	 * Method proceedResource.
	 * 
	 * @param aResource
	 * @param aClassesList
	 * @param aWOComponentsList
	 * @param aWOAppResourcesList
	 */
	private void proceedResource(IResource aResource, List aClassesList,
			List aWOComponentsList, List aWOAppResourcesList) {
		try {
			String aPath = aResource.getProjectRelativePath().toString();
			File aFile = new File(aResource.getLocation().toOSString());
			IFolder aFolder = null;
			if (aFile.isDirectory())
				aFolder = this.projectContainer.getFolder(aResource
						.getProjectRelativePath());
			if (aFolder != null) {
				if (aPath.endsWith(".wo"))
					aWOComponentsList.add(aPath);
				else if (!aPath.endsWith(".woa") && !aPath.endsWith(".build")
						&& !aPath.endsWith(".framework")) {
					IResource[] resources;
					resources = aFolder.members();
					int lastResource = resources.length;
					int i = 0;
					while (i < lastResource) {
						IResource aFolderResource = resources[i];
						i++;
						this.proceedResource(aFolderResource, aClassesList,
								aWOComponentsList, aWOAppResourcesList);
					}
				}
			} else {
				if (aPath.endsWith(".java"))
					aClassesList.add(aPath);
				if (aPath.endsWith(".api"))
					aWOAppResourcesList.add(aPath);
			}
		} catch (Exception anException) {
			this.handleException(anException);
		}
	}
	/**
	 * Method syncProjectName.
	 */
	public void syncProjectName() {
		if (!this.projectContainer.getName().equals(
				this.pbProject.getProjectName())) {
			this.pbProject.setProjectName(this.projectContainer.getName());
			try {
				this._saveChanges();
			} catch (Throwable e) {
				this.handleException(e);
			}
		}
	}
	/**
	 * Method syncClasses.
	 * 
	 * @param list
	 */
	private void syncClasses(List list) {
		this.pbProject.setClasses(list);
	}
	/**
	 * Method syncWOComponents.
	 * 
	 * @param list
	 */
	private void syncWOComponents(List list) {
		this.pbProject.setWoComponents(list);
	}
	/**
	 * Method syncWOAppResources.
	 * 
	 * @param list
	 */
	private void syncWOAppResources(List list) {
		this.pbProject.setWoAppResources(list);
	}
	/**
	 * Method syncFilestable.
	 * 
	 * @param changedResources
	 * @param kindOfChange
	 */
	public void syncFilestable(Map changedResources, int kindOfChange) {
		List actualResources;
		String currentKey;
		Object[] allKeys = changedResources.keySet().toArray();
		for (int i = 0; i < allKeys.length; i++) {
			currentKey = (String) allKeys[i];
			if (IWOLipsModel.RESOURCES_ID.equals(currentKey)) {
				actualResources = this.pbProject.getWoAppResources();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						this.pbProject.setWoAppResources(addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						this.pbProject.setWoAppResources(removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (IWOLipsModel.WS_RESOURCES_ID.equals(currentKey)) {
				actualResources = this.pbProject.getWebServerResources();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						this.pbProject.setWebServerResources(addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						this.pbProject.setWebServerResources(removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (IWOLipsModel.CLASSES_ID.equals(currentKey)) {
				actualResources = this.pbProject.getClasses();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						this.pbProject.setClasses(addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						this.pbProject.setClasses(removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (IWOLipsModel.SUBPROJECTS_ID.equals(currentKey)) {
				actualResources = this.pbProject.getSubprojects();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						this.pbProject.setSubprojects(addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						this.pbProject.setSubprojects(removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			} else if (IWOLipsModel.COMPONENTS_ID.equals(currentKey)) {
				actualResources = this.pbProject.getWoComponents();
				switch (kindOfChange) {
					case IResourceDelta.ADDED :
						this.pbProject.setWoComponents(addResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
					case IResourceDelta.REMOVED :
						this.pbProject.setWoComponents(removeResources(
								(List) changedResources.get(currentKey),
								actualResources));
						break;
				}
			}
		}
		try {
			_saveChanges();
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
	}
	/**
	 * Method addResources.
	 * 
	 * @param newResources
	 * @param actualResources
	 * @return List
	 */
	private List addResources(List newResources, List actualResources) {
		if (actualResources == null) {
			actualResources = new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile = this.projectContainer.getFile(new Path(
				IWOLipsModel.PROJECT_FILE_NAME));
		for (int i = 0; i < newResources.size(); i++) {
			relativResourcePath = relativResourcePath((IResource) newResources
					.get(i), projectFile);
			if (relativResourcePath != null
					&& !actualResources.contains(relativResourcePath)) {
				actualResources.add(relativResourcePath);
			}
		}
		return actualResources;
	}
	private List removeResources(List removedResources, List actualResources) {
		if (actualResources == null) {
			return new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile = this.projectContainer.getFile(new Path(
				IWOLipsModel.PROJECT_FILE_NAME));
		for (int i = 0; i < removedResources.size(); i++) {
			relativResourcePath = relativResourcePath(
					(IResource) removedResources.get(i), projectFile);
			if (relativResourcePath != null
					&& actualResources.contains(relativResourcePath)) {
				actualResources.remove(relativResourcePath);
			}
		}
		return actualResources;
	}
	/**
	 * Method relativResourcePath.
	 * 
	 * @param resource
	 * @param projectFile
	 * @return String
	 */
	private String relativResourcePath(IResource resource, IFile projectFile) {
		// determine relativ path to resource
		String resourcePath;
		if (projectFile.getParent().equals(resource.getParent())) {
			// same folder
			resourcePath = resource.getName();
		} else if (projectFile.getParent().getFullPath().matchingFirstSegments(
				resource.getFullPath()) == projectFile.getParent()
				.getFullPath().segmentCount()) {
			// resource is deeper in directory structure
			resourcePath = resource.getFullPath().removeFirstSegments(
					projectFile.getParent().getFullPath()
							.matchingFirstSegments(resource.getFullPath()))
					.toString();
		} else {
			// resource is higher or paralell in directory structure
			resourcePath = resource.getProjectRelativePath().toString();
			for (int i = 0; i < projectFile.getProjectRelativePath()
					.segmentCount() - 1; i++) {
				resourcePath = "../" + resourcePath;
			}
		}
		return resourcePath;
	}
	/**
	 *  
	 */
	public void addLocalFrameworkSectionToPBProject() {
		try {
			List actualFrameworkSearch = this.pbProject.getFrameworkSearch();
			if (actualFrameworkSearch == null) {
				this.pbProject.setFrameworkSearch(new ArrayList());
				actualFrameworkSearch = this.pbProject.getFrameworkSearch();
			}
			if (!actualFrameworkSearch
					.contains(PBProjectUpdater.DefaultLocalFrameworkSearch)) {
				actualFrameworkSearch
						.add(PBProjectUpdater.DefaultLocalFrameworkSearch);
			}
			_saveChanges();
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
	}
	/**
	 * Method addFrameworks.
	 * 
	 * @param newFrameworks
	 */
	public void addFrameworks(List newFrameworks) {
		boolean saveRequired = false;
		List actualFrameworks = this.pbProject.getFrameworks();
		for (int j = 0; j < newFrameworks.size(); j++) {
			String[] frameworkIdentifiers = frameworkIdentifiersFromPath((Path) newFrameworks
					.get(j));
			for (int k = 0; k < frameworkIdentifiers.length; k++) {
				String frameworkName = frameworkIdentifiers[k];
				if (frameworkName != null
						&& !actualFrameworks.contains(frameworkName)) {
					actualFrameworks.add(frameworkName);
					saveRequired = true;
				}
			}
		}
		try {
			if (saveRequired)
				_saveChanges();
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
	}
	/**
	 * Method removeFrameworks.
	 * 
	 * @param removedFrameworks
	 */
	public void removeFrameworks(List removedFrameworks) {
		boolean saveRequired = false;
		List actualFrameworks = this.pbProject.getFrameworks();
		for (int j = 0; j < removedFrameworks.size(); j++) {
			String[] frameworkIdentifiers = frameworkIdentifiersFromPath((Path) removedFrameworks
					.get(j));
			for (int k = 0; k < frameworkIdentifiers.length; k++) {
				String frameworkName = frameworkIdentifiers[k];
				if (frameworkName != null
						&& actualFrameworks.contains(frameworkName)) {
					actualFrameworks.remove(frameworkName);
					saveRequired = true;
				}
			}
		}
		try {
			if (saveRequired)
				_saveChanges();
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
	}
	/**
	 * Method frameworkIdentifierFromPath.
	 * 
	 * @param frameworkPath
	 * @return String
	 */
	private String[] frameworkIdentifiersFromPath(Path frameworkPath) {
		String[] frameworkNames = null;
		// search framework segment in path
		if (frameworkPath.segmentCount() > 0
				&& "org.objectstyle.wolips.WO_CLASSPATH".equals(frameworkPath
						.segment(0))) {
			frameworkNames = new String[frameworkPath.segments().length - 1];
			System.arraycopy(frameworkPath.segments(), 1, frameworkNames, 0,
					frameworkNames.length);
			for (int i = 0; i < frameworkNames.length; i++) {
				frameworkNames[i] = frameworkNames[i] + "."
						+ IWOLipsModel.EXT_FRAMEWORK;
			}
		} else {
			String frameworkName = this.getFrameworkName(frameworkPath);
			if (frameworkName == null)
				frameworkNames = new String[]{};
			else
				frameworkNames = new String[]{frameworkName};
		}
		return frameworkNames;
	}

	private String getFrameworkName(Path frameworkPath) {
		String frameworkName = null;
		int i = 0;
		int count = frameworkPath.segmentCount();
		while (i < count && frameworkName == null) {
			String segment = frameworkPath.segment(i);
			if (segment.endsWith("." + IWOLipsModel.EXT_FRAMEWORK))
				frameworkName = segment;
			else
				i++;
		}
		return frameworkName;
	}
}
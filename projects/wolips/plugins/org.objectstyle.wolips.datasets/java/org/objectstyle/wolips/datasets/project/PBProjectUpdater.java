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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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

	private static final String MARKER_ID = "org.objectstyle.wolips.datasets.pbproject.warning";

	private boolean saveRequired = false;

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
		this.projectContainer = aProjectContainer;
		this.removeProjectMarker();
		this.getPBProject(this.projectContainer);
		//projectContainer = aProjectContainer;
	}

	private final void removeProjectMarker() {
		try {
			IFile aFile = projectContainer.getFile(new Path(
					IWOLipsModel.PROJECT_FILE_NAME));
			if (aFile.exists()) {
				// these old markers were making it impossible to launch in
				// E-3.0m9
				aFile
						.deleteMarkers(IMarker.PROBLEM, false,
								IResource.DEPTH_ONE);
				// we also have to get rid of our own (new) markers
				aFile.deleteMarkers(MARKER_ID, true, IResource.DEPTH_ONE);
			}
		} catch (Exception e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	private final void addProjectMarker() {
		try {
			IFile aFile = this.projectContainer.getFile(new Path(
					IWOLipsModel.PROJECT_FILE_NAME));
			if (aFile.exists()) {
				IMarker marker = aFile.createMarker(MARKER_ID);
				marker.setAttribute(IMarker.MESSAGE,
						"Error while updating PB.project");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			DataSetsPlugin.getDefault().getPluginLogger().log(e);
		}
	}

	private final void handleException(Throwable throwable) {
		this.addProjectMarker();
		class RunnableExceptionHandler implements Runnable {
			public IContainer projectContainer;

			public Throwable throwable;

			public void run() {
				WorkbenchUtilitiesPlugin.handleException(new Shell(),
						throwable,
						"An error occured while reading/saving the PB.project in project: "
								+ projectContainer.getProject().getName());
			}
		}
		;
		RunnableExceptionHandler runnable = new RunnableExceptionHandler();
		runnable.projectContainer = this.projectContainer;
		runnable.throwable = throwable;
		Display.getDefault().asyncExec(runnable);
	}

	/**
	 * Method instance.
	 * 
	 * @param aProjectContainer
	 * @return PBProjectUpdater
	 */
	public static PBProjectUpdater instance(IContainer aProjectContainer) {
		IContainer findContainer = aProjectContainer;
		IContainer container = null;
		while ((findContainer.findMember(IWOLipsModel.PROJECT_FILE_NAME) == null)
				&& (findContainer.getParent() != null)) {
			findContainer = findContainer.getParent();
		}
		if (findContainer.getParent() == null)
			container = findContainer.getProject();
		if (findContainer.findMember(IWOLipsModel.PROJECT_FILE_NAME) != null)
			container = findContainer;
		PBProjectUpdater updater = null;
		if (container != null) {
			updater = new PBProjectUpdater(container);
			if (null == updater.pbProject) {
				updater = null;
			}
		}

		return updater;
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
					Project project = (Project) this.projectContainer
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
			Project project = (Project) this.projectContainer.getProject()
					.getAdapter(Project.class);
			this.pbProject = new PBProject(aFile, project.isFramework());
			if (sync) {
				//TODO: uli
			}
		} catch (Exception anException) {
			this.handleException(anException);
		} finally {
			aFile = null;
		}
	}

	/**
	 */
	public void cleanTables() {
		this.pbProject.setClasses(new ArrayList());
		this.pbProject.setWebServerResources(new ArrayList());
		this.pbProject.setWoAppResources(new ArrayList());
		this.pbProject.setWoComponents(new ArrayList());
		this.saveRequired = true;
		this._saveChanges();
	}

	private void _saveChanges() {
		if (this.saveRequired) {
			this.pbProject.saveChanges();
			_tryRefresh();
			this.saveRequired = false;
		}
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
	 * Method syncProjectName.
	 *  
	 */
	public void syncProjectName() {
		if (!this.projectContainer.getName().equals(
				this.pbProject.getProjectName())) {
			this.pbProject.setProjectName(this.projectContainer.getName());
			try {
				this.saveRequired = true;
				this._saveChanges();
			} catch (Throwable e) {
				this.handleException(e);
			}
		}
	}

	/**
	 * Method syncFilestable.
	 * 
	 * @param changedResources
	 * @param kindOfChange
	 */
	public void syncFilestable(Map changedResources, int kindOfChange,
			String[] languages) {
		List actualResources;
		String currentKey;
		Object[] allKeys = changedResources.keySet().toArray();
		for (int i = 0; i < allKeys.length; i++) {
			currentKey = (String) allKeys[i];
			if (IWOLipsModel.RESOURCES_ID.equals(currentKey)) {
				for (int j = 0; j <= languages.length; j++) {
					String language = null;
					if (j < languages.length) {
						language = languages[j];
					}
					actualResources = this.pbProject.getWoAppResources(language);
					switch (kindOfChange) {
					case IResourceDelta.ADDED:
						this.pbProject.setWoAppResources(addResources(
								(List) changedResources.get(currentKey),
								actualResources, language), language);
						break;
					case IResourceDelta.REMOVED:
						this.pbProject.setWoAppResources(removeResources(
								(List) changedResources.get(currentKey),
								actualResources, language), language);
						break;
					}
				}
			} else if (IWOLipsModel.WS_RESOURCES_ID.equals(currentKey)) {
				for (int j = 0; j <= languages.length; j++) {
					String language = null;
					if (j < languages.length) {
						language = languages[j];
					}
					actualResources = this.pbProject.getWebServerResources(language);
					switch (kindOfChange) {
					case IResourceDelta.ADDED:
						this.pbProject.setWebServerResources(addResources(
								(List) changedResources.get(currentKey),
								actualResources, language), language);
						break;
					case IResourceDelta.REMOVED:
						this.pbProject.setWebServerResources(removeResources(
								(List) changedResources.get(currentKey),
								actualResources, language), language);
						break;
					}
				}
			} else if (IWOLipsModel.CLASSES_ID.equals(currentKey)) {
				actualResources = this.pbProject.getClasses();
				switch (kindOfChange) {
				case IResourceDelta.ADDED:
					this.pbProject.setClasses(addResources(
							(List) changedResources.get(currentKey),
							actualResources, null));
					break;
				case IResourceDelta.REMOVED:
					this.pbProject.setClasses(removeResources(
							(List) changedResources.get(currentKey),
							actualResources, null));
					break;
				}
			} else if (IWOLipsModel.SUBPROJECTS_ID.equals(currentKey)) {
				actualResources = this.pbProject.getSubprojects();
				switch (kindOfChange) {
				case IResourceDelta.ADDED:
					this.pbProject.setSubprojects(addResources(
							(List) changedResources.get(currentKey),
							actualResources, null));
					break;
				case IResourceDelta.REMOVED:
					this.pbProject.setSubprojects(removeResources(
							(List) changedResources.get(currentKey),
							actualResources, null));
					break;
				}
			} else if (IWOLipsModel.COMPONENTS_ID.equals(currentKey)) {
				for (int j = 0; j <= languages.length; j++) {
					String language = null;
					if (j < languages.length) {
						language = languages[j];
					}
					actualResources = this.pbProject.getWoComponents(language);
					switch (kindOfChange) {
					case IResourceDelta.ADDED:
						this.pbProject.setWoComponents(addResources(
								(List) changedResources.get(currentKey),
								actualResources, language), language);
						break;
					case IResourceDelta.REMOVED:
						this.pbProject.setWoComponents(removeResources(
								(List) changedResources.get(currentKey),
								actualResources, language), language);
						break;
					}
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
	private List addResources(List newResources, List actualResources,
			String language) {
		if (actualResources == null) {
			actualResources = new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile = this.projectContainer.getFile(new Path(
				IWOLipsModel.PROJECT_FILE_NAME));
		for (int i = 0; i < newResources.size(); i++) {
			IResource resource = (IResource) newResources.get(i);
			String parentExtension = resource.getParent().getFileExtension();
			String parentName = resource.getParent().getName();
			if ((language == null && parentExtension == null)
					|| (language == null && !IWOLipsModel.EXT_LPROJ.equals(parentExtension))
					|| (language != null && parentName.equals(language + "."
							+ IWOLipsModel.EXT_LPROJ))) {
				relativResourcePath = relativResourcePath(resource, projectFile, language);
				if (relativResourcePath != null
						&& !actualResources.contains(relativResourcePath)) {
					this.saveRequired = true;
					actualResources.add(relativResourcePath);
				}
			}
		}
		return actualResources;
	}

	private List removeResources(List removedResources, List actualResources,
			String language) {
		if (actualResources == null) {
			return new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile = this.projectContainer.getFile(new Path(
				IWOLipsModel.PROJECT_FILE_NAME));
		for (int i = 0; i < removedResources.size(); i++) {
			IResource resource = (IResource) removedResources.get(i);
			String parentExtension = resource.getParent().getFileExtension();
			String parentName = resource.getParent().getName();
			if ((language == null && parentExtension == null)
					|| (language == null &&  !IWOLipsModel.EXT_LPROJ.equals(parentExtension))
					|| (language != null && parentName.equals(language + "."
							+ IWOLipsModel.EXT_LPROJ))) {
				relativResourcePath = relativResourcePath(resource, projectFile, language);
				if (relativResourcePath != null
						&& actualResources.contains(relativResourcePath)) {
					this.saveRequired = true;
					actualResources.remove(relativResourcePath);
				}
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
	private String relativResourcePath(IResource resource, IFile projectFile, String language) {
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
		if(language != null) {
			if(resourcePath.startsWith(language + "." + IWOLipsModel.EXT_LPROJ)) {
				resourcePath = resourcePath.substring(language.length() + 7);
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
	public void setFrameworks(List newFrameworks) {
		List actualFrameworks = this.pbProject.getFrameworks();
		if (actualFrameworks.size() != newFrameworks.size()) {
			this.saveRequired = true;
		} else {
			if (!actualFrameworks.containsAll(newFrameworks)) {
				this.saveRequired = true;
			}
		}
		if (this.saveRequired) {
			this.pbProject.setFrameworks(newFrameworks);
		}
		try {
			this._saveChanges();
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
		List actualFrameworks = this.pbProject.getFrameworks();
		for (int i = 0; i < newFrameworks.size(); i++) {
			if (!actualFrameworks.contains(newFrameworks.get(i))) {
				actualFrameworks.add(newFrameworks.get(i));
				this.saveRequired = true;
			}
		}
		try {
			this._saveChanges();
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
		List actualFrameworks = this.pbProject.getFrameworks();
		for (int i = 0; i < removedFrameworks.size(); i++) {
			if (actualFrameworks.contains(removedFrameworks.get(i))) {
				actualFrameworks.remove(removedFrameworks.get(i));
				this.saveRequired = true;
			}
		}
		try {
			_saveChanges();
		} catch (Throwable throwable) {
			this.handleException(throwable);
		}
	}

}
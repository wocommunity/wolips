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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.core.plugin.logging.WOLipsLog;
import org.objectstyle.wolips.core.plugin.IWOLipsPluginConstants;
import org.objectstyle.woproject.pb.PBProject;
import org.objectstyle.woproject.util.FileStringScanner;

/**
 * @author uli
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class _PBProjectUpdater {
	//sometimes the EOModeler converts the PB.project to an xml file.
	private static final String dirtyPBProject = "<?xml";
	//instance of PBProject
	private PBProject pbProject;
	//The folder for the PB.project
	private IContainer projectContainer;
	
	/**
	 * Constructor for PBProjectUpdater.
	 */
	protected _PBProjectUpdater(IContainer aProjectContainer) {
		super();
		//check if theres a PB.project in the Container. If not go to the parent
		IContainer findContainer = aProjectContainer;
		while ((findContainer
			.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
			== null)
			&& (findContainer.getParent() != null)) {
			findContainer = findContainer.getParent();
		}
		if (findContainer.getParent() == null)
			projectContainer = projectContainer.getProject();
		if (findContainer.findMember(IWOLipsPluginConstants.PROJECT_FILE_NAME)
			!= null)
			projectContainer = findContainer;
		pbProject = getPBProject(projectContainer);
		projectContainer = aProjectContainer;
	}
	/**
	 * Method getPbProject.
	 * @return PBProject
	 */
	protected PBProject getPbProject() {
		return pbProject;
	}
	/**
	 * Method getProjectContainer.
	 * @return IContainer
	 */
	protected IContainer getProjectContainer() {
		return projectContainer;
	}
	/**
	 * On MacOSX the EOModeler converts the PB.project file to xml.
	 */
	private static void fixEOModelerMacOSXBug(File aFile) {
		String file = null;
		try {
			if ((aFile != null) && (aFile.exists())) {
				file = FileStringScanner.stringFromFile(aFile);
				if (file.startsWith(_PBProjectUpdater.dirtyPBProject))
					aFile.delete();
			}
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		} finally {
			file = null;
		}
	}
	/**
	 * Method getPBProject.
	 * @param aProject
	 */
	private static PBProject getPBProject(IContainer aProject) {
		File aFile =
			aProject
				.getFile(new Path(IWOLipsPluginConstants.PROJECT_FILE_NAME))
				.getLocation()
				.toFile();
		PBProject apbProject = null;
		fixEOModelerMacOSXBug(aFile);
		try {
			boolean sync = !aFile.exists();
			//create a new one
//			TODO: is this a framework or a application
			/*apbProject =
				new PBProject(
					aFile,
					!ProjectHelper.isWOAppBuilderInstalled(
						(IProject) aProject.getProject()));*/
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		} finally {
			aFile = null;
		}
		return apbProject;
	}
	/**
	 * Method syncPBProjectWithProject.
	 */
	protected void syncPBProjectWithProject() {
		try {
			this.getPbProject().update();
			this.syncFilestable();
			this.syncProjectName();
			this.getPbProject().saveChanges();
		} catch (Exception ioex) {
			WOLipsLog.log(ioex);
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
			resources = projectContainer.members();
		} catch (Exception anException) {
			WOLipsLog.log(anException);
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
			proceedResource(
				aResource,
				aClassesList,
				aWOComponentsList,
				aWOAppResourcesList);
		}
		this.syncClasses(aClassesList);
		this.syncWOComponents(aWOComponentsList);
		this.syncWOAppResources(aWOAppResourcesList);
	}
	/**
	 * Method proceedResource.
	 * @param aResource
	 * @param aClassesList
	 * @param aWOComponentsList
	 * @param aWOAppResourcesList
	 */
	private void proceedResource(
		IResource aResource,
		List aClassesList,
		List aWOComponentsList,
		List aWOAppResourcesList) {
		try {
			String aPath = aResource.getProjectRelativePath().toString();
			File aFile = new File(aResource.getLocation().toOSString());
			IFolder aFolder = null;
			if (aFile.isDirectory())
				aFolder =
					projectContainer.getFolder(
						aResource.getProjectRelativePath());
			if (aFolder != null) {
				if (aPath.endsWith(".wo"))
					aWOComponentsList.add(aPath);
				else if (
					!aPath.endsWith(".woa")
						&& !aPath.endsWith(".build")
						&& !aPath.endsWith(".framework")) {
					IResource[] resources;
					resources = aFolder.members();
					int lastResource = resources.length;
					int i = 0;
					while (i < lastResource) {
						IResource aFolderResource = resources[i];
						i++;
						this.proceedResource(
							aFolderResource,
							aClassesList,
							aWOComponentsList,
							aWOAppResourcesList);
					}
				}
			} else {
				if (aPath.endsWith(".java"))
					aClassesList.add(aPath);
				if (aPath.endsWith(".api"))
					aWOAppResourcesList.add(aPath);
			}
		} catch (Exception anException) {
			WOLipsLog.log(anException);
		}
	}
	/**
	 * Method syncProjectName.
	 */
	private void syncProjectName() {
		this.getPbProject().setProjectName(projectContainer.getName());
	}
	/**
	 * Method syncClasses.
	 * @param list
	 */
	private void syncClasses(List list) {
		this.getPbProject().setClasses(list);
	}
	/**
	 * Method syncWOComponents.
	 * @param list
	 */
	private void syncWOComponents(List list) {
		this.getPbProject().setWoComponents(list);
	}
	/**
	 * Method syncWOAppResources.
	 * @param list
	 */
	private void syncWOAppResources(List list) {
		this.getPbProject().setWoAppResources(list);
	}

	/**
	 * Method addResources.
	 * @param newResources
	 * @param actualResources
	 * @return List
	 */
	protected List addResources(List newResources, List actualResources) {
		if (actualResources == null) {
			actualResources = new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile =
			projectContainer.getFile(
				new Path(IWOLipsPluginConstants.PROJECT_FILE_NAME));
		for (int i = 0; i < newResources.size(); i++) {
			relativResourcePath =
				relativResourcePath(
					(IResource) newResources.get(i),
					projectFile);
			if (relativResourcePath != null
				&& !actualResources.contains(relativResourcePath)) {
				actualResources.add(relativResourcePath);
			}
		}
		return actualResources;
	}
	/**
	 * Method removeResources.
	 * @param removedResources
	 * @param actualResources
	 * @return List
	 */
	protected List removeResources(List removedResources, List actualResources) {
		if (actualResources == null) {
			return new ArrayList();
		}
		String relativResourcePath;
		IFile projectFile =
			projectContainer.getFile(
				new Path(IWOLipsPluginConstants.PROJECT_FILE_NAME));
		for (int i = 0; i < removedResources.size(); i++) {
			relativResourcePath =
				relativResourcePath(
					(IResource) removedResources.get(i),
					projectFile);
			if (relativResourcePath != null
				&& actualResources.contains(relativResourcePath)) {
				actualResources.remove(relativResourcePath);
			}
		}
		return actualResources;
	}
	/**
	 * Method relativResourcePath.
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
		} else if (
			projectFile.getParent().getFullPath().matchingFirstSegments(
				resource.getFullPath())
				== projectFile.getParent().getFullPath().segmentCount()) {
			// resource is deeper in directory structure 
			resourcePath =
				resource
					.getFullPath()
					.removeFirstSegments(
						projectFile
							.getParent()
							.getFullPath()
							.matchingFirstSegments(
							resource.getFullPath()))
					.toString();
		} else {
			// resource is higher or paralell in directory structure
			resourcePath = resource.getProjectRelativePath().toString();
			for (int i = 0;
				i < projectFile.getProjectRelativePath().segmentCount() - 1;
				i++) {
				resourcePath = "../" + resourcePath;
			}
		}
		return resourcePath;
	}

	/**
	 * Method frameworkIdentifierFromPath.
	 * @param frameworkPath
	 * @return String
	 */
	protected static String frameworkIdentifierFromPath(Path frameworkPath) {
		String frameworkName = null;
		// search framework segment in path
		for (int i = 0; i < frameworkPath.segmentCount(); i++) {
			frameworkName = frameworkPath.segment(i);
			if (frameworkName
				.endsWith("." + IWOLipsPluginConstants.EXT_FRAMEWORK)) {
				break;
			} else {
				frameworkName = null;
			}
		}
		return frameworkName;
	}
}

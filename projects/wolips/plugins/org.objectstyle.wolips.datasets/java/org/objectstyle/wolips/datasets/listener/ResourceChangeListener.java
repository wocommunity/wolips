/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2002 - 2004 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.datasets.listener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.datasets.DataSetsPlugin;
import org.objectstyle.wolips.datasets.pattern.PatternsetWriter;
import org.objectstyle.wolips.datasets.pattern.PatternsetMatcher;
import org.objectstyle.wolips.datasets.pattern.StringListMatcher;
import org.objectstyle.wolips.datasets.project.IWOLipsProject;
import org.objectstyle.wolips.datasets.project.PBProjectUpdater;
import org.objectstyle.wolips.datasets.project.WOLipsCore;
import org.objectstyle.wolips.datasets.project.WOLipsJavaProject;
import org.objectstyle.wolips.datasets.resources.IWOLipsModel;
/**
 * Tracking changes in resources and synchronizes webobjects project file
 */
public class ResourceChangeListener implements IResourceChangeListener {
	/**
	 * Constructor for ResourceChangeListener.
	 */
	public ResourceChangeListener() {
		super();
		//	this.setUpMatcher();
	}
	/**
	 * Adds instance of inner class ProjectFileResourceValidator to events
	 * resource delta. <br>
	 * 
	 * @param event
	 * @see ProjectFileResourceValidator
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public final void resourceChanged(IResourceChangeEvent event) {
		//System.out.println("******* resourceChanged begin ");
		ProjectFileResourceValidator resourceValidator = new ProjectFileResourceValidator();
		try {
			event.getDelta().accept(resourceValidator);
		} catch (CoreException e) {
			DataSetsPlugin.log(e);
		}
		// update project files
		IFile projectFileToUpdate;
		PBProjectUpdater projectUpdater;
		Object[] allAddedKeys = resourceValidator
				.getAddedResourcesProjectDict().keySet().toArray();
		for (int i = 0; i < allAddedKeys.length; i++) {
			projectFileToUpdate = (IFile) allAddedKeys[i];
			projectUpdater = PBProjectUpdater.instance(projectFileToUpdate
					.getParent());
			if (projectFileToUpdate.getParent().getType() == IResource.PROJECT)
				projectUpdater.syncProjectName();
			projectUpdater.syncFilestable((HashMap) resourceValidator
					.getAddedResourcesProjectDict().get(projectFileToUpdate),
					IResourceDelta.ADDED);
		}
		Object[] allRemovedKeys = resourceValidator
				.getRemovedResourcesProjectDict().keySet().toArray();
		for (int i = 0; i < allRemovedKeys.length; i++) {
			projectFileToUpdate = (IFile) allRemovedKeys[i];
			// ensure project file container exists
			// if no container exists the whole project is deleted
			if (projectFileToUpdate.getParent().exists()) {
				projectUpdater = PBProjectUpdater.instance(projectFileToUpdate
						.getParent());
				projectUpdater.syncFilestable((HashMap) resourceValidator
						.getRemovedResourcesProjectDict().get(
								projectFileToUpdate), IResourceDelta.REMOVED);
			}
		}
	}
	private final class ProjectFileResourceValidator
			implements
				IResourceDeltaVisitor {
		//private QualifiedName resourceQualifier;
		private HashMap addedResourcesProjectDict;
		private HashMap removedResourcesProjectDict;
		PatternsetMatcher woappResourcesIncludeMatcher = null;
		PatternsetMatcher woappResourcesExcludeMatcher = null;
		PatternsetMatcher resourcesIncludeMatcher = null;
		PatternsetMatcher resourcesExcludeMatcher = null;
		StringListMatcher classesIncludeMatcher = null;
		StringListMatcher classesExcludeMatcher = null;
		/**
		 * @see java.lang.Object#Object()
		 */
		/**
		 * Constructor for ProjectFileResourceValidator.
		 */
		public ProjectFileResourceValidator() {
			super();
			addedResourcesProjectDict = new HashMap();
			removedResourcesProjectDict = new HashMap();
		}
		private void setUpMatcher(IProject project) {
			IFile wsresourcesIncludePatternset = project
					.getFile("wsresources.include.patternset");
			if (!wsresourcesIncludePatternset.exists())
				PatternsetWriter.create(wsresourcesIncludePatternset,
						new String[]{"**/*.gif"});
			woappResourcesIncludeMatcher = new PatternsetMatcher(
					wsresourcesIncludePatternset);
			try {
				wsresourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				DataSetsPlugin.log(e);
			}
			IFile wsresourcesExcludePatternset = project
					.getFile("wsresources.exclude.patternset");
			if (!wsresourcesExcludePatternset.exists())
				PatternsetWriter.create(wsresourcesExcludePatternset,
						new String[]{"**/*.woa/**", "**/*.framework/**"});
			woappResourcesExcludeMatcher = new PatternsetMatcher(
					wsresourcesExcludePatternset);
			try {
				wsresourcesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				DataSetsPlugin.log(e);
			}
			IFile resourcesIncludePatternset = project
					.getFile("resources.include.patternset");
			if (!resourcesIncludePatternset.exists())
				PatternsetWriter.create(resourcesIncludePatternset,
						new String[]{"Properties", "**/*.eomodeld/",
								"**/*.d2wmodel", "**/*.wo/", "**/*.api",
								"**/*.strings"});
			resourcesIncludeMatcher = new PatternsetMatcher(
					resourcesIncludePatternset);
			try {
				resourcesIncludePatternset.refreshLocal(IResource.DEPTH_ONE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				DataSetsPlugin.log(e);
			}
			IFile resourcesExcludePatternset = project
					.getFile("resources.exclude.patternset");
			if (!resourcesExcludePatternset.exists())
				PatternsetWriter.create(resourcesExcludePatternset,
						new String[]{"**/*.eomodeld~/", "**/*.woa/**",
								"**/*.framework/**"});
			resourcesExcludeMatcher = new PatternsetMatcher(
					resourcesExcludePatternset);
			try {
				resourcesExcludePatternset.refreshLocal(IResource.DEPTH_ONE,
						new NullProgressMonitor());
			} catch (CoreException e) {
				DataSetsPlugin.log(e);
			}
			classesIncludeMatcher = new StringListMatcher("*.java");
			classesExcludeMatcher = new StringListMatcher("");
		}
		/**
		 * @param delta
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
		 * @return
		 */
		public final boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();
			try {
				return examineResource(resource, delta.getKind());
			} catch (CoreException e) {
				DataSetsPlugin.log(e);
				return false;
			}
		}
		/**
		 * Method examineResource. Examines changed resources for added and/or
		 * removed webobjects project resources and synchronizes project file.
		 * <br>
		 * 
		 * @see #updateProjectFile(int, IResource, String, IFile)
		 * @param resource
		 * @param kindOfChange
		 * @return boolean
		 * @throws CoreException
		 */
		private final boolean examineResource(IResource resource,
				int kindOfChange) throws CoreException {
			//see bugreport #708385
			if (!resource.isAccessible()
					&& kindOfChange != IResourceDelta.REMOVED)
				return false;
			switch (resource.getType()) {
				case IResource.ROOT :
					// further investigation of resource delta needed
					return true;
				case IResource.PROJECT :
					if (!resource.exists() || !resource.isAccessible()) {
						// project deleted no further investigation needed
						return false;
					}
					IWOLipsProject wolipsProject = WOLipsCore
							.createProject((IProject) resource);
					if (wolipsProject.getNaturesAccessor().hasWOLipsNature()) {
						this.setUpMatcher((IProject) resource);
						// resource change concerns to webobjects project
						// -> visit childs
						return true;
					} // no webobjects project
					return false;
				case IResource.FOLDER :
					//is this really required?
					// what if this delta has no changes but a child of it?
					if (IWOLipsModel.EXT_FRAMEWORK.equals(resource
							.getFileExtension())
							|| IWOLipsModel.EXT_WOA.equals(resource
									.getFileExtension())
							|| "build".equals(resource.getName())
							|| "dist".equals(resource.getName())) {
						// no further examination needed
						return false;
					}
					if (needsProjectFileUpdate(kindOfChange)) {
						if (IWOLipsModel.EXT_COMPONENT.equals(resource
								.getFileExtension())) {
							updateProjectFile(
									kindOfChange,
									resource,
									IWOLipsModel.COMPONENTS_ID,
									resource
											.getParent()
											.getFile(
													new Path(
															IWOLipsModel.PROJECT_FILE_NAME)));
						} else if (matchWOAppResourcesPattern(resource)) {
							updateProjectFile(
									kindOfChange,
									resource,
									IWOLipsModel.RESOURCES_ID,
									resource
											.getParent()
											.getFile(
													new Path(
															IWOLipsModel.PROJECT_FILE_NAME)));
						} /*
						   * else if (
						   * EXT_EOMODEL.equals(resource.getFileExtension())) {
						   * updateProjectFile( kindOfChange, resource,
						   * RESOURCES_ID, resource.getParent().getFile( new
						   * Path(PROJECT_FILE_NAME))); }
						   */
						/*
						 * else if ( EXT_EOMODEL_BACKUP.equals(
						 * resource.getFileExtension())) {
						 * deleteTeamPrivateMembers((IFolder) resource); }
						 */
						else if (IWOLipsModel.EXT_SUBPROJECT.equals(resource
								.getFileExtension())) {
							updateProjectFile(
									kindOfChange,
									resource,
									IWOLipsModel.SUBPROJECTS_ID,
									resource
											.getParent()
											.getFile(
													new Path(
															IWOLipsModel.PROJECT_FILE_NAME)));
							if (IResourceDelta.REMOVED == kindOfChange) {
								// remove project's source folder from
								// classpathentries
								try {
									WOLipsJavaProject woLipsJavaProject = new WOLipsJavaProject(
											JavaCore.create(resource
													.getProject()));
									woLipsJavaProject
											.getClasspathAccessor()
											.removeSourcefolderFromClassPath(
													woLipsJavaProject
															.getClasspathAccessor()
															.getSubprojectSourceFolder(
																	(IFolder) resource,
																	false),
													null);
								} catch (InvocationTargetException e) {
									DataSetsPlugin.log(e);
								}
							}
						}
					}
					// further examination of resource delta needed
					return true;
				case IResource.FILE :
					if (IResourceDelta.CHANGED == kindOfChange) {
						/*
						 * if (".project".equals(resource.getName()) ||
						 * ".classpath".equals(resource.getName())) {
						 * UpdateOtherClasspathIncludeFiles
						 * updateOtherClasspathIncludeFiles = new
						 * UpdateOtherClasspathIncludeFiles();
						 * updateOtherClasspathIncludeFiles.setIProject(
						 * resource.getProject());
						 * updateOtherClasspathIncludeFiles.execute();
						 * UpdateFrameworkIncludeFiles
						 * updateFrameworkIncludeFiles = new
						 * UpdateFrameworkIncludeFiles();
						 * updateFrameworkIncludeFiles.setIProject(
						 * resource.getProject());
						 * updateFrameworkIncludeFiles.execute();
						 * 
						 * ArrayList addedFrameworks = new ArrayList();
						 * IProject[] referencedProjects =
						 * resource.getProject().getReferencedProjects(); for
						 * (int j = 0; j < referencedProjects.length; j++) { if
						 * (referencedProjects[j].isAccessible() &&
						 * referencedProjects[j].isOpen()) { IWOLipsProject
						 * referencedWOLipsProject = WOLipsCore.createProject(
						 * referencedProjects[j]); if (referencedWOLipsProject !=
						 * null && referencedWOLipsProject .getNaturesAccessor()
						 * .hasWOLipsNature() && referencedWOLipsProject
						 * .getNaturesAccessor() .isFramework())
						 * addedFrameworks.add( new Path(
						 * referencedWOLipsProject .getProject() .getName() +
						 * "." + IWOLipsModel .EXT_FRAMEWORK)); } } if
						 * (addedFrameworks.size() > 0) { PBProjectUpdater
						 * projectUpdater = PBProjectUpdater.instance(
						 * resource.getProject());
						 * projectUpdater.addFrameworks(addedFrameworks); } }
						 */
					}
					if (needsProjectFileUpdate(kindOfChange)) {
						// files with java extension are located in src folders
						// the relating project file is determined through the
						// name of the src folder containing the java file
						if (matchResourcesPattern(resource)) {
							updateProjectFile(
									kindOfChange,
									resource,
									IWOLipsModel.RESOURCES_ID,
									resource
											.getParent()
											.getFile(
													new Path(
															IWOLipsModel.PROJECT_FILE_NAME)));
						} else if (matchWOAppResourcesPattern(resource)) {
							updateProjectFile(
									kindOfChange,
									resource,
									IWOLipsModel.WS_RESOURCES_ID,
									resource
											.getParent()
											.getFile(
													new Path(
															IWOLipsModel.PROJECT_FILE_NAME)));
						} else if (matchClassesPattern(resource)) {
							updateProjectFile(
									kindOfChange,
									resource,
									IWOLipsModel.CLASSES_ID,
									resource
											.getParent()
											.getFile(
													new Path(
															IWOLipsModel.PROJECT_FILE_NAME)));
						}
						//return false;
					}
			}
			return false;
		}
		private String toProjectRelativePath(IResource resource) {
			IPath path = resource.getProjectRelativePath();
			return path.toString();
		}
		private boolean matchClassesPattern(IResource resource) {
			String string = this.toProjectRelativePath(resource);
			if (classesExcludeMatcher.match(string))
				return false;
			if (classesIncludeMatcher.match(string))
				return true;
			return false;
		}
		private boolean matchWOAppResourcesPattern(IResource resource) {
			String string = this.toProjectRelativePath(resource);
			if (woappResourcesExcludeMatcher.match(string))
				return false;
			if (woappResourcesIncludeMatcher.match(string))
				return true;
			return false;
		}
		private boolean matchResourcesPattern(IResource resource) {
			String string = this.toProjectRelativePath(resource);
			if (resourcesExcludeMatcher.match(string))
				return false;
			if (resourcesIncludeMatcher.match(string))
				return true;
			return false;
		}
		/**
		 * Method needsProjectFileUpdate.
		 * 
		 * @param kindOfChange
		 * @return boolean
		 */
		private final boolean needsProjectFileUpdate(int kindOfChange) {
			return IResourceDelta.ADDED == kindOfChange
					|| IResourceDelta.REMOVED == kindOfChange;
		}
		/**
		 * Method updateProjectFile adds or removes resources from project file
		 * if the resources belongs to project file (determined in
		 *  @param kindOfChange
		 * @param resourceToUpdate
		 * @param fileStableId
		 * @param projectFileToUpdate
		 * @link WOProjectFileUpdater#isProjectResource(IResource))
		 */
		private final void updateProjectFile(int kindOfChange,
				IResource resourceToUpdate, String fileStableId,
				IFile projectFileToUpdate) {
			if (projectFileToUpdate == null) {
				return;
			}
			ArrayList changedResourcesArray = null;
			// let's examine the type of change
			switch (kindOfChange) {
				case IResourceDelta.ADDED :
					changedResourcesArray = getChangedResourcesArray(
							addedResourcesProjectDict, fileStableId,
							projectFileToUpdate);
					break;
				case IResourceDelta.REMOVED :
					changedResourcesArray = getChangedResourcesArray(
							removedResourcesProjectDict, fileStableId,
							projectFileToUpdate);
					break;
			}
			if (changedResourcesArray != null) {
				changedResourcesArray.add(resourceToUpdate);
			}
		}
		/**
		 * Method getChangedResourcesArray.
		 * 
		 * @param projectDict
		 * @param fileStableId
		 * @param projectFileToUpdate
		 * @return NSMutableArray
		 */
		private final ArrayList getChangedResourcesArray(HashMap projectDict,
				String fileStableId, IFile projectFileToUpdate) {
			HashMap fileStableIdDict;
			ArrayList changedResourcesArray;
			if (projectDict.get(projectFileToUpdate) == null) {
				// new project found add file stable dict
				fileStableIdDict = new HashMap();
				projectDict.put(projectFileToUpdate, fileStableIdDict);
			} else {
				fileStableIdDict = (HashMap) projectDict
						.get(projectFileToUpdate);
			}
			if (fileStableIdDict.get(fileStableId) == null) {
				// add changedResourcesArray of type fileStableId
				changedResourcesArray = new ArrayList();
				fileStableIdDict.put(fileStableId, changedResourcesArray);
			} else {
				changedResourcesArray = (ArrayList) fileStableIdDict
						.get(fileStableId);
			}
			return changedResourcesArray;
		}
		/**
		 * Returns the addedResourcesProjectDict.
		 * 
		 * @return NSMutableDictionary
		 */
		public final HashMap getAddedResourcesProjectDict() {
			return addedResourcesProjectDict;
		}
		/**
		 * Returns the removedResourcesProjectDict.
		 * 
		 * @return NSMutableDictionary
		 */
		public final HashMap getRemovedResourcesProjectDict() {
			return removedResourcesProjectDict;
		}
	}
}
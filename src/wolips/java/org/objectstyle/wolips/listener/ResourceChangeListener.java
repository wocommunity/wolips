package org.objectstyle.wolips.listener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.objectstyle.wolips.IWOLipsPluginConstants;
import org.objectstyle.wolips.WOLipsPlugin;
import org.objectstyle.wolips.project.PBProjectUpdater;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Tracking changes in resources and synchronizes webobjects project file
 */
public class ResourceChangeListener implements IResourceChangeListener {

	/**
	 * Constructor for ResourceChangeListener.
	 */
	public ResourceChangeListener() {
		super();
	}

	/**
	 * Adds instance of inner class ProjectFileResourceValidator to events
	 * resource delta.
	 * <br>
	 * @see ProjectFileResourceValidator
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		//System.out.println("******* resourceChanged begin ");
		ProjectFileResourceValidator resourceValidator =
			new ProjectFileResourceValidator();
		try {
			event.getDelta().accept(resourceValidator);
		} catch (CoreException e) {
			WOLipsPlugin.log(e);
		}
		// update project files
		IFile projectFileToUpdate;
		PBProjectUpdater projectUpdater;
		for (int i = 0;
			i
				< resourceValidator
					.getAddedResourcesProjectDict()
					.allKeys()
					.count();
			i++) {
			projectFileToUpdate =
				(IFile) resourceValidator
					.getAddedResourcesProjectDict()
					.allKeys()
					.objectAtIndex(i);
			projectUpdater =
				new PBProjectUpdater(projectFileToUpdate.getParent());
			projectUpdater.syncFilestable(
				(NSDictionary) resourceValidator
					.getAddedResourcesProjectDict()
					.objectForKey(
					projectFileToUpdate),
				IResourceDelta.ADDED);
		}
		for (int i = 0;
			i
				< resourceValidator
					.getRemovedResourcesProjectDict()
					.allKeys()
					.count();
			i++) {
			projectFileToUpdate =
				(IFile) resourceValidator
					.getRemovedResourcesProjectDict()
					.allKeys()
					.objectAtIndex(i);
			projectUpdater =
				new PBProjectUpdater(projectFileToUpdate.getParent());
			projectUpdater.syncFilestable(
				(NSDictionary) resourceValidator
					.getRemovedResourcesProjectDict()
					.objectForKey(
					projectFileToUpdate),
				IResourceDelta.REMOVED);
		}
/*
		System.out.println(
			"******* resource added "
				+ resourceValidator.getAddedResourcesProjectDict());
		System.out.println(
			"******* resource removed "
				+ resourceValidator.getRemovedResourcesProjectDict());
		System.out.println("******* resourceChanged end ");
		*/
	}

	private class ProjectFileResourceValidator
		implements IResourceDeltaVisitor {

		//private QualifiedName resourceQualifier;
		private IFile projectFile;
		private NSMutableDictionary addedResourcesProjectDict;
		private NSMutableDictionary removedResourcesProjectDict;

		/**
		 * Constructor for ProjectFileResourceValidator.
		 */
		public ProjectFileResourceValidator() {
			super();
			addedResourcesProjectDict = new NSMutableDictionary();
			removedResourcesProjectDict = new NSMutableDictionary();
		}

		/**
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) {
			IResource resource = delta.getResource();
			try {
				return examineResource(resource, delta.getKind());
			} catch (CoreException e) {
				WOLipsPlugin.log(e);
				return false;
			}

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
		private boolean examineResource(IResource resource, int kindOfChange)
			throws CoreException {

			// reset project file to update
			projectFile = null;

			switch (resource.getType()) {

				case IResource.ROOT :

					// further investigation of resource delta needed
					return true;

				case IResource.PROJECT :

					if (((IProject) resource)
						.hasNature(IWOLipsPluginConstants.WO_APPLICATION_NATURE)
						|| ((IProject) resource).hasNature(
							IWOLipsPluginConstants.WO_FRAMEWORK_NATURE)) {
						// resource change concerns to webobjects project

						// -> visit childs
						return true;
					} // no webobjects project
					return false;

				case IResource.FOLDER :
					if (needsProjectFileUpdate(kindOfChange)) {
						if (IWOLipsPluginConstants
							.COMPONENT
							.equals(resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								IWOLipsPluginConstants.COMPONENTS_ID,
								resource.getParent().getFile(
									new Path(
										IWOLipsPluginConstants
											.PROJECT_FILE_NAME)));
						} else if (
							IWOLipsPluginConstants.EOMODEL.equals(
								resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								IWOLipsPluginConstants.RESOURCES_ID,
								resource.getParent().getFile(
									new Path(
										IWOLipsPluginConstants
											.PROJECT_FILE_NAME)));
						} else if (
							IWOLipsPluginConstants.SUBPROJECT.equals(
								resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								IWOLipsPluginConstants.SUBPROJECTS_ID,
								resource.getParent().getFile(
									new Path(
										IWOLipsPluginConstants
											.PROJECT_FILE_NAME)));
						}
					}
					// further examination of resource delta needed
					return true;
				case IResource.FILE :

					if (needsProjectFileUpdate(kindOfChange)) {
						// this file must be an webobjects project file

						QualifiedName resourceQualifier = null;

						// files with java extension are located in src folders
						// the relating project file is determined through the
						// name of the src folder containing the java file
						if (IWOLipsPluginConstants
							.CLASS
							.equals(resource.getFileExtension())) {

							// determine project file
							IResource parent = resource;
							IPath projectFilePath =
								new Path(
									IWOLipsPluginConstants.PROJECT_FILE_NAME);
							while ((parent = parent.getParent())
								!= resource.getProject()) {
								if (IWOLipsPluginConstants
									.SRC
									.equals(parent.getFileExtension())
									&& parent instanceof IContainer) {

									// determine name of project file container
									// (remove ".src" extension from resource containing
									// changed java file
									final String projectFileContainerName =
										parent.getName().substring(
											0,
											parent.getName().length()
												- (IWOLipsPluginConstants
													.SRC
													.length()
													+ 1));

									// search for project file
									IResourceVisitor projectFileFinder =
										new IResourceVisitor() {
										private IContainer projectFileContainer;
										public boolean visit(IResource resource) {
											if ((resource.getType()
												== IResource.FOLDER
												|| resource.getType()
													== IResource.PROJECT)
												&& resource.getName().equals(
													projectFileContainerName)) {
												projectFileContainer =
													(IContainer) resource;
												projectFile =
													projectFileContainer
														.getFile(
														new Path(
															IWOLipsPluginConstants
																.PROJECT_FILE_NAME));
												if (projectFile.exists()) {
													// file found
													return false;
												} else {
													// continue search	
													projectFile = null;
													return true;
												}
											}
											return true;
										}
									};

									resource.getProject().accept(
										projectFileFinder);

									if (projectFile == null
										&& resource
											.getProject()
											.getFile(projectFilePath)
											.exists()) {
										// try to find application project file
										projectFile =
											resource.getProject().getFile(
												projectFilePath);
									}
								}
							}
							updateProjectFile(
								kindOfChange,
								resource,
								IWOLipsPluginConstants.CLASSES_ID,
								projectFile);

						} else if (
							IWOLipsPluginConstants.API.equals(
								resource.getFileExtension())) {
							updateProjectFile(
								kindOfChange,
								resource,
								IWOLipsPluginConstants.RESOURCES_ID,
								resource.getParent().getFile(
									new Path(
										IWOLipsPluginConstants
											.PROJECT_FILE_NAME)));
						}

						//return false;
					}
			}
			return false;
		}

		private boolean needsProjectFileUpdate(int kindOfChange) {
			return IResourceDelta.ADDED == kindOfChange
				|| IResourceDelta.REMOVED == kindOfChange;
		}

		/**
		* Method updateProjectFile adds or removes resources from project file
		* if the resources belongs to project file (determined in
		*  @link WOProjectFileUpdater#isProjectResource(IResource))
		* <br><br>
		* 
		* @param kind of change - resource added or removed
		* @param resource to update
		*/
		private void updateProjectFile(
			int kindOfChange,
			IResource resourceToUpdate,
			String fileStableId,
			IFile projectFileToUpdate) {

			if (projectFileToUpdate == null) {
				return;
			}

			NSMutableArray changedResourcesArray = null;

			// let's examine the type of change
			switch (kindOfChange) {
				case IResourceDelta.ADDED :
					changedResourcesArray =
						getChangedResourcesArray(
							addedResourcesProjectDict,
							fileStableId,
							projectFileToUpdate);
					break;
				case IResourceDelta.REMOVED :
					changedResourcesArray =
						getChangedResourcesArray(
							removedResourcesProjectDict,
							fileStableId,
							projectFileToUpdate);
					break;
			}
			if (changedResourcesArray != null) {
				changedResourcesArray.addObject(resourceToUpdate);
			}
		}

		private NSMutableArray getChangedResourcesArray(
			NSMutableDictionary projectDict,
			String fileStableId,
			IFile projectFileToUpdate) {

			NSMutableDictionary fileStableIdDict;
			NSMutableArray changedResourcesArray;

			if (projectDict.objectForKey(projectFileToUpdate) == null) {
				// new project found add file stable dict
				fileStableIdDict = new NSMutableDictionary();
				projectDict.setObjectForKey(
					fileStableIdDict,
					projectFileToUpdate);

			} else {

				fileStableIdDict =
					(NSMutableDictionary) projectDict.objectForKey(
						projectFileToUpdate);
			}
			if (fileStableIdDict.objectForKey(fileStableId) == null) {
				// add changedResourcesArray of type fileStableId
				changedResourcesArray = new NSMutableArray();
				fileStableIdDict.setObjectForKey(
					changedResourcesArray,
					fileStableId);
			} else {
				changedResourcesArray =
					(NSMutableArray) fileStableIdDict.objectForKey(
						fileStableId);
			}
			return changedResourcesArray;

		}

		/**
		 * Returns the addedResourcesProjectDict.
		 * @return NSMutableDictionary
		 */
		public NSMutableDictionary getAddedResourcesProjectDict() {
			return addedResourcesProjectDict;
		}

		/**
		 * Returns the removedResourcesProjectDict.
		 * @return NSMutableDictionary
		 */
		public NSMutableDictionary getRemovedResourcesProjectDict() {
			return removedResourcesProjectDict;
		}

	}

}

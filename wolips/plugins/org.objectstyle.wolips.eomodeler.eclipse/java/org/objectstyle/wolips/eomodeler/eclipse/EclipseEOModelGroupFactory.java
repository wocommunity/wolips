/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 - 2007 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.eomodeler.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.preferences.PreferenceConstants;
import org.objectstyle.wolips.eomodeler.utils.EclipseFileUtils;

public class EclipseEOModelGroupFactory implements IEOModelGroupFactory {
	public void loadModelGroup(Object modelGroupResource, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws EOModelException {
		try {
			modelGroup.setCreateDefaultDatabaseConfig(org.objectstyle.wolips.eomodeler.Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CREATE_DEFAULT_DATABASE_CONFIG));
			IResource modelGroupEclipseResource = getEclipseResourceForModelResource(modelGroupResource);
			if (modelGroupEclipseResource != null) {
				IProject project = modelGroupEclipseResource.getProject();
				if ("eomodelgroup".equals(modelGroupEclipseResource.getFileExtension())) {
					addModelsFromEOModelGroupFile((IFile) modelGroupEclipseResource, modelGroup, failures, skipOnDuplicates, progressMonitor);
				} else {
					addModelsFromProject(modelGroup, project, new HashSet<Object>(), new HashSet<IProject>(), failures, skipOnDuplicates, progressMonitor, 0);
					// modelGroup.resolve(failures);
					// modelGroup.verify(failures);
				}
			}
		} catch (EOModelException e) {
			throw e;
		} catch (Throwable t) {
			throw new EOModelException("Failed to load model group.", t);
		}
	}

	protected IResource getEclipseResourceForModelResource(Object modelResource) {
		IResource resource = null;
		if (modelResource instanceof IResource) {
			resource = (IResource) modelResource;
		} else if (modelResource instanceof URL) {
			resource = EclipseFileUtils.getEclipseFile((URL) modelResource);
		} else if (modelResource instanceof URI) {
			resource = EclipseFileUtils.getEclipseFile((URI) modelResource);
		} else {
			resource = null;
		}
		return resource;
	}

	protected void addModelsFromProject(EOModelGroup modelGroup, IProject project, Set<Object> searchedResources, Set<IProject> searchedProjects, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor, int depth) throws IOException, EOModelException, CoreException {
		if (!searchedProjects.contains(project)) {
			progressMonitor.setTaskName("Adding models from " + project.getName() + " ...");
			searchedProjects.add(project);
			if (!project.exists()) {
				failures.add(new EOModelVerificationFailure(null, "The dependent project '" + project.getName() + "' does not exist.", false));
			} else if (!project.isOpen()) {
				failures.add(new EOModelVerificationFailure(null, "The dependent project '" + project.getName() + "' exists but is not open.", false));
			} else {
				boolean visitedProject = false;
				boolean isJavaProject = project.getNature(JavaCore.NATURE_ID) != null;
				IClasspathEntry[] classpathEntries = null;
				if (isJavaProject) {
					IJavaProject javaProject = JavaCore.create(project);
					classpathEntries = javaProject.getResolvedClasspath(true);
				} else {
					classpathEntries = new IClasspathEntry[0];
				}
				boolean showProgress = (depth == 0);
				if (showProgress) {
					progressMonitor.beginTask("Scanning " + project.getName() + " classpath ...", classpathEntries.length + 1);
				}
				for (int classpathEntryNum = 0; classpathEntryNum < classpathEntries.length; classpathEntryNum++) {
					IClasspathEntry entry = classpathEntries[classpathEntryNum];
					int entryKind = entry.getEntryKind();
					if (entryKind == IClasspathEntry.CPE_LIBRARY) {
						IPath path = entry.getPath();
						IPath frameworkPath = null;
						while (frameworkPath == null && path.lastSegment() != null) {
							String lastSegment = path.lastSegment();
							if (lastSegment != null && lastSegment.endsWith(".framework")) {
								frameworkPath = path;
								File resourcesFolder = frameworkPath.append("Resources").toFile();
								if (!searchedResources.contains(resourcesFolder) && resourcesFolder.exists()) {
									searchedResources.add(resourcesFolder);
									URL urlToLoad = resourcesFolder.toURL();
									modelGroup.loadModelsFromURL(urlToLoad, 1, failures, skipOnDuplicates, progressMonitor);
								}
							} else if (lastSegment != null && lastSegment.endsWith(".jar")) {
								frameworkPath = path;
								URL urlToLoad = new URL("jar:" + path.toFile().toURL() + "!/Resources");
								if (!searchedResources.contains(urlToLoad) && URLUtils.exists(urlToLoad)) {
									modelGroup.loadModelsFromURL(urlToLoad, 1, failures, skipOnDuplicates, progressMonitor);
								}
							} else {
								path = path.removeLastSegments(1);
							}
						}
					} else if (entryKind == IClasspathEntry.CPE_PROJECT) {
						IPath path = entry.getPath();
						IProject dependsOnProject = ResourcesPlugin.getWorkspace().getRoot().getProject(path.lastSegment());
						addModelsFromProject(modelGroup, dependsOnProject, searchedResources, searchedProjects, failures, skipOnDuplicates, progressMonitor, depth + 1);
					} else if (entryKind == IClasspathEntry.CPE_SOURCE) {
						visitedProject = true;
						project.accept(new ModelVisitor(modelGroup, searchedResources, failures, skipOnDuplicates, progressMonitor), IResource.DEPTH_INFINITE, IContainer.EXCLUDE_DERIVED);
					}
					if (showProgress) {
						progressMonitor.worked(1);
					}
				}

				if (!visitedProject) {
					project.accept(new ModelVisitor(modelGroup, searchedResources, failures, skipOnDuplicates, progressMonitor), IResource.DEPTH_INFINITE, IContainer.EXCLUDE_DERIVED);
					if (showProgress) {
						progressMonitor.worked(1);
					}
				}
			}
		}
	}

	protected void addModelsFromEOModelGroupFile(IFile eoModelGroupFile, EOModelGroup modelGroup, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws ParseException, CoreException, IOException, EOModelException {
		EOModel model = null;
		IProject project = eoModelGroupFile.getProject();
		EOGeneratorModel eogeneratorModel = EOGeneratorModel.createModelFromFile(eoModelGroupFile);
		List<EOModelReference> modelRefList = new LinkedList<EOModelReference>();
		modelRefList.addAll(eogeneratorModel.getModels());
		modelRefList.addAll(eogeneratorModel.getRefModels());
		for (EOModelReference modelRef : modelRefList) {
			String modelPath = modelRef.getPath(project);
			File modelFolder = new File(modelPath);
			if (!modelFolder.isAbsolute()) {
				modelFolder = new File(project.getLocation().toFile(), modelPath);
			}
			if (model == null) {
				modelGroup.setEditingModelURL(modelFolder.toURL());
			}
			EOModel modelGroupModel = modelGroup.loadModelFromURL(modelFolder.toURL(), failures, skipOnDuplicates, progressMonitor);
			if (model == null) {
				model = modelGroupModel;
			}
		}
	}

	protected static class ModelVisitor implements IResourceVisitor {
		private EOModelGroup _modelGroup;

		private Set<EOModelVerificationFailure> _failures;

		private Set<Object> _searchedResources;

		private boolean _skipOnDuplicates;

		private IProgressMonitor _progressMonitor;

		public ModelVisitor(EOModelGroup modelGroup, Set<Object> searchedResources, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) {
			_modelGroup = modelGroup;
			_failures = failures;
			_searchedResources = searchedResources;
			_skipOnDuplicates = skipOnDuplicates;
			_progressMonitor = progressMonitor;
		}

		public boolean visit(IResource resource) throws CoreException {
			try {
				if (!resource.isAccessible()) {
					return false;
				}
				if (resource.isDerived()) {
					return false;
				}

				String name = resource.getName();
				if (name != null) {
					if ("build".equals(name) || "dist".equals(name) || "target".equals(name) || name.endsWith(".wo")) {
						return false;
					}

					if (name.endsWith(".framework") || name.endsWith(".woa")) {
						String projectName = resource.getProject().getName();
						if (name.equals(projectName + ".framework") || name.equals(projectName + ".woa")) {
							return false;
						}
					}
				}

				if (resource.getType() == IResource.FOLDER) {
					_progressMonitor.setTaskName("Scanning " + resource.getName() + " ...");
					File resourceFile = resource.getLocation().toFile();
					if (!_searchedResources.contains(resourceFile) && "eomodeld".equals(resource.getFileExtension())) {
						_modelGroup.loadModelFromURL(resourceFile.toURL(), _failures, _skipOnDuplicates, _progressMonitor);
						return false;
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed to load model in " + resource + ": " + e, e));
			}
		}
	}
}

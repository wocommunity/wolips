/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
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
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.wodclipse.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.builder.AbstractFullAndIncrementalBuilder;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.wod.WodReconcilingStrategy;
import org.objectstyle.wolips.wodclipse.wod.model.WodProblem;

public class WodBuilder extends AbstractFullAndIncrementalBuilder {
	private boolean myValidateWOD;

	public WodBuilder() {
		super();
	}

	public boolean buildStarted(int _kind, Map _args, IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		myValidateWOD = Preferences.shouldValidateWODOnBuild();
		return false;
	}

	public boolean buildPreparationDone(int _kind, Map _args, IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		return false;
	}

	public void handleClasses(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// DO NOTHING
	}

	public void handleSource(IResource _resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		if (myValidateWOD) {
			try {
				touchRelatedResources(_resource, _progressMonitor, _buildCache);
			} catch (CoreException e) {
				WodclipsePlugin.getDefault().log(e);
			}
		}
	}

	public void handleClasspath(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// DO NOTHING
	}

	public void handleOther(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		if (myValidateWOD) {
			try {
				boolean validateWodFile = false;
				if (_resource instanceof IFile) {
					IFile file = (IFile) _resource;
					String fileExtension = file.getFileExtension();
					if ("wod".equals(fileExtension)) {
						validateWodFile = true;
					} else if ("html".equals(fileExtension) && _resource.getParent().getName().endsWith(".wo")) {
						validateWodFile = true;
					} else if ("api".equals(fileExtension)) {
						// should we really do something with the component when
						// we change the api?
						// shoulnd't we validate all files using the api?
						validateWodFile = false;

						try {
							touchRelatedResources(_resource, _monitor, _buildCache);
						} catch (CoreException e) {
							WodclipsePlugin.getDefault().log(e);
						}
					}

					if (validateWodFile) {
						validateWodFile(file, _monitor, _buildCache);
					}
				}
			} catch (Throwable e) {
				WodclipsePlugin.getDefault().log(e);
			}
		} else {
			if (_resource instanceof IFile) {
				IFile file = (IFile) _resource;
				String fileExtension = file.getFileExtension();
				if ("wod".equals(fileExtension)) {
					WodReconcilingStrategy.deleteWodProblems(file);
				}
			}
		}
	}

	public void handleWebServerResources(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// DO NOTHING
	}

	public void handleWoappResources(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// DO NOTHING
	}

	protected void touchRelatedResources(IResource _resource, IProgressMonitor _progressMonitor, Map _buildCache) throws CoreException {
		if (_progressMonitor != null) {
			_progressMonitor.subTask("Touching files related to " + _resource.getName() + " ...");
		}

		IMarker[] markers = _resource.getProject().findMarkers(WodclipsePlugin.WOD_PROBLEM, true, IResource.DEPTH_INFINITE);
		Set relatedResources = new HashSet();
		String name = _resource.getName();
		for (int markerNum = 0; markerNum < markers.length; markerNum++) {
			// System.out.println("WodBuilder.touchRelatedResources: Checking "
			// + markers[markerNum]);
			String relatedToFileNames = (String) markers[markerNum].getAttribute(WodProblem.RELATED_TO_FILE_NAMES);
			// System.out.println("WodBuilder.touchRelatedResources: problem in
			// " + markers[markerNum].getResource().getName() + " is related to
			// " + relatedToFileNames);
			if (relatedToFileNames != null && relatedToFileNames.indexOf(name) != -1) {
				// System.out.println("WodBuilder.touchRelatedResources: ...
				// which is this: " + _resource);
				relatedResources.add(markers[markerNum].getResource());
			}
		}

		Iterator relatedResourcesIter = relatedResources.iterator();
		while (relatedResourcesIter.hasNext()) {
			IResource relatedResource = (IResource) relatedResourcesIter.next();
			handleWoappResources(relatedResource, _progressMonitor, _buildCache);
			// relatedResource.touch(_progressMonitor);
		}
	}

	protected void validateWodFile(IFile file, IProgressMonitor _progressMonitor, Map _buildCache) throws CoreException, LocateException {
		String _resourceName = file.getName();
		if (_progressMonitor != null) {
			_progressMonitor.subTask("Locating components for " + _resourceName + " ...");
		}
		LocalizedComponentsLocateResult _locateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(file);
		IFile wodFile = _locateResults.getFirstWodFile();
		if (wodFile != null) {
			if (_progressMonitor != null) {
				_progressMonitor.subTask("Building WOD file " + wodFile.getName() + " ...");
			}

			FileEditorInput input = new FileEditorInput(wodFile);
			WodFileDocumentProvider provider = new WodFileDocumentProvider();
			provider.connect(input);
			try {
				IDocument document = provider.getDocument(input);
				WodReconcilingStrategy.reconcileWodModel(document, _locateResults, _buildCache, _buildCache, _buildCache);
			} finally {
				provider.disconnect(input);
			}
		}
	}
}
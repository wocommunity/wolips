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
package org.objectstyle.wolips.eogenerator.builder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.core.resources.builder.AbstractFullAndIncrementalBuilder;
import org.objectstyle.wolips.eogenerator.model.EOGenerateWorkspaceJob;
import org.objectstyle.wolips.eogenerator.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.ui.Activator;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.result.DefaultLocateResult;
import org.objectstyle.wolips.locate.scope.EOGenLocateScope;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGeneratorBuilder extends AbstractFullAndIncrementalBuilder {

	public EOGeneratorBuilder() {
		super();
	}

	public boolean buildStarted(int _kind, Map _args, IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		return false;
	}

	public boolean buildPreparationDone(int _kind, Map _args, IProgressMonitor _monitor, IProject _project, Map _buildCache) {
		return false;
	}

	public void handleClasses(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// do nothing
	}

	public void handleSource(IResource _resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		// do nothing
	}

	public void handleClasspath(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// do nothing
	}

	public void handleOther(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// do nothing
	}

	public void handleWebServerResources(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		// do nothing
	}

	public void handleWoappResources(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
		try {
			if (Preferences.shouldAutoEOGeneratorOnBuild() && _resource instanceof IContainer && _resource.getName().endsWith(".eomodeld")) {
				EOModelReference modifiedModelReference = new EOModelReference(_resource.getLocation());
				DefaultLocateResult result = new DefaultLocateResult();
				Locate locate = new Locate(new EOGenLocateScope(_resource.getProject()), result);
				locate.locate();
				Set referencingEOGenFiles = new HashSet();
				IResource[] eogenFiles = result.getResources();
				for (int eogenFileNum = 0; eogenFileNum < eogenFiles.length; eogenFileNum++) {
					IFile eogenFile = (IFile) eogenFiles[eogenFileNum];
					EOGeneratorModel eogenModel = EOGeneratorModel.createModelFromFile(eogenFile);
					if (eogenModel.isModelReferenced(modifiedModelReference)) {
						referencingEOGenFiles.add(eogenFile);
					}
				}

				IFile[] finalEOGenFiles = (IFile[]) referencingEOGenFiles.toArray(new IFile[referencingEOGenFiles.size()]);
				EOGenerateWorkspaceJob eogenerateJob = new EOGenerateWorkspaceJob(finalEOGenFiles, false);
				eogenerateJob.schedule();
			}
		} catch (Throwable e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "Internal Error", e));
		}
	}
}
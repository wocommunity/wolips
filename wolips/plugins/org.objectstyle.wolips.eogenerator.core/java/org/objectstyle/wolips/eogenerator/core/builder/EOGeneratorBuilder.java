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
package org.objectstyle.wolips.eogenerator.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.resources.builder.AbstractFullAndIncrementalBuilder;
import org.objectstyle.wolips.eogenerator.core.Activator;
import org.objectstyle.wolips.eogenerator.core.model.EOGenerateWorkspaceJob;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.MarkerEOGeneratorListener;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.result.DefaultLocateResult;
import org.objectstyle.wolips.locate.scope.EOGenLocateScope;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGeneratorBuilder extends AbstractFullAndIncrementalBuilder {

	public EOGeneratorBuilder() {
		super();
	}

	public boolean buildStarted(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		return false;
	}

	public boolean buildPreparationDone(int kind, Map args, IProgressMonitor monitor, IProject project, Map buildCache) {
		return false;
	}

	public void handleClasses(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// do nothing
	}

	public void handleSource(IResource resource, IProgressMonitor progressMonitor, Map buildCache) {
		// do nothing
	}

	public void handleClasspath(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// do nothing
	}

	public void handleOther(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// do nothing
	}

	public void handleWebServerResources(IResource resource, IProgressMonitor monitor, Map buildCache) {
		// do nothing
	}

	public void handleWoappResources(IResource resource, IProgressMonitor monitor, Map buildCache) {
		try {
			if (Preferences.shouldAutoEOGeneratorOnBuild() && resource instanceof IContainer && resource.getName().endsWith(".eomodeld")) {
				EOModelReference modifiedModelReference = new EOModelReference(resource.getLocation());
				DefaultLocateResult result = new DefaultLocateResult();
				Locate locate = new Locate(new EOGenLocateScope(resource.getProject()), result);
				locate.locate();

				IResource[] eogenFiles = result.getResources();
				for (IResource eogenResource : eogenFiles) {
					IFile eogenFile = (IFile) eogenResource;
					EOGeneratorModel eogenModel = EOGeneratorModel.createModelFromFile(eogenFile);
					if (eogenModel.isModelReferenced(modifiedModelReference)) {
						EOGenerateWorkspaceJob eogenerateJob = new EOGenerateWorkspaceJob(eogenFile);
						eogenerateJob.addListener(new MarkerEOGeneratorListener());
						eogenerateJob.schedule();
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			Activator.getDefault().log(e);
		}
	}
}
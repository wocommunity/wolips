/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2005 - 2006 The ObjectStyle Group,
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
package org.objectstyle.wolips.core.resources.internal.build;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.builder.AbstractOldBuilder;
import org.objectstyle.wolips.core.resources.internal.types.project.ProjectAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IProductAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

public abstract class Builder extends IncrementalProjectBuilder {

	protected final static String INCREMENTAL_BUILDER_ID = "org.objectstyle.wolips.incrementalbuilder";

	protected final static String ANT_BUILDER_ID = "org.objectstyle.wolips.antbuilder";

	private BuilderWrapper[] builderWrappers;

	public Builder() {
		super();
		this.builderWrappers = CorePlugin.getDefault().getBuilderWrapper(this.getContext());
	}

	public abstract String getContext();

	protected void clean(IProgressMonitor monitor) throws CoreException {
		IProject project = this.getProject();
		IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
		IBuildAdapter buildAdapter = projectAdapter.getBuildAdapter();
		if (buildAdapter != null) {
			buildAdapter.clean(monitor);
		}
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IProject project = this.getProject();
		IProjectAdapter projectAdapter = (IProjectAdapter) project.getAdapter(IProjectAdapter.class);
		IBuildAdapter buildAdapter = null;
		if ( projectAdapter != null ) {
			buildAdapter = projectAdapter.getBuildAdapter();
		}

		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			this.clean(monitor);
		}
		// System.out.println("Running builder: " + this);
		Map buildCache = new HashMap();
		FullBuildDeltaVisitor fullBuildDeltaVisitor = null;
		IncrementalBuildDeltaVisitor deltaVisitor = null;
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuildDeltaVisitor = new FullBuildDeltaVisitor(builderWrappers, monitor, buildCache);
		} else {
			deltaVisitor = new IncrementalBuildDeltaVisitor(builderWrappers, monitor, buildCache);
		}

		IResourceDelta projectDelta = getDelta(project);
		this.invokeOldBuilder(kind, args, monitor, projectDelta);
		this.notifyBuilderBuildStarted(kind, args, monitor, buildCache);
		try {
			if (fullBuildDeltaVisitor != null) {
				fullBuildDeltaVisitor.buildStarted(project);
				project.accept(fullBuildDeltaVisitor);
			}
			if (deltaVisitor != null && projectDelta != null) {
				deltaVisitor.buildStarted(project);
				projectDelta.accept(deltaVisitor);
			}
		} finally {
			this.notifyBuildPreparationDone(kind, args, monitor, buildCache);

			if (fullBuildDeltaVisitor != null) {
				fullBuildDeltaVisitor.visitingDone();
			}
			if (deltaVisitor != null) {
				deltaVisitor.visitingDone();
			}
		}
		if (buildAdapter != null) {
			buildAdapter.markAsDerivated(monitor);
		} else if (projectAdapter != null) {
			IProductAdapter productAdapter = projectAdapter.getProductAdapter();
			if (productAdapter != null) {
				productAdapter.markAsDerivated(monitor);
			}
		}
		else {
			// IGNORE FOR NOW
		}
		// IWoprojectAdapter woprojectAdapter =
		// projectAdapter.getWoprojectAdapter();
		// if (woprojectAdapter != null) {
		// woprojectAdapter.markAsDerivated(monitor);
		// }
		final IProject workspaceRunnableProject = project;
		IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor runInWorkspaceMonitor) throws CoreException {
				workspaceRunnableProject.refreshLocal(IResource.DEPTH_INFINITE, runInWorkspaceMonitor);
				// MS: touch a build/.version file to notify rapid turnaround of
				// changes
				IFolder buildFolder = workspaceRunnableProject.getFolder(IBuildAdapter.FILE_NAME_BUILD);
				if (buildFolder.exists()) {
					try {
						IFile buildVersion = buildFolder.getFile(".version");
						InputStream versionInputStream = new ByteArrayInputStream(String.valueOf(System.currentTimeMillis()).getBytes());
						if (buildVersion.exists()) {
							buildVersion.setContents(versionInputStream, IResource.FORCE | IResource.DERIVED, runInWorkspaceMonitor);
						} else {
							buildVersion.create(versionInputStream, IResource.FORCE | IResource.DERIVED, runInWorkspaceMonitor);
						}
					} catch (Throwable t) {
						// MS: Don't let this kill the build ...
						CorePlugin.getDefault().log(t);
					}
				}
			}
		};
		ResourcesPlugin.getWorkspace().run(workspaceRunnable, workspaceRunnableProject, 0, (IProgressMonitor) null);
		return null;
	}

	private void invokeOldBuilder(int kind, Map args, IProgressMonitor monitor, IResourceDelta delta) throws CoreException {
		for (int i = 0; i < builderWrappers.length; i++) {
			boolean isOldBuilder = builderWrappers[i].isOldBuilder();
			if (isOldBuilder) {
				AbstractOldBuilder abstractOldBuilder = (AbstractOldBuilder) builderWrappers[i].getBuilder();
				abstractOldBuilder.setProject(this.getProject());
				abstractOldBuilder.invokeOldBuilder(kind, args, monitor, delta);
			}
		}
	}

	private void notifyBuilderBuildStarted(int kind, Map args, IProgressMonitor monitor, Map buildCache) {
		for (int i = 0; i < builderWrappers.length; i++) {
			builderWrappers[i].getBuilder().buildStarted(kind, args, monitor, this.getProject(), buildCache);
		}
	}

	private void notifyBuildPreparationDone(int kind, Map args, IProgressMonitor monitor, Map buildCache) {
		for (int i = 0; i < builderWrappers.length; i++) {
			builderWrappers[i].getBuilder().buildPreparationDone(kind, args, monitor, this.getProject(), buildCache);
		}
	}
}

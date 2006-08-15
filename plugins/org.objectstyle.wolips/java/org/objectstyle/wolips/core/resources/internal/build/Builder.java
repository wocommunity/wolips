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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.core.resources.builder.AbstractOldBuilder;
import org.objectstyle.wolips.core.resources.types.folder.IBuildAdapter;
import org.objectstyle.wolips.core.resources.types.folder.IWoprojectAdapter;
import org.objectstyle.wolips.core.resources.types.project.IProjectAdapter;

public abstract class Builder extends IncrementalProjectBuilder {

	protected final static String INCREMENTAL_BUILDER_ID = "org.objectstyle.wolips.incrementalbuilder";

	protected final static String ANT_BUILDER_ID = "org.objectstyle.wolips.antbuilder";

	private BuilderWrapper[] builderWrappers;

	public Builder() {
		super();
	}

	public abstract String getContext();

	protected void clean(IProgressMonitor monitor) throws CoreException {
		IProject project = this.getProject();
		IProjectAdapter projectAdapter = (IProjectAdapter) project
				.getAdapter(IProjectAdapter.class);
		IBuildAdapter buildAdapter = projectAdapter.getBuildAdapter();
		if (buildAdapter != null) {
			buildAdapter.clean(monitor);
		}
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (this.builderWrappers == null) {
			this.builderWrappers = CorePlugin.getDefault().getBuilderWrapper(
					this.getContext());
		}
		return build(kind, args, monitor, this.builderWrappers);
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor,
			BuilderWrapper[] _builderWrappers) throws CoreException {
		IProject project = this.getProject();
		IProjectAdapter projectAdapter = (IProjectAdapter) project
				.getAdapter(IProjectAdapter.class);
		IBuildAdapter buildAdapter = projectAdapter.getBuildAdapter();
		if (kind == IncrementalProjectBuilder.CLEAN_BUILD
				|| kind == IncrementalProjectBuilder.FULL_BUILD) {
			if (buildAdapter != null) {
				buildAdapter.clean(monitor);
			}
			if (kind == IncrementalProjectBuilder.CLEAN_BUILD) {
				return null;
			}
		}
		Map buildCache = new HashMap();
		AbstractBuildVisitor buildVisitor;
		if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD
				|| kind == IncrementalProjectBuilder.AUTO_BUILD) {
			buildVisitor = new DeltaVisitor(_builderWrappers, monitor,
					buildCache);
		} else {
			buildVisitor = new CleanVisitor(_builderWrappers, monitor,
					buildCache);
		}
		Set builderWrappersRequestingFullBuild = new HashSet();
		IResourceDelta projectDelta = getDelta(project);
		this.invokeOldBuilder(kind, args, monitor, projectDelta,
				_builderWrappers);
		this.notifyBuilderBuildStarted(builderWrappersRequestingFullBuild,
				kind, args, monitor, buildCache, _builderWrappers);
		buildVisitor.buildStarted(project);
		try {
			if (buildVisitor instanceof DeltaVisitor) {
				if (projectDelta != null) {
					projectDelta.accept((DeltaVisitor) buildVisitor);
				}
			} else {
				project.accept((CleanVisitor) buildVisitor);
			}
		} finally {
			this.notifyBuildPreparationDone(builderWrappersRequestingFullBuild,
					kind, args, monitor, buildCache, _builderWrappers);
			buildVisitor.visitingDone();
		}

		if (buildVisitor instanceof DeltaVisitor) {
			BuilderWrapper[] deltaBuilderWrappersRequestingFullBuild = ((DeltaVisitor) buildVisitor)
					.getBuilderWrappersRequestingFullBuild();
			for (int i = 0; i < deltaBuilderWrappersRequestingFullBuild.length; i++) {
				builderWrappersRequestingFullBuild
						.add(deltaBuilderWrappersRequestingFullBuild[i]);
			}

			if (builderWrappersRequestingFullBuild.size() > 0) {
				BuilderWrapper[] builderWrappersRequestingFullBuildList = (BuilderWrapper[]) builderWrappersRequestingFullBuild
						.toArray(new BuilderWrapper[builderWrappersRequestingFullBuild
								.size()]);
				build(IncrementalProjectBuilder.FULL_BUILD, args, monitor,
						builderWrappersRequestingFullBuildList);
			}
		}
		if (kind != IncrementalProjectBuilder.CLEAN_BUILD) {
			if (buildAdapter != null) {
				buildAdapter.markAsDerivated(monitor);
			}
			IWoprojectAdapter woprojectAdapter = projectAdapter
					.getWoprojectAdapter();
			if (woprojectAdapter != null) {
				woprojectAdapter.markAsDerivated(monitor);
			}
		}
		return null;
	}

	private void invokeOldBuilder(int kind, Map args, IProgressMonitor monitor,
			IResourceDelta delta, BuilderWrapper[] _builderWrappers)
			throws CoreException {
		for (int i = 0; i < _builderWrappers.length; i++) {
			boolean isOldBuilder = _builderWrappers[i].isOldBuilder();
			if (isOldBuilder) {
				AbstractOldBuilder abstractOldBuilder = (AbstractOldBuilder) _builderWrappers[i]
						.getBuilder();
				abstractOldBuilder.setProject(this.getProject());
				abstractOldBuilder.invokeOldBuilder(kind, args, monitor, delta);
			}
		}
	}

	private void notifyBuilderBuildStarted(
			Set _builderWrappersRequestingFullBuild, int kind, Map args,
			IProgressMonitor monitor, Map _buildCache,
			BuilderWrapper[] _builderWrappers) {
		for (int i = 0; i < _builderWrappers.length; i++) {
			if (_builderWrappers[i].getBuilder().buildStarted(kind, args,
					monitor, this.getProject(), _buildCache)) {
				_builderWrappersRequestingFullBuild.add(_builderWrappers[i]);
			}
		}
	}

	private void notifyBuildPreparationDone(
			Set _builderWrappersRequestingFullBuild, int kind, Map args,
			IProgressMonitor monitor, Map _buildCache,
			BuilderWrapper[] _builderWrappers) {
		for (int i = 0; i < _builderWrappers.length; i++) {

			if (_builderWrappers[i].getBuilder().buildPreparationDone(kind,
					args, monitor, this.getProject(), _buildCache)) {
				_builderWrappersRequestingFullBuild.add(_builderWrappers[i]);
			}
		}
	}
}

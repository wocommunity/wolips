/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.core.resources.internal.build;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.resources.builder.IBuilder;
import org.objectstyle.wolips.core.resources.builder.IFullBuilder;

/**
 * @author ulrich
 */
public class FullBuildDeltaVisitor extends AbstractBuildVisitor implements IResourceVisitor {
	public FullBuildDeltaVisitor(BuilderWrapper[] builderWrappers, IProgressMonitor _progressMonitor, Map _buildCache) {
		super(builderWrappers, _progressMonitor, _buildCache);
	}

	public boolean visit(IResource resource) throws CoreException {
		boolean visitChildren;
		if (resource == null || isCanceled()) {
			visitChildren = false;
		}
		else if (resource.isDerived()) {
			visitChildren = false;
		} else {
			visitChildren = true;
			Map buildCache = getBuildCache();
			IProgressMonitor progressMonitor = getProgressMonitor();
			int woResourceType = getWoResourceType(resource);
			if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_CLASS) {
				this.notifyBuilderHandleClasses(resource, progressMonitor, buildCache);
			} else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_CLASSPATH) {
				this.notifyBuilderHandleOther(resource, progressMonitor, buildCache);
				this.notifyBuilderClasspath(resource, progressMonitor, buildCache);
			} else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE) {
				visitChildren = false;
			} else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_OTHER) {
				this.notifyBuilderHandleOther(resource, progressMonitor, buildCache);
			} else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_RESOURCE) {
				this.notifyBuilderHandleResources(resource, progressMonitor, buildCache);
			} else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_SOURCE) {
				this.notifyBuilderHandleSource(resource, progressMonitor, buildCache);
			} else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_WEB_SERVER_RESOURCE) {
				this.notifyBuilderHandleWebServerResources(resource, progressMonitor, buildCache);
			}
		}
		return visitChildren;
	}

	private void notifyBuilderClasspath(IResource resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		BuilderWrapper[] builderWrappers = getBuilderWrappers();
		for (int i = 0; i < builderWrappers.length; i++) {
			IBuilder builder = builderWrappers[i].getBuilder();
			if (builder.isEnabled() && builder instanceof IFullBuilder) {
				((IFullBuilder) builder).handleClasspath(resource, _progressMonitor, _buildCache);
			}
		}
	}

	private void notifyBuilderHandleClasses(IResource resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		BuilderWrapper[] builderWrappers = getBuilderWrappers();
		for (int i = 0; i < builderWrappers.length; i++) {
			IBuilder builder = builderWrappers[i].getBuilder();
			if (builder.isEnabled() && builder instanceof IFullBuilder) {
				((IFullBuilder) builder).handleClasses(resource, _progressMonitor, _buildCache);
			}
		}
	}

	private void notifyBuilderHandleSource(IResource resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		BuilderWrapper[] builderWrappers = getBuilderWrappers();
		for (int i = 0; i < builderWrappers.length; i++) {
			IBuilder builder = builderWrappers[i].getBuilder();
			if (builder.isEnabled() && builder instanceof IFullBuilder) {
				((IFullBuilder) builder).handleSource(resource, _progressMonitor, _buildCache);
			}
		}
	}

	private void notifyBuilderHandleResources(IResource resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		BuilderWrapper[] builderWrappers = getBuilderWrappers();
		for (int i = 0; i < builderWrappers.length; i++) {
			IBuilder builder = builderWrappers[i].getBuilder();
			if (builder.isEnabled() && builder instanceof IFullBuilder) {
				((IFullBuilder) builder).handleWoappResources(resource, _progressMonitor, _buildCache);
			}
		}
	}

	private void notifyBuilderHandleWebServerResources(IResource resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		BuilderWrapper[] builderWrappers = getBuilderWrappers();
		for (int i = 0; i < builderWrappers.length; i++) {
			IBuilder builder = builderWrappers[i].getBuilder();
			if (builder.isEnabled() && builder instanceof IFullBuilder) {
				((IFullBuilder) builder).handleWebServerResources(resource, _progressMonitor, _buildCache);
			}
		}
	}

	private void notifyBuilderHandleOther(IResource resource, IProgressMonitor _progressMonitor, Map _buildCache) {
		BuilderWrapper[] builderWrappers = getBuilderWrappers();
		for (int i = 0; i < builderWrappers.length; i++) {
			IBuilder builder = builderWrappers[i].getBuilder();
			if (builder.isEnabled() && builder instanceof IFullBuilder) {
				((IFullBuilder) builder).handleOther(resource, _progressMonitor, _buildCache);
			}
		}
	}
}

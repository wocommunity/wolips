/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.objectstyle.wolips.core.resources.builder.IBuilder;
import org.objectstyle.wolips.core.resources.builder.IDeltaBuilder;

/**
 * @author ulrich/mike
 */
public class DeltaVisitor extends AbstractBuildVisitor implements IResourceDeltaVisitor {
  private Set myFullBuilderWrappers;

  public DeltaVisitor(BuilderWrapper[] builderWrappers, IProgressMonitor _progressMonitor, Map _buildCache) {
    super(builderWrappers, _progressMonitor, _buildCache);
    myFullBuilderWrappers = new HashSet();
  }

  public BuilderWrapper[] getBuilderWrappersRequestingFullBuild() {
    BuilderWrapper[] builderWrappers = (BuilderWrapper[]) myFullBuilderWrappers.toArray(new BuilderWrapper[myFullBuilderWrappers.size()]);
    return builderWrappers;
  }

  public boolean visit(IResourceDelta delta) throws CoreException {
    if (delta == null || isCanceled()) {
      return false;
    }
    IResource resource = delta.getResource();
    IProgressMonitor progressMonitor = getProgressMonitor();
    Map buildCache = getBuildCache();
    int woResourceType = getWoResourceType(resource);
    if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_CLASS) {
      this.notifyBuilderHandleClassesDelta(delta, progressMonitor, buildCache);
    }
    else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_CLASSPATH) {
      this.notifyBuilderHandleOtherDelta(delta, progressMonitor, buildCache);
      this.notifyBuilderClasspathChanged(delta, progressMonitor, buildCache);
    }
    else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_IGNORE) {

    }
    else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_OTHER) {
      this.notifyBuilderHandleOtherDelta(delta, progressMonitor, buildCache);
    }
    else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_RESOURCE) {
      this.notifyBuilderHandleResourcesDelta(delta, progressMonitor, buildCache);
    }
    else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_SOURCE) {
      this.notifyBuilderHandleSourceDelta(delta, progressMonitor, buildCache);
    }
    else if (woResourceType == AbstractBuildVisitor.WO_RESOURCE_TYPE_WEB_SERVER_RESOURCE) {
      this.notifyBuilderHandleWebServerResourcesDelta(delta, progressMonitor, buildCache);
    }
    return true;
  }

  private void notifyBuilderClasspathChanged(IResourceDelta delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    BuilderWrapper[] builderWrappers = getBuilderWrappers();
    for (int i = 0; i < builderWrappers.length; i++) {
      IBuilder builder = builderWrappers[i].getBuilder();
      if (builder instanceof IDeltaBuilder) {
        if (((IDeltaBuilder) builder).classpathChanged(delta, _progressMonitor, _buildCache)) {
          myFullBuilderWrappers.add(builderWrappers[i]);
        }
      }
    }
  }

  private void notifyBuilderHandleClassesDelta(IResourceDelta delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    BuilderWrapper[] builderWrappers = getBuilderWrappers();
    for (int i = 0; i < builderWrappers.length; i++) {
      IBuilder builder = builderWrappers[i].getBuilder();
      if (builder instanceof IDeltaBuilder) {
        if (((IDeltaBuilder) builder).handleClassesDelta(delta, _progressMonitor, _buildCache)) {
          myFullBuilderWrappers.add(builderWrappers[i]);
        }
      }
    }
  }

  private void notifyBuilderHandleResourcesDelta(IResourceDelta delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    BuilderWrapper[] builderWrappers = getBuilderWrappers();
    for (int i = 0; i < builderWrappers.length; i++) {
      IBuilder builder = builderWrappers[i].getBuilder();
      if (builder instanceof IDeltaBuilder) {
        if (((IDeltaBuilder) builder).handleWoappResourcesDelta(delta, _progressMonitor, _buildCache)) {
          myFullBuilderWrappers.add(builderWrappers[i]);
        }
      }
    }
  }

  private void notifyBuilderHandleSourceDelta(IResourceDelta delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    BuilderWrapper[] builderWrappers = getBuilderWrappers();
    for (int i = 0; i < builderWrappers.length; i++) {
      IBuilder builder = builderWrappers[i].getBuilder();
      if (builder instanceof IDeltaBuilder) {
        if (((IDeltaBuilder) builder).handleSourceDelta(delta, _progressMonitor, _buildCache)) {
          myFullBuilderWrappers.add(builderWrappers[i]);
        }
      }
    }
  }

  private void notifyBuilderHandleWebServerResourcesDelta(IResourceDelta delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    BuilderWrapper[] builderWrappers = getBuilderWrappers();
    for (int i = 0; i < builderWrappers.length; i++) {
      IBuilder builder = builderWrappers[i].getBuilder();
      if (builder instanceof IDeltaBuilder) {
        if (((IDeltaBuilder) builder).handleWebServerResourcesDelta(delta, _progressMonitor, _buildCache)) {
          myFullBuilderWrappers.add(builderWrappers[i]);
        }
      }
    }
  }

  private void notifyBuilderHandleOtherDelta(IResourceDelta delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    BuilderWrapper[] builderWrappers = getBuilderWrappers();
    for (int i = 0; i < builderWrappers.length; i++) {
      IBuilder builder = builderWrappers[i].getBuilder();
      if (builder instanceof IDeltaBuilder) {
        if (((IDeltaBuilder) builder).handleOtherDelta(delta, _progressMonitor, _buildCache)) {
          myFullBuilderWrappers.add(builderWrappers[i]);
        }
      }
    }
  }
}

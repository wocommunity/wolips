/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.eomodeler.model;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.objectstyle.wolips.datasets.adaptable.Project;

public class EclipseEOModelGroupFactory {
  protected static void addModelsFromFolderIfNecessary(EOModelGroup _modelGroup, File _folder, Set _searchedFolders, boolean _recursive) throws IOException, EOModelException {
    if (!_searchedFolders.contains(_folder) && _folder.exists()) {
      _searchedFolders.add(_folder);
      _modelGroup.addModelsFromFolder(_folder, _recursive);
    }
  }

  public static EOModel createModel(IResource _modelResource) throws CoreException, IOException, EOModelException {
    IProject project = _modelResource.getProject();
    EOModelGroup modelGroup = EclipseEOModelGroupFactory.createModelGroup(project);
    IContainer modelContainer;
    if (_modelResource.getType() == IResource.FILE) {
      modelContainer = _modelResource.getParent();
    }
    else {
      modelContainer = (IContainer) _modelResource;
    }
    String modelFileName = modelContainer.getName();
    String modelName = modelFileName.substring(0, modelFileName.indexOf('.'));
    EOModel model = modelGroup.getModelNamed(modelName);
    return model;
  }

  public static EOModelGroup createModelGroup(IProject _project) throws CoreException, IOException, EOModelException {
    EOModelGroup modelGroup = new EOModelGroup();
    Set searchedFolders = new HashSet();

    EclipseEOModelGroupFactory.addModelsFromFolderIfNecessary(modelGroup, _project.getLocation().toFile(), searchedFolders, true);

    IJavaProject javaProject = JavaCore.create(_project);
    IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
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
          }
          else {
            path = path.removeLastSegments(1);
          }
        }
        if (frameworkPath != null) {
          EclipseEOModelGroupFactory.addModelsFromFolderIfNecessary(modelGroup, frameworkPath.append("Resources").toFile(), searchedFolders, false);
        }
      }
      else if (entryKind == IClasspathEntry.CPE_PROJECT) {
        IPath path = entry.getPath();
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.lastSegment());
        if (project != null) {
          Project wolipsProject = (Project) project.getAdapter(Project.class);
          if (wolipsProject != null && wolipsProject.hasWOLipsNature()) {
            EclipseEOModelGroupFactory.addModelsFromFolderIfNecessary(modelGroup, project.getLocation().toFile(), searchedFolders, true);
          }
        }
      }
    }
    return modelGroup;
  }
}

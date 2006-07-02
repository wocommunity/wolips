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
  protected static void addModelsFromFolderIfNecessary(EOModelGroup _modelGroup, File _folder, Set _searchedFolders, boolean _recursive) throws IOException {
    if (!_searchedFolders.contains(_folder) && _folder.exists()) {
      _searchedFolders.add(_folder);
      _modelGroup.addModelsFromFolder(_folder, _recursive);
    }
  }

  public static EOModel createModel(IResource _modelResource) throws CoreException, IOException {
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

  public static EOModelGroup createModelGroup(IProject _project) throws CoreException, IOException {
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

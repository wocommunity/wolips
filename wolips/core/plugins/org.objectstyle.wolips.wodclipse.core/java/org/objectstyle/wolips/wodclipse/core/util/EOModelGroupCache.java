package org.objectstyle.wolips.wodclipse.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;

public class EOModelGroupCache {
  private Map<IProject, EOModelGroup> _modelGroupCache;

  public EOModelGroupCache() {
    _modelGroupCache = new HashMap<IProject, EOModelGroup>();
  }

  public synchronized EOModelGroup getModelGroup(IProject project) {
    EOModelGroup modelGroup;
    if (project == null) {
      modelGroup = new EOModelGroup();
    }
    else {
      modelGroup = _modelGroupCache.get(project);
      if (modelGroup == null) {
        modelGroup = new EOModelGroup();

        Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
        try {
          IEOModelGroupFactory.Utility.loadModelGroup(project, modelGroup, failures, true, null, new NullProgressMonitor());
          _modelGroupCache.put(project, modelGroup);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return modelGroup;
  }

  public synchronized void clearCacheForProject(IProject project) {
    clearCacheForProject(project, new HashSet<IProject>());
  }

  public synchronized void clearCacheForProject(IProject project, Set<IProject> visitedProjects) {
    visitedProjects.add(project);
    _modelGroupCache.remove(project);
    for (IProject referencingProject : project.getReferencingProjects()) {
      if (!visitedProjects.contains(referencingProject)) {
        clearCacheForProject(referencingProject, visitedProjects);
      }
    }
  }

  public synchronized void clearCache() {
    _modelGroupCache.clear();
  }
}

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
        System.out.println("EOModelGroupCache.getModelGroup: loading " + project);
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
      else {
        System.out.println("EOModelGroupCache.getModelGroup: loaded " + project + " from cache!");
      }
    }
    return modelGroup;
  }

  public synchronized void clearCacheForProject(IProject project) {
    System.out.println("EOModelGroupCache.clearCacheForProject: clearing " + project);
    _modelGroupCache.remove(project);
    for (IProject referencingProject : project.getReferencingProjects()) {
      clearCacheForProject(referencingProject);
    }
  }

  public synchronized void clearCache() {
    _modelGroupCache.clear();
  }
}

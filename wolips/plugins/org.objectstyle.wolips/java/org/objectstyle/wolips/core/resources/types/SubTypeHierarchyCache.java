package org.objectstyle.wolips.core.resources.types;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeHierarchyChangedListener;
import org.eclipse.jdt.core.JavaModelException;

public class SubTypeHierarchyCache {

  private static class HierarchyCacheEntry implements ITypeHierarchyChangedListener {
	  private IJavaProject fProject;
    private ITypeHierarchy fTypeHierarchy;
    private long fLastAccess;

    public HierarchyCacheEntry(ITypeHierarchy hierarchy, IJavaProject project) {
      fTypeHierarchy = hierarchy;
      fTypeHierarchy.addTypeHierarchyChangedListener(this);
      fProject = project;
      markAsAccessed();
    }

    public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
      removeHierarchyEntryFromCache(this);
    }

    public ITypeHierarchy getTypeHierarchy() {
      return fTypeHierarchy;
    }

    public void markAsAccessed() {
      fLastAccess = System.currentTimeMillis();
    }

    public long getLastAccess() {
      return fLastAccess;
    }

    public IJavaProject getProject() {
      return fProject;
    }
    
    public void dispose() {
      fTypeHierarchy.removeTypeHierarchyChangedListener(this);
      fTypeHierarchy = null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "Sub hierarchy of: " + fTypeHierarchy.getType().getElementName(); //$NON-NLS-1$
    }

  }

  private static final int CACHE_SIZE = 24;

  private static List<HierarchyCacheEntry> fgHierarchyCache = new ArrayList<HierarchyCacheEntry>(CACHE_SIZE);

  private static int fgCacheHits = 0;
  private static int fgCacheMisses = 0;

  /**
   * Get a hierarchy for the given type
   */
  public static ITypeHierarchy getTypeHierarchyInProject(IType type, IJavaProject project) throws JavaModelException {
    return getTypeHierarchyInProject(type, project, null);
  }

  /**
   * Get a hierarchy for the given type
   */
  public static ITypeHierarchy getTypeHierarchyInProject(IType type, IJavaProject project, IProgressMonitor progressMonitor) throws JavaModelException {
    ITypeHierarchy hierarchy = findTypeHierarchyInProjectInCache(type, project);
    if (hierarchy == null) {
      fgCacheMisses++;
      if (project != null) {
    	  hierarchy = type.newTypeHierarchy(project, progressMonitor);
      }
      else {
    	  hierarchy = type.newTypeHierarchy(progressMonitor);
      }
      addTypeHierarchyInProjectToCache(hierarchy, project);
    }
    else {
      fgCacheHits++;
    }
    return hierarchy;
  }

  private static void addTypeHierarchyInProjectToCache(ITypeHierarchy hierarchy, IJavaProject project) {
    synchronized (fgHierarchyCache) {
      int nEntries = fgHierarchyCache.size();
      if (nEntries >= CACHE_SIZE) {
        // find obsolete entries or remove entry that was least recently accessed
        HierarchyCacheEntry oldest = null;
        List<HierarchyCacheEntry> obsoleteHierarchies = new ArrayList<HierarchyCacheEntry>(CACHE_SIZE);
        for (int i = 0; i < nEntries; i++) {
          HierarchyCacheEntry entry = fgHierarchyCache.get(i);
          ITypeHierarchy curr = entry.getTypeHierarchy();
          if (!curr.exists() || hierarchy.contains(curr.getType())) {
            obsoleteHierarchies.add(entry);
          }
          else {
            if (oldest == null || entry.getLastAccess() < oldest.getLastAccess()) {
              oldest = entry;
            }
          }
        }
        if (!obsoleteHierarchies.isEmpty()) {
          for (int i = 0; i < obsoleteHierarchies.size(); i++) {
            removeHierarchyEntryFromCache(obsoleteHierarchies.get(i));
          }
        }
        else if (oldest != null) {
          removeHierarchyEntryFromCache(oldest);
        }
      }
      HierarchyCacheEntry newEntry = new HierarchyCacheEntry(hierarchy, project);
      fgHierarchyCache.add(newEntry);
    }
  }

  /**
   * Check if the given type is in the hierarchy
   * @param type
   * @return Return <code>true</code> if a hierarchy for the given type is cached.
   */
  public static boolean hasInCacheInProject(IType type, IJavaProject project) {
    return findTypeHierarchyInProjectInCache(type, project) != null;
  }

  private static ITypeHierarchy findTypeHierarchyInProjectInCache(IType type, IJavaProject project) {
    synchronized (fgHierarchyCache) {
      for (int i = fgHierarchyCache.size() - 1; i >= 0; i--) {
        HierarchyCacheEntry curr = fgHierarchyCache.get(i);
        ITypeHierarchy hierarchy = curr.getTypeHierarchy();
        if (!hierarchy.exists()) {
          removeHierarchyEntryFromCache(curr);
        }
        else {
          if (((project == null && curr.getProject() == null) || (project != null && project.equals(curr.getProject()))) && hierarchy.contains(type)) {
            curr.markAsAccessed();
            return hierarchy;
          }
        }
      }
    }
    return null;
  }

  static void removeHierarchyEntryFromCache(HierarchyCacheEntry entry) {
    synchronized (fgHierarchyCache) {
      entry.dispose();
      fgHierarchyCache.remove(entry);
    }
  }

  /**
   * Gets the number of times the hierarchy could be taken from the hierarchy.
   * @return Returns a int
   */
  public static int getCacheHits() {
    return fgCacheHits;
  }

  /**
   * Gets the number of times the hierarchy was build. Used for testing.
   * @return Returns a int
   */
  public static int getCacheMisses() {
    return fgCacheMisses;
  }
}

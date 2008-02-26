package org.objectstyle.wolips.bindings.wod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.core.resources.types.LimitedLRUCache;
import org.objectstyle.wolips.core.resources.types.SubTypeHierarchyCache;
import org.objectstyle.wolips.core.resources.types.SuperTypeHierarchyCache;

public class TypeCache {
  private Map<IJavaProject, ApiCache> _apiCache;

  private LimitedLRUCache<IType, TypeCacheEntry> _typeCacheEntries;

  public TypeCache() {
    _typeCacheEntries = new LimitedLRUCache<IType, TypeCacheEntry>(1000);
    _apiCache = new HashMap<IJavaProject, ApiCache>();
  }

  public TypeCacheEntry getTypeCacheEntry(IType type) throws JavaModelException {
    synchronized (_typeCacheEntries) {
      TypeCacheEntry entry = _typeCacheEntries.get(type);
      if (entry == null) {
        entry = new TypeCacheEntry(type);
        _typeCacheEntries.put(type, entry);
      }
      return entry;
    }
  }

  public ApiCache getApiCache(IJavaProject javaProject) {
    ApiCache apiCache;
    if (javaProject == null) {
      apiCache = new ApiCache();
    }
    else {
      synchronized (_apiCache) {
        apiCache = _apiCache.get(javaProject);
        if (apiCache == null) {
          apiCache = new ApiCache();
          _apiCache.put(javaProject, apiCache);
        }
      }
    }
    return apiCache;

  }

  public List<BindingValueKey> getBindingValueAccessorKeys(IJavaProject javaProject, IType type, String name) throws JavaModelException {
    return getTypeCacheEntry(type).getBindingValueAccessorKeys(javaProject, name);
  }

  public List<BindingValueKey> getBindingValueMutatorKeys(IJavaProject javaProject, IType type, String name) throws JavaModelException {
    return getTypeCacheEntry(type).getBindingValueMutatorKeys(javaProject, name);
  }

  public void clearCacheForProject(IProject project) {
    if (project != null) {
      System.out.println("TypeCache.clearCacheForProject: CLEARING " + project);
      List<IType> typesToClear = new LinkedList<IType>();
      synchronized (_typeCacheEntries) {
        for (Map.Entry<IType, TypeCacheEntry> entry : _typeCacheEntries.entrySet()) {
          IResource resource = entry.getValue().getResource();
          if (resource != null && project.equals(resource.getProject())) {
            typesToClear.add(entry.getKey());
          }
        }
        for (IType typeToClear : typesToClear) {
          clearCacheForType(typeToClear);
        }
      }
    }
  }

  public void clearCacheForResource(IResource resource) {
    if (resource != null) {
      List<IType> typesToClear = new LinkedList<IType>();
      synchronized (_typeCacheEntries) {
        for (Map.Entry<IType, TypeCacheEntry> entry : _typeCacheEntries.entrySet()) {
          if (resource.equals(entry.getValue().getResource())) {
            typesToClear.add(entry.getKey());
          }
        }
        for (IType typeToClear : typesToClear) {
          clearCacheForType(typeToClear);
        }
      }
    }
  }

  public void clearCacheForType(IType declaringType) {
    synchronized (_typeCacheEntries) {
      System.out.println("TypeCache.clearCacheForType: clearing cache for " + declaringType.getFullyQualifiedName());
      _typeCacheEntries.remove(declaringType);
    }
  }

  public IType getTypeForNameInType(String typeName, IType declaringType) throws JavaModelException {
    return getTypeCacheEntry(declaringType).getTypeForName(typeName);
  }

  public void clearCache() {
    synchronized (_typeCacheEntries) {
      _typeCacheEntries.clear();
    }
  }

  public List<IType> getSupertypesOf(IType type) throws JavaModelException {
    //System.out.println("TypeCache.getSupertypesOf: " + type.getFullyQualifiedName() + " (hits=" + SuperTypeHierarchyCache.getCacheHits() + ",misses=" + SuperTypeHierarchyCache.getCacheMisses() + ")");
    try {
      return getTypeCacheEntry(type).getSupertypes();
    }
    catch (JavaModelException e) {
      clearCacheForType(type);
      throw e;
    }
  }

  public List<IType> getSubtypesOfInProject(IType type, IJavaProject project) throws JavaModelException {
    try {
      return getTypeCacheEntry(type).getSubtypesInProject(project);
    }
    catch (JavaModelException e) {
      clearCacheForType(type);
      throw e;
    }
  }

  public class TypeCacheEntry {
    private IType _type;

    private IResource _resource;

    private Map<String, IType> _nextTypeCache;

    private Map<String, List<BindingValueKey>> _bindingValueAccessorKeys;

    private Map<String, List<BindingValueKey>> _bindingValueMutatorKeys;

    public TypeCacheEntry(IType type) throws JavaModelException {
      _type = type;
      _resource = _type.getUnderlyingResource();
      _nextTypeCache = new HashMap<String, IType>();
      _bindingValueAccessorKeys = new HashMap<String, List<BindingValueKey>>();
      _bindingValueMutatorKeys = new HashMap<String, List<BindingValueKey>>();
    }

    public IResource getResource() {
      return _resource;
    }

    public List<BindingValueKey> getBindingValueAccessorKeys(IJavaProject javaProject, String name) throws JavaModelException {
      synchronized (_bindingValueAccessorKeys) {
        List<BindingValueKey> bindingValueAccessorKeys = _bindingValueAccessorKeys.get(name);
        //System.out.println("TypeCacheEntry.getBindingValueAccessorKeys: " + name + ": " + bindingValueAccessorKeys);
        if (bindingValueAccessorKeys == null) {
          //System.out.println("TypeCache.getBindingValueAccessorKeys: MISS " + type.getElementName() + ": " + name);
          bindingValueAccessorKeys = BindingReflectionUtils.getBindingKeys(javaProject, _type, name, true, BindingReflectionUtils.ACCESSORS_OR_VOID, false, TypeCache.this);
          // MS: Don't cache this for now -- I don't know how many end up in here and how long they
          // hang around, but I think the answer is "a lot" and "for a long time".  However, it's a huge performance win.
          _bindingValueAccessorKeys.put(name, bindingValueAccessorKeys);
        }
        else {
          //System.out.println("TypeCache.getBindingValueAccessorKeys: HIT  " + type.getElementName() + ": " + name);
        }
        return bindingValueAccessorKeys;
      }
    }

    public List<BindingValueKey> getBindingValueMutatorKeys(IJavaProject javaProject, String name) throws JavaModelException {
      synchronized (_bindingValueMutatorKeys) {
        List<BindingValueKey> bindingValueMutatorKeys = _bindingValueMutatorKeys.get(name);
        if (bindingValueMutatorKeys == null) {
          //System.out.println("TypeCache.getBindingValueMutatorKeys: MISS " + type.getElementName() + ": " + name);
          bindingValueMutatorKeys = BindingReflectionUtils.getBindingKeys(javaProject, _type, name, true, BindingReflectionUtils.MUTATORS_ONLY, false, TypeCache.this);
          // MS: Don't cache this for now -- I don't know how many end up in here and how long they
          // hang around, but I think the answer is "a lot" and "for a long time".  However, it's a huge performance win.
          _bindingValueMutatorKeys.put(name, bindingValueMutatorKeys);
        }
        else {
          //System.out.println("TypeCache.getBindingValueMutatorKeys: HIT  " + type.getElementName() + ": " + name);
        }
        return bindingValueMutatorKeys;
      }
    }

    public IType getTypeForName(String typeName) throws JavaModelException {
      IType type;
      if ("void".equals(typeName) || (typeName != null && typeName.length() == 1)) {
        // ignore primitives
        type = null;
      }
      else {
        synchronized (_nextTypeCache) {
          type = _nextTypeCache.get(typeName);
        }
        if (type == null) {
          //long t = System.currentTimeMillis();
          // MS: This call right here is the DEVIL.  This is BY FAR where the
          // majority of time is spent during component validation.  It's also
          // unfortunately completely necessary, but caching should focus on 
          // this in the future.
          String resolvedNextTypeName = JavaModelUtil.getResolvedTypeName(typeName, _type);
          if (resolvedNextTypeName == null) {
            System.out.println("TypeCacheEntry.getTypeForName: Failed to resolve type name " + typeName + " in component " + _type.getElementName());
          }
          else if (BindingReflectionUtils.isPrimitive(resolvedNextTypeName)) {
            // ignore primitives if we get this far
          }
          else {
            type = JavaModelUtil.findType(_type.getJavaProject(), resolvedNextTypeName);
            if (type != null) {
              synchronized (_nextTypeCache) {
                _nextTypeCache.put(typeName, type);
              }
            }
            else {
              System.out.println("TypeCacheEntry.getTypeForName: couldn't resolve " + resolvedNextTypeName);
            }
          }
        }
        // System.out.println("TypeCacheEntry.getTypeForName:   " + typeName + " => " + (System.currentTimeMillis() - t) + " => " + type);
      }
      return type;
    }

    public List<IType> getSupertypes() throws JavaModelException {
      ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(_type);
      List<IType> types = new LinkedList<IType>();
      types.add(_type);
      for (IType type : typeHierarchy.getAllSupertypes(_type)) {
        types.add(type);
      }
      return types;
    }

    public List<IType> getSubtypesInProject(IJavaProject project) throws JavaModelException {
      //System.out.println("TypeCache.getSubtypesOf: " + type.getFullyQualifiedName() + " (hits=" + SubTypeHierarchyCache.getCacheHits() + ",misses=" + SubTypeHierarchyCache.getCacheMisses() + ")");
      ITypeHierarchy typeHierarchy = SubTypeHierarchyCache.getTypeHierarchyInProject(_type, project);
      List<IType> types = new LinkedList<IType>();
      IType[] subtypes = typeHierarchy.getAllSubtypes(_type);
      for (int subtypeNum = subtypes.length - 1; subtypeNum >= 0; subtypeNum--) {
        types.add(subtypes[subtypeNum]);
      }
      types.add(_type);
      return types;
    }
  }
}

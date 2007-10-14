package org.objectstyle.wolips.bindings.wod;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.core.resources.types.SubTypeHierarchyCache;
import org.objectstyle.wolips.core.resources.types.SuperTypeHierarchyCache;

public class TypeCache {
  private ApiCache _apiCache;

  private HashMap<IType, Map<String, IType>> _typeContextCache;

  public TypeCache() {
    this(null);
  }

  public TypeCache(ApiCache apiCache) {
    _typeContextCache = new HashMap<IType, Map<String, IType>>();
    _apiCache = apiCache;
    if (_apiCache == null) {
      _apiCache = new ApiCache();
    }
  }

  public ApiCache getApiCache() {
    return _apiCache;
  }

  public Map<IType, Map<String, IType>> getTypeContextCache() {
    return _typeContextCache;
  }

  public void clearCache() {
    _typeContextCache.clear();
  }

  public IType[] getSupertypesOf(IType type) throws JavaModelException {
    //System.out.println("TypeCache.getSupertypesOf: " + type.getFullyQualifiedName() + " (hits=" + SuperTypeHierarchyCache.getCacheHits() + ",misses=" + SuperTypeHierarchyCache.getCacheMisses() + ")");
    ITypeHierarchy typeHierarchy = SuperTypeHierarchyCache.getTypeHierarchy(type);
    IType[] types = typeHierarchy.getAllTypes();
    return types;
  }

  public IType[] getSubtypesOf(IType type) throws JavaModelException {
    //System.out.println("TypeCache.getSubtypesOf: " + type.getFullyQualifiedName() + " (hits=" + SubTypeHierarchyCache.getCacheHits() + ",misses=" + SubTypeHierarchyCache.getCacheMisses() + ")");
    ITypeHierarchy typeHierarchy = SubTypeHierarchyCache.getTypeHierarchy(type);
    IType[] types = typeHierarchy.getAllTypes();
    return types;
  }
}

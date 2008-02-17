package org.objectstyle.wolips.bindings.wod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.core.resources.types.SubTypeHierarchyCache;
import org.objectstyle.wolips.core.resources.types.SuperTypeHierarchyCache;

public class TypeCache {
  private ApiCache _apiCache;

  private Map<IType, Map<String, IType>> _typeContextCache;
  
  private Map<IType, Map<String, List<BindingValueKey>>> _bindingValueAccessorKeys;
  
  private Map<IType, Map<String, List<BindingValueKey>>> _bindingValueMutatorKeys;

  public TypeCache() {
    this(null);
  }

  public TypeCache(ApiCache apiCache) {
    _typeContextCache = new HashMap<IType, Map<String, IType>>();
    _bindingValueAccessorKeys = new HashMap<IType, Map<String,List<BindingValueKey>>>();
    _bindingValueMutatorKeys = new HashMap<IType, Map<String,List<BindingValueKey>>>();
    _apiCache = apiCache;
    if (_apiCache == null) {
      _apiCache = new ApiCache();
    }
  }

  public List<BindingValueKey> getBindingValueAccessorKeys(IJavaProject javaProject, IType type, String name) throws JavaModelException {
    List<BindingValueKey> bindingValueAccessorKeys = null;
    Map<String, List<BindingValueKey>> bindingValueAccessorKeysForName = _bindingValueAccessorKeys.get(type);
    if (bindingValueAccessorKeysForName == null) {
      bindingValueAccessorKeysForName = new HashMap<String, List<BindingValueKey>>();
      _bindingValueAccessorKeys.put(type, bindingValueAccessorKeysForName);
    }
    else {
      bindingValueAccessorKeys = bindingValueAccessorKeysForName.get(name);
    }
    if (bindingValueAccessorKeys == null) {
      //System.out.println("TypeCache.getBindingValueAccessorKeys: MISS " + type.getElementName() + ": " + name);
      bindingValueAccessorKeys = BindingReflectionUtils.getBindingKeys(javaProject, type, name, true, BindingReflectionUtils.ACCESSORS_OR_VOID, this);
      // MS: Don't cache this for now -- I don't know how many end up in here and how long they
      // hang around, but I think the answer is "a lot" and "for a long time".  However, it's a huge performance win.
      // bindingValueAccessorKeysForName.put(name, bindingValueAccessorKeys);
    }
    else {
      //System.out.println("TypeCache.getBindingValueAccessorKeys: HIT  " + type.getElementName() + ": " + name);
    }
    return bindingValueAccessorKeys;
  }

  public List<BindingValueKey> getBindingValueMutatorKeys(IJavaProject javaProject, IType type, String name) throws JavaModelException {
    List<BindingValueKey> bindingValueMutatorKeys = null;
    Map<String, List<BindingValueKey>> bindingValueMutatorKeysForName = _bindingValueMutatorKeys.get(type);
    if (bindingValueMutatorKeysForName == null) {
      bindingValueMutatorKeysForName = new HashMap<String, List<BindingValueKey>>();
      _bindingValueMutatorKeys.put(type, bindingValueMutatorKeysForName);
    }
    else {
      bindingValueMutatorKeys = bindingValueMutatorKeysForName.get(name);
    }
    if (bindingValueMutatorKeys == null) {
      //System.out.println("TypeCache.getBindingValueMutatorKeys: MISS " + type.getElementName() + ": " + name);
      bindingValueMutatorKeys = BindingReflectionUtils.getBindingKeys(javaProject, type, name, true, BindingReflectionUtils.MUTATORS_ONLY, this);
      // MS: Don't cache this for now -- I don't know how many end up in here and how long they
      // hang around, but I think the answer is "a lot" and "for a long time".  However, it's a huge performance win.
      // bindingValueMutatorKeysForName.put(name, bindingValueMutatorKeys);
    }
    else {
      //System.out.println("TypeCache.getBindingValueMutatorKeys: HIT  " + type.getElementName() + ": " + name);
    }
    return bindingValueMutatorKeys;
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

  public IType[] getSubtypesOfInProject(IType type, IJavaProject project) throws JavaModelException {
    //System.out.println("TypeCache.getSubtypesOf: " + type.getFullyQualifiedName() + " (hits=" + SubTypeHierarchyCache.getCacheHits() + ",misses=" + SubTypeHierarchyCache.getCacheMisses() + ")");
    ITypeHierarchy typeHierarchy = SubTypeHierarchyCache.getTypeHierarchyInProject(type, project);
    IType[] types = typeHierarchy.getAllTypes();
    return types;
  }
}

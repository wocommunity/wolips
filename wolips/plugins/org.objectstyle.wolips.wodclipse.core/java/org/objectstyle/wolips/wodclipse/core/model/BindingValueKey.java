package org.objectstyle.wolips.wodclipse.core.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class BindingValueKey {
  private String _bindingName;

  private IMember _bindingMember;

  private IJavaProject _javaProject;

  private IType _nextType;

  private WodParserCache _cache;

  public BindingValueKey(String bindingName, IMember bindingMember, IJavaProject javaProject, WodParserCache cache) {
    _bindingName = bindingName;
    _bindingMember = bindingMember;
    _javaProject = javaProject;
    _cache = cache;
  }

  public IType getDeclaringType() {
    return (_bindingMember == null) ? null : _bindingMember.getDeclaringType();
  }

  public String getBindingName() {
    return _bindingName;
  }

  public IMember getBindingMember() {
    return _bindingMember;
  }

  public String getNextTypeName() {
    try {
      String nextTypeName;
      if (_bindingMember instanceof IMethod) {
        nextTypeName = ((IMethod) _bindingMember).getReturnType();
      }
      else {
        nextTypeName = ((IField) _bindingMember).getTypeSignature();
      }
      return nextTypeName;
    }
    catch (JavaModelException e) {
      throw new RuntimeException("Failed to get the next type name for " + _bindingMember + ".", e);
    }
  }

  public IType getNextType() throws JavaModelException {
    if (_nextType == null) {
      String nextTypeName = getNextTypeName();
      // MS: Primitives have a return type of "I" or "C" ... Just skip them because they won't resolve.
      if (nextTypeName != null && nextTypeName.length() == 0) {
        _nextType = null;
      }
      else {
        IType typeContext = getDeclaringType();
        Map<String, IType> nextTypeCache = (Map<String, IType>) _cache.getTypeContextCache().get(typeContext);
        if (nextTypeCache == null) {
          nextTypeCache = new HashMap<String, IType>();
          _cache.getTypeContextCache().put(typeContext, nextTypeCache);
        }
        _nextType = nextTypeCache.get(nextTypeName);
        if (_nextType == null) {
          String resolvedNextTypeName = JavaModelUtil.getResolvedTypeName(nextTypeName, typeContext);
          if (resolvedNextTypeName == null) {
            Activator.getDefault().log("Failed to resolve type name " + nextTypeName + " in component " + typeContext.getElementName());
          }
          else if ("boolean".equals(resolvedNextTypeName) || "byte".equals(resolvedNextTypeName) || "char".equals(resolvedNextTypeName) || "int".equals(resolvedNextTypeName) || "short".equals(resolvedNextTypeName) || "float".equals(resolvedNextTypeName) || "double".equals(resolvedNextTypeName)) {
            // ignore primitives
          }
          else {
            _nextType = JavaModelUtil.findType(_javaProject, resolvedNextTypeName);
            if (_nextType != null) {
              nextTypeCache.put(nextTypeName, _nextType);
            }
            else {
              System.out.println("BindingValueKey.getNextType: couldn't resolve " + resolvedNextTypeName);
            }
          }
        }
      }
    }
    return _nextType;
  }

  @Override
  public String toString() {
    return "[BindingKey: bindingName = " + _bindingName + "; bindingMember = " + _bindingMember + "]";
  }
}

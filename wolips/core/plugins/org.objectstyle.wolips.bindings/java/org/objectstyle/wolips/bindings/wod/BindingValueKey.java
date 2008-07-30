package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

public class BindingValueKey implements Comparable<BindingValueKey> {
  private String _bindingName;

  private IMember _bindingMember;

  private IJavaProject _javaProject;

  private IType _nextType;

//  private IType _nextTypeArgument;

  private TypeCache _cache;

  public BindingValueKey(String bindingName, IMember bindingMember, IJavaProject javaProject, TypeCache cache) {
    _bindingName = bindingName;
    _bindingMember = bindingMember;
    _javaProject = javaProject;
    _cache = cache;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof BindingValueKey && compareTo((BindingValueKey) o) == 0;
  }

  @Override
  public int hashCode() {
    return (_bindingName == null) ? 0 : _bindingName.hashCode();
  }

  public int compareTo(BindingValueKey o) {
    return (o == null) ? -1 : (_bindingName == null) ? ((o._bindingName == null) ? 0 : 1) : _bindingName.compareTo(o._bindingName);
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
      if (_nextType != null) {
        nextTypeName = _nextType.getFullyQualifiedName();
      }
      else if (_bindingMember == null) {
        nextTypeName = null;
      }
      else if (_bindingMember instanceof IMethod) {
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

//  public IType getNextTypeArgument() throws JavaModelException {
//    ensureNextTypeInfoLoaded();
//    return _nextTypeArgument;
//  }

  public IType getNextType() throws JavaModelException {
    ensureNextTypeInfoLoaded();
    return _nextType;
  }

  public boolean isLeaf() throws JavaModelException {
    boolean isLeaf = false;
    IType nextType = getNextType();
    if (nextType != null) {
      String name = nextType.getFullyQualifiedName();
      if ("java.lang.String".equals(name) || "java.lang.Object".equals(name)) {
        isLeaf = true;
      }
    }
    else {
      isLeaf = true;
    }
    return isLeaf;
  }

  protected void ensureNextTypeInfoLoaded() throws JavaModelException {
    if (_nextType == null) {
      String nextTypeName = getNextTypeName();
      // MS: Primitives have a return type of "I" or "C" ... Just skip them because they won't resolve.
      if (nextTypeName == null) {
        _nextType = null;
      }
      else if (nextTypeName != null && nextTypeName.length() == 0) {
        _nextType = null;
      }
      else if (nextTypeName != null && nextTypeName.matches("^\\[{0,1}Q.;$")) {
        _nextType = null;
      }
      else {
        IType declaringType = getDeclaringType();
//        String[] typeArguments = Signature.getTypeArguments(nextTypeName);
//        if (typeArguments.length == 1) {
//          if (typeArguments[0].length() == "QK;".length()) {
//            // don't even bother with pure parameterized type like NSArray<K> for now ..
//          }
//          else {
//            _nextTypeArgument = _cache.getTypeForNameInType(typeArguments[0], declaringType);
//          }
//        }
        String nextTypeNameErasure = Signature.getTypeErasure(nextTypeName);
        _nextType = _cache.getTypeForNameInType(nextTypeNameErasure, declaringType);
      }
    }
  }

  @Override
  public String toString() {
    return "[BindingKey: bindingName = " + _bindingName + "; bindingMember = " + _bindingMember + "]";
  }
}

package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.objectstyle.wolips.baseforplugins.util.ArrayUtilities;
import org.objectstyle.wolips.baseforplugins.util.StringUtilities;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;

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
      if (nextTypeName == null) {
        _nextType = null;
      }
      else if (nextTypeName != null && nextTypeName.length() == 0) {
        _nextType = null;
      }
      else if (nextTypeName != null) {
        IType declaringType = getDeclaringType();
        for (String param : declaringType.getTypeParameterSignatures()) {
    		String n = Signature.getSignatureSimpleName(nextTypeName);
    		String p = Signature.getTypeVariable(param);
    		if (n.equals(p)) {
    			/* XXX: Q - This is a hack, I don't like it, but it's better than no 
    			 * validation at all. Erasure resolution assumes an erased type of Object, 
    			 * which isn't correct but will have to do for now.
    			 * There is no way to determining the actual declared generic type without 
    			 * refactoring the caching behaviour as well as passing in the declaring type, 
    			 * which would be the preferred solution to this problem.
    			 */
    			nextTypeName = "QObject;";
    			break;
    		}
    	}
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

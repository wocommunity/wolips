package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

public class BindingValueKey implements Comparable<BindingValueKey> {
  private String _bindingName;

  private IMember _bindingMember;

  private IType _bindingDeclaringType;

  private IJavaProject _javaProject;

  private IType _nextType;


  private TypeCache _cache;

  public BindingValueKey(String bindingName, IType bindingDeclaringType, IMember bindingMember, IJavaProject javaProject, TypeCache cache) {
    _bindingName = bindingName;
    _bindingMember = bindingMember;
    _bindingDeclaringType = bindingDeclaringType;
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

  public String getMemberTypeName(IMember member) throws JavaModelException {
	  String result = null;
	  if (member != null) {
		  if (member instanceof IMethod) {
			  result = ((IMethod) member).getReturnType();
		  }
		  else {
			  result = ((IField) member).getTypeSignature();
		  }
	  }
	  return result;
  }
  
  public String getNextTypeName() {
    try {
      String nextTypeName;
      if (_nextType != null) {
        nextTypeName = Signature.createTypeSignature(_nextType.getFullyQualifiedName(),true);
      }
      else {
    	nextTypeName = getMemberTypeName(_bindingMember);
      }
      return nextTypeName;
    }
    catch (JavaModelException e) {
      throw new RuntimeException("Failed to get the next type name for " + _bindingMember + ".", e);
    }
  }

  public IType getNextType() throws JavaModelException {
	  if (_nextType == null) {
		  _nextType = resolveNextType(null);
	  }
	  return _nextType;
  }
  
  public IType getNextType(BindingValueKey parentBinding) throws JavaModelException {
    _nextType = resolveNextType(parentBinding);
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

  protected IType resolveNextType(BindingValueKey parentBinding) throws JavaModelException {
    String nextTypeName = getNextTypeName();
    if (nextTypeName == null || nextTypeName.length() == 0) {
      return null;
    }
    String typeSignatureName = Signature.getSignatureSimpleName(Signature.getElementType(nextTypeName));
    String typeSignature = null;
    
    boolean isField = false;
    if (_bindingMember.getElementType() == IJavaElement.FIELD) {
      isField = true;
    }
    
    if (parentBinding != null) {
      if (isField) {
        typeSignature = getMemberTypeName(parentBinding._bindingMember);
      } else {
        typeSignature = parentBinding._bindingDeclaringType.getSuperclassTypeSignature();
      }
    } else if (_bindingDeclaringType != null) {
      typeSignature = _bindingDeclaringType.getSuperclassTypeSignature();
    }
    IType declaringType = getDeclaringType();
    typeSignature = typeSignature == null ? "QObject;" : typeSignature;
    
    String[] typeParameters = declaringType.getTypeParameterSignatures();
    String[] typeArguments = Signature.getTypeArguments(typeSignature);
    
    /* If we have type parameters we need to use the parent type to resolve 
     * the generic type */
    if (typeParameters.length > 0) {
      if (parentBinding != null) {
        declaringType = parentBinding.getBindingMember().getDeclaringType();
      } else {
        declaringType = _bindingDeclaringType;
      }
    }
    
    /* Resolve next type using generic type arguments */
    for (int i = 0; i < typeParameters.length; i++) {
      String param = typeParameters[i];
      String currentParameterType = Signature.getTypeVariable(param);
      if (typeSignatureName.equals(currentParameterType) &&
          i < typeArguments.length) {
        nextTypeName = Signature.getElementType(typeArguments[i]);
        break;
      }
    }
    String nextTypeNameErasure = Signature.getTypeErasure(nextTypeName);
    //System.out.println("BindingValueKey.resolveNextType: " + nextTypeNameErasure + " / " + _bindingDeclaringType);
    return _cache.getTypeForNameInType(nextTypeNameErasure, declaringType);
  }

  @Override
  public String toString() {
    return "[BindingKey: bindingName = " + _bindingName + "; bindingMember = " + _bindingMember + "]";
  }
}

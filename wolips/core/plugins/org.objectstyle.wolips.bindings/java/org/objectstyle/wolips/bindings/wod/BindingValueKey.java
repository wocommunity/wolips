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
        nextTypeName = "Q" + _nextType.getElementName() + ";";
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
      IType declaringType = getDeclaringType();
	  String typeSignatureName = Signature.getSignatureSimpleName(nextTypeName);
	  String typeSignature = "QObject;";
	  if (parentBinding != null) {
	  	typeSignature = getMemberTypeName(parentBinding._bindingMember);
	  }
	  String[] typeParameters = declaringType.getTypeParameterSignatures();
	  String[] typeArguments = Signature.getTypeArguments(typeSignature);
      for (int i = 0; i < typeParameters.length; i++) {
    	  String param = typeParameters[i];
    	  String currentParameterType = Signature.getTypeVariable(param);
    	  if (typeSignatureName.equals(currentParameterType) &&
    			  i < typeArguments.length) {
  			/* XXX: Q - This is (still) a hack, I don't like it, but it's better than no 
  			 * validation at all. Generic type resolution assumes that it was declared in
  			 * the parent binding, which isn't always the case, but it is most of the time. 
  			 * There is no way of determining the actual declared generic type without 
  			 * refactoring the caching behaviour as well as passing in the declaring type
  			 * and the type signature, which would be the preferred solution to this problem.
  			 */
    		String typeArgument = typeArguments[i];
    		typeArgument = typeArgument.substring(typeArgument.indexOf('Q'));
    		nextTypeName = typeArgument;
    		declaringType = parentBinding.getDeclaringType();
    		break;
    	  }
      }
      String nextTypeNameErasure = Signature.getTypeErasure(nextTypeName);
      return _cache.getTypeForNameInType(nextTypeNameErasure, declaringType);
  }

  @Override
  public String toString() {
    return "[BindingKey: bindingName = " + _bindingName + "; bindingMember = " + _bindingMember + "]";
  }
}

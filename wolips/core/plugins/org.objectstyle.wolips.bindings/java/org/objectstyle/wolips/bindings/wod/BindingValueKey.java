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

  private IType _bindingDeclaringType;

  private IType _nextType;

  private TypeCache _cache;
  
  private BindingValueKey _parent;

  protected IJavaProject _javaProject;
  
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
    if (_nextType == null) {
      _nextType = resolveNextType(parentBinding);
    }
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

    if (parentBinding != null) {
      _parent = parentBinding;
    } else {
      _parent = this;
    }
      
    // Q: This is probably one of the most convoluted pieces of code I have written in wolips
    //    Nothing to see here.. it appears to work, move along.
    
    // Resolve type name for binding
    BindingValueKey binding = this;
    IType declaringType = getDeclaringType();
    String lastTypeName = null;
    while(isGenericType(nextTypeName, declaringType) && !nextTypeName.equals(lastTypeName)) {
      //System.out.println("BindingValueKey.resolveNextType.while_isGenericType: " + nextTypeName + " / " + lastTypeName + " " + toString());
      lastTypeName = nextTypeName;
      typeSignatureName = Signature.getSignatureSimpleName(Signature.getElementType(nextTypeName));

      String[] declaringTypeParameters = declaringType.getTypeParameterSignatures();
      String[] declaringTypeArgs = binding._parent._bindingDeclaringType.getTypeParameterSignatures();
      String[] memberTypeArgs = Signature.getTypeArguments(getMemberTypeName(binding._parent._bindingMember));
      String[] superTypeArgs = Signature.getTypeArguments(binding._parent._bindingDeclaringType.getSuperclassTypeSignature());

      /* Resolve next type using generic type arguments
       * 
       * This iterates over the declaring type's parameter list to find the index of the type value,
       * it then checks the declaring type for a matching argument, if none is found it defers to the 
       * binding member (corresponding method or field) for type args, lastly it will check the 
       * superclass type signature for a match. If no match is found it will fall through and fail to
       * resolve the type.
       */
      
      for (int i = 0; i < declaringTypeParameters.length; i++) {
        String param = declaringTypeParameters[i];
        String currentParameterType = Signature.getTypeVariable(param);
        // If the typeSignature name is he same as the declaring type parameter we know what parameter index to look for
        if (typeSignatureName.equals(currentParameterType)) {
          // Try declared type arguments first
          if (i < declaringTypeArgs.length) {
            nextTypeName = Signature.createTypeSignature(Signature.getTypeVariable(declaringTypeArgs[i]), false);            
            binding = binding._parent;
            declaringType = binding._bindingDeclaringType;
            break;
          }
          // Try binding type parameter on method or field next
          if (i < memberTypeArgs.length) {
            nextTypeName = memberTypeArgs[i];
            declaringType = binding._parent.getDeclaringType();
            break;
          }
          // Last chance is to check the type parameters of the superclass for a match
          if (i < superTypeArgs.length) {
            IType superType = _cache.getTypeForNameInType(binding._parent._bindingDeclaringType.getSuperclassTypeSignature(), declaringType); 
            String[] superTypeParameters = superType.getTypeParameterSignatures();
            if (typeSignatureName.equals(Signature.getTypeVariable(superTypeParameters[i]))) {
              nextTypeName = superTypeArgs[i];
              binding = binding._parent;
              declaringType = binding._bindingDeclaringType;
              break;
            }
          }
        }
      }
    }
            
    String nextTypeNameErasure = Signature.getTypeErasure(nextTypeName);
    //System.out.println("BindingValueKey.resolveNextType: " + nextTypeNameErasure + " / " + _bindingDeclaringType);
    return _cache.getTypeForNameInType(nextTypeNameErasure, declaringType);
  }

  private static boolean isGenericType(String typeName, IType declaringType) throws JavaModelException {    
    String[] typeParameters = declaringType.getTypeParameterSignatures();
    String typeVariable = Signature.getSignatureSimpleName(typeName);
    
    for (int i = 0; i < typeParameters.length; i++) {
      String currentParameterType = Signature.getTypeVariable(typeParameters[i]);
      if (typeVariable.equals(currentParameterType))
        return true;
    }
    
    return false;
  }
  
  @Override
  public String toString() {
    return "[BindingKey: bindingName = " + _bindingName + "; bindingMember = " + _bindingMember + "]";
  }
}

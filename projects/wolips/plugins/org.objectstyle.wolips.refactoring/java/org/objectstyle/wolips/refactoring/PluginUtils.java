package org.objectstyle.wolips.refactoring;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;


public class PluginUtils {
  public static boolean isOfType(IType _type, String _lookingForTypeName) throws JavaModelException {
    boolean isWOComponent;
    String qualifiedTypeName = _type.getFullyQualifiedName();
    if (qualifiedTypeName.equals(_lookingForTypeName)) {
      isWOComponent = true;
    }
    else {
      IJavaProject javaProject = _type.getJavaProject();
      String superclassName = _type.getSuperclassName();
      if (superclassName == null) {
        isWOComponent = false;
      }
      else {
        String superclassQualifiedTypeName = JavaModelUtil.getResolvedTypeName(_type.getSuperclassTypeSignature(), _type);
        IType superclassType = _type.getJavaProject().findType(superclassQualifiedTypeName);
        isWOComponent = isOfType(superclassType, _lookingForTypeName);
      }
    }
    return isWOComponent;
  }
  
  public static IResource findResource(IContainer _container, String _resourceName) throws CoreException {
    IResource matchingResource = null;
    IResource[] members = _container.members();
    for (int i = 0; matchingResource == null && i < members.length; i++) {
      if (members[i].getName().equals(_resourceName)) {
        matchingResource = members[i];
      }
      else if (members[i] instanceof IContainer) {
        matchingResource = findResource((IContainer) members[i], _resourceName);
      }
    }
    return matchingResource;
  }

}

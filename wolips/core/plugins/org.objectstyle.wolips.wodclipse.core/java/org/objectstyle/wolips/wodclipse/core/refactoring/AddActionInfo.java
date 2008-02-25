package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;

public class AddActionInfo {
  private IType _componentType;

  private String _name;

  private String _typeName;

  public AddActionInfo(IType componentType) {
    _componentType = componentType;
    _name = "newAction";
    _typeName = "WOActionResults";
  }

  public String getJavaTypeName() throws JavaModelException {
    String javaTypeName = _typeName;
    if (javaTypeName != null) {
      javaTypeName = BindingReflectionUtils.getFullClassName(_componentType.getJavaProject(), javaTypeName);
    }
    return javaTypeName;
  }

  public IType getComponentType() {
    return _componentType;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public String getTypeName() {
    return _typeName;
  }

  public void setTypeName(String typeName) {
    _typeName = typeName;
  }
}

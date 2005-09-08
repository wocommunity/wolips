package org.objectstyle.wolips.wodclipse.wod.completion;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class MemberBindingKey implements IBindingKey {
  private String myBindingName;
  private IMember myBindingMember;

  public MemberBindingKey(String _bindingName, IMember _bindingMember) {
    myBindingName = _bindingName;
    myBindingMember = _bindingMember;
  }

  public String getBindingName() {
    return myBindingName;
  }

  public IMember getBindingMember() {
    return myBindingMember;
  }

  public String getNextTypeName() {
    try {
      String nextTypeName;
      if (myBindingMember instanceof IMethod) {
        nextTypeName = ((IMethod) myBindingMember).getReturnType();
      }
      else {
        nextTypeName = ((IField) myBindingMember).getTypeSignature();
      }
      return nextTypeName;
    }
    catch (JavaModelException e) {
      throw new RuntimeException("Failed to get the next type name for " + myBindingMember + ".", e);
    }
  }

  public String toString() {
    return "[BindingKey: bindingName = " + myBindingName + "; bindingMember = " + myBindingMember + "]";
  }
}

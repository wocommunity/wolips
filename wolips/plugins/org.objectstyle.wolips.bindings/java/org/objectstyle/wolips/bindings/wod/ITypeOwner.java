package org.objectstyle.wolips.bindings.wod;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.locate.LocateException;

public interface ITypeOwner {
  public IType getType() throws CoreException, LocateException;
  
  public TypeCache getCache();
}

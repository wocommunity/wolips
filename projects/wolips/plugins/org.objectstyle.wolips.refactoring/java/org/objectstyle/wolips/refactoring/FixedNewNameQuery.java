package org.objectstyle.wolips.refactoring;

import org.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;

public class FixedNewNameQuery implements INewNameQuery {
  private String myNewName;

  public FixedNewNameQuery(String _newName) {
    myNewName = _newName;
  }

  public String getNewName() {
    return myNewName;
  }
}


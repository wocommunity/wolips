package org.objectstyle.wolips.wodclipse.core.refactoring;

public class ElementRename {
  private String _oldName;
  private String _newName;

  public ElementRename(String oldName, String newName) {
    _oldName = oldName;
    _newName = newName;
  }

  public String getOldName() {
    return _oldName;
  }

  public String getNewName() {
    return _newName;
  }
}
package org.objectstyle.wolips.eomodeler.model;

public class DuplicateNameException extends EOModelException {
  private String myName;

  public DuplicateNameException(String _name, String _message) {
    this(_name, _message, null);
  }

  public DuplicateNameException(String _name, String _message, Throwable _throwable) {
    super(_message, _throwable);
    myName = _name;
  }

  public String getName() {
    return myName;
  }

}

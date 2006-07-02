package org.objectstyle.wolips.eomodeler.model;

public class DuplicateEntityNameException extends DuplicateNameException {
  private EOModel myModel;

  public DuplicateEntityNameException(String _name, EOModel _model) {
    this(_name, _model, null);
  }

  public DuplicateEntityNameException(String _name, EOModel _model, Throwable _throwable) {
    super(_name, "There is already an entity named '" + _name + "' in this model group.", _throwable);
    myModel = _model;
  }

}

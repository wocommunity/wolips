package org.objectstyle.wolips.eomodeler.model;

public class DuplicateAttributeNameException extends DuplicateNameException {
  private EOEntity myEntity;

  public DuplicateAttributeNameException(String _name, EOEntity _entity) {
    this(_name, _entity, null);
  }

  public DuplicateAttributeNameException(String _name, EOEntity _entity, Throwable _throwable) {
    super(_name, "There is already an attribute named '" + _name + "' in " + _entity.getName() + ".", _throwable);
    myEntity = _entity;
  }

}

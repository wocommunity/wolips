package org.objectstyle.wolips.eomodeler.model;

public class DuplicateRelationshipNameException extends DuplicateNameException {
  private EOEntity myEntity;

  public DuplicateRelationshipNameException(String _name, EOEntity _entity) {
    this(_name, _entity, null);
  }

  public DuplicateRelationshipNameException(String _name, EOEntity _entity, Throwable _throwable) {
    super(_name, "There is already a relationship named '" + _name + "' in " + _entity.getName() + ".", _throwable);
    myEntity = _entity;
  }

}

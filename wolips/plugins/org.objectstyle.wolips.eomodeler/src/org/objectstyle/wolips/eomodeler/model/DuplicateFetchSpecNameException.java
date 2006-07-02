package org.objectstyle.wolips.eomodeler.model;

public class DuplicateFetchSpecNameException extends DuplicateNameException {
  private EOEntity myEntity;

  public DuplicateFetchSpecNameException(String _name, EOEntity _entity) {
    this(_name, _entity, null);
  }

  public DuplicateFetchSpecNameException(String _name, EOEntity _entity, Throwable _throwable) {
    super(_name, "There is already a fetch spec named '" + _name + "' in " + _entity.getName() + ".", _throwable);
    myEntity = _entity;
  }

}

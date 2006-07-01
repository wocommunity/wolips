package org.objectstyle.wolips.eomodeler.model;

public class MissingEntityFailure extends EOModelVerificationFailure {
  private String myEntityName;

  public MissingEntityFailure(String _entityName) {
    this(_entityName, null);
  }

  public MissingEntityFailure(String _entityName, Throwable _throwable) {
    super("Unable to resolve the entity named '" + _entityName + "'.", _throwable);
    myEntityName = _entityName;
  }

  public String getEntityName() {
    return myEntityName;
  }
}

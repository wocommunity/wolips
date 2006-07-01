package org.objectstyle.wolips.eomodeler.model;

public class MissingAttributeFailure extends EOModelVerificationFailure {
  private EOEntity myEntity;
  private String myAttributeName;

  public MissingAttributeFailure(EOEntity _entity, String _attributeName) {
    this(_entity, _attributeName, null);
  }

  public MissingAttributeFailure(EOEntity _entity, String _attributeName, Throwable _throwable) {
    super("Unable to resolve the attribute named '" + _attributeName + "' on " + _entity + ".", _throwable);
    myEntity = _entity;
    myAttributeName = _attributeName;
  }

  public EOEntity getEntity() {
    return myEntity;
  }

  public String getAttributeName() {
    return myAttributeName;
  }
}

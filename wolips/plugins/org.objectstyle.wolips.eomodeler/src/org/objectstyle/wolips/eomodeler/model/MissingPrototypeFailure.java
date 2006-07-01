package org.objectstyle.wolips.eomodeler.model;

public class MissingPrototypeFailure extends EOModelVerificationFailure {
  private EOAttribute myReferencingAttribute;
  private String myPrototypeName;

  public MissingPrototypeFailure(String _prototypeName, EOAttribute _referencingAttribute) {
    this(_prototypeName, _referencingAttribute, null);
  }

  public MissingPrototypeFailure(String _prototypeName, EOAttribute _referencingAttribute, Throwable _throwable) {
    super("Unable to find the prototype named '" + _prototypeName + "' referenced by the attribute '" + _referencingAttribute + "'.", _throwable);
    myPrototypeName = _prototypeName;
    myReferencingAttribute = _referencingAttribute;
  }

  public String getPrototypeName() {
    return myPrototypeName;
  }

  public EOAttribute getReferencingAttribute() {
    return myReferencingAttribute;
  }
}

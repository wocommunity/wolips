package org.objectstyle.wolips.eomodeler.model;

public class EOModelVerificationFailure {
  private String myMessage;
  private Throwable myRootCause;

  public EOModelVerificationFailure(String _message) {
    this(_message, null);
  }

  public EOModelVerificationFailure(String _message, Throwable _rootCause) {
    myMessage = _message;
    myRootCause = _rootCause;
  }

  public String getMessage() {
    return myMessage;
  }

  public Throwable getRootCause() {
    return myRootCause;
  }

  public String toString() {
    return "[EOModelVerificationFailure: " + myMessage + "]";
  }
}

package org.objectstyle.wolips.eomodeler.model;

public class EOModelException extends Exception {
  public EOModelException(String _message) {
    this(_message, null);
  }

  public EOModelException(String _message, Throwable _throwable) {
    super(_message, _throwable);
  }
}

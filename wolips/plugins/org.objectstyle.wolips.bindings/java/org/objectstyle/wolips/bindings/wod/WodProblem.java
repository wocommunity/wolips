package org.objectstyle.wolips.bindings.wod;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Position;

public class WodProblem {
  private IWodModel _model;
  private String _message;
  private Position _position;
  private int _lineNumber;
  private boolean _warning;
  private IFile _forceFile;

  public WodProblem(String message, Position position, int lineNumber, boolean warning) {
    _message = message;
    _position = position;
    _lineNumber = lineNumber;
    _warning = warning;
  }
  
  public void setModel(IWodModel model) {
    _model = model;
  }

  public void setForceFile(IFile forceFile) {
    _forceFile = forceFile;
  }
  
  public IFile getForceFile() {
    return _forceFile;
  }

  public String getMessage() {
    return _message;
  }

  public IWodModel getModel() {
    return _model;
  }

  public Position getPosition() {
    return _position;
  }

  public int getLineNumber() {
    return _lineNumber;
  }

  public boolean isWarning() {
    return _warning;
  }

  @Override
  public String toString() {
    return "[" + getClass().getName() + ": message = " + _message + "]";
  }
}

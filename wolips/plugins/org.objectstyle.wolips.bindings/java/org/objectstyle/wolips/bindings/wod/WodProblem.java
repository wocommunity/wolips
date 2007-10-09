package org.objectstyle.wolips.bindings.wod;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.Position;

public class WodProblem {
  public static final String RELATED_TO_FILE_NAMES = "org.objectstyle.wolips.wodclipse.wod.RelatedToFileNames";

  private IWodModel _model;
  private String _message;
  private Position _position;
  private int _lineNumber;
  private boolean _warning;
  private String[] _relatedToFileNames;
  private IFile _forceFile;

  public WodProblem(String message, Position position, int lineNumber, boolean warning, String relatedToFileNames) {
    this(message, position, lineNumber, warning, new String[] { relatedToFileNames });
  }

  public WodProblem(String message, Position position, int lineNumber, boolean warning, String[] relatedToFileNames) {
    _message = message;
    _position = position;
    _lineNumber = lineNumber;
    _warning = warning;
    _relatedToFileNames = relatedToFileNames;
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

  public String[] getRelatedToFileNames() {
    return _relatedToFileNames;
  }

  @Override
  public String toString() {
    return "[WodProblem: message = " + _message + "]";
  }
}

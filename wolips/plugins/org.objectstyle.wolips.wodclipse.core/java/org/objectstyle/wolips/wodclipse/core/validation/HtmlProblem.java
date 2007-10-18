package org.objectstyle.wolips.wodclipse.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.wodclipse.core.Activator;

public class HtmlProblem {
  //WodModelUtils.addMarker(getFile(), IMarker.SEVERITY_ERROR, getLineAtOffset(offset), offset, length, message);
  private IFile _htmlFile;

  private String _message;

  private Position _position;
  private int _lineNumber;

  private boolean _warning;

  public HtmlProblem(IFile htmlFile, String message, Position position, int lineNumber, boolean warning) {
    _htmlFile = htmlFile;
    _message = message;
    _position = position;
    _lineNumber = lineNumber;
    _warning = warning;
  }

  public IFile getHtmlFile() {
    return _htmlFile;
  }

  public String getMessage() {
    return _message;
  }

  public Position getPosition() {
    return _position;
  }

  public boolean isWarning() {
    return _warning;
  }

  @Override
  public String toString() {
    return "[HtmlProblem: message = " + _message + "]";
  }

  public IMarker createMarker(IFile file) {
    IMarker marker = null;
    try {
      marker = _htmlFile.createMarker(Activator.TEMPLATE_PROBLEM_MARKER);
      marker.setAttribute(IMarker.MESSAGE, getMessage());
      int severity;
      if (isWarning()) {
        severity = IMarker.SEVERITY_WARNING;
      }
      else {
        severity = IMarker.SEVERITY_ERROR;
      }
      marker.setAttribute(IMarker.SEVERITY, new Integer(severity));
      marker.setAttribute(IMarker.LINE_NUMBER, _lineNumber);
      marker.setAttribute(IMarker.CHAR_START, _position.getOffset());
      marker.setAttribute(IMarker.CHAR_END, _position.getOffset() + _position.getLength());
      marker.setAttribute(IMarker.TRANSIENT, false);
    }
    catch (CoreException e) {
      Activator.getDefault().log(e);
    }
    return marker;
  }
}

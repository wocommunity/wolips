package org.objectstyle.wolips.wodclipse.core.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.bindings.wod.WodProblem;
import org.objectstyle.wolips.wodclipse.core.Activator;

public class HtmlProblem {
  //WodModelUtils.addMarker(getFile(), IMarker.SEVERITY_ERROR, getLineAtOffset(offset), offset, length, message);
  private IFile _htmlFile;

  private String _message;

  private Position _position;
  private int _lineNumber;

  private boolean _warning;

  private String[] _relatedToFileNames;

  public HtmlProblem(IFile htmlFile, String message, Position position, int lineNumber, boolean warning, String[] relatedToFileNames) {
    _htmlFile = htmlFile;
    _message = message;
    _position = position;
    _lineNumber = lineNumber;
    _warning = warning;
    _relatedToFileNames = relatedToFileNames;
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

  public String[] getRelatedToFileNames() {
    return _relatedToFileNames;
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
      String[] relatedToFileNames = getRelatedToFileNames();
      if (relatedToFileNames != null) {
        StringBuffer relatedToFileNamesBuffer = new StringBuffer();
        for (int i = 0; i < relatedToFileNames.length; i++) {
          relatedToFileNamesBuffer.append(relatedToFileNames[i]);
          relatedToFileNamesBuffer.append(", ");
        }
        marker.setAttribute(WodProblem.RELATED_TO_FILE_NAMES, relatedToFileNamesBuffer.toString());
      }
    }
    catch (CoreException e) {
      Activator.getDefault().log(e);
    }
    return marker;
  }
}

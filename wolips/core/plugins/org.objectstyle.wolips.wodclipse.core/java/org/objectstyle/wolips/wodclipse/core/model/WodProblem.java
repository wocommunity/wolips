package org.objectstyle.wolips.wodclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Position;
import org.objectstyle.wolips.wodclipse.core.Activator;

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

  public IMarker createMarker(IFile file) {
    Position problemPosition = getPosition();

    // String type = "org.eclipse.ui.workbench.texteditor.error";
    // String type = "org.eclipse.ui.workbench.texteditor.warning";
    // Annotation problemAnnotation = new Annotation(type, false,
    // problem.getMessage());
    // Position problemPosition = currentPosition.getPosition();
    // annotationModel.addAnnotation(problemAnnotation,
    // problemPosition);

    IMarker marker = null;
    try {
      if (_forceFile != null) {
        marker = _forceFile.createMarker(Activator.TEMPLATE_PROBLEM_MARKER);
      }
      else {
        marker = file.createMarker(Activator.TEMPLATE_PROBLEM_MARKER);
      }
      marker.setAttribute(IMarker.MESSAGE, getMessage());
      int severity;
      if (isWarning()) {
        severity = IMarker.SEVERITY_WARNING;
      }
      else {
        severity = IMarker.SEVERITY_ERROR;
      }
      marker.setAttribute(IMarker.SEVERITY, new Integer(severity));
      if (problemPosition != null) {
        IWodModel model = getModel();
//        if (_lineNumber == -1 && model instanceof DocumentWodModel) {
//          marker.setAttribute(IMarker.LINE_NUMBER, ((DocumentWodModel) model).getDocument().getLineOfOffset(problemPosition.getOffset()));
//        }
//        else
        if (_lineNumber != -1) {
          marker.setAttribute(IMarker.LINE_NUMBER, _lineNumber);
        }
        marker.setAttribute(IMarker.CHAR_START, problemPosition.getOffset());
        marker.setAttribute(IMarker.CHAR_END, problemPosition.getOffset() + problemPosition.getLength());
      }
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
      e.printStackTrace();
      Activator.getDefault().log(e);
    }
//    catch (BadLocationException e) {
//      Activator.getDefault().log(e);
//    }
    return marker;
  }
}

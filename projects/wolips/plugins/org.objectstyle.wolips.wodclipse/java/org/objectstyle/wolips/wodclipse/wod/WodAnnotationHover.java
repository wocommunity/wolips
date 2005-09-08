package org.objectstyle.wolips.wodclipse.wod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

public class WodAnnotationHover implements IAnnotationHover, ITextHover {
  private IAnnotationModel myAnnotationModel;

  public WodAnnotationHover(IAnnotationModel _annotationModel) {
    myAnnotationModel = _annotationModel;
  }

  public String getHoverInfo(ISourceViewer _sourceViewer, int _lineNumber) {
    List annotations = getAnnotationsForLine(_sourceViewer, _lineNumber);
    if (annotations != null) {
      List messages = new ArrayList();
      Iterator e = annotations.iterator();
      while (e.hasNext()) {
        Annotation annotation = (Annotation) e.next();
        String message = annotation.getText();
        if (message != null) {
          message = message.trim();
          if (message.length() > 0) {
            messages.add(message);
          }
        }
      }
      String message;
      if (messages.size() == 1) {
        message = (String) messages.get(0);
      }
      else if (messages.size() > 1) {
        message = formatMessages(messages);
      }
    }
    return null;
  }

  public String getHoverInfo(ITextViewer _textViewer, IRegion _hoverRegion) {
    Iterator e = myAnnotationModel.getAnnotationIterator();
    while (e.hasNext()) {
      Annotation a = (Annotation) e.next();
      Position p = myAnnotationModel.getPosition(a);
      if (p.overlapsWith(_hoverRegion.getOffset(), _hoverRegion.getLength())) {
        String text = a.getText();
        if ((text != null) && (text.trim().length() > 0)) {
          return text;
        }
      }
    }
    return null;
  }

  public IRegion getHoverRegion(ITextViewer _textViewer, int _offset) {
    // TODO If this is too slow then we might return new Region(offset, 0)
    Iterator e = myAnnotationModel.getAnnotationIterator();
    while (e.hasNext()) {
      Annotation a = (Annotation) e.next();
      Position p = myAnnotationModel.getPosition(a);
      if (p.overlapsWith(_offset, 0)) {
        String text = a.getText();
        if ((text != null) && (text.trim().length() > 0)) {
          return new Region(p.offset, p.length);
        }
      }
    }
    return null;
  }

  private String formatMessages(List messages) {
    StringBuffer buffer = new StringBuffer();
    Iterator e = messages.iterator();
    while (e.hasNext()) {
      buffer.append("- "); //$NON-NLS-1$
      buffer.append(e.next());
      buffer.append('\n');
    }
    return buffer.toString();
  }

  private List getAnnotationsForLine(ISourceViewer viewer, int line) {
    IDocument document = viewer.getDocument();
    IAnnotationModel model = viewer.getAnnotationModel();
    if (model == null) {
      return null;
    }
    List retVal = new ArrayList();
    Iterator e = model.getAnnotationIterator();
    while (e.hasNext()) {
      Annotation a = (Annotation) e.next();
      Position position = model.getPosition(a);
      if (position != null) {
        try {
          int annotationLine = document.getLineOfOffset(position.getOffset());
          if (annotationLine == line) {
            retVal.add(a);
          }
        }
        catch (BadLocationException e1) {
          // ignore
        }
      }
    }
    return retVal;
  }

}

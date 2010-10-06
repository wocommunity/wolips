package org.objectstyle.wolips.templateeditor;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;

public class TemplateTripleClickAdapter extends MouseAdapter implements MouseMoveListener {
  private TemplateEditor _editor;
  private Point _clickPoint;
  private int _clickCount;

  public TemplateTripleClickAdapter(TemplateEditor editor) {
    _editor = editor;
  }

  @Override
  public void mouseDown(MouseEvent e) {
    if (_clickCount == 3) {
      _clickCount = 0;
    }
    if (_clickCount == 0) {
      _clickPoint = new Point(e.x, e.y);
    }
  }

  @Override
  public void mouseUp(MouseEvent event) {
    _clickCount++;
    /*
    if (_clickCount == 2) {
      StyledText textWidget = _editor.getSourceEditor().getViewer().getTextWidget();
      int offset = textWidget.getOffsetAtLocation(_clickPoint);
      _editor.selectionChangedToOffset(offset);
    }
    */
    /*
    else if (_clickCount == 3) {
      StyledText textWidget = _editor.getSourceEditor().getViewer().getTextWidget();
      FileEditorInput input = (FileEditorInput) _editor.getEditorInput();
      try {
        if (_tripleClickPoint != null) {
          WodParserCache cache = WodParserCache.parser(input.getFile());
          int offset = textWidget.getOffsetAtLocation(_tripleClickPoint);
          FuzzyXMLElement element = cache.getHtmlEntry().getModel().getElementByOffset(offset);
          if (element != null) {
            textWidget.setSelectionRange(element.getOffset(), element.getLength());
            //textWidget.showSelection();
          }
        }
      }
      catch (Throwable e) {
        e.printStackTrace();
      }
    }
    */
  }

  public void mouseMove(MouseEvent e) {
    if (_clickCount > 0) {
      _clickCount = 0;
      _clickPoint = null;
    }
  }
}

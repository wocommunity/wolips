package org.objectstyle.wolips.templateeditor;

import jp.aonir.fuzzyxml.FuzzyXMLElement;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class TemplateTripleClickAdapter extends MouseAdapter implements MouseMoveListener {
  private TemplateEditor _editor;
  private Point _tripleClickPoint;
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
      _tripleClickPoint = new Point(e.x, e.y);
    }
  }

  @Override
  public void mouseUp(MouseEvent event) {
    _clickCount++;
    if (_clickCount == 3) {
      StyledText textWidget = _editor.getSourceEditor().getViewer().getTextWidget();
      FileEditorInput input = (FileEditorInput) _editor.getEditorInput();
      try {
        WodParserCache cache = WodParserCache.parser(input.getFile());
        int offset = textWidget.getOffsetAtLocation(_tripleClickPoint);
        FuzzyXMLElement element = cache.getHtmlEntry().getModel().getElementByOffset(offset);
        if (element != null) {
          textWidget.setSelectionRange(element.getOffset(), element.getLength());
          //textWidget.showSelection();
        }
      }
      catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  public void mouseMove(MouseEvent e) {
    if (_clickCount > 0) {
      _clickCount = 0;
      _tripleClickPoint = null;
    }
  }
}

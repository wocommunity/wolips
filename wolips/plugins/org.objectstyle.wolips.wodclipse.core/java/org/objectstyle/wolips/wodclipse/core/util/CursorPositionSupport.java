package org.objectstyle.wolips.wodclipse.core.util;

import java.util.LinkedList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.editors.text.TextEditor;

public class CursorPositionSupport {
  private TextEditor _editor;
  private LinkedList<ICursorPositionListener> _listeners;

  public CursorPositionSupport(TextEditor editor) {
    _editor = editor;
    _listeners = new LinkedList<ICursorPositionListener>();
  }

  public synchronized void addCursorPositionListener(ICursorPositionListener listener) {
    _listeners.add(listener);
  }

  public synchronized void removeCursorPositionListener(ICursorPositionListener listener) {
    _listeners.remove(listener);
  }

  public synchronized void cursorPositionChanged(Point selectionRange) {
    for (int i = _listeners.size() - 1; i >= 0; i--) {
      _listeners.get(i).cursorPositionChanged(_editor, selectionRange);
    }
  }
}

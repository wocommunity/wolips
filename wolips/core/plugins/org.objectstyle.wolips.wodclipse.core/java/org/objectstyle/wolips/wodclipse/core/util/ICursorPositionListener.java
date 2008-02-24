package org.objectstyle.wolips.wodclipse.core.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.editors.text.TextEditor;

public interface ICursorPositionListener {
  public void cursorPositionChanged(TextEditor editor, Point selectionRange);
}

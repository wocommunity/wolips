package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class WODWhitespaceDetector implements IWhitespaceDetector {
  public boolean isWhitespace(char _ch) {
    return (_ch == ' ' || _ch == '\t' || _ch == '\n' || _ch == '\r');
  }
}

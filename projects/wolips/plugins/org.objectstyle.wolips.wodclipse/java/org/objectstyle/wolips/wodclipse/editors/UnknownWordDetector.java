package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class UnknownWordDetector implements IWordDetector {
  public UnknownWordDetector() {
  }

  public boolean isWordStart(char _ch) {
    return isUnknownLetter(_ch);
  }

  public boolean isWordPart(char _ch) {
    return isUnknownLetter(_ch);
  }

  private boolean isUnknownLetter(char _ch) {
    boolean unknownLetter = Character.isLetterOrDigit(_ch) || _ch == '.';
    return unknownLetter;
  }
}

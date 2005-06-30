package org.objectstyle.wolips.wodclipse.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class ComponentTypeOperatorWordDetector implements IWordDetector {
  public ComponentTypeOperatorWordDetector() {
  }

  public boolean isWordStart(char _ch) {
    return _ch == ':';
  }

  public boolean isWordPart(char _ch) {
    return false;
  }
}

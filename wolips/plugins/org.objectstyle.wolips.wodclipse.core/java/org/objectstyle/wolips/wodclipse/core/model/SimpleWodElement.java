package org.objectstyle.wolips.wodclipse.core.model;

import org.eclipse.jface.text.Position;

public class SimpleWodElement extends AbstractWodElement {
  private String _name;
  private String _type;

  public SimpleWodElement(String name, String type) {
    _name = name;
    _type = type;
  }

  public String getElementName() {
    return _name;
  }

  public String getElementType() {
    return _type;
  }

  public int getEndOffset() {
    return 0;
  }

  public int getStartOffset() {
    return 0;
  }

  public Position getElementNamePosition() {
    return null;
  }

  public Position getElementTypePosition() {
    return null;
  }

  @Override
  public int getLineNumber() {
    return -1;
  }
}

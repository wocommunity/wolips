package org.objectstyle.wolips.wodclipse.core.model;

import org.eclipse.jface.text.Position;

public class SimpleWodBinding extends AbstractWodBinding {
  private String _name;

  private String _value;

  private Position _namePosition;

  private Position _valuePosition;
  private int _lineNumber;

  public SimpleWodBinding(String name, String value) {
    this(name, value, null, null, -1);
  }

  public SimpleWodBinding(String name, String value, Position namePosition, Position valuePosition, int lineNumber) {
    _name = name;
    _value = value;
    _namePosition = namePosition;
    _valuePosition = valuePosition;
    _lineNumber = lineNumber;
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  public int getEndOffset() {
    return (_namePosition == null) ? 0 : (_namePosition.getOffset() + _namePosition.getLength());
  }

  public int getStartOffset() {
    return (_namePosition == null) ? 0 : _namePosition.getOffset();
  }

  public Position getNamePosition() {
    return _namePosition;
  }

  public Position getValuePosition() {
    return _valuePosition;
  }

  @Override
  public int getLineNumber() {
    return _lineNumber;
  }
}

package org.objectstyle.wolips.bindings.wod;

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

  public SimpleWodBinding(String name, String value, boolean literal) {
    this(name, (literal) ? ("\"" + value + "\"") : value, null, null, -1);
  }

  public SimpleWodBinding(String name, String value, Position namePosition, Position valuePosition, int lineNumber) {
    _name = name;
    _value = value;
    _namePosition = namePosition;
    _valuePosition = valuePosition;
    _lineNumber = lineNumber;
  }

  public SimpleWodBinding(IWodBinding wodBinding) {
    this(wodBinding.getName(), wodBinding.getValue(), wodBinding.getNamePosition(), wodBinding.getValuePosition(), wodBinding.getLineNumber());
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  public void setValue(String value) {
    String oldValue = _value;
    _value = value;
    if (_valuePosition != null && _value != null) {
      setValuePosition(new Position(_valuePosition.getOffset(), _value.length()));
    }
  }

  public void setName(String name) {
    String oldName = _name;
    _name = name;
    if (_namePosition != null && _name != null) {
      setNamePosition(new Position(_namePosition.getOffset(), _name.length()));
    }
    if (oldName != null) {
      int oldLength = oldName.length();
      int newLength = name.length();
      int diff = newLength - oldLength;
      setValuePosition(new Position(_valuePosition.getOffset() + diff, _valuePosition.getLength()));
    }
  }

  public int getEndOffset() {
    return (_namePosition == null) ? 0 : (_namePosition.getOffset() + _namePosition.getLength());
  }

  public int getStartOffset() {
    return (_namePosition == null) ? 0 : _namePosition.getOffset();
  }

  public void setNamePosition(Position namePosition) {
    _namePosition = namePosition;
  }

  public Position getNamePosition() {
    return _namePosition;
  }

  public void setValuePosition(Position valuePosition) {
    _valuePosition = valuePosition;
  }

  public Position getValuePosition() {
    return _valuePosition;
  }

  @Override
  public int getLineNumber() {
    return _lineNumber;
  }
}

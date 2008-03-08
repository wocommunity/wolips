package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class SimpleWodElement extends AbstractWodElement {
  private String _elementName;
  private String _elementType;
  private Position _elementNamePosition;
  private Position _elementTypePosition;
  private int _startOffset;
  private int _endOffset;
  private int _fullEndOffset;
  private int _newBindingOffset;
  private int _newBindingIndent;

  public SimpleWodElement(IWodElement wodElement) {
    if (wodElement != null) {
      _elementName = wodElement.getElementName();
      _elementNamePosition = wodElement.getElementNamePosition();
      _elementType = wodElement.getElementType();
      _elementTypePosition = wodElement.getElementTypePosition();
      setInline(wodElement.isInline());
      for (IWodBinding binding : wodElement.getBindings()) {
        addBinding(new SimpleWodBinding(binding));
      }
      _startOffset = wodElement.getStartOffset();
      _endOffset = wodElement.getEndOffset();
      _fullEndOffset = wodElement.getFullEndOffset();
      setNewBindingIndent(wodElement.getNewBindingIndent());
      setNewBindingOffset(wodElement.getNewBindingOffset());
    }
    else {
      setInline(true);
    }
  }

  public SimpleWodElement(String name, String type) {
    _elementName = name;
    _elementType = type;
  }

  protected SimpleWodElement() {
    // DO NOTHING
  }

  protected void _setElementName(String name) {
    _elementName = name;
  }

  protected void _setElementType(String type) {
    _elementType = type;
  }

  public void setElementName(String name) {
    String oldElementName = _elementName;
    _elementName = name;
    if (_elementNamePosition != null && _elementName != null) {
      setElementNamePosition(new Position(_elementNamePosition.getOffset(), _elementName.length()));
    }
    if (!isInline() && oldElementName != null) {
      int oldLength = oldElementName.length();
      int newLength = name.length();
      int diff = newLength - oldLength;
      setElementTypePosition(new Position(_elementTypePosition.getOffset() + diff, _elementTypePosition.getLength()));
    }
  }

  public String getElementName() {
    return _elementName;
  }

  public Position getElementNamePosition() {
    return _elementNamePosition;
  }

  public void setElementNamePosition(Position elementNamePosition) {
    _elementNamePosition = elementNamePosition;
  }

  public String getElementType() {
    return _elementType;
  }

  public void setElementType(String elementType) {
    _elementType = elementType;
    if (_elementTypePosition != null && _elementType != null) {
      setElementTypePosition(new Position(_elementTypePosition.getOffset(), _elementType.length()));
    }
  }

  public void setElementTypePosition(Position elementTypePosition) {
    _elementTypePosition = elementTypePosition;
  }

  public Position getElementTypePosition() {
    return _elementTypePosition;
  }

  public void setEndOffset(int endOffset) {
    _endOffset = endOffset;
  }

  public int getEndOffset() {
    return _endOffset;
  }

  public int getFullEndOffset() {
    return _fullEndOffset;
  }

  public void setFullEndOffset(int fullEndOffset) {
    _fullEndOffset = fullEndOffset;
  }

  public void setStartOffset(int startOffset) {
    _startOffset = startOffset;
  }

  public int getStartOffset() {
    return _startOffset;
  }

  @Override
  public int getLineNumber() {
    return -1;
  }

  public void setNewBindingOffset(int newBindingOffset) {
    _newBindingOffset = newBindingOffset;
  }

  public int getNewBindingOffset() {
    int newBindingOffset = _newBindingOffset;
    if (newBindingOffset == -1) {
      newBindingOffset = getEndOffset() - 1;
    }
    return newBindingOffset;
  }

  public void setNewBindingIndent(int newBindingIndent) {
    _newBindingIndent = newBindingIndent;
  }

  public int getNewBindingIndent() {
    return _newBindingIndent;
  }
}

package org.objectstyle.wolips.bindings.wod;

import org.eclipse.jface.text.Position;

public class SimpleWodBinding extends AbstractWodBinding {
  private String _namespace;

  private String _name;

  private String _value;

  private Position _namespacePosition;
  private Position _namePosition;
  private Position _valuePosition;
  
  private int _lineNumber;

  private int _startOffset;
  private int _endOffset;

  public SimpleWodBinding(IWodBinding wodBinding) {
    this(wodBinding.getNamespace(), wodBinding.getName(), wodBinding.getValue(), wodBinding.getNamespacePosition(), wodBinding.getNamePosition(), wodBinding.getValuePosition(), wodBinding.getLineNumber());
  }

  public SimpleWodBinding(String namespace, String name, String value) {
    this(namespace, name, value, null, null, null, -1);
  }

  public SimpleWodBinding(String namespace, String name, String value, boolean literal) {
    this(namespace, name, (literal) ? ("\"" + value + "\"") : value, null, null, null, -1);
  }

  public SimpleWodBinding(String namespace, String name, String value, Position namespacePosition, Position namePosition, Position valuePosition, int lineNumber) {
    _namespace = namespace;
    _name = name;
    _value = value;
    _namePosition = namePosition;
    _valuePosition = valuePosition;
    _lineNumber = lineNumber;
    _startOffset = -1;
    _endOffset = -1;
  }
  
  public String getNamespace() {
    return _namespace;
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
  
  public void setNamespace(String namespace) {
    String oldNamespace = _namespace;
    _namespace = namespace;
    if (_namespacePosition != null && _namespace != null) {
      setNamespacePosition(new Position(_namespacePosition.getOffset(), _namespace.length()));
    }
    int oldLength;
    if (oldNamespace != null) {
      oldLength = oldNamespace.length();
    }
    else {
      oldLength = 0;
    }
    
    int newLength = namespace.length();
    int diff = newLength - oldLength;
    
    setNamePosition(new Position(_namePosition.getOffset() + diff, _valuePosition.getLength()));
    setValuePosition(new Position(_valuePosition.getOffset() + diff, _valuePosition.getLength()));
  }
  
  public void setName(String name) {
    String oldName = _name;
    _name = name;
    if (_namePosition != null && _name != null) {
      setNamePosition(new Position(_namePosition.getOffset(), _name.length()));
    }
    int oldLength;
    if (oldName != null) {
      oldLength = oldName.length();
    }
    else {
      oldLength = 0;
    }
    
    int newLength = name.length();
    int diff = newLength - oldLength;
    setValuePosition(new Position(_valuePosition.getOffset() + diff, _valuePosition.getLength()));
  }

  public void setEndOffset(int endOffset) {
    _endOffset = endOffset;
  }

  public int getEndOffset() {
    int endOffset;
    if (_endOffset != -1) {
      endOffset = _endOffset;
    }
    else if (_valuePosition != null) {
      endOffset = _valuePosition.getOffset() + _valuePosition.getLength();
    }
    else {
      endOffset = getStartOffset();
    }
    return endOffset;
  }

  public void setStartOffset(int startOffset) {
    _startOffset = startOffset;
  }

  public int getStartOffset() {
    int startOffset;
    if (_startOffset != -1) {
      startOffset = _startOffset;
    }
    else if (_namespacePosition != null) {
      startOffset = _namespacePosition.getOffset();
    }
    else if (_namePosition != null) {
      startOffset = _namePosition.getOffset();
    }
    else {
      startOffset = 0;
    }
    return startOffset;
  }
  
  public void setNamespacePosition(Position namespacePosition) {
    _namespacePosition = namespacePosition;
  }
  
  public Position getNamespacePosition() {
    return _namespacePosition;
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

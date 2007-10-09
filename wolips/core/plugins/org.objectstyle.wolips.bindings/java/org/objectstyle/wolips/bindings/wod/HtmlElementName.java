package org.objectstyle.wolips.bindings.wod;

import org.eclipse.core.resources.IFile;

public class HtmlElementName {
  private IFile _htmlFile;
  private int _lineNumber;
  private int _startOffset;
  private int _endOffset;
  private String _name;

  public HtmlElementName(IFile htmlFile, String name, int lineNumber, int startOffset, int endOffset) {
    _htmlFile = htmlFile;
    _name = name;
    _lineNumber = lineNumber;
    _startOffset = startOffset;
    _endOffset = endOffset;
  }

  @Override
  public int hashCode() {
    return _name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof HtmlElementName && ((HtmlElementName) obj)._name.equals(_name);
  }
  
  public IFile getHtmlFile() {
    return _htmlFile;
  }

  public String getName() {
    return _name;
  }

  public int getLineNumber() {
    return _lineNumber;
  }

  public int getStartOffset() {
    return _startOffset;
  }

  public int getEndOffset() {
    return _endOffset;
  }
}
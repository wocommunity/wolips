package jp.aonir.fuzzyxml.internal;

import java.util.HashMap;
import java.util.Map;

public class RenderContext {
  private int _indent;
  private int _indentSize;
  private boolean _showNewlines;
  private boolean _indentTabs;
  private boolean _html;
  private boolean _addMissingQuotes;
  private boolean _spaceInEmptyTags;
  private boolean _spacesAroundEquals;
  private boolean _trim;
  private boolean _lowercaseTags;
  private boolean _lowercaseAttributes;
  private RenderDelegate _delegate;
  private Map<String, Object> _attributes;
  private boolean _shouldFormat;

  public RenderContext(boolean html) {
    _indentSize = 0;
    _showNewlines = false;
    _indentTabs = false;
    _html = html;
    _shouldFormat = true;
    _attributes = new HashMap<String, Object>();
  }
  
  public void setShouldFormat(boolean shouldFormat) {
    _shouldFormat = shouldFormat;
  }
  
  public boolean shouldFormat() {
    return _shouldFormat;
  }
  
  public void setAttribute(String key, Object value) {
    _attributes.put(key, value);
  }
  
  public Object getAttribute(String key) {
    return _attributes.get(key);
  }
  
  public void setDelegate(RenderDelegate delegate) {
    _delegate = delegate;
  }
  
  public RenderDelegate getDelegate() {
    return _delegate;
  }
  
  public void setLowercaseTags(boolean lowercaseTags) {
    _lowercaseTags = lowercaseTags;
  }
  
  public boolean isLowercaseTags() {
    return _lowercaseTags;
  }
  
  public void setLowercaseAttributes(boolean lowercaseAttributes) {
    _lowercaseAttributes = lowercaseAttributes;
  }
  
  public boolean isLowercaseAttributes() {
    return _lowercaseAttributes;
  }

  public void setSpaceInEmptyTags(boolean spaceInEmptyTags) {
    _spaceInEmptyTags = spaceInEmptyTags;
  }

  public boolean isSpaceInEmptyTags() {
    return _spaceInEmptyTags;
  }

  public void setSpacesAroundEquals(boolean spacesAroundEquals) {
    _spacesAroundEquals = spacesAroundEquals;
  }

  public boolean isSpacesAroundEquals() {
    return _spacesAroundEquals;
  }

  public void setTrim(boolean trim) {
    _trim = trim;
  }

  public boolean isTrim() {
    return _trim;
  }

  public void setAddMissingQuotes(boolean addMissingQuotes) {
    _addMissingQuotes = addMissingQuotes;
  }

  public boolean isAddMissingQuotes() {
    return _addMissingQuotes;
  }

  public void setShowNewlines(boolean newlines) {
    _showNewlines = newlines;
  }

  public boolean isShowNewlines() {
    return _showNewlines;
  }

  public void setHtml(boolean html) {
    _html = html;
  }

  public boolean isHtml() {
    return _html;
  }

  public void setIndentTabs(boolean indentTabs) {
    _indentTabs = indentTabs;
  }

  public boolean isIndentTabs() {
    return _indentTabs;
  }

  public void setIndent(int indent) {
    _indent = indent;
  }

  public int getIndent() {
    return _indent;
  }

  public void appendIndent(StringBuffer sb) {
    for (int indentNum = 0; indentNum < _indent; indentNum++) {
      if (_indentTabs) {
        sb.append("\t");
      }
      else {
        for (int spaceNum = 0; spaceNum < _indentSize; spaceNum++) {
          sb.append(" ");
        }
      }
    }
  }

  public void indent() {
    _indent++;
  }

  public void outdent() {
    _indent--;
  }

  public int getIndentSize() {
    return _indentSize;
  }

  public void setIndentSize(int indentSize) {
    _indentSize = indentSize;
  }

  @Override
  public RenderContext clone() {
    RenderContext clone = new RenderContext(isHtml());
    clone._addMissingQuotes = _addMissingQuotes;
    clone._attributes = _attributes;
    clone._delegate = _delegate;
    clone._indent = _indent;
    clone._indentSize = _indentSize;
    clone._indentTabs = _indentTabs;
    clone._lowercaseAttributes = _lowercaseAttributes;
    clone._lowercaseTags = _lowercaseTags;
    clone._shouldFormat = _shouldFormat;
    clone._showNewlines = _showNewlines;
    clone._spaceInEmptyTags = _spaceInEmptyTags;
    clone._spacesAroundEquals = _spacesAroundEquals;
    clone._trim = _trim;
    return clone;
  }
}

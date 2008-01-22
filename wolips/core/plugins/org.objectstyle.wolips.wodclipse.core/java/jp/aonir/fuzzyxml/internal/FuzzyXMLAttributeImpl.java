package jp.aonir.fuzzyxml.internal;

import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLAttributeImpl extends AbstractFuzzyXMLNode implements FuzzyXMLAttribute {

  private char _quote = '"';
  private boolean _escape = true;
  private String _name;
  private String _value;
  private int _valueOffset;

  public FuzzyXMLAttributeImpl(String name) {
    this(null, name, null, -1, -1, -1);
  }

  public FuzzyXMLAttributeImpl(String name, String value) {
    this(null, name, null, -1, -1, -1);
    setValue(value);
  }

  public FuzzyXMLAttributeImpl(FuzzyXMLNode parent, String name, String value, int offset, int length, int valueOffset) {
    super(parent, offset, length);
    this._name = name;
    this._value = value;
    _valueOffset = valueOffset;
  }

  public String getName() {
    return _name;
  }
  
  public int getNameOffset() {
    return getOffset();
  }
  
  public int getNameLength() {
    return _name != null ? _name.length() : 0;
  }
  
  public int getValueOffset() {
    return _valueOffset;
  }
  
  public int getValueLength() {
    return _value != null ? _value.length() : 0;
  }
  
  public int getValueDataOffset() {
    int offset = 0;
    if (_value != null) {
      offset = getValueOffset();
      if (isQuoted()) {
        offset ++;
      }
    }
    return offset;
  }
  
  public int getValueDataLength() {
    int length = 0;
    if (_value != null) {
      length = getValueLength();
    }
    return length;
  }

  public void setValue(String value) {
    if (this._value == null) {
      this._value = "";
    }
    
    int length = this._value.length();
    this._value = (value == null) ? "" : value;

    // 更新イベントを発火
    fireModifyEvent(toXMLString(new RenderContext(getDocument().isHTML())), getOffset(), getLength());
    // 位置情報を更新
    appendOffset((FuzzyXMLElement) getParentNode(), getOffset(), this._value.length() - length);
  }

  public String getValue() {
    return _value;
  }

  public boolean isQuoted() {
    return _quote != 0;
  }
  
  public char getQuoteCharacter() {
    return _quote;
  }

  public void setQuoteCharacter(char c) {
    _quote = c;
  }

  public void setEscape(boolean escape) {
    this._escape = escape;
  }

  public boolean isEscape() {
    return this._escape;
  }

  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    boolean isHTML = renderContext.isHtml();

    xmlBuffer.append(" ");
    String attributeName = FuzzyXMLUtil.escape(getName(), isHTML);
    if (renderContext.isLowercaseAttributes()) {
      FuzzyXMLNode parentNode = getParentNode();
      boolean inlineTag = (parentNode instanceof FuzzyXMLElement && WodHtmlUtils.isInline(((FuzzyXMLElement)parentNode).getName()));
      if (!inlineTag && FuzzyXMLUtil.isAllUppercase(attributeName)) {
        attributeName = attributeName.toLowerCase();
      }
    }
    xmlBuffer.append(attributeName);
    if (renderContext.isSpacesAroundEquals()) {
      xmlBuffer.append(" ");
    }
    xmlBuffer.append("=");
    if (renderContext.isSpacesAroundEquals()) {
      xmlBuffer.append(" ");
    }
    char quote = _quote;
    if (renderContext.isAddMissingQuotes()) {
      quote = '"';
    }
    if (quote != 0) {
      xmlBuffer.append(quote);
    }
    if (_escape) {
      xmlBuffer.append(_value);//FuzzyXMLUtil.escape(getValue(), isHTML));
    }
    else {
      String value = getValue();
      for (int i = 0; i < value.length(); i++) {
        char c = value.charAt(i);
        if (_quote == c) {
          xmlBuffer.append('\\');
        }
        xmlBuffer.append(c);
      }
    }
    if (quote != 0) {
      xmlBuffer.append(quote);
    }
  }

  @Override
  public String toString() {
    return "attr: " + getName() + "=" + getValue();
  }
}

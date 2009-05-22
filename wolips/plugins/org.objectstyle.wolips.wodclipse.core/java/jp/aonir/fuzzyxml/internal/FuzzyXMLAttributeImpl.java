package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;

public class FuzzyXMLAttributeImpl extends AbstractFuzzyXMLNode implements FuzzyXMLAttribute {

  private char _quote = '"';
  private boolean _escape = true;
  private String _namespace;
  private String _name;
  private String _value;
  private String _rawValue;
  private int _valueOffset;

  public FuzzyXMLAttributeImpl(String namespace, String name) {
    this(null, namespace, name, null, null, -1, -1, -1);
  }

  public FuzzyXMLAttributeImpl(String namespace, String name, String value, String rawValue) {
    this(null, namespace, name, value, rawValue, -1, -1, -1);
    setValue(value);
    _rawValue = rawValue;
  }

  public FuzzyXMLAttributeImpl(FuzzyXMLNode parent, String namespace, String name, String value, String rawValue, int offset, int length, int valueOffset) {
    super(parent, offset, length);
    this._namespace = namespace;
    this._name = name;
    this._value = value;
    this._rawValue = rawValue;
    _valueOffset = valueOffset;
  }

  public String getRawValue() {
    return _rawValue;
  }
  
  public String getName() {
    return _name;
  }
  
  public String getNamespace() {
    return _namespace;
  }
  
  public int getNamespaceOffset() {
    return getOffset() + 1;
  }
  
  public int getNamespaceLength() {
    return _namespace != null ? _namespace.length() : 0;
  }
  
  public int getNameOffset() {
    int nameOffset;
    if (_namespace == null) {
      nameOffset = getOffset() + 1;
    }
    else {
      nameOffset = getNamespaceOffset() + getNamespaceLength() + 1;
    }
    return nameOffset;
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
    this._rawValue = this._value;

    // 更新イベントを発火
    FuzzyXMLDocumentImpl document = getDocument();
    boolean html = (document == null) ? true : document.isHTML();
    fireModifyEvent(toXMLString(new RenderContext(html)), getOffset(), getLength());
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

  public String getNamespaceName() {
    String attributeName = getName();
    String namespace = getNamespace();
    if (namespace != null && namespace.length() > 0) {
      attributeName = namespace + ":" + attributeName;
    }
    return attributeName;
  }
  
  public String toDebugString() {
    StringBuffer sb = new StringBuffer();
    toDebugString(sb, 0);
    return sb.toString();
  }

  public void toDebugString(StringBuffer buffer, int indent) {
    for (int i = 0; i < indent; i ++) {
      buffer.append("  ");
    }
    buffer.append(getName() + "=" + getValue());
  }
  
  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    boolean isHTML = renderContext.isHtml();

    xmlBuffer.append(" ");
    String attributeName = FuzzyXMLUtil.escape(getName(), isHTML);
    String namespace = getNamespace();
    if (namespace != null && namespace.length() > 0) {
      attributeName = namespace + ":" + attributeName;
    }
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
      // Only do minimal XML escaping on the contents of attribute values
      xmlBuffer.append(FuzzyXMLUtil.escape(_value, false));
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

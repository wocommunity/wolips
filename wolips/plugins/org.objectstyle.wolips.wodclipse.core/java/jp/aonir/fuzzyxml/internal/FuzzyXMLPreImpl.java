package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLPreImpl extends FuzzyXMLElementImpl {

  private String _value;
  
  public FuzzyXMLPreImpl(String value) {
    this(null, value, -1, -1);
  }

  public FuzzyXMLPreImpl(FuzzyXMLNode parent, String value, int offset, int length) {
    super(parent, "pre", offset, length, -1);
    this._value = value;
  }

  @Override
  public String getValue() {
    return this._value;
  }
  
  @Override
  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    renderContext.appendIndent(xmlBuffer);
    xmlBuffer.append("<pre>").append(getValue()).append("</pre>");
    if (renderContext.isShowNewlines()) {
      xmlBuffer.append("\n");
    }
  }

  @Override
  public String toString() {
    return "PRE: " + getValue();
  }
  
}

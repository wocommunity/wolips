package jp.aonir.fuzzyxml.internal;

import jp.aonir.fuzzyxml.FuzzyXMLCDATA;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

public class FuzzyXMLCDATAImpl extends FuzzyXMLElementImpl implements FuzzyXMLCDATA {

  private String _value;

  public FuzzyXMLCDATAImpl(String value) {
    this(null, value, -1, -1);
  }

  public FuzzyXMLCDATAImpl(FuzzyXMLNode parent, String value, int offset, int length) {
    super(parent, "", offset, length, -1);
    this._value = value;
  }

  @Override
  public String getValue() {
    return this._value;
  }

  @Override
  public void toXMLString(RenderContext renderContext, StringBuffer xmlBuffer) {
    renderContext.appendIndent(xmlBuffer);
    xmlBuffer.append("<![CDATA[");
    //xmlBuffer.append(FuzzyXMLUtil.escapeCDATA(getValue()));
    xmlBuffer.append(getValue());
    xmlBuffer.append("]]>");
    if (renderContext.isShowNewlines()) {
      xmlBuffer.append("\n");
    }
  }

  @Override
  public String toString() {
    return "CDATA: " + getValue();
  }

}
